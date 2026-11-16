package tcenglish.agent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import tcenglish.Translations;

/**
 * Java agent that rewrites TobysCamera's Chinese string constants to English as
 * its classes are loaded. Only classes under dev/tobyscamera/ are touched, and
 * only CONSTANT_Utf8 entries that exactly match a key in {@link Translations}.
 */
public final class EnglishAgent implements ClassFileTransformer {

    public static final String STATUS_PROPERTY = "tcenglish.agent";
    public static final String COUNT_PROPERTY = "tcenglish.replaced";

    private static final String TARGET_PREFIX = "dev/tobyscamera/";
    private static final AtomicInteger REPLACED = new AtomicInteger();

    private EnglishAgent() {}

    public static void premain(String args, Instrumentation inst) {
        Map<String, String> table = Translations.CONSTANTS; // validates the table
        inst.addTransformer(new EnglishAgent());
        System.setProperty(STATUS_PROPERTY, "active");
        System.setProperty(COUNT_PROPERTY, "0");
        System.out.println("[tobyscamera-english-patch] Agent active with " + table.size() + " translations.");
    }

    @Override
    public byte[] transform(Module module, ClassLoader loader, String className,
                            Class<?> redefined, ProtectionDomain domain, byte[] bytes) {
        if (className == null || !className.startsWith(TARGET_PREFIX)) return null;
        try {
            byte[] out = rewrite(bytes);
            return out; // null when nothing changed
        } catch (Throwable t) {
            System.err.println("[tobyscamera-english-patch] Could not translate " + className + ", leaving it unchanged: " + t);
            return null;
        }
    }

    private static byte[] rewrite(byte[] b) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));
        if (in.readInt() != 0xCAFEBABE) return null;
        int minor = in.readUnsignedShort();
        int major = in.readUnsignedShort();
        int poolCount = in.readUnsignedShort();

        ByteArrayOutputStream poolBytes = new ByteArrayOutputStream(b.length + 1024);
        DataOutputStream pool = new DataOutputStream(poolBytes);
        int changed = 0;

        for (int i = 1; i < poolCount; i++) {
            int tag = in.readUnsignedByte();
            pool.writeByte(tag);
            switch (tag) {
                case 1 -> { // CONSTANT_Utf8 (modified UTF-8, same encoding as readUTF/writeUTF)
                    String s = in.readUTF();
                    String english = Translations.CONSTANTS.get(s);
                    if (english != null) {
                        s = english;
                        changed++;
                    }
                    pool.writeUTF(s);
                }
                case 3, 4 -> copy(in, pool, 4);        // Integer, Float
                case 5, 6 -> { copy(in, pool, 8); i++; } // Long, Double take two slots
                case 7, 8, 16, 19, 20 -> copy(in, pool, 2); // Class, String, MethodType, Module, Package
                case 9, 10, 11, 12, 17, 18 -> copy(in, pool, 4); // refs, NameAndType, Dynamic, InvokeDynamic
                case 15 -> copy(in, pool, 3);          // MethodHandle
                default -> throw new IOException("unknown constant pool tag " + tag + " at index " + i);
            }
        }
        if (changed == 0) return null;

        int rest = in.available(); // everything after the constant pool is copied verbatim
        ByteArrayOutputStream out = new ByteArrayOutputStream(8 + 2 + poolBytes.size() + rest);
        DataOutputStream o = new DataOutputStream(out);
        o.writeInt(0xCAFEBABE);
        o.writeShort(minor);
        o.writeShort(major);
        o.writeShort(poolCount);
        poolBytes.writeTo(o);
        in.transferTo(o);
        o.flush();

        System.setProperty(COUNT_PROPERTY, Integer.toString(REPLACED.addAndGet(changed)));
        return out.toByteArray();
    }

    private static void copy(DataInputStream in, DataOutputStream out, int n) throws IOException {
        byte[] buf = new byte[n];
        in.readFully(buf);
        out.write(buf);
    }

    /** Offline self-test: translate a directory of .class files in place. */
    public static void main(String[] args) throws IOException {
        java.nio.file.Path root = java.nio.file.Path.of(args[0]);
        try (var files = java.nio.file.Files.walk(root)) {
            for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) files.filter(f -> f.toString().endsWith(".class"))::iterator) {
                byte[] out = rewrite(java.nio.file.Files.readAllBytes(p));
                if (out != null) java.nio.file.Files.write(p, out);
            }
        }
        System.out.println("Replaced " + REPLACED.get() + " constants.");
    }
}

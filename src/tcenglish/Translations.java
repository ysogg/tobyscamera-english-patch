package tcenglish;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Chinese -> English table for TobysCamera 0.2.10.
 *
 * Keys are the exact string constants found in the TobysCamera class files.
 * U+0001 marks a runtime value inserted by Java string concatenation
 * (e.g. a player name or number); the English text must keep the same number
 * of U+0001 markers, in the same order.
 */
public final class Translations {

    public static final char VALUE = '\u0001';

    /** Every constant to rewrite in TobysCamera's classes. */
    public static final Map<String, String> CONSTANTS;

    /** Subset that TobysCamera writes onto persistent items (names / lore). */
    private static final Map<String, String> ITEM_TEXT;
    private static final List<Template> ITEM_TEMPLATES;

    static {
        Map<String, String> item = new LinkedHashMap<>();
        // --- Photo bags (PhotoBagFactory) ---
        item.put("右键长按: 取出地图", "Hold right-click: take out maps");
        item.put("相片ID", "Photo ID");
        item.put("录像ID", "Video ID");
        item.put("相片", "Photo");
        item.put("录像", "Video");
        item.put("底片", "Negative");
        item.put("\u0001袋", "\u0001 Bag");
        item.put("尺寸: \u0001×\u0001", "Size: \u0001×\u0001");
        item.put("拍摄者: \u0001", "Photographer: \u0001");
        item.put("拍摄坐标: \u0001", "Location: \u0001");
        item.put("拍摄时间: \u0001", "Taken: \u0001");
        item.put("右键空展示框: 展开\u0001", "Right-click an empty item frame: unpack \u0001");
        item.put("类型: \u0001", "Type: \u0001");
        item.put("预览地图: #\u0001", "Preview map: #\u0001");
        // --- Camera film (CameraFilmService) ---
        item.put("剩余胶卷: ", "Film remaining: ");
        item.put("最大尺寸: ", "Max size: ");
        item.put("剩余胶卷: \u0001", "Film remaining: \u0001");
        item.put("最大尺寸: \u0001x", "Max size: \u0001x");
        // --- Photo maps (MapItemPresentation) ---
        item.put("照片", "Photo");
        item.put("网格位置: \u0001, \u0001", "Grid position: \u0001, \u0001");

        Map<String, String> all = new LinkedHashMap<>(item);
        // --- Gallery GUI and messages (PhotoGalleryListener) ---
        all.put("拍摄信息", "Capture Info");
        all.put("旧照片未保存拍摄信息", "Older photo: no capture info was saved");
        all.put("拍摄者", "Photographer");
        all.put("拍摄坐标", "Location");
        all.put("拍摄时间", "Time Taken");
        all.put("照片标题", "Photo Title");
        all.put("描述", "Description");
        all.put("公开信息", "Public Info");
        all.put("输入玩家名称、UUID 前缀或照片 ID 前缀进行搜索。",
                "Type a player name, UUID prefix, or photo ID prefix to search.");
        all.put("照片袋已交付。", "Photo bag delivered.");
        all.put("是", "Yes");
        all.put("否", "No");
        all.put("无", "None");
        all.put("照片已删除。", "Photo deleted.");
        all.put("上传者", "Uploader");
        all.put("保存时间", "Saved");
        all.put("尺寸", "Size");
        all.put("照片 ID", "Photo ID");
        all.put("再次点击确认领取照片袋", "Click again to confirm claiming the photo bag");
        all.put("领取不可复制照片袋", "Claim non-copyable photo bag");
        all.put("不会作为底片交付", "Will not be delivered as a negative");
        all.put("解除上传禁用", "Re-enable uploads");
        all.put("禁用该玩家上传", "Block this player from uploading");
        all.put("再次点击确认删除", "Click again to confirm deletion");
        all.put("删除此照片", "Delete this photo");
        all.put("删除后无法恢复", "This cannot be undone");
        all.put("返回列表", "Back to list");
        all.put("关闭", "Close");
        all.put("上一页", "Previous page");
        all.put("玩家", "Player");
        all.put("返回玩家列表", "Back to player list");
        all.put("排序: 最新优先", "Sort: Newest first");
        all.put("排序: 最早优先", "Sort: Oldest first");
        all.put("点击切换", "Click to switch");
        all.put("下一页", "Next page");
        all.put("还有更多照片", "More photos available");
        all.put("已到最后一页", "This is the last page");
        all.put("按玩家/ID 搜索", "Search by player/ID");
        all.put("点击后在聊天栏输入", "Click, then type in chat");
        all.put("清除筛选", "Clear filter");
        all.put("恢复全部照片", "Show all photos again");
        all.put("玩家列表", "Player list");
        all.put("按玩家聚合浏览", "Browse grouped by player");
        all.put("恢复全部玩家", "Show all players again");
        all.put("还有更多玩家", "More players available");
        all.put("全部照片", "All photos");
        all.put("不按玩家聚合浏览", "Browse without grouping by player");
        all.put("TobysCamera 地图画图库", "TobysCamera Map Art Gallery");
        all.put("玩家地图画", "Player Map Art");
        all.put("地图画详情", "Map Art Details");
        all.put("坐标: \u0001", "Location: \u0001");
        all.put("时间: \u0001", "Time: \u0001");
        all.put("照片数量: \u0001", "Photos: \u0001");
        all.put("最近保存: \u0001", "Last saved: \u0001");
        all.put("尺寸: \u0001 x \u0001", "Size: \u0001 x \u0001");
        all.put("再次点击确认\u0001", "Click again to confirm: \u0001");
        all.put("当前: \u0001", "Current: \u0001");
        all.put("共 \u0001 张照片", "\u0001 photos total");

        for (Map.Entry<String, String> e : all.entrySet()) {
            if (count(e.getKey()) != count(e.getValue())) {
                throw new IllegalStateException("Placeholder mismatch for: " + e.getKey());
            }
        }
        CONSTANTS = Collections.unmodifiableMap(all);
        ITEM_TEXT = Collections.unmodifiableMap(item);

        List<Template> templates = new ArrayList<>();
        for (Map.Entry<String, String> e : item.entrySet()) {
            if (count(e.getKey()) > 0) templates.add(new Template(e.getKey(), e.getValue()));
        }
        // Most specific (longest literal text) first.
        templates.sort((a, b) -> Integer.compare(b.literalLength, a.literalLength));
        ITEM_TEMPLATES = Collections.unmodifiableList(templates);
    }

    private Translations() {}

    private static int count(String s) {
        int n = 0;
        for (int i = 0; i < s.length(); i++) if (s.charAt(i) == VALUE) n++;
        return n;
    }

    /** True if the text contains CJK characters (cheap pre-check). */
    public static boolean hasCjk(String s) {
        for (int i = 0; i < s.length(); i++) if (s.charAt(i) >= '⺀') return true;
        return false;
    }

    /**
     * Translates one already-rendered line of item text (as stored on an existing
     * item), or returns null if it isn't TobysCamera item text.
     */
    public static String translateItemLine(String line) {
        if (line == null || !hasCjk(line)) return null;
        String exact = ITEM_TEXT.get(line);
        if (exact != null && count(line) == 0) return exact;
        for (Template t : ITEM_TEMPLATES) {
            String r = t.apply(line);
            if (r != null) return r;
        }
        return null;
    }

    private static final class Template {
        final Pattern pattern;
        final String[] englishParts;
        final int literalLength;

        Template(String chinese, String english) {
            StringBuilder re = new StringBuilder("^");
            String[] parts = chinese.split("\u0001", -1);
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) re.append("(.*?)");
                if (!parts[i].isEmpty()) re.append(Pattern.quote(parts[i]));
            }
            re.append('$');
            this.pattern = Pattern.compile(re.toString(), Pattern.DOTALL);
            this.englishParts = english.split("\u0001", -1);
            this.literalLength = chinese.length() - count(chinese);
        }

        String apply(String line) {
            Matcher m = pattern.matcher(line);
            if (!m.matches()) return null;
            StringBuilder out = new StringBuilder(englishParts[0]);
            for (int g = 1; g <= m.groupCount(); g++) {
                String value = m.group(g);
                String translatedValue = ITEM_TEXT.get(value); // e.g. "相片" in "相片袋"
                out.append(translatedValue != null ? translatedValue : value);
                out.append(englishParts[g]);
            }
            return out.toString();
        }
    }
}

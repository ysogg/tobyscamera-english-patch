#!/bin/sh
# Builds <server>/plugins/tobyscamera-english-patch.jar (Git Bash on Windows, or Linux/macOS).
# Compiles against the server's libraries folder. The server defaults to the current
# directory; override it with:
#   SERVER="C:/path/to/server" sh build.sh     (Windows)
#   SERVER="/path/to/server" sh build.sh       (Linux/macOS)
set -e
# pwd -W only works in Git Bash/MSYS, where javac needs Windows-style paths and ';' as the
# classpath separator. Elsewhere it fails and we use plain pwd and ':'.
if pwd -W >/dev/null 2>&1; then SEP=';'; else SEP=':'; fi
HERE="$(cd "$(dirname "$0")" && (pwd -W 2>/dev/null || pwd))"
SERVER="${SERVER:-$(pwd -W 2>/dev/null || pwd)}"
LIB="$SERVER/libraries"
CP=$(find "$LIB" -name 'paper-api-*.jar' -o -name 'purpur-api-*.jar' -o -name 'folia-api-*.jar' -o -name 'adventure-api-5*.jar' -o -name 'adventure-key-5*.jar' \
      -o -name 'examination-api-*.jar' -o -name 'annotations-*.jar' -o -name 'jspecify-*.jar' -o -name 'guava-*.jar' | tr '\n' "$SEP")
rm -rf "$HERE/build"; mkdir -p "$HERE/build/classes"
find "$HERE/src" -name '*.java' | sed 's/.*/"&"/' > "$HERE/build/sources.txt"
javac --release 21 -encoding UTF-8 -nowarn -cp "$CP" -d "$HERE/build/classes" @"$HERE/build/sources.txt"
cp "$HERE/resources/plugin.yml" "$HERE/build/classes/"
jar --create --file "$SERVER/plugins/tobyscamera-english-patch.jar" \
    --manifest "$HERE/resources/META-INF/MANIFEST.MF" -C "$HERE/build/classes" .
echo "Built $SERVER/plugins/tobyscamera-english-patch.jar"

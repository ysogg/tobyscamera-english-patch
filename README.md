# An English Language Patch For TobysCamera Minecraft Plugin


#### TobysCamera has its Chinese text compiled with no language file, so this patch is one jar with two parts:

  1. A Java agent (loaded with -javaagent) that swaps TobysCamera's 78 Chinese
     strings for English as its classes load. It covers photo/map/bag names and
     lore, camera film lore, the /tobyscamera gallery GUI and chat messages.
     The TobysCamera jar itself is never modified.
  2. A normal plugin that converts Chinese lore on items made BEFORE the patch
     (on join, when a container is opened, and on item pickup). This matters
     because TobysCamera reads its own lore back: the film lines are found by
     prefix and negatives by the "Negative" line.

## Install
- Place this plugin in your server's plugins folder

- Your server launch script must contain:  
```-javaagent:plugins/tobyscamera-english-patch.jar``` before  ```-jar <paper/purpur/folia>.jar```
> Supports Paper, Purpur, and Folia. If it doesn't work on your server please leave an issue and I'll address it as soon as possible.

To remove: delete the jar AND the -javaagent part of run.bat. Items already
converted keep English lore, and the Chinese plugin will then add duplicate film
lines to cameras, so only remove it if you are switching to an official English
TobysCamera build.

## Build
Run the provided build script and specify SERVER var as the location of your server folder.

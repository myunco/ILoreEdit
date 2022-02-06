package xyz.myunco.iloreedit.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TabComplete {
    public static HashMap<String, List<String>> tabListMap = new HashMap<>();

    static {
        tabListMap.put("ILoreEdit", Arrays.asList("help", "reload", "version"));
        tabListMap.put("EditLore", Arrays.asList("name", "add", "set", "ins", "del", "clear", "import", "export", "owner", "model"));
        tabListMap.put("EditLore.clear", Arrays.asList("name", "lore", "model"));
        tabListMap.put("EditLore.owner", Collections.emptyList());
        tabListMap.put("EditLore.export", Collections.emptyList());
    }

    public static List<String> getTabPath(String[] args, String command) {
        StringBuilder builder = new StringBuilder(command);
        for (int i = 1; i < args.length; i++) {
            builder.append(".").append(args[i - 1].toLowerCase());
        }
        return tabListMap.get(builder.toString());
    }

    public static List<String> getCompleteList(String[] args, List<String> list) {
        return getCompleteList(args, list, false);
    }

    public static List<String> getCompleteList(String[] args, List<String> list, boolean listToLowerCase) {
        List<String> ret = new ArrayList<>();
        if (list == null) {
            return ret; //默认情况下 返回空List
        } else if (list.isEmpty()) {
            return null; //返回null时 游戏会用线玩家的名字列表作为候选
        } else if (args[args.length - 1].equals("")) {
            return list;
        }
        String arg = args[args.length - 1].toLowerCase();
        for (String value : list) {
            if (listToLowerCase) {
                if (value.toLowerCase().startsWith(arg)) {
                    ret.add(value);
                }
            } else {
                if (value.startsWith(arg)) {
                    ret.add(value);
                }
            }
        }
        return ret;
    }

}

package xyz.myunco.iloreedit.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.myunco.iloreedit.ILoreEdit;
import xyz.myunco.iloreedit.config.Language;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static int getLine(Player player, String number, int loreSize) {
        int line;
        try {
            line = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            player.sendMessage(Language.prefix + Language.invalidLine);
            return 0;
        }
        if (line > loreSize) {
            player.sendMessage(Language.prefix + Language.errorLine);
            return 0;
        } else if (line == 0) {
            player.sendMessage(Language.prefix + Language.zeroLine);
            return 0;
        }
        return line;
    }

    public static List<String> getLore(ItemMeta meta) {
        List<String> lore = meta.getLore();
        return lore == null ? new ArrayList<>() : lore;
    }

    public static String translateColor(String str) {
        if (str.contains("&")) {
            if (ILoreEdit.mcVersion > 15) {
                return ChatColor.translateAlternateColorCodes('&', ColorUtil.processHexColor(ColorUtil.processGradientColor(str)));
            }
            return ChatColor.translateAlternateColorCodes('&', str);
        } else {
            return str;
        }
    }

    public static String getTabPath(String[] args, String command) {
        StringBuilder builder = new StringBuilder(command);
        for (int i = 1; i < args.length; i++) {
            builder.append(".").append(args[i - 1].toLowerCase());
        }
        return builder.toString();
    }

    public static List<String> getTabList(String[] args, List<String> list) {
        return getTabList(args, list, false);
    }

    public static List<String> getTabList(String[] args, List<String> list, boolean listToLowerCase) {
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

    public static String getTextLeft(String str, String subStr) {
        int index = str.indexOf(subStr);
        if (index == -1 || subStr.length() > str.length()) {
            return str;
        }
        return str.substring(0, index);
    }

    public static String getTextRight(String str, String subStr) {
        int index = str.indexOf(subStr);
        if (index == -1 || subStr.length() > str.length()) {
            return "";
        }
        return str.substring(index + subStr.length());
    }

    public static String[] getArgs(String arg) {
        return arg.split(" ");
    }
}

package cn.suml.iloreedit.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import cn.suml.iloreedit.config.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cn.suml.iloreedit.ILoreEdit.plugin;

public class Utils {

    public static int getLine(CommandSender sender, String number, int loreSize) {
        int line;
        try {
            line = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            sender.sendMessage(Language.messagePrefix + Language.commandEditloreInvalidLine);
            return 0;
        }
        if (line > loreSize || line < 1) {
            sender.sendMessage(Language.messagePrefix + Language.replaceArgs(Language.commandEditloreErrorLine, line));
            return 0;
        }
        return line;
    }

    public static List<String> getLore(ItemMeta meta) {
        List<String> lore = meta.getLore();
        return lore == null ? new ArrayList<>() : lore;
    }

    public static String translateColor(Player player, String str) {
        str = replacePlaceholders(player, str);
        if (str.contains("&")) {
            if (plugin.mcVersion.isGreaterThan(15)) {
                return ChatColor.translateAlternateColorCodes('&', ColorUtil.processHexColor(ColorUtil.processGradientColor(str)));
            }
            return ChatColor.translateAlternateColorCodes('&', str);
        } else {
            return str;
        }
    }

    public static String replacePlaceholders(Player player, String text) {
        if (text == null) {
            return null;
        }
        if (plugin.enablePAPI && mayContainPlaceholders(text)) {
            return PlaceholderAPI.setPlaceholders(player, text.indexOf('{') == -1 ? text : text.replace("{player}", player.getName()));
        }
        return text.indexOf('{') == -1 ? text : text.replace("{player}", player.getName());
    }

    public static boolean mayContainPlaceholders(String text) {
        char[] value = text.toCharArray();
        int count = 0;
        for (char c : value) {
            if (c == '%') {
                count++;
                if (count == 2) {
                    return text.indexOf('_') != -1;
                }
            }
        }
        return false;
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


    public static String replaceSpace(String text) {
        return text.indexOf('"') == -1 ? text : text.replace("\"\"", " ");
    }

    /**
     * 将一个字符串解析为int类型
     * @param str 数字字符串
     * @return 返回str表示的int. 无效数字格式返回-1.
     */
    public static int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static List<String> generateLineNumber(int size) {
        return IntStream.rangeClosed(1, size)
                .mapToObj(Integer::toString)
                .collect(Collectors.toList());
    }

}

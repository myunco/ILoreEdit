package xyz.myunco.iloreedit.config;

import org.bukkit.configuration.file.YamlConfiguration;
import xyz.myunco.iloreedit.ILoreEdit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class ConfigLoader {

    @SuppressWarnings("SpellCheckingInspection")
    public static void load() {
        ILoreEdit.plugin.saveDefaultConfig();
        YamlConfiguration config = loadConfiguration(new File(ILoreEdit.plugin.getDataFolder(), "config.yml"));
        Config.checkUpdate = config.getBoolean("checkUpdate", true);
        Language.enable = config.getString("message.enable", "§b已启用.");
        Language.disable = config.getString("message.disable", "§7已卸载.");
        Language.prefix = config.getString("message.prefix", "§8[§3ILoreEdit§8] §c-> ");
        StringBuilder helpMsg = new StringBuilder();
        for (String line : config.getStringList("message.helpMsg")) {
            if (helpMsg.length() != 0) {
                helpMsg.append('\n');
            }
            helpMsg.append(line);
        }
        if (helpMsg.length() == 0) {
            helpMsg.append("§e===========§bILoreEdit§e===========\n")
                    .append("§a/lore name <名字> §7---- §b设置物品名字")
                    .append("§a/lore add <内容> §7---- §b添加一行Lore")
                    .append("§a/lore set <行号> <内容> §7---- §b修改指定行的Lore")
                    .append("§a/lore ins <行号> <内容> §7---- §b在指定行插入Lore")
                    .append("§a/lore del [行号] §7---- §b删除指定行的Lore,不指定行号则删除末行")
                    .append("§a/lore clear name §7---- §b清除物品名字")
                    .append("§a/lore clear lore §7---- §b清除所有Lore")
                    .append("§a/lore clear model §7---- §b清除自定义模型数据")
                    .append("§a/lore import <模板名> §7---- §b导入指定的模板")
                    .append("§a/lore export <模板名> §7---- §b导出到指定的模板")
                    .append("§a/lore owner <玩家名> §7---- §b修改头颅主人")
                    .append("§a/lore model <模型数据> §7---- §b设置自定义模型数据")
                    .append("§e/ILoreEdit help §7---- §a查看指令帮助")
                    .append("§e/ILoreEdit reload §7---- §a重载插件配置")
                    .append("§e/ILoreEdit version §7---- §a查看插件版本");
        }
        Language.helpMsg = helpMsg.toString();
        Language.canOnlyPlayer = config.getString("message.canOnlyPlayer", "§b本命令只能玩家使用!");
        Language.argsError = config.getString("message.argsError", "§c错误的命令参数!");
        Language.reloaded = config.getString("message.reloaded", "§a配置文件重载完成.");
        Language.noItem = config.getString("message.noItem", "§d你确定你手里有物品???");
        Language.editDisplayName = config.getString("message.editDisplayName", "§a已修改物品显示名.");
        Language.addLore = config.getString("message.addLore", "§a已添加Lore.");
        Language.noLore = config.getString("message.noLore", "§b该物品没有Lore,你要修改什么?");
        Language.invalidLine = config.getString("message.invalidLine", "§4你输入的行号不是一个有效的数字.");
        Language.errorLine = config.getString("message.errorLine", "§b你数数一共有几行···");
        Language.zeroLine = config.getString("message.zeroLine", "§b行数从1开始.");
        Language.setLore = config.getString("message.setLore", "§a已修改Lore.");
        Language.noLore_ins = config.getString("message.noLore-ins", "§b该物品没有Lore,你想怎么插入?");
        Language.insLore = config.getString("message.insLore", "§a已插入Lore.");
        Language.noLore_del = config.getString("message.noLore-del", "§b该物品没有Lore,你要删除啥?");
        Language.delLore = config.getString("message.delLore", "§a已删除Lore.");
        Language.clearDisplayName = config.getString("message.clearDisplayName", "§a已清除物品显示名.");
        Language.clearLore = config.getString("message.clearLore", "§a已清除Lore.");
        Language.saveError = config.getString("message.saveError", "§c保存修改失败,请重试.");
        Language.usage = config.getString("message.usage", "§6用法:");
        Language.usageEditLore = config.getString("message.usageEditLore", "§e/lore §3<§aname§7|§aadd§7|§aset§7|§ains§7|§adel§7|§aclear§7|§aimport§7|§aexport§7|§aowner§7|§amodel§3> §7- 使用/ile help查看详细帮助");
        Language.usageILoreEdit = config.getString("message.usageILoreEdit", "§e/ILoreEdit §3<§ahelp§c|§aversion§c|§areload§3>");
        Language.templateNotExist = config.getString("message.templateNotExist", "§c此模板不存在!");
        Language.templateImported = config.getString("message.templateImported", "§a已导入模板.");
        Language.noExport = config.getString("message.noExport", "§e你手中的物品没什么可导出的.");
        Language.templateExported = config.getString("message.templateExported", "§a已导出至模板.");
        Language.noSkull = config.getString("message.noSkull", "§d你确定你手里拿的是头颅???");
        Language.changedOwner = config.getString("message.changedOwner", "§a已修改头颅主人.");
        Language.invalidTemplateName = config.getString("message.invalidTemplateName", "§c无效的模板名称!");
        Language.notSupport = config.getString("message.notSupport", "§c服务器版本过低,不支持设置自定义模型数据");
        Language.invalidData = config.getString("message.invalidData", "§4你输入的模型数据不是一个有效的数字.");
        Language.setModelData = config.getString("message.setModelData", "§a已设置自定义模型数据.");
        Language.clearModelData = config.getString("message.clearModelData", "§a已清除自定义模型数据.");
        Language.foundNewVersion = config.getString("message.foundNewVersion", "§c发现新版本可用! §b当前版本: {current} §d最新版本: {latest}");
        Language.downloadLink = config.getString("message.downloadLink", "§a下载地址: ");
        Language.majorUpdate = config.getString("message.majorUpdate", "§e(有大更新)");
        Language.checkUpdateFailed = config.getString("message.checkUpdateFailed", "§e检查更新失败, 状态码: ");
        Language.checkUpdateException = config.getString("message.checkUpdateException", "§4检查更新时发生IO异常.");
    }

    public static YamlConfiguration loadConfiguration(File file) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append('\n');
                }
            } finally {
                reader.close();
            }
            config.loadFromString(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return config;
    }

    public static void saveConfiguration(YamlConfiguration config, File file) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(config.saveToString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package cn.suml.iloreedit.config;

import org.bukkit.configuration.file.YamlConfiguration;
import cn.suml.iloreedit.ILoreEdit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Language {
    public static ILoreEdit plugin = ILoreEdit.plugin;
    public static int version;
    public static String logPrefix;
    public static String messagePrefix;
    public static String languageVersionError;
    public static String languageVersionOutdated;
    public static String languageUpdateComplete;
    public static String configVersionError;
    public static String configVersionOutdated;
    public static String configUpdateComplete;
    public static String updateFoundNewVersion;
    public static String updateMajorUpdate;
    public static String updateDownloadLink;
    public static String updateCheckFailure;
    public static String updateCheckException;
    public static String enableMessage;
    public static String disableMessage;
    public static String commandIloreeditUsage;
    public static String commandIloreeditHelp;
    public static String commandIloreeditReload;
    public static String commandIloreeditVersion;
    public static String commandIloreeditUnknown;
    public static String commandEditloreUsage;
    public static String commandEditloreNotItem;
    public static String commandEditloreArgsError;
    public static String commandEditloreUnknown;
    public static String commandEditloreNameUsage;
    public static String commandEditloreName;
    public static String commandEditloreAddUsage;
    public static String commandEditloreAdd;
    public static String commandEditloreInvalidLine;
    public static String commandEditloreErrorLine;
    public static String commandEditloreSetUsage;
    public static String commandEditloreSetNotLore;
    public static String commandEditloreSet;
    public static String commandEditloreInsUsage;
    public static String commandEditloreInsNotLore;
    public static String commandEditloreIns;
    public static String commandEditloreDelUsage;
    public static String commandEditloreDelNotLore;
    public static String commandEditloreDel;
    public static String commandEditloreClearUsage;
    public static String commandEditloreClearNameNone;
    public static String commandEditloreClearName;
    public static String commandEditloreClearLoreNone;
    public static String commandEditloreClearLore;
    public static String commandEditloreClearModelNone;
    public static String commandEditloreClearModel;
    public static String commandEditloreTemplateDontExist;
    public static String commandEditloreTemplateInvalidName;
    public static String commandEditloreImportUsage;
    public static String commandEditloreImport;
    public static String commandEditloreExportUsage;
    public static String commandEditloreExportNone;
    public static String commandEditloreExport;
    public static String commandEditloreOwnerUsage;
    public static String commandEditloreOwnerNotSkull;
    public static String commandEditloreOwner;
    public static String commandEditloreModelUsage;
    public static String commandEditloreModelNotSupport;
    public static String commandEditloreModelInvalidData;
    public static String commandEditloreModel;
    public static String commandEditloreSaveError;
    public static String commandEditloreConsoleUsage;
    public static String commandEditloreConsoleNotFoundPlayer;

    public static void loadLanguage(String language) {
        if (language == null || !language.matches("[a-zA-Z]{2}[_-][a-zA-Z]{2}")) {
            plugin.getLogger().severe("§4语言文件名称格式错误: " + language);
            language = "zh_cn";
        }
        String langPath = "lang/" + language + ".yml";
        File lang = new File(plugin.getDataFolder(), langPath);
        saveDefaultLanguage(lang, langPath);
        YamlConfiguration config = Config.loadConfiguration(lang);
        version = config.getInt("version");
        logPrefix = config.getString("log-prefix", "§8[§3ILoreEdit§8] ");
        messagePrefix = config.getString("message-prefix", "§8[§3ILoreEdit§8] §c-> ");
        languageVersionError = config.getString("language-version-error", "§c语言文件版本错误: ");
        languageVersionOutdated = config.getString("language-version-outdated", "§e当前语言文件版本：§a{0} §c最新版本：§b{1} §6需要更新.");
        languageUpdateComplete = config.getString("language-update-complete", "§a语言文件更新完成!");
        configVersionError = config.getString("config-version-error", "§c配置文件版本错误: ");
        configVersionOutdated = config.getString("config-version-outdated", "§e当前配置文件版本：§a{0} §c最新版本：§b{1} §6需要更新.");
        configUpdateComplete = config.getString("config-update-complete", "§a配置文件更新完成!");
        languageUpdate(config, lang);
        updateFoundNewVersion = config.getString("update-found-new-version", "§c发现新版本可用! §b当前版本: {0} §d最新版本: {1}");
        updateMajorUpdate = config.getString("update-major-update", "§e(有大更新)");
        updateDownloadLink = config.getString("update-download-link", "§a下载地址: ");
        updateCheckFailure = config.getString("update-check-failure", "§e检查更新失败, 状态码: ");
        updateCheckException = config.getString("update-check-exception", "§4检查更新时发生IO异常.");
        enableMessage = config.getString("enable-message", "§b已启用.");
        disableMessage = config.getString("disable-message", "§7已卸载.");
        commandIloreeditUsage = config.getString("command-iloreedit-usage", "§6用法: §e/ILoreEdit §3<§ahelp§c|§areload§c|§aversion§3>");
        StringBuilder helpMsg = new StringBuilder();
        for (String line : config.getStringList("command-iloreedit-help")) {
            if (helpMsg.length() != 0) {
                helpMsg.append('\n');
            }
            helpMsg.append(line);
        }
        if (helpMsg.length() == 0) {
            helpMsg.append("§e===========§bILoreEdit§e===========\n")
                    .append("§a/lore name <名字> §7---- §b设置物品显示名\n")
                    .append("§a/lore add <内容> §7---- §b添加一行Lore\n")
                    .append("§a/lore set <行号> <内容> §7---- §b修改指定行的Lore\n")
                    .append("§a/lore ins <行号> <内容> §7---- §b在指定行插入Lore\n")
                    .append("§a/lore del [行号] §7---- §b删除指定行的Lore, 不指定行号则删除末行\n")
                    .append("§a/lore clear name §7---- §b清除物品名字\n")
                    .append("§a/lore clear lore §7---- §b清除所有Lore\n")
                    .append("§a/lore clear model §7---- §b清除自定义模型数据\n")
                    .append("§a/lore import <模板名> §7---- §b导入指定的模板\n")
                    .append("§a/lore export <模板名> §7---- §b导出到指定的模板\n")
                    .append("§a/lore owner <玩家名> §7---- §b修改头颅主人\n")
                    .append("§a/lore model <模型数据> §7---- §b设置自定义模型数据\n")
                    .append("§e/ILoreEdit help §7---- §a查看指令帮助\n")
                    .append("§e/ILoreEdit reload §7---- §a重载插件配置\n")
                    .append("§e/ILoreEdit version §7---- §a查看插件版本");
        }
        commandIloreeditHelp = helpMsg.toString();
        commandIloreeditReload = config.getString("command-iloreedit-reload", "§a配置文件重载完成.");
        commandIloreeditVersion = config.getString("command-iloreedit-version", "§b当前版本§e: §a{0}");
        commandIloreeditUnknown = config.getString("command-iloreedit-unknown", "§6未知的子命令");
        commandEditloreUsage = config.getString("command-editlore-usage", "§6用法: §e/lore §3<§aname§7|§aadd§7|§aset§7|§ains§7|§adel§7|§aclear§7|§aimport§7|§aexport§7|§aowner§7|§amodel§3> §7- 使用/ile help查看详细帮助");
        commandEditloreNotItem = config.getString("command-editlore-not-item", "§d你确定你手里有物品???");
        commandEditloreArgsError = config.getString("command-editlore-args-error", "§c错误的命令参数!");
        commandEditloreUnknown = config.getString("command-editlore-unknown", "§6未知的子命令");
        commandEditloreNameUsage = config.getString("command-editlore-name-usage", "§6用法: §a/lore name <名字> §7---- §b设置物品名字");
        commandEditloreName = config.getString("command-editlore-name", "§a已修改物品显示名.");
        commandEditloreAddUsage = config.getString("command-editlore-add-usage", "§6用法: §a/lore add <内容> §7---- §b添加一行Lore");
        commandEditloreAdd = config.getString("command-editlore-add", "§a已添加Lore.");
        commandEditloreInvalidLine = config.getString("command-editlore-invalid-line", "§c你输入的行号不是一个有效的数字.");
        commandEditloreErrorLine = config.getString("command-editlore-error-line", "§b指定的行号 §7{0} §b不存在!");
        commandEditloreSetUsage = config.getString("command-editlore-set-usage", "§6用法: §a/lore set <行号> <内容> §7---- §b修改指定行的Lore");
        commandEditloreSetNotLore = config.getString("command-editlore-set-not-lore", "§b该物品没有Lore, 请添加后再修改!");
        commandEditloreSet = config.getString("command-editlore-set", "§a已修改Lore.");
        commandEditloreInsUsage = config.getString("command-editlore-ins-usage", "§6用法: §a/lore ins <行号> <内容> §7---- §b在指定行插入Lore");
        commandEditloreInsNotLore = config.getString("command-editlore-ins-not-lore", "§b该物品没有Lore, 无法插入!");
        commandEditloreIns = config.getString("command-editlore-ins", "§a已插入Lore.");
        commandEditloreDelUsage = config.getString("command-editlore-del-usage", "§6用法: §a/lore del [行号] §7---- §b删除指定行的Lore, 不指定行号则删除末行");
        commandEditloreDelNotLore = config.getString("command-editlore-del-not-lore", "§b该物品没有Lore, 你要删除啥?");
        commandEditloreDel = config.getString("command-editlore-del", "§a已删除Lore.");
        commandEditloreClearUsage = config.getString("command-editlore-clear-usage", "§6用法: §a/lore clear <name/lore/model> §7---- §b清除物品显示名/Lore/自定义模型数据");
        commandEditloreClearNameNone = config.getString("command-editlore-clear-name-none", "§b该物品没有显示名.");
        commandEditloreClearName = config.getString("command-editlore-clear-name", "§a已清除物品显示名.");
        commandEditloreClearLoreNone = config.getString("command-editlore-clear-lore-none", "§b该物品没有Lore.");
        commandEditloreClearLore = config.getString("command-editlore-clear-lore", "§a已清除Lore.");
        commandEditloreClearModelNone = config.getString("command-editlore-clear-model-none", "§b该物品没有自定义模型数据.");
        commandEditloreClearModel = config.getString("command-editlore-clear-model", "§a已清除自定义模型数据.");
        commandEditloreTemplateDontExist = config.getString("command-editlore-template-dont-exist", "§c指定的模板不存在!");
        commandEditloreTemplateInvalidName = config.getString("command-editlore-template-invalid-name", "§c无效的模板名称!");
        commandEditloreImportUsage = config.getString("command-editlore-import-usage", "§6用法: §a/lore import <模板名> §7---- §b导入指定的模板");
        commandEditloreImport = config.getString("command-editlore-import", "§a已导入模板.");
        commandEditloreExportUsage = config.getString("command-editlore-export-usage", "§6用法: §a/lore export <模板名> §7---- §b导出到指定的模板");
        commandEditloreExportNone = config.getString("command-editlore-export-none", "§e你手中的物品没什么可导出的.");
        commandEditloreExport = config.getString("command-editlore-export", "§a已导出至模板.");
        commandEditloreOwnerUsage = config.getString("command-editlore-owner-usage", "§6用法: §a/lore owner <玩家名> §7---- §b修改头颅主人");
        commandEditloreOwnerNotSkull = config.getString("command-editlore-owner-not-skull", "§d你确定你手里拿的是头颅???");
        commandEditloreOwner = config.getString("command-editlore-owner", "§a已修改头颅主人.");
        commandEditloreModelUsage = config.getString("command-editlore-model-usage", "§6用法: §a/lore model <模型数据> §7---- §b设置自定义模型数据");
        commandEditloreModelNotSupport = config.getString("command-editlore-model-not-support", "§c服务器版本过低, 不支持设置自定义模型数据.");
        commandEditloreModelInvalidData = config.getString("command-editlore-model-invalid-data", "§4你输入的模型数据不是一个有效的数字.");
        commandEditloreModel = config.getString("command-editlore-model", "§a已设置自定义模型数据.");
        commandEditloreSaveError = config.getString("command-editlore-save-error", "§c保存修改失败, 请重试!");
        commandEditloreConsoleUsage = config.getString("command-editlore-console-usage", "§a控制台使用此命令需要在子命令前面指定玩家名称! §6示例格式: /lore <玩家名> name <名字>");
        commandEditloreConsoleNotFoundPlayer = config.getString("command-editlore-console-not-found-player", "§c指定的玩家不在线或不存在!");
    }

    private static void saveDefaultLanguage(File lang, String langPath) {
        if (!lang.exists()) {
            if (plugin.classLoader().getResource(langPath) == null) {
                InputStream in = plugin.getResource("lang/zh_cn.yml");
                if (in != null) {
                    try {
                        OutputStream out = new FileOutputStream(lang);
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) != -1) {
                            out.write(buf, 0, len);
                        }
                        out.close();
                        in.close();
                        plugin.logMessage("§a语言文件: " + lang.getName() + " 不存在, 已自动创建。");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    plugin.logMessage("§4语言文件: " + lang.getName() + " 不存在, 并且在插件内找不到默认语言文件: zh_cn.yml");
                }
            } else {
                plugin.saveResource(langPath, true);
            }
        }
    }

    private static void languageUpdate(YamlConfiguration config, File lang) {
        int latestVersion = 1;
        if (version < latestVersion) {
            plugin.logMessage(replaceArgs(languageVersionOutdated, version, latestVersion));
            //目前最新版本是1 所以暂时不写升级代码
            if (version < 1) {
                plugin.logMessage(languageVersionError + version);
                return;
            }
            plugin.logMessage(languageUpdateComplete);
            version = latestVersion;
            config.set("version", latestVersion);
            Config.saveConfiguration(config, lang);
        }
    }

    public static String replaceArgs(String msg, Object... args) {
        for (int i = 0; i < args.length; i++) {
            msg = msg.replace("{0}".replace('0', (char) (i + 48)), args[i].toString());
        }
        return msg;
    }

}

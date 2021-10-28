package xyz.myunco.iloreedit.config;

import org.bukkit.configuration.file.FileConfiguration;
import xyz.myunco.iloreedit.ILoreEdit;

public class ConfigLoader {

    public static void load() {
        ILoreEdit.plugin.saveDefaultConfig();
        ILoreEdit.plugin.reloadConfig();
        FileConfiguration config = ILoreEdit.plugin.getConfig();
        Language.enable = config.getString("message.enable");
        Language.disable = config.getString("message.disable");
        Language.prefix = config.getString("message.prefix");
        final StringBuilder helpMsg = new StringBuilder();
        config.getStringList("message.helpMsg").forEach(value -> helpMsg.append(value).append("\n"));
        Language.helpMsg = helpMsg.toString();
        Language.canOnlyPlayer = config.getString("message.canOnlyPlayer");
        Language.argsError = config.getString("message.argsError");
        Language.reloaded = config.getString("message.reloaded");
        Language.noItem = config.getString("message.noItem");
        Language.editDisplayName = config.getString("message.editDisplayName");
        Language.addLore = config.getString("message.addLore");
        Language.noLore = config.getString("message.noLore");
        Language.invalidLine = config.getString("message.invalidLine");
        Language.errorLine = config.getString("message.errorLine");
        Language.zeroLine = config.getString("message.zeroLine");
        Language.setLore = config.getString("message.setLore");
        Language.noLore_ins = config.getString("message.noLore-ins");
        Language.insLore = config.getString("message.insLore");
        Language.noLore_del = config.getString("message.noLore-del");
        Language.delLore = config.getString("message.delLore");
        Language.clearDisplayName = config.getString("message.clearDisplayName");
        Language.clearLore = config.getString("message.clearLore");
        Language.saveError = config.getString("message.saveError");
        Language.usage = config.getString("message.usage");
        Language.usageEditLore = config.getString("message.usageEditLore");
        Language.usageILoreEdit = config.getString("message.usageILoreEdit");
        Language.templateNotExist = config.getString("message.templateNotExist");
        Language.templateImported = config.getString("message.templateImported");
        Language.noExport = config.getString("message.noExport");
        Language.templateExported = config.getString("message.templateExported");
        Language.noSkull = config.getString("message.noSkull");
        Language.changedOwner = config.getString("message.changedOwner");
    }
}

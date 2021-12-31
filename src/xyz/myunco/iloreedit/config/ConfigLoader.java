package xyz.myunco.iloreedit.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.myunco.iloreedit.ILoreEdit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class ConfigLoader {

    public static void load() {
        ILoreEdit.plugin.saveDefaultConfig();
        FileConfiguration config = loadConfiguration(new File(ILoreEdit.plugin.getDataFolder(), "config.yml"));
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
        Language.invalidTemplateName = config.getString("message.invalidTemplateName");
        Language.notSupport = config.getString("message.notSupport");
        Language.invalidData = config.getString("message.invalidData");
        Language.setModelData = config.getString("message.setModelData");
        Language.clearModelData = config.getString("message.clearModelData");
    }

    public static YamlConfiguration loadConfiguration(File file) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while((line = reader.readLine()) != null) {
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

    public static void saveConfiguration(FileConfiguration config, File file) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(config.saveToString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

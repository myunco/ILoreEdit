package cn.suml.iloreedit.config;

import org.bukkit.configuration.file.YamlConfiguration;
import cn.suml.iloreedit.ILoreEdit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class Config {
    public static ILoreEdit plugin = ILoreEdit.plugin;
    public static int version;
    public static String language;
    public static boolean checkUpdate;

    public static void loadConfig() {
        plugin.saveDefaultConfig();
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        YamlConfiguration config = loadConfiguration(configFile);
        version = config.getInt("version");
        language = config.getString("language", "zh_cn");
        Language.loadLanguage(language);
        configUpdate(config, configFile);
        checkUpdate = config.getBoolean("checkUpdate", true);
    }

    private static void configUpdate(YamlConfiguration config, File configFile) {
        int latestVersion = 1;
        if (version < latestVersion) {
            plugin.logMessage(Language.replaceArgs(Language.configVersionOutdated, version, latestVersion));
            switch (version) {
                case 0:
                    config.set("language", "zh_cn");
                    break;
                default:
                    plugin.logMessage(Language.configVersionError + version);
                    return;
            }
            plugin.logMessage(Language.configUpdateComplete);
            version = latestVersion;
            config.set("version", latestVersion);
            saveConfiguration(config, configFile);
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

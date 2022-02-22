package xyz.myunco.iloreedit;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.myunco.iloreedit.command.ChatPacketListener;
import xyz.myunco.iloreedit.command.EditLoreCommand;
import xyz.myunco.iloreedit.command.ILoreEditCommand;
import xyz.myunco.iloreedit.config.Config;
import xyz.myunco.iloreedit.config.Language;
import xyz.myunco.iloreedit.metrics.Metrics;
import xyz.myunco.iloreedit.update.UpdateChecker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ILoreEdit extends JavaPlugin {
    public ProtocolManager manager;
    public static ILoreEdit plugin;
    public static int mcVersion;

    @Override
    public void onEnable() {
        plugin = this;
        mcVersion = Integer.parseInt(getServer().getBukkitVersion().replace('-', '.').split("\\.")[1]);
        if (!getServer().getPluginManager().isPluginEnabled("ProtocolLib") && mcVersion < 16) {
            getLogger().info("未找到ProtocolLib插件, 将不支持直接连续空格。");
        } else {
            manager = ProtocolLibrary.getProtocolManager();
        }
        initConfig();
        initCommand();
        new Metrics(this, 12935);
        logMessage(Language.enableMessage);
    }

    public void initConfig() {
        Config.loadConfig();
        if (Config.checkUpdate) {
            UpdateChecker.start();
        }
        if (!new File(plugin.getDataFolder(), "templates.yml").exists()) {
            plugin.saveResource("templates.yml", false);
        }
    }

    private void initCommand() {
        PluginCommand iLoreEdit = getCommand("ILoreEdit");
        if (iLoreEdit != null) {
            iLoreEdit.setExecutor(new ILoreEditCommand());
            iLoreEdit.setTabCompleter((TabCompleter) iLoreEdit.getExecutor());
        }
        PluginCommand editLore = getCommand("EditLore");
        if (editLore != null) {
            editLore.setExecutor(new EditLoreCommand());
            editLore.setTabCompleter((TabCompleter) editLore.getExecutor());
            if (manager != null) {
                List<String> commands = new ArrayList<>(editLore.getAliases());
                commands.add("editlore");
                new ChatPacketListener(commands, this);
            }
        }
    }

    @Override
    public void onDisable() {
        if (manager != null) {
            manager.removePacketListeners(this);
        }
        UpdateChecker.stop();
        logMessage(Language.disableMessage);
    }

    public ClassLoader classLoader() {
        return getClassLoader();
    }

    public void logMessage(String msg) {
        getServer().getConsoleSender().sendMessage(Language.logPrefix + msg);
    }

}

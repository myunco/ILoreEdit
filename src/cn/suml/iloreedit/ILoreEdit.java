package cn.suml.iloreedit;

import cn.suml.iloreedit.command.ChatPacketListener;
import cn.suml.iloreedit.command.ILoreEditCommand;
import cn.suml.iloreedit.config.Config;
import cn.suml.iloreedit.config.Language;
import cn.suml.iloreedit.update.UpdateChecker;
import cn.suml.iloreedit.update.UpdateNotification;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import cn.suml.iloreedit.command.EditLoreCommand;
import cn.suml.iloreedit.metrics.Metrics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ILoreEdit extends JavaPlugin {
    public ProtocolManager manager;
    public static ILoreEdit plugin;
    public static int mcVersion;
    public boolean enablePAPI;

    @Override
    public void onEnable() {
        plugin = this;
        mcVersion = Integer.parseInt(getServer().getBukkitVersion().replace('-', '.').split("\\.")[1]);
        if (!getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            if (mcVersion < 16) { //新版本就不提示了 因为新版服务端已经不能通过ProtocolLib获取连续空格了
                getLogger().info("未找到ProtocolLib插件, 将不支持直接连续空格。");
            }
        } else {
            manager = ProtocolLibrary.getProtocolManager();
        }
        initConfig();
        initCommand();
        if (Config.checkUpdate) {
            getServer().getPluginManager().registerEvents(new UpdateNotification(), this);
        }
        new Metrics(this, 12935);
        logMessage(Language.enableMessage);
    }

    public void initConfig() {
        Config.loadConfig();
        if (Config.checkUpdate) {
            UpdateChecker.start();
        }
        if (!enablePAPI) {
            Plugin papi = getServer().getPluginManager().getPlugin("PlaceholderAPI");
            enablePAPI = papi != null && papi.isEnabled();
            if (enablePAPI) {
                logMessage("Found PlaceholderAPI: §3v" + papi.getDescription().getVersion());
            }
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

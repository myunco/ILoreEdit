package cn.suml.iloreedit;

import cn.suml.iloreedit.command.ChatPacketListener;
import cn.suml.iloreedit.command.ILoreEditCommand;
import cn.suml.iloreedit.config.Config;
import cn.suml.iloreedit.config.Language;
import cn.suml.iloreedit.update.UpdateChecker;
import cn.suml.iloreedit.update.UpdateNotification;
import cn.suml.iloreedit.util.Version;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.myunco.folia.FoliaCompatibleAPI;
import net.myunco.folia.task.CompatibleScheduler;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import cn.suml.iloreedit.command.EditLoreCommand;
import cn.suml.iloreedit.metrics.Metrics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ILoreEdit extends JavaPlugin {
    public ProtocolManager manager;
    public static ILoreEdit plugin;
    public Version mcVersion;
    public boolean enablePAPI;
    private FoliaCompatibleAPI foliaCompatibleAPI;
    private CompatibleScheduler scheduler;

    @Override
    public void onEnable() {
        plugin = this;
        mcVersion = new Version(getServer().getBukkitVersion());
        if (!getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            if (mcVersion.isLessThan(16)) { //新版本就不提示了 因为新版服务端已经不能通过ProtocolLib获取连续空格了
                getLogger().info("未找到ProtocolLib插件, 将不支持直接连续空格。");
            }
        } else {
            manager = ProtocolLibrary.getProtocolManager();
        }
        initFoliaCompatibleAPI();
        scheduler = foliaCompatibleAPI.getScheduler(this);
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

    public void initFoliaCompatibleAPI() {
        Plugin api = getServer().getPluginManager().getPlugin("FoliaCompatibleAPI");
        if (api == null) {
            getLogger().warning("FoliaCompatibleAPI not found!");
            File file = new File(getDataFolder().getParentFile(), "FoliaCompatibleAPI-1.1.0.jar");
            InputStream in = getResource("lib/FoliaCompatibleAPI-1.1.0.jar");
            try {
                saveResource(file, in);
                api = getServer().getPluginManager().loadPlugin(file);
                if (api == null) {
                    throw new Exception("FoliaCompatibleAPI load failed!");
                }
                getServer().getPluginManager().enablePlugin(api);
                api.onLoad();
            } catch (Exception e) {
                e.printStackTrace();
                getLogger().severe("未安装 FoliaCompatibleAPI ，本插件无法运行！");
                return;
            }
        }
        foliaCompatibleAPI = (FoliaCompatibleAPI) api;
        getServer().getConsoleSender().sendMessage("[ILoreEdit] Found FoliaCompatibleAPI: §3v" + api.getDescription().getVersion());
    }

    private void saveResource(File target, InputStream source) throws Exception {
        if (source != null) {
            //noinspection IOStreamConstructor
            OutputStream out = new FileOutputStream(target);
            byte[] buf = new byte[8192];
            int len;
            while ((len = source.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            source.close();
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

    public CompatibleScheduler getScheduler() {
        return scheduler;
    }

    public void logMessage(String msg) {
        getServer().getConsoleSender().sendMessage(Language.logPrefix + msg);
    }

}

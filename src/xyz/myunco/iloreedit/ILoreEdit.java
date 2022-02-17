package xyz.myunco.iloreedit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.myunco.iloreedit.command.EditLoreCommand;
import xyz.myunco.iloreedit.command.ILoreEditCommand;
import xyz.myunco.iloreedit.config.Config;
import xyz.myunco.iloreedit.config.ConfigLoader;
import xyz.myunco.iloreedit.config.Language;
import xyz.myunco.iloreedit.config.TemplateInfo;
import xyz.myunco.iloreedit.metrics.Metrics;
import xyz.myunco.iloreedit.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ILoreEdit extends JavaPlugin {
    public ProtocolManager manager;
    public static ILoreEdit plugin;
    public static int mcVersion;
    public static String version;
    private Timer timer;

    @Override
    public void onEnable() {
        init();
        if (!getServer().getPluginManager().isPluginEnabled("ProtocolLib") && mcVersion < 16) {
            getLogger().info("未找到ProtocolLib插件, 将不支持直接连续空格。");
        } else {
            manager = ProtocolLibrary.getProtocolManager();
        }
        PluginCommand iLoreEdit = getCommand("ILoreEdit");
        if (iLoreEdit != null) {
            iLoreEdit.setExecutor(new ILoreEditCommand());
            iLoreEdit.setTabCompleter((TabCompleter) iLoreEdit.getExecutor());
        }
        PluginCommand editLore = getCommand("EditLore");
        if (editLore != null) {
            List<String> commands = new ArrayList<>(editLore.getAliases());
            commands.add("editlore");
            editLore.setExecutor(new EditLoreCommand(commands));
            editLore.setTabCompleter((TabCompleter) editLore.getExecutor());
        }
        checkUpdate();
        new Metrics(this, 12935);
        Bukkit.getConsoleSender().sendMessage("§8[§3ILoreEdit§8] " + Language.enable);
    }

    private void init() {
        plugin = this;
        //TODO
        version = getDescription().getVersion();
        ConfigLoader.load();
        if (!new File(plugin.getDataFolder(), "templates.yml").exists()) {
            plugin.saveResource("templates.yml", false);
        }
        mcVersion = Integer.parseInt(Bukkit.getBukkitVersion().replace('-', '.').split("\\.")[1]);
    }

    @Override
    public void onDisable() {
        if (manager != null) {
            manager.removePacketListeners(this);
        }
        stopCheckUpdate();
        Bukkit.getConsoleSender().sendMessage("§8[§3ILoreEdit§8] " + Language.disable);
    }

    public void checkUpdate() {
        if (Config.checkUpdate) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Utils.checkVersionUpdate(getServer().getConsoleSender());
                }
            }, 12000, 12 * 60 * 60 * 1000);
        }
    }

    public void stopCheckUpdate() {
        if (Config.checkUpdate) {
            timer.cancel();
        }
    }

    @SuppressWarnings({"deprecation"})
    public void commandEditLore(Player player, String arg, String[] args) {
        if (args[0].isEmpty()) {
            sendMessage(player, Language.usage);
            sendMessage(player, Language.usageEditLore);
            return;
        }
        ItemStack item;
        if (mcVersion > 8) {
            item = player.getInventory().getItemInMainHand();
        } else {
            item = player.getItemInHand();
        }
        ItemMeta meta = item.getItemMeta();
        if (item.getType() == Material.AIR || meta == null) {
            sendMessage(player, Language.noItem);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "name":
                //ll name test
                if (args.length < 2) {
                    sendMessage(player, Language.argsError);
                    return;
                }
                meta.setDisplayName(Utils.translateColor(Utils.getTextRight(arg, args[0] + " ")));
                sendMessage(player, Language.editDisplayName);
                break;
            case "add": {
                List<String> lore = Utils.getLore(meta);
                //ll add test
                if (args.length < 2) {
                    sendMessage(player, Language.argsError);
                    return;
                }
                lore.add(Utils.translateColor(Utils.getTextRight(arg, args[0] + " ")));
                meta.setLore(lore);
                sendMessage(player, Language.addLore);
                break;
            }
            case "set": {
                List<String> lore = Utils.getLore(meta);
                //ll set 1 test
                if (args.length < 3) {
                    sendMessage(player, Language.argsError);
                    return;
                } else if (lore.size() == 0) {
                    sendMessage(player, Language.noLore);
                    return;
                }
                int line = Utils.getLine(player, args[1], lore.size());
                if (line == 0) {
                    return;
                }
                lore.set(line - 1, Utils.translateColor(Utils.getTextRight(arg, args[1] + " ")));
                meta.setLore(lore);
                sendMessage(player, Language.setLore);
                break;
            }
            case "ins": {
                List<String> lore = Utils.getLore(meta);
                //ll ins 1 test
                if (args.length < 3) {
                    sendMessage(player, Language.argsError);
                    return;
                }
                if (lore.size() == 0) {
                    sendMessage(player, Language.noLore_ins);
                    return;
                }
                int line = Utils.getLine(player, args[1], lore.size());
                if (line == 0) {
                    return;
                }
                lore.add(line - 1, Utils.translateColor(Utils.getTextRight(arg, args[1] + " ")));
                meta.setLore(lore);
                sendMessage(player, Language.insLore);
                break;
            }
            case "del": {
                //ll del
                //ll del 1
                List<String> lore = Utils.getLore(meta);
                if (args.length > 2) {
                    sendMessage(player, Language.argsError);
                    return;
                }
                if (lore.size() == 0) {
                    sendMessage(player, Language.noLore_del);
                    return;
                }
                int line;
                if (args.length == 1) {
                    line = lore.size();
                } else {
                    line = Utils.getLine(player, args[1], lore.size());
                    if (line == 0) {
                        return;
                    }
                }
                lore.remove(line - 1);
                meta.setLore(lore);
                sendMessage(player, Language.delLore);
                break;
            }
            case "clear":
                //ll clear name
                //ll clear lore
                if (args.length != 2) {
                    sendMessage(player, Language.argsError);
                    return;
                }
                switch (args[1].toLowerCase()) {
                    case "name":
                        meta.setDisplayName(null);
                        sendMessage(player, Language.clearDisplayName);
                        break;
                    case "lore":
                        meta.setLore(null);
                        sendMessage(player, Language.clearLore);
                        break;
                    case "model":
                        if (mcVersion < 14) {
                            sendMessage(player, Language.notSupport);
                            return;
                        }
                        meta.setCustomModelData(null);
                        sendMessage(player, Language.clearModelData);
                        break;
                    default:
                        sendMessage(player, Language.argsError);
                        return;
                }
                break;
            case "import": {
                //ll import test
                if (args.length != 2) {
                    sendMessage(player, Language.argsError);
                    return;
                }
                TemplateInfo template = new TemplateInfo(this); //模板需要实时更新 所以每次都重新加载
                if (template.exists(args[1])) {
                    meta.setDisplayName(template.getDisplayName(args[1]));
                    meta.setLore(template.getLore(args[1]));
                    if (mcVersion >= 14 && template.hasCustomModelData(args[1])) {
                        meta.setCustomModelData(template.getCustomModelData(args[1]));
                    }
                    sendMessage(player, Language.templateImported);
                } else {
                    sendMessage(player, Language.templateNotExist);
                    return;
                }
                break;
            }
            case "export": {
                //ll export test
                if (args.length != 2) {
                    sendMessage(player, Language.argsError);
                    return;
                } else if (!meta.hasDisplayName() && !meta.hasLore()) {
                    sendMessage(player, Language.noExport);
                    return;
                } else if (args[1].indexOf('.') != -1) {
                    sendMessage(player, Language.invalidTemplateName);
                    return;
                }
                TemplateInfo template = new TemplateInfo(this);
                if (template.exists(args[1])) {
                    //如果存在就先删除模板，以免不能完全覆盖
                    template.delete(args[1]);
                }
                if (meta.hasDisplayName()) {
                    template.setDisplayName(args[1], meta.getDisplayName());
                }
                if (meta.hasLore()) {
                    template.setLore(args[1], meta.getLore());
                }
                if (mcVersion >= 14 && meta.hasCustomModelData()) {
                    template.setCustomModelData(args[1], meta.getCustomModelData());
                }
                template.save();
                sendMessage(player, Language.templateExported);
                break;
            }
            case "owner":
                //ll owner test
                if (args.length != 2) {
                    sendMessage(player, Language.argsError);
                    return;
                }
                if (meta instanceof SkullMeta) {
                    ((SkullMeta) meta).setOwner(args[1]);
                    sendMessage(player, Language.changedOwner);
                } else {
                    sendMessage(player, Language.noSkull);
                }
                break;
            case "model": {
                //ll model 1
                if (args.length != 2) {
                    sendMessage(player, Language.argsError);
                    return;
                } else if (mcVersion < 14) {
                    sendMessage(player, Language.notSupport);
                    return;
                }
                try {
                    int data = Integer.parseInt(args[1]);
                    meta.setCustomModelData(data);
                    sendMessage(player, Language.setModelData);
                } catch (NumberFormatException e) {
                    sendMessage(player, Language.invalidData);
                }
                break;
            }
            default:
                sendMessage(player, Language.argsError);
                return;
        }
        if (!item.setItemMeta(meta)) {
            sendMessage(player, Language.saveError);
        }
    }
    
    public static void sendMessage(Player player, String msg) {
        player.sendMessage(Language.prefix + msg);
    }
    
    public static void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(Language.prefix + msg);
    }
}

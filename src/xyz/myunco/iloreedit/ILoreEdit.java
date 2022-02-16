package xyz.myunco.iloreedit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.myunco.iloreedit.command.TabComplete;
import xyz.myunco.iloreedit.config.Config;
import xyz.myunco.iloreedit.config.ConfigLoader;
import xyz.myunco.iloreedit.config.Language;
import xyz.myunco.iloreedit.config.TemplateInfo;
import xyz.myunco.iloreedit.metrics.Metrics;
import xyz.myunco.iloreedit.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ILoreEdit extends JavaPlugin {
    private static ProtocolManager manager;
    public static ILoreEdit plugin;
    public static int mcVersion;
    private boolean enableProtocol;
    private Timer timer;
    public static String version;

    @Override
    public void onEnable() {
        init();
        enableProtocol = getServer().getPluginManager().isPluginEnabled("ProtocolLib");
        if (!enableProtocol && mcVersion < 16) {
            getLogger().info("未找到ProtocolLib插件, 将不支持直接连续空格。");
        }
        PluginCommand iLoreEdit = getCommand("ILoreEdit");
        if (iLoreEdit != null) {
            iLoreEdit.setTabCompleter(this);
        }
        PluginCommand editLore = getCommand("EditLore");
        if (enableProtocol && editLore != null) {
            editLore.setTabCompleter(this);
            final List<String> commands = new ArrayList<>(editLore.getAliases());
            commands.add("editlore");
            manager = ProtocolLibrary.getProtocolManager();
            manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.CHAT) {
                @Override
                public void onPacketReceiving(PacketEvent event) {
                    String msg = event.getPacket().getStrings().read(0);
                    if (msg.startsWith("/")) {
                        if (commands.contains(Util.getTextLeft(msg, " ").substring(1).toLowerCase())) {
                            Player player = event.getPlayer();
                            if (player.hasPermission("ILoreEdit.use")) {
                                commandEditLore(msg.replace("\"\"", " "), player);
                            }
                        }
                    }
                }
            });
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
        if (enableProtocol) {
            manager.removePacketListeners(this);
        }
        stopCheckUpdate();
        Bukkit.getConsoleSender().sendMessage("§8[§3ILoreEdit§8] " + Language.disable);
    }

    private void checkUpdate() {
        if (Config.checkUpdate) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Util.checkVersionUpdate(getServer().getConsoleSender());
                }
            }, 12000, 12 * 60 * 60 * 1000);
        }
    }

    private void stopCheckUpdate() {
        if (Config.checkUpdate) {
            timer.cancel();
        }
    }

    @SuppressWarnings({"NullableProblems"})
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName()) {
            case "EditLore":
                if (!(sender instanceof Player)) {
                    sendMessage(sender, Language.canOnlyPlayer);
                }
                break;
            case "ILoreEdit":
                if (args.length < 1) {
                    sendMessage(sender, Language.usage);
                    sendMessage(sender, Language.usageILoreEdit);
                    break;
                }
                switch (args[0].toLowerCase()) {
                    case "version":
                        sendMessage(sender, "§bVersion§e: §a" + getDescription().getVersion());
                        break;
                    case "reload":
                        stopCheckUpdate();
                        ConfigLoader.load();
                        checkUpdate();
                        sendMessage(sender, Language.reloaded);
                        break;
                    default:
                        sender.sendMessage(Language.helpMsg);
                }
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2 && args[0].equalsIgnoreCase("import")) {
            return TabComplete.getCompleteList(args, new TemplateInfo(this).getTemplateList(), true);
        }
        return TabComplete.getCompleteList(args, TabComplete.getTabPath(args, command.getName()));
    }

    @SuppressWarnings({"deprecation"})
    public void commandEditLore(String msg, Player p) {
        String arg = Util.getTextRight(msg, " ");
        String[] args = Util.getArgs(arg);
        if ("".equals(args[0])) {
            sendMessage(p, Language.usage);
            sendMessage(p, Language.usageEditLore);
            return;
        }
        ItemStack item;
        if (mcVersion > 8) {
            item = p.getInventory().getItemInMainHand();
        } else {
            item = p.getItemInHand(); //1.7-1.8没有getInventory().getItemInMainHand()
        }
        ItemMeta meta = item.getItemMeta();
        if (item.getType() == Material.AIR || meta == null) {
            sendMessage(p, Language.noItem);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "name":
                //ll name test
                if (args.length < 2) {
                    sendMessage(p, Language.argsError);
                    return;
                }
                meta.setDisplayName(Util.translateColor(Util.getTextRight(arg, args[0] + " ")));
                sendMessage(p, Language.editDisplayName);
                break;
            case "add": {
                List<String> lore = Util.getLore(meta);
                //ll add test
                if (args.length < 2) {
                    sendMessage(p, Language.argsError);
                    return;
                }
                lore.add(Util.translateColor(Util.getTextRight(arg, args[0] + " ")));
                meta.setLore(lore);
                sendMessage(p, Language.addLore);
                break;
            }
            case "set": {
                List<String> lore = Util.getLore(meta);
                //ll set 1 test
                if (args.length < 3) {
                    sendMessage(p, Language.argsError);
                    return;
                } else if (lore.size() == 0) {
                    sendMessage(p, Language.noLore);
                    return;
                }
                int line = Util.getLine(p, args[1], lore.size());
                if (line == 0) {
                    return;
                }
                lore.set(line - 1, Util.translateColor(Util.getTextRight(arg, args[1] + " ")));
                meta.setLore(lore);
                sendMessage(p, Language.setLore);
                break;
            }
            case "ins": {
                List<String> lore = Util.getLore(meta);
                //ll ins 1 test
                if (args.length < 3) {
                    sendMessage(p, Language.argsError);
                    return;
                }
                if (lore.size() == 0) {
                    sendMessage(p, Language.noLore_ins);
                    return;
                }
                int line = Util.getLine(p, args[1], lore.size());
                if (line == 0) {
                    return;
                }
                lore.add(line - 1, Util.translateColor(Util.getTextRight(arg, args[1] + " ")));
                meta.setLore(lore);
                sendMessage(p, Language.insLore);
                break;
            }
            case "del": {
                //ll del
                //ll del 1
                List<String> lore = Util.getLore(meta);
                if (args.length > 2) {
                    sendMessage(p, Language.argsError);
                    return;
                }
                if (lore.size() == 0) {
                    sendMessage(p, Language.noLore_del);
                    return;
                }
                int line;
                if (args.length == 1) {
                    line = lore.size();
                } else {
                    line = Util.getLine(p, args[1], lore.size());
                    if (line == 0) {
                        return;
                    }
                }
                lore.remove(line - 1);
                meta.setLore(lore);
                sendMessage(p, Language.delLore);
                break;
            }
            case "clear":
                //ll clear name
                //ll clear lore
                if (args.length != 2) {
                    sendMessage(p, Language.argsError);
                    return;
                }
                switch (args[1].toLowerCase()) {
                    case "name":
                        meta.setDisplayName(null);
                        sendMessage(p, Language.clearDisplayName);
                        break;
                    case "lore":
                        meta.setLore(null);
                        sendMessage(p, Language.clearLore);
                        break;
                    case "model":
                        if (mcVersion < 14) {
                            sendMessage(p, Language.notSupport);
                            return;
                        }
                        meta.setCustomModelData(null);
                        sendMessage(p, Language.clearModelData);
                        break;
                    default:
                        sendMessage(p, Language.argsError);
                        return;
                }
                break;
            case "import": {
                //ll import test
                if (args.length != 2) {
                    sendMessage(p, Language.argsError);
                    return;
                }
                TemplateInfo template = new TemplateInfo(this); //模板需要实时更新 所以每次都重新加载
                if (template.exists(args[1])) {
                    meta.setDisplayName(template.getDisplayName(args[1]));
                    meta.setLore(template.getLore(args[1]));
                    if (mcVersion >= 14 && template.hasCustomModelData(args[1])) {
                        meta.setCustomModelData(template.getCustomModelData(args[1]));
                    }
                    sendMessage(p, Language.templateImported);
                } else {
                    sendMessage(p, Language.templateNotExist);
                    return;
                }
                break;
            }
            case "export": {
                //ll export test
                if (args.length != 2) {
                    sendMessage(p, Language.argsError);
                    return;
                } else if (!meta.hasDisplayName() && !meta.hasLore()) {
                    sendMessage(p, Language.noExport);
                    return;
                } else if (args[1].indexOf('.') != -1) {
                    sendMessage(p, Language.invalidTemplateName);
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
                sendMessage(p, Language.templateExported);
                break;
            }
            case "owner":
                //ll owner test
                if (args.length != 2) {
                    sendMessage(p, Language.argsError);
                    return;
                }
                if (meta instanceof SkullMeta) {
                    ((SkullMeta) meta).setOwner(args[1]);
                    sendMessage(p, Language.changedOwner);
                } else {
                    sendMessage(p, Language.noSkull);
                }
                break;
            case "model": {
                //ll model 1
                if (args.length != 2) {
                    sendMessage(p, Language.argsError);
                    return;
                } else if (mcVersion < 14) {
                    sendMessage(p, Language.notSupport);
                    return;
                }
                try {
                    int data = Integer.parseInt(args[1]);
                    meta.setCustomModelData(data);
                    sendMessage(p, Language.setModelData);
                } catch (NumberFormatException e) {
                    sendMessage(p, Language.invalidData);
                }
                break;
            }
            default:
                sendMessage(p, Language.argsError);
                return;
        }
        if (!item.setItemMeta(meta)) {
            sendMessage(p, Language.saveError);
        }
    }
    
    public static void sendMessage(Player player, String msg) {
        player.sendMessage(Language.prefix + msg);
    }
    
    public static void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(Language.prefix + msg);
    }
}

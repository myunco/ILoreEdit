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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.myunco.iloreedit.config.ConfigLoader;
import xyz.myunco.iloreedit.config.Language;
import xyz.myunco.iloreedit.config.TemplateInfo;
import xyz.myunco.iloreedit.metrics.Metrics;
import xyz.myunco.iloreedit.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ILoreEdit extends JavaPlugin {
    static final String PREFIX = "§3[§9ILoreEdit§3] ";
    static String messagePrefix;
    static HashMap<String, List<String>> tabList = new HashMap<>();
    static ProtocolManager manager;
    public static ILoreEdit plugin;
    public static int mcVersion;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        init();
        getLogger().info("Minecraft version : 1." + mcVersion);
        final List<String> commands = new ArrayList<>(Bukkit.getPluginCommand("EditLore").getAliases());
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
        new Metrics(this, 12935);
        Bukkit.getConsoleSender().sendMessage(PREFIX + Language.enable);
    }

    private void init() {
        plugin = this;
        ConfigLoader.load();
        messagePrefix = Language.prefix;
        if (!new File(plugin.getDataFolder(), "templates.yml").exists()) {
            plugin.saveResource("templates.yml", false);
        }
        mcVersion = Integer.parseInt(Bukkit.getBukkitVersion().replace('-', '.').split("\\.")[1]);
    }

    @Override
    public void onDisable() {
        manager.removePacketListeners(this);
        Bukkit.getConsoleSender().sendMessage(PREFIX + Language.disable);
    }

    @SuppressWarnings({"NullableProblems"})
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName()) {
            case "EditLore":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(messagePrefix + Language.canOnlyPlayer);
                }
                break;
            case "ILoreEdit":
                if (args.length < 1) {
                    sender.sendMessage(messagePrefix + Language.usage);
                    sender.sendMessage(messagePrefix + Language.usageILoreEdit);
                    break;
                }
                switch (args[0].toLowerCase()) {
                    case "version":
                        sender.sendMessage(messagePrefix + "§bVersion§e: §a" + getDescription().getVersion());
                        break;
                    case "reload":
                        ConfigLoader.load();
                        sender.sendMessage(messagePrefix + Language.reloaded);
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
        if (tabList.isEmpty()) {
            tabList.put("ILoreEdit", Arrays.asList("help", "version", "reload"));
            tabList.put("EditLore", Arrays.asList("name", "add", "set", "ins", "del", "clear", "import", "export", "owner", "model"));
            tabList.put("EditLore.clear", Arrays.asList("name", "lore", "model"));
            tabList.put("EditLore.owner", Collections.emptyList());
            tabList.put("EditLore.export", Collections.emptyList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("import")) {
            return Util.getTabList(args, new TemplateInfo(this).getTemplateList(), true);
        }
        return Util.getTabList(args, tabList.get(Util.getTabPath(args, command.getName())));
    }

    @SuppressWarnings({"deprecation"})
    public void commandEditLore(String msg, Player p) {
        String arg = Util.getTextRight(msg, " ");
        String[] args = Util.getArgs(arg);
        if ("".equals(args[0])) {
            p.sendMessage(messagePrefix + Language.usage);
            p.sendMessage(messagePrefix + Language.usageEditLore);
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
            p.sendMessage(messagePrefix + Language.noItem);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "name":
                //ll name test
                if (args.length < 2) {
                    p.sendMessage(messagePrefix + Language.argsError);
                    return;
                }
                meta.setDisplayName(Util.translateColor(Util.getTextRight(arg, args[0] + " ")));
                p.sendMessage(messagePrefix + Language.editDisplayName);
                break;
            case "add": {
                List<String> lore = Util.getLore(meta);
                //ll add test
                if (args.length < 2) {
                    p.sendMessage(messagePrefix + Language.argsError);
                    return;
                }
                lore.add(Util.translateColor(Util.getTextRight(arg, args[0] + " ")));
                meta.setLore(lore);
                p.sendMessage(messagePrefix + Language.addLore);
                break;
            }
            case "set": {
                List<String> lore = Util.getLore(meta);
                //ll set 1 test
                if (args.length < 3) {
                    p.sendMessage(messagePrefix + Language.argsError);
                    return;
                } else if (lore.size() == 0) {
                    p.sendMessage(messagePrefix + Language.noLore);
                    return;
                }
                int line = Util.getLine(p, args[1], lore.size());
                if (line == 0) {
                    return;
                }
                lore.set(line - 1, Util.translateColor(Util.getTextRight(arg, args[1] + " ")));
                meta.setLore(lore);
                p.sendMessage(messagePrefix + Language.setLore);
                break;
            }
            case "ins": {
                List<String> lore = Util.getLore(meta);
                //ll ins 1 test
                if (args.length < 3) {
                    p.sendMessage(messagePrefix + Language.argsError);
                    return;
                }
                if (lore.size() == 0) {
                    p.sendMessage(messagePrefix + Language.noLore_ins);
                    return;
                }
                int line = Util.getLine(p, args[1], lore.size());
                if (line == 0) {
                    return;
                }
                lore.add(line - 1, Util.translateColor(Util.getTextRight(arg, args[1] + " ")));
                meta.setLore(lore);
                p.sendMessage(messagePrefix + Language.insLore);
                break;
            }
            case "del": {
                //ll del
                //ll del 1
                List<String> lore = Util.getLore(meta);
                if (args.length > 2) {
                    p.sendMessage(messagePrefix + Language.argsError);
                    return;
                }
                if (lore.size() == 0) {
                    p.sendMessage(messagePrefix + Language.noLore_del);
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
                p.sendMessage(messagePrefix + Language.delLore);
                break;
            }
            case "clear":
                //ll clear name
                //ll clear lore
                if (args.length != 2) {
                    p.sendMessage(messagePrefix + Language.argsError);
                    return;
                }
                switch (args[1].toLowerCase()) {
                    case "name":
                        meta.setDisplayName(null);
                        p.sendMessage(messagePrefix + Language.clearDisplayName);
                        break;
                    case "lore":
                        meta.setLore(null);
                        p.sendMessage(messagePrefix + Language.clearLore);
                        break;
                    case "model":
                        if (mcVersion < 14) {
                            p.sendMessage(messagePrefix + Language.notSupport);
                            return;
                        }
                        meta.setCustomModelData(null);
                        p.sendMessage(messagePrefix + Language.clearModelData);
                        break;
                    default:
                        p.sendMessage(messagePrefix + Language.argsError);
                        return;
                }
                break;
            case "import": {
                //ll import test
                if (args.length != 2) {
                    p.sendMessage(messagePrefix + Language.argsError);
                    return;
                }
                TemplateInfo template = new TemplateInfo(this); //模板需要实时更新 所以每次都重新加载
                if (template.exists(args[1])) {
                    meta.setDisplayName(template.getDisplayName(args[1]));
                    meta.setLore(template.getLore(args[1]));
                    if (mcVersion >= 14 && template.hasCustomModelData(args[1])) {
                        meta.setCustomModelData(template.getCustomModelData(args[1]));
                    }
                    p.sendMessage(messagePrefix + Language.templateImported);
                } else {
                    p.sendMessage(messagePrefix + Language.templateNotExist);
                    return;
                }
                break;
            }
            case "export": {
                //ll export test
                if (args.length != 2) {
                    p.sendMessage(messagePrefix + Language.argsError);
                    return;
                } else if (!meta.hasDisplayName() && !meta.hasLore()) {
                    p.sendMessage(messagePrefix + Language.noExport);
                    return;
                } else if (args[1].indexOf('.') != -1) {
                    p.sendMessage(messagePrefix + Language.invalidTemplateName);
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
                p.sendMessage(messagePrefix + Language.templateExported);
                break;
            }
            case "owner":
                //ll owner test
                if (args.length != 2) {
                    p.sendMessage(messagePrefix + Language.argsError);
                    return;
                }
                if (meta instanceof SkullMeta) {
                    ((SkullMeta) meta).setOwner(args[1]);
                    p.sendMessage(messagePrefix + Language.changedOwner);
                } else {
                    p.sendMessage(messagePrefix + Language.noSkull);
                }
                break;
            case "model": {
                //ll model 1
                if (args.length != 2) {
                    p.sendMessage(messagePrefix + Language.argsError);
                    return;
                } else if (mcVersion < 14) {
                    p.sendMessage(messagePrefix + Language.notSupport);
                    return;
                }
                try {
                    int data = Integer.parseInt(args[1]);
                    meta.setCustomModelData(data);
                    p.sendMessage(messagePrefix + Language.setModelData);
                } catch (NumberFormatException e) {
                    p.sendMessage(messagePrefix + Language.invalidData);
                }
                break;
            }
            default:
                p.sendMessage(messagePrefix + Language.argsError);
                return;
        }
        if (!item.setItemMeta(meta)) {
            p.sendMessage(messagePrefix + Language.saveError);
        }
    }
}

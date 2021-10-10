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
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ILoreEdit extends JavaPlugin {
    static final String PREFIX = "§3[§9ILoreEdit§3] ";
    static String MESSAGE_PREFIX;
    static HashMap<String, List<String>> tabList = new HashMap<>();
    static ProtocolManager manager;
    static ILoreEdit plugin;

    @SuppressWarnings({"ConstantConditions", "SpellCheckingInspection"})
    @Override
    public void onEnable() {
        plugin = this;
        ConfigLoader.load();
        MESSAGE_PREFIX = Language.MESSAGE_PREFIX;
        List<String> commands = new ArrayList<>(Bukkit.getPluginCommand("EditLore").getAliases());
        commands.add("editlore");
        manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.CHAT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Client.CHAT) {
                    String msg = event.getPacket().getStrings().read(0);
                    if (msg.startsWith("/")) {
                        if (commands.contains(Util.getTextLeft(msg, " ").toLowerCase().substring(1))) {
                            Player p = event.getPlayer();
                            if (p.hasPermission("ILoreEdit.use")) {
                                commandIItem(msg, p);
                            }
                        }
                    }
                }
            }
        });
        Bukkit.getConsoleSender().sendMessage(PREFIX + Language.enable);
    }

    @Override
    public void onDisable() {
        manager.removePacketListeners(this);
        Bukkit.getConsoleSender().sendMessage(PREFIX + Language.disable);
    }

    @SuppressWarnings({"SpellCheckingInspection", "NullableProblems"})
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName()) {
            case "EditLore":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Language.MESSAGE_PREFIX + Language.canOnlyPlayer);
                }
                break;
            case "ILoreEdit":
                if (args.length < 1) {
                    sender.sendMessage("§6用法:");
                    sender.sendMessage("§e/ILoreEdit §3<§ahelp§c|§aversion§c|§areload§3>");
                    break;
                }
                switch (args[0].toLowerCase()) {
                    case "version":
                        sender.sendMessage(Language.MESSAGE_PREFIX + "§bVersion§e: §a" + getDescription().getVersion());
                        break;
                    case "reload":
                        ConfigLoader.load();
                        sender.sendMessage(Language.MESSAGE_PREFIX + Language.reloaded);
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
            tabList.put("EditLore", Arrays.asList("name", "add", "set", "ins", "del", "clear"));
            tabList.put("EditLore.clear", Arrays.asList("name", "lore"));
        }
        return Util.getTabList(args, tabList.get(Util.getTabPath(args, command.getName())));
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void commandIItem(String msg, Player p) {
        String arg = Util.getTextRight(msg, " ");
        String[] args = Util.getArgs(arg);
        if ("".equals(args[0])) {
            p.sendMessage("§6用法:");
            p.sendMessage("§e/lore §3<§aname§7|§aadd§7|§aset§7|§ains§7|§adel§7|§aclear§3>§b [...]");
            return;
        }
        ItemStack item = p.getInventory().getItemInMainHand();
        //ItemStack item = p.getItemInHand(); 1.7-1.8用
        ItemMeta meta = item.getItemMeta();
        if (item.getType() == Material.AIR || meta == null) {
            p.sendMessage(MESSAGE_PREFIX + Language.noItem);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "name":
                //ll name test
                if (args.length < 2) {
                    p.sendMessage(Language.argsError);
                    return;
                }
                meta.setDisplayName(Util.translateColor(Util.getTextRight(arg, args[0] + " ")));
                p.sendMessage(MESSAGE_PREFIX + Language.editDisplayName);
                break;
            case "add": {
                List<String> lore = Util.getLore(meta);
                //ll add test
                if (args.length < 2) {
                    p.sendMessage(Language.argsError);
                    return;
                }
                lore.add(Util.translateColor(Util.getTextRight(arg, args[0] + " ")));
                meta.setLore(lore);
                p.sendMessage(MESSAGE_PREFIX + Language.addLore);
                break;
            }
            case "set": {
                List<String> lore = Util.getLore(meta);
                //ll set 1 test
                if (args.length < 3) {
                    p.sendMessage(Language.argsError);
                    return;
                } else if (lore.size() == 0) {
                    p.sendMessage(MESSAGE_PREFIX + Language.noLore);
                    return;
                }
                int line = Util.getLine(p, args[1], lore.size());
                if (line == 0) {
                    return;
                }
                lore.set(line - 1, Util.translateColor(Util.getTextRight(arg, args[1] + " ")));
                meta.setLore(lore);
                p.sendMessage(MESSAGE_PREFIX + Language.setLore);
                break;
            }
            case "ins": {
                List<String> lore = Util.getLore(meta);
                //ll ins 1 test
                if (args.length < 3) {
                    p.sendMessage(Language.argsError);
                    return;
                }
                if (lore.size() == 0) {
                    p.sendMessage(MESSAGE_PREFIX + Language.noLore_ins);
                    return;
                }
                int line = Util.getLine(p, args[1], lore.size());
                if (line == 0) {
                    return;
                }
                lore.add(line - 1, Util.translateColor(Util.getTextRight(arg, args[1] + " ")));
                meta.setLore(lore);
                p.sendMessage(MESSAGE_PREFIX + Language.insLore);
                break;
            }
            case "del": {
                //ll del
                //ll del 1
                List<String> lore = Util.getLore(meta);
                if (args.length > 2) {
                    p.sendMessage(Language.argsError);
                    return;
                }
                if (lore.size() == 0) {
                    p.sendMessage(MESSAGE_PREFIX + Language.noLore_del);
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
                p.sendMessage(MESSAGE_PREFIX + Language.delLore);
                break;
            }
            case "clear":
                //ll clear name
                //ll clear lore
                if (args.length < 2) {
                    p.sendMessage(Language.argsError);
                    return;
                }
                switch (args[1].toLowerCase()) {
                    case "name":
                        meta.setDisplayName(null);
                        p.sendMessage(MESSAGE_PREFIX + Language.clearDisplayName);
                        break;
                    case "lore":
                        meta.setLore(null);
                        p.sendMessage(MESSAGE_PREFIX + Language.clearLore);
                        break;
                    default:
                        p.sendMessage(Language.argsError);
                        return;
                }
                break;
            default:
                p.sendMessage(Language.argsError);
                return;
        }
        if (!item.setItemMeta(meta)) {
            p.sendMessage(Language.saveError);
        }
    }
}

package xyz.myunco.iloreedit;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.myunco.iloreedit.command.ChatPacketListener;
import xyz.myunco.iloreedit.command.EditLoreCommand;
import xyz.myunco.iloreedit.command.ILoreEditCommand;
import xyz.myunco.iloreedit.config.Config;
import xyz.myunco.iloreedit.config.Language;
import xyz.myunco.iloreedit.config.TemplateInfo;
import xyz.myunco.iloreedit.metrics.Metrics;
import xyz.myunco.iloreedit.update.UpdateChecker;
import xyz.myunco.iloreedit.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

    @SuppressWarnings({"deprecation"})
    public void commandEditLore(CommandSender sender, Player player, String arg, String[] args) {
        if (args.length == 0 || args[0].isEmpty()) {
            sendMessage(sender, Language.commandEditloreUsage);
            return;
        }
        logMessage("arg = " + arg);
        logMessage("args = " + Arrays.toString(args));
        ItemStack item = mcVersion > 8 ? player.getInventory().getItemInMainHand() : player.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        if (item.getType() == Material.AIR || meta == null) {
            sendMessage(sender, Language.commandEditloreNotItem);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "name":
                //ll name test
                if (args.length < 2) {
                    sendMessage(sender, Language.commandEditloreArgsError);
                    sendMessage(sender, Language.commandEditloreNameUsage);
                    return;
                }
                meta.setDisplayName(Utils.translateColor(Utils.getTextRight(arg, args[0] + " ")));
                sendMessage(sender, Language.commandEditloreName);
                break;
            case "add": {
                List<String> lore = Utils.getLore(meta);
                //ll add test
                if (args.length < 2) {
                    sendMessage(sender, Language.commandEditloreArgsError);
                    sendMessage(sender, Language.commandEditloreAddUsage);
                    return;
                }
                lore.add(Utils.translateColor(Utils.getTextRight(arg, args[0] + " ")));
                meta.setLore(lore);
                sendMessage(sender, Language.commandEditloreAdd);
                break;
            }
            case "set": {
                List<String> lore = Utils.getLore(meta);
                //ll set 1 test
                if (args.length < 3) {
                    sendMessage(sender, Language.commandEditloreArgsError);
                    sendMessage(sender, Language.commandEditloreSetUsage);
                    return;
                } else if (lore.size() == 0) {
                    sendMessage(sender, Language.commandEditloreSetNotLore);
                    return;
                }
                int line = Utils.getLine(sender, args[1], lore.size());
                if (line == 0) {
                    return;
                }
                lore.set(line - 1, Utils.translateColor(Utils.getTextRight(arg, args[1] + " ")));
                meta.setLore(lore);
                sendMessage(sender, Language.commandEditloreSet);
                break;
            }
            case "ins": {
                List<String> lore = Utils.getLore(meta);
                //ll ins 1 test
                if (args.length < 3) {
                    sendMessage(sender, Language.commandEditloreArgsError);
                    sendMessage(sender, Language.commandEditloreInsUsage);
                    return;
                }
                if (lore.size() == 0) {
                    sendMessage(sender, Language.commandEditloreInsNotLore);
                    return;
                }
                int line = Utils.getLine(sender, args[1], lore.size());
                if (line == 0) {
                    return;
                }
                lore.add(line - 1, Utils.translateColor(Utils.getTextRight(arg, args[1] + " ")));
                meta.setLore(lore);
                sendMessage(sender, Language.commandEditloreIns);
                break;
            }
            case "del": {
                //ll del
                //ll del 1
                List<String> lore = Utils.getLore(meta);
                if (args.length > 2) {
                    sendMessage(sender, Language.commandEditloreArgsError);
                    sendMessage(sender, Language.commandEditloreDelUsage);
                    return;
                }
                if (lore.size() == 0) {
                    sendMessage(sender, Language.commandEditloreDelNotLore);
                    return;
                }
                int line;
                if (args.length == 1) {
                    line = lore.size();
                } else {
                    line = Utils.getLine(sender, args[1], lore.size());
                    if (line == 0) {
                        return;
                    }
                }
                lore.remove(line - 1);
                meta.setLore(lore);
                sendMessage(sender, Language.commandEditloreDel);
                break;
            }
            case "clear":
                //ll clear name
                //ll clear lore
                if (args.length != 2) {
                    sendMessage(sender, Language.commandEditloreArgsError);
                    sendMessage(sender, Language.commandEditloreClearUsage);
                    return;
                }
                switch (args[1].toLowerCase()) {
                    case "name":
                        meta.setDisplayName(null);
                        sendMessage(sender, Language.commandEditloreClearName);
                        break;
                    case "lore":
                        meta.setLore(null);
                        sendMessage(sender, Language.commandEditloreClearLore);
                        break;
                    case "model":
                        if (mcVersion < 14) {
                            sendMessage(sender, Language.commandEditloreModelNotSupport);
                            return;
                        }
                        meta.setCustomModelData(null);
                        sendMessage(sender, Language.commandEditloreClearModel);
                        break;
                    default:
                        sendMessage(sender, Language.commandEditloreArgsError);
                        sendMessage(sender, Language.commandEditloreClearUsage);
                        return;
                }
                break;
            case "import": {
                //ll import test
                if (args.length != 2) {
                    sendMessage(sender, Language.commandEditloreArgsError);
                    sendMessage(sender, Language.commandEditloreImportUsage);
                    return;
                }
                TemplateInfo template = new TemplateInfo(this); //模板需要实时更新 所以每次都重新加载
                if (template.exists(args[1])) {
                    meta.setDisplayName(template.getDisplayName(args[1]));
                    meta.setLore(template.getLore(args[1]));
                    if (mcVersion >= 14 && template.hasCustomModelData(args[1])) {
                        meta.setCustomModelData(template.getCustomModelData(args[1]));
                    }
                    sendMessage(sender, Language.commandEditloreImport);
                } else {
                    sendMessage(sender, Language.commandEditloreTemplateDontExist);
                    return;
                }
                break;
            }
            case "export": {
                //ll export test
                if (args.length != 2) {
                    sendMessage(sender, Language.commandEditloreArgsError);
                    sendMessage(sender, Language.commandEditloreExportUsage);
                    return;
                } else if (!meta.hasDisplayName() && !meta.hasLore()) {
                    sendMessage(sender, Language.commandEditloreExportNone);
                    return;
                } else if (args[1].indexOf('.') != -1) {
                    sendMessage(sender, Language.commandEditloreTemplateInvalidName);
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
                sendMessage(sender, Language.commandEditloreExport);
                break;
            }
            case "owner":
                //ll owner test
                if (args.length != 2) {
                    sendMessage(sender, Language.commandEditloreArgsError);
                    sendMessage(sender, Language.commandEditloreOwnerUsage);
                    return;
                }
                if (meta instanceof SkullMeta) {
                    ((SkullMeta) meta).setOwner(args[1]);
                    sendMessage(sender, Language.commandEditloreOwner);
                } else {
                    sendMessage(sender, Language.commandEditloreOwnerNotSkull);
                }
                break;
            case "model": {
                //ll model 1
                if (args.length != 2) {
                    sendMessage(sender, Language.commandEditloreArgsError);
                    sendMessage(sender, Language.commandEditloreModelUsage);
                    return;
                } else if (mcVersion < 14) {
                    sendMessage(sender, Language.commandEditloreModelNotSupport);
                    return;
                }
                try {
                    int data = Integer.parseInt(args[1]);
                    meta.setCustomModelData(data);
                    sendMessage(sender, Language.commandEditloreModel);
                } catch (NumberFormatException e) {
                    sendMessage(sender, Language.commandEditloreModelInvalidData);
                }
                break;
            }
            default:
                sendMessage(sender, Language.commandEditloreUnknown);
                return;
        }
        if (!item.setItemMeta(meta)) {
            sendMessage(sender, Language.commandEditloreSaveError);
        }
    }

    public static void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(Language.messagePrefix + msg);
    }

    public void logMessage(String msg) {
        getServer().getConsoleSender().sendMessage(Language.logPrefix + msg);
    }

}

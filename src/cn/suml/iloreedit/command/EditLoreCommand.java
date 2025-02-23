package cn.suml.iloreedit.command;

import cn.suml.iloreedit.ILoreEdit;
import cn.suml.iloreedit.config.Language;
import cn.suml.iloreedit.config.TemplateInfo;
import cn.suml.iloreedit.util.UndoList;
import cn.suml.iloreedit.util.Utils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class EditLoreCommand implements TabExecutor {
    public static final HashMap<String, String> cmdMap = new HashMap<>();
    private final HashMap<String, UndoList<ItemMeta>> undoMap = new HashMap<>();
    private final ILoreEdit plugin = ILoreEdit.plugin;
    private List<String> templateList;
    private long templateListTime;

    @Override
    @SuppressWarnings("NullableProblems")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String arg = cmdMap.remove(player.getName());
            if (arg == null) {
                arg = Utils.replaceSpace(String.join(" ", args));
            }
            commandEditLore(sender, player, arg, Utils.getArgs(arg));
        } else {
            if (args.length == 0) {
                sendMessage(sender, Language.commandEditloreConsoleUsage);
            } else {
                Player player = plugin.getServer().getPlayer(args[0]);
                if (player == null) {
                    sendMessage(sender, Language.commandEditloreConsoleNotFoundPlayer);
                } else {
                    String[] cmdArgs = Arrays.copyOfRange(args, 1, args.length);
                    commandEditLore(sender, player, String.join(" ", cmdArgs), cmdArgs);
                }
            }
        }
        return true;
    }

    @Override
    @SuppressWarnings({"NullableProblems", "deprecation"})
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 1) {
                return null;
            }
            args = Arrays.copyOfRange(args, 1, args.length);
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("import") || args[0].equalsIgnoreCase("remove"))) {
            if (System.currentTimeMillis() - templateListTime > 10000L) { //缓存10秒
                templateList = new TemplateInfo(plugin).getTemplateList();
                templateListTime = System.currentTimeMillis();
            }
            return TabComplete.getCompleteList(args, templateList, true);
        } else if (args.length >= 2 && sender instanceof Player && args[0].equalsIgnoreCase("set")) {
            ItemMeta meta = ((Player) sender).getInventory().getItemInHand().getItemMeta();
            if (meta != null && meta.hasLore()) {
                List<String> lore = meta.getLore();
                assert lore != null;
                if (args.length == 2) {
                    return TabComplete.getCompleteList(args, Utils.generateLineNumber(lore.size()));
                } else if (args.length == 3) {
                    int line = Utils.parseInt(args[1]) - 1;
                    if (line >= 0 && line < lore.size()) {
                        return TabComplete.getCompleteList(args, Collections.singletonList(lore.get(line).replace('§', '&')), true);
                    }
                }
            }
        } else if (args.length == 2 && sender instanceof Player && args[0].equalsIgnoreCase("name")) {
            ItemMeta meta = ((Player) sender).getInventory().getItemInHand().getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                return TabComplete.getCompleteList(args, Collections.singletonList(meta.getDisplayName().replace('§', '&')), true);
            }

        }
        return TabComplete.getCompleteList(args, TabComplete.getTabList(args, command.getName()));
    }

    @SuppressWarnings("deprecation")
    private void commandEditLore(CommandSender sender, Player player, String arg, String[] args) {
        if (args.length == 0 || args[0].isEmpty()) {
            sendMessage(sender, Language.commandEditloreUsage);
            return;
        }
        ItemStack item = plugin.mcVersion.isGreaterThan(8) ? player.getInventory().getItemInMainHand() : player.getItemInHand();
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
                meta.setDisplayName(Utils.translateColor(player, Utils.getTextRight(arg, args[0] + " ")));
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
                lore.add(Utils.translateColor(player, Utils.getTextRight(arg, args[0] + " ")));
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
                } else if (lore.isEmpty()) {
                    sendMessage(sender, Language.commandEditloreSetNotLore);
                    return;
                }
                int line = Utils.getLine(sender, args[1], lore.size());
                if (line == 0) {
                    return;
                }
                lore.set(line - 1, Utils.translateColor(player, Utils.getTextRight(arg, args[1] + " ")));
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
                if (lore.isEmpty()) {
                    sendMessage(sender, Language.commandEditloreInsNotLore);
                    return;
                }
                int line = Utils.getLine(sender, args[1], lore.size());
                if (line == 0) {
                    return;
                }
                lore.add(line - 1, Utils.translateColor(player, Utils.getTextRight(arg, args[1] + " ")));
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
                if (lore.isEmpty()) {
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
                        if (!meta.hasDisplayName()) {
                            sendMessage(sender, Language.commandEditloreClearNameNone);
                            return;
                        }
                        meta.setDisplayName(null);
                        sendMessage(sender, Language.commandEditloreClearName);
                        break;
                    case "lore":
                        if (!meta.hasLore()) {
                            sendMessage(sender, Language.commandEditloreClearLoreNone);
                            return;
                        }
                        meta.setLore(null);
                        sendMessage(sender, Language.commandEditloreClearLore);
                        break;
                    case "model":
                        if (plugin.mcVersion.isLessThan(14)) {
                            sendMessage(sender, Language.commandEditloreModelNotSupport);
                            return;
                        }
                        if (!meta.hasCustomModelData()) {
                            sendMessage(sender, Language.commandEditloreClearModelNone);
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
                TemplateInfo template = new TemplateInfo(plugin); //模板需要实时更新 所以每次都重新加载
                if (template.exists(args[1])) {
                    if (template.getDisplayName(args[1]) != null) {
                        meta.setDisplayName(Utils.translateColor(player, template.getDisplayName(args[1])));
                    }
                    List<String> lore = template.getLore(args[1]);
                    if (!lore.isEmpty()) {
                        lore.replaceAll(text -> Utils.translateColor(player, text));
                        meta.setLore(lore);
                    }
                    if (plugin.mcVersion.isGreaterThanOrEqualTo(14) && template.hasCustomModelData(args[1])) {
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
                TemplateInfo template = new TemplateInfo(plugin);
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
                if (plugin.mcVersion.isGreaterThanOrEqualTo(14) && meta.hasCustomModelData()) {
                    template.setCustomModelData(args[1], meta.getCustomModelData());
                }
                template.save();
                sendMessage(sender, Language.commandEditloreExport);
                return;
            }
            case "remove": {
                //ll remove test
                if (args.length != 2) {
                    sendMessage(sender, Language.commandEditloreArgsError);
                    sendMessage(sender, Language.commandEditloreRemoveUsage);
                    return;
                }
                TemplateInfo template = new TemplateInfo(plugin);
                if (template.exists(args[1])) {
                    template.delete(args[1]);
                    template.save();
                    sendMessage(sender, Language.commandEditloreRemove);
                } else {
                    sendMessage(sender, Language.commandEditloreTemplateDontExist);
                }
                return;
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
                } else if (plugin.mcVersion.isLessThan(14)) {
                    sendMessage(sender, Language.commandEditloreModelNotSupport);
                    return;
                }
                try {
                    int data = Integer.parseInt(args[1]);
                    meta.setCustomModelData(data == 0 ? null : data);
                    sendMessage(sender, Language.commandEditloreModel);
                } catch (NumberFormatException e) {
                    sendMessage(sender, Language.commandEditloreModelInvalidData);
                }
                break;
            }
            case "undo":
                commandUndo(player, item);
                return;
            case "redo":
                commandRedo(player, item);
                return;
            default:
                sendMessage(sender, Language.commandEditloreUnknown);
                return;
        }
        if (!item.setItemMeta(meta)) {
            sendMessage(sender, Language.commandEditloreSaveError);
        }
        if (undoMap.get(player.getName()) == null) {
            undoMap.put(player.getName(), new UndoList<>());
        }
        undoMap.get(player.getName()).push(meta.clone());
    }

    public void commandUndo(Player player, ItemStack item) {
        UndoList<ItemMeta> undoList = undoMap.get(player.getName());
        if (undoList == null || !undoList.canUndo()) {
            sendMessage(player, Language.commandEditloreUndoCannot);
            return;
        }
        ItemMeta meta = undoList.undo();
        if (!item.setItemMeta(meta)) {
            sendMessage(player, Language.commandEditloreSaveError);
        } else {
            sendMessage(player, Language.commandEditloreUndo);
        }
    }

    public void commandRedo(Player player, ItemStack item) {
        UndoList<ItemMeta> undoList = undoMap.get(player.getName());
        if (undoList == null || !undoList.canRedo()) {
            sendMessage(player, Language.commandEditloreRedoCannot);
            return;
        }
        if (!item.setItemMeta(undoList.redo())) {
            sendMessage(player, Language.commandEditloreSaveError);
        } else {
            sendMessage(player, Language.commandEditloreRedo);
        }
    }

    public void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(Language.messagePrefix + msg);
    }

}

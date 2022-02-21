package xyz.myunco.iloreedit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import xyz.myunco.iloreedit.ILoreEdit;
import xyz.myunco.iloreedit.config.Language;
import xyz.myunco.iloreedit.config.TemplateInfo;
import xyz.myunco.iloreedit.util.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EditLoreCommand implements TabExecutor {
    private final ILoreEdit plugin = ILoreEdit.plugin;
    public static final HashMap<String, String> cmdMap = new HashMap<>();
    private List<String> templateList;
    private long templateListTime;

    @Override
    @SuppressWarnings("NullableProblems")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String arg = cmdMap.get(player.getName());
            if (arg == null) {
                arg = Utils.replaceSpace(String.join(" ", args));
            }
            plugin.commandEditLore(sender, player, arg, Utils.getArgs(arg));
        } else {
            if (args.length == 0) {
                ILoreEdit.sendMessage(sender, Language.commandEditloreConsoleUsage);
            } else {
                Player player = plugin.getServer().getPlayer(args[0]);
                if (player == null) {
                    ILoreEdit.sendMessage(sender, Language.commandEditloreConsoleNotFoundPlayer);
                } else {
                    String[] cmdArgs = Arrays.copyOfRange(args, 1, args.length);
                    plugin.commandEditLore(sender, player, String.join(" ", cmdArgs), cmdArgs);
                }
            }
        }
        return true;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2 && args[0].equalsIgnoreCase("import")) {
            if (System.currentTimeMillis() - templateListTime > 10000L) { //缓存10秒
                templateList = new TemplateInfo(plugin).getTemplateList();
                templateListTime = System.currentTimeMillis();
            }
            return TabComplete.getCompleteList(args, templateList, true);
        }
        return TabComplete.getCompleteList(args, TabComplete.getTabList(args, command.getName()));
    }

}

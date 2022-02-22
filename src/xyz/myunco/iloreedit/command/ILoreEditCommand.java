package xyz.myunco.iloreedit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import xyz.myunco.iloreedit.ILoreEdit;
import xyz.myunco.iloreedit.config.Language;
import xyz.myunco.iloreedit.update.UpdateChecker;

import java.util.List;

public class ILoreEditCommand implements TabExecutor {
    private final ILoreEdit plugin = ILoreEdit.plugin;

    @Override
    @SuppressWarnings("NullableProblems")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sendMessage(sender, Language.commandIloreeditUsage);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "help":
                sendMessage(sender, Language.commandIloreeditHelp);
                break;
            case "reload":
                UpdateChecker.stop();
                plugin.initConfig();
                sendMessage(sender, Language.commandIloreeditReload);
                break;
            case "version":
                sendMessage(sender, Language.replaceArgs(Language.commandIloreeditVersion, plugin.getDescription().getVersion()));
                break;
            default:
                sendMessage(sender, Language.commandIloreeditUnknown);
        }
        return true;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return TabComplete.getCompleteList(args, TabComplete.getTabList(args, command.getName()));
    }

    private static void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(Language.messagePrefix + msg);
    }

}

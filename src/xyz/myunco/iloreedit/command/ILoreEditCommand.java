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
    @SuppressWarnings({"NullableProblems"})
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            ILoreEdit.sendMessage(sender, Language.usage);
            ILoreEdit.sendMessage(sender, Language.usageILoreEdit);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "version":
                ILoreEdit.sendMessage(sender, "§bVersion§e: §a" + plugin.getDescription().getVersion());
                break;
            case "reload":
                UpdateChecker.stop();
                plugin.initConfig();
                ILoreEdit.sendMessage(sender, Language.reloaded);
                break;
            case "help":
                //TODO
                break;
            default:
                sender.sendMessage(Language.helpMsg);
        }
        return true;
    }

    @Override
    @SuppressWarnings({"NullableProblems"})
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return TabComplete.getCompleteList(args, TabComplete.getTabList(args, command.getName()));
    }

}

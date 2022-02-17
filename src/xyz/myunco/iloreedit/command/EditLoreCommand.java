package xyz.myunco.iloreedit.command;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import xyz.myunco.iloreedit.ILoreEdit;
import xyz.myunco.iloreedit.config.TemplateInfo;
import xyz.myunco.iloreedit.util.Utils;

import java.util.HashMap;
import java.util.List;

public class EditLoreCommand implements TabExecutor {
    private final ILoreEdit plugin = ILoreEdit.plugin;
    private final HashMap<String, String> cmdMap = new HashMap<>();
    private List<String> templateList;
    private long templateListTime;

    public EditLoreCommand(List<String> commands) {
        if (plugin.manager == null) {
            return;
        }
        plugin.manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.CHAT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                String msg = event.getPacket().getStrings().read(0);
                if (msg.startsWith("/") && commands.contains(Utils.getTextLeft(msg, " ").substring(1).toLowerCase())) {
                    Player player = event.getPlayer();
                    if (player.hasPermission("iloreedit.use")) {
                        String arg = Utils.getTextRight(replaceSpace(msg), " ");
                        cmdMap.put(player.getName(), arg);
                    }
                }
            }
        });
    }

    private String replaceSpace(String text) {
        return text.indexOf('"') == -1 ? text : text.replace("\"\"", " ");
    }

    @Override
    @SuppressWarnings({"NullableProblems"})
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String arg = cmdMap.get(player.getName());
            if (arg != null) {
                plugin.commandEditLore(player, arg, Utils.getArgs(arg));
            } else {
                plugin.commandEditLore(player, String.join(" ", args), args);
            }
        }
        //TODO
        return true;
    }

    @Override
    @SuppressWarnings({"NullableProblems"})
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

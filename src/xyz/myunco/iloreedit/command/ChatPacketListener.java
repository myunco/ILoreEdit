package xyz.myunco.iloreedit.command;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import xyz.myunco.iloreedit.ILoreEdit;
import xyz.myunco.iloreedit.util.Utils;

import java.util.List;

public class ChatPacketListener {

    public ChatPacketListener(List<String> commands, ILoreEdit plugin) {
        plugin.manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.CHAT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                String msg = event.getPacket().getStrings().read(0);
                if (msg.startsWith("/") && commands.contains(Utils.getTextLeft(msg, " ").substring(1).toLowerCase())) {
                    Player player = event.getPlayer();
                    if (player.hasPermission("iloreedit.use")) {
                        String arg = Utils.getTextRight(Utils.replaceSpace(msg), " ");
                        EditLoreCommand.cmdMap.put(player.getName(), arg);
                    }
                }
            }
        });
    }

}

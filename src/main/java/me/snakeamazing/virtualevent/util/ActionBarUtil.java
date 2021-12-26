package me.snakeamazing.virtualevent.util;

import me.snakeamazing.virtualevent.VirtualEvent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ActionBarUtil {

    public static void sendActionBar(Player player, String message) {
        IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(icbc, (byte) 2);
        sendPacket(player, packetPlayOutChat);
    }

    private static void sendPacket(Player player, Object object) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet) object);
    }

    public static int initSendPacketTask(VirtualEvent virtualEvent, Set<UUID> players, String message) {

        return Bukkit.getScheduler().scheduleSyncRepeatingTask(virtualEvent, new Runnable() {
            @Override
            public void run() {
                for (UUID uuid : players) {
                    sendActionBar(Bukkit.getPlayer(uuid), message);
                }

            }
        }, 5, 40);
    }
}

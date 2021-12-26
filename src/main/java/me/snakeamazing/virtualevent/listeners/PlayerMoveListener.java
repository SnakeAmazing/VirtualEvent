package me.snakeamazing.virtualevent.listeners;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.snakeamazing.virtualevent.manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Set;
import java.util.UUID;

public class PlayerMoveListener implements Listener {

    private final GameManager gameManager;

    public PlayerMoveListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Set<UUID> players = gameManager.getPlayers();

        if (!players.contains(player.getUniqueId())) {
            return;
        }

        RegionContainer regionContainer = WorldGuardPlugin.inst().getRegionContainer();

        RegionQuery regionQuery = regionContainer.createQuery();
        ApplicableRegionSet regions = regionQuery.getApplicableRegions(player.getLocation());

        boolean isInEvent = false;

        for (ProtectedRegion protectedRegion : regions) {
            if (protectedRegion.getId().equals("event")) {
                if (gameManager.getGameState() && !gameManager.getCanRun()) {
                    player.setHealth(0);
                    return;
                }
                isInEvent = true;
            }
        }

        if (gameManager.getGameState() && players.contains(player.getUniqueId()) && !isInEvent) {
            players.remove(player.getUniqueId());
        }

    }
}

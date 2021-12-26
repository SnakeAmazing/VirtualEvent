package me.snakeamazing.virtualevent.listeners;

import me.snakeamazing.virtualevent.file.FileMatcher;
import me.snakeamazing.virtualevent.file.YAMLFile;
import me.snakeamazing.virtualevent.manager.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private final GameManager gameManager;
    private final YAMLFile messages;

    public PlayerDeathListener(GameManager gameManager, FileMatcher fileMatcher) {
        this.gameManager = gameManager;
        this.messages = fileMatcher.getFile("messages");
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!gameManager.getPlayers().contains(player.getUniqueId())) {
            return;
        }

        player.getWorld().strikeLightningEffect(player.getLocation());
        event.setDeathMessage(messages.getString("messages.game.player-death")
                .replace("%player%", player.getName()));

        gameManager.removePlayerFromGame(player);
    }
}

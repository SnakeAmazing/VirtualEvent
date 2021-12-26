package me.snakeamazing.virtualevent.tasks;

import me.snakeamazing.virtualevent.manager.GameManager;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateGameTask extends BukkitRunnable {

    private final GameManager gameManager;

    public UpdateGameTask(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        gameManager.updateGame();
        this.cancel();
    }
}

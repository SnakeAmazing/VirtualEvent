package me.snakeamazing.virtualevent;

import me.snakeamazing.virtualevent.commands.StartCommand;
import me.snakeamazing.virtualevent.file.FileMatcher;
import me.snakeamazing.virtualevent.listeners.PlayerDeathListener;
import me.snakeamazing.virtualevent.listeners.PlayerMoveListener;
import me.snakeamazing.virtualevent.manager.GameManager;
import org.bukkit.plugin.java.JavaPlugin;

public class VirtualEvent extends JavaPlugin {

    private FileMatcher fileMatcher;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        fileMatcher = new FileMatcher(this);
        gameManager = new GameManager(this, fileMatcher);

        registerCommands(fileMatcher, gameManager);
        registerListeners(fileMatcher, gameManager);
    }

    @Override
    public void onDisable() {

    }

    private void registerCommands(FileMatcher fileMatcher, GameManager gameManager) {
        this.getCommand("luzverde").setExecutor(new StartCommand(gameManager, fileMatcher));
    }

    private void registerListeners(FileMatcher fileMatcher, GameManager gameManager) {
        this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(gameManager), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(gameManager, fileMatcher), this);
    }
}

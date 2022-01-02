package me.snakeamazing.virtualevent.commands;

import me.snakeamazing.virtualevent.file.FileMatcher;
import me.snakeamazing.virtualevent.file.YAMLFile;
import me.snakeamazing.virtualevent.manager.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor {

    private final GameManager gameManager;

    private final YAMLFile messages;

    public StartCommand(GameManager gameManager, FileMatcher fileMatcher) {
        this.gameManager = gameManager;

        this.messages = fileMatcher.getFile("messages");
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = (Player) commandSender;

        if (!player.hasPermission("virtualevent.luzverde")) return false;

        if (gameManager.getGameStarting()) {
            gameManager.finishGame();
            player.sendMessage(messages.getString("messages.game.finish"));
            return false;
        }

        player.sendMessage(messages.getString("messages.game.start"));

        gameManager.initGame();

        return false;
    }
}

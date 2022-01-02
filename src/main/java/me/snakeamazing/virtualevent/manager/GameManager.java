package me.snakeamazing.virtualevent.manager;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import me.snakeamazing.virtualevent.VirtualEvent;
import me.snakeamazing.virtualevent.file.FileMatcher;
import me.snakeamazing.virtualevent.file.YAMLFile;
import me.snakeamazing.virtualevent.tasks.UpdateGameTask;
import me.snakeamazing.virtualevent.util.ActionBarUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;

public class GameManager {

    private final VirtualEvent virtualEvent;

    private final Set<UUID> players = new HashSet<>();

    private final Random random;
    private final YAMLFile messages;

    private boolean started = false;
    private boolean canRun = false;
    private boolean starting = false;

    private UpdateGameTask updateGameTask;
    private int actionBarTask;
    private BukkitTask startingTask;

    private Song song;
    private SongPlayer songPlayer;

    public GameManager(VirtualEvent virtualEvent, FileMatcher fileMatcher) {
        this.virtualEvent = virtualEvent;
        this.random = new Random();

        this.messages = fileMatcher.getFile("messages");

    }

    public void initGame() {
        starting = true;
        song = NBSDecoder.parse(new File(virtualEvent.getDataFolder(), "song.nbs"));
        songPlayer = new RadioSongPlayer(song);

        RegionContainer regionContainer = WorldGuardPlugin.inst().getRegionContainer();

        for (Player player : Bukkit.getOnlinePlayers()) {
            RegionQuery regionQuery = regionContainer.createQuery();
            ApplicableRegionSet regions = regionQuery.getApplicableRegions(player.getLocation());

            for (ProtectedRegion protectedRegion : regions) {
                if (!protectedRegion.getId().equals("event")) continue;

                addPlayerToGame(player);
            }
        }

        songPlayer.setPlaying(true);

        actionBarTask = ActionBarUtil.initSendPacketTask(virtualEvent, players, messages.getString("messages.game.initializing"));

        Bukkit.broadcastMessage(messages.getString("messages.game.start-message"));

        startingTask = Bukkit.getScheduler().runTaskLaterAsynchronously(virtualEvent, new Runnable() {
            @Override
            public void run() {
                updateGame(messages.getInt("messages.game.time"));
            }
        }, 20 * 60);
    }

    public void finishGame() {
        players.clear();

        if (updateGameTask != null) {
            updateGameTask.cancel();
        } else {
            startingTask.cancel();
        }

        Bukkit.getScheduler().cancelTask(actionBarTask);
        started = false;
        canRun = false;
        starting = false;
        songPlayer.destroy();
    }

    public boolean getGameStarting() {
        return starting;
    }

    public boolean getGameState() {
        return started;
    }

    public boolean getCanRun() {
        return canRun;
    }

    public Set<UUID> getPlayers() {
        return players;
    }

    public void addPlayerToGame(Player player) {
        players.add(player.getUniqueId());
        songPlayer.addPlayer(player);
    }

    public void removePlayerFromGame(Player player) {
        players.remove(player.getUniqueId());
        songPlayer.removePlayer(player);
    }

    public void updateGame() {
        Bukkit.getScheduler().cancelTask(actionBarTask);

        if (players.isEmpty()) {
            finishGame();
            return;
        }

        if (canRun) {
            actionBarTask = ActionBarUtil.initSendPacketTask(virtualEvent, players, messages.getString("messages.game.can-t-run"));
            Bukkit.getScheduler().runTaskLaterAsynchronously(virtualEvent, new Runnable() {
                @Override
                public void run() {
                    canRun = false;
                    songPlayer.setPlaying(false);
                }
            }, 30);
        } else {
            actionBarTask = ActionBarUtil.initSendPacketTask(virtualEvent, players, messages.getString("messages.game.can-run"));
            songPlayer.setPlaying(true);
            canRun = true;
        }

        updateGameTask = new UpdateGameTask(this);
        updateGameTask.runTaskLaterAsynchronously(virtualEvent, 20 * random.nextInt(15));
    }

    public void updateGame(int seconds) {
        Bukkit.getScheduler().cancelTask(actionBarTask);

        Bukkit.broadcastMessage(messages.getString("messages.game.starting-soon")
                .replace("%seconds%", String.valueOf(seconds)));

        updateGameTask = new UpdateGameTask(this);
        updateGameTask.runTaskLaterAsynchronously(virtualEvent, 20 * seconds);

        Bukkit.getScheduler().runTaskLaterAsynchronously(virtualEvent, new Runnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(messages.getString("messages.game.game-started"));
                started = true;
            }
        }, 20 * 15);
    }
}

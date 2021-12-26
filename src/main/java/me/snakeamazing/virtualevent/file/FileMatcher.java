package me.snakeamazing.virtualevent.file;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class FileMatcher {

    private final Plugin plugin;

    public FileMatcher(Plugin plugin) {
        this.plugin = plugin;

        setup();
    }

    private final Map<String, YAMLFile> files = new HashMap<>();

    public void setup() {
        files.put("messages", new YAMLFile(plugin, "messages"));
    }

    public YAMLFile getFile(String key) {
        return files.get(key);
    }
}

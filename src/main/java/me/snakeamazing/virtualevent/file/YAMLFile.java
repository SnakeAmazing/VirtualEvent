package me.snakeamazing.virtualevent.file;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class YAMLFile extends YamlConfiguration {
    private final String fileName;

    private final Plugin plugin;

    private final File file;

    public YAMLFile(Plugin plugin, String filename, String fileExtension, File folder) {
        this.plugin = plugin;
        this.fileName = filename + (filename.endsWith(fileExtension) ? "" : fileExtension);
        this.file = new File(folder, this.fileName);
        createFile();
    }

    public YAMLFile(Plugin plugin, String fileName) {
        this(plugin, fileName, ".yml");
    }

    public YAMLFile(Plugin plugin, String fileName, String fileExtension) {
        this(plugin, fileName, fileExtension, plugin.getDataFolder());
    }

    private void createFile() {
        try {
            if (!this.file.exists()) {
                if (this.plugin.getResource(this.fileName) != null) {
                    this.plugin.saveResource(this.fileName, false);
                } else {
                    save(this.file);
                }
                load(this.file);
                return;
            }
            load(this.file);
            save(this.file);
        } catch (InvalidConfigurationException|IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        try {
            load(this.file);
        } catch (InvalidConfigurationException|IOException e) {
            e.printStackTrace();
        }
    }

    public String getString(String path) {
        return ChatColor.translateAlternateColorCodes('&', Objects.<String>requireNonNull(super.getString(path)));
    }

    public String getString(String path, String def) {
        String s = super.getString(path, def);
        return (s != null) ? ChatColor.translateAlternateColorCodes('&', s) : def;
    }

    public List<String> getStringList(String path) {
        List<String> list = super.getStringList(path);
        list.replaceAll(line -> ChatColor.translateAlternateColorCodes('&', line));
        return list;
    }
}

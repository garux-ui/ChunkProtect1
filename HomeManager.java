package com.chunkprotect.managers;

import com.chunkprotect.ChunkProtectPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomeManager {

    private final ChunkProtectPlugin plugin;
    private final Map<UUID, Map<String, Location>> homes = new HashMap<>();
    private File dataFile;

    public HomeManager(ChunkProtectPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "homes.yml");
    }

    public void load() {
        if (!dataFile.exists()) return;
        FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection playersSection = data.getConfigurationSection("homes");
        if (playersSection == null) return;

        for (String uuidStr : playersSection.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidStr);
            ConfigurationSection playerHomes = playersSection.getConfigurationSection(uuidStr);
            if (playerHomes == null) continue;

            Map<String, Location> playerHomeMap = new HashMap<>();
            for (String homeName : playerHomes.getKeys(false)) {
                String worldName = playerHomes.getString(homeName + ".world");
                double x = playerHomes.getDouble(homeName + ".x");
                double y = playerHomes.getDouble(homeName + ".y");
                double z = playerHomes.getDouble(homeName + ".z");
                float yaw = (float) playerHomes.getDouble(homeName + ".yaw");
                float pitch = (float) playerHomes.getDouble(homeName + ".pitch");
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    playerHomeMap.put(homeName, new Location(world, x, y, z, yaw, pitch));
                }
            }
            homes.put(uuid, playerHomeMap);
        }
    }

    public void save() {
        FileConfiguration data = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Location>> entry : homes.entrySet()) {
            String uuidStr = entry.getKey().toString();
            for (Map.Entry<String, Location> homeEntry : entry.getValue().entrySet()) {
                String homeName = homeEntry.getKey();
                Location loc = homeEntry.getValue();
                String path = "homes." + uuidStr + "." + homeName;
                data.set(path + ".world", loc.getWorld().getName());
                data.set(path + ".x", loc.getX());
                data.set(path + ".y", loc.getY());
                data.set(path + ".z", loc.getZ());
                data.set(path + ".yaw", loc.getYaw());
                data.set(path + ".pitch", loc.getPitch());
            }
        }
        try { data.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public boolean setHome(UUID uuid, String name, Location location) {
        Map<String, Location> playerHomes = homes.computeIfAbsent(uuid, k -> new HashMap<>());
        int max = plugin.getConfig().getInt("max-homes-per-player", 5);
        if (!playerHomes.containsKey(name) && playerHomes.size() >= max) return false;
        playerHomes.put(name, location);
        save();
        return true;
    }

    public Location getHome(UUID uuid, String name) {
        Map<String, Location> playerHomes = homes.get(uuid);
        if (playerHomes == null) return null;
        return playerHomes.get(name);
    }

    public boolean deleteHome(UUID uuid, String name) {
        Map<String, Location> playerHomes = homes.get(uuid);
        if (playerHomes == null || !playerHomes.containsKey(name)) return false;
        playerHomes.remove(name);
        save();
        return true;
    }

    public Map<String, Location> getHomes(UUID uuid) {
        return homes.getOrDefault(uuid, Collections.emptyMap());
    }

    public int getMaxHomes() {
        return plugin.getConfig().getInt("max-homes-per-player", 5);
    }
}

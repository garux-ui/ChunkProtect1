package com.chunkprotect.managers;

import com.chunkprotect.ChunkProtectPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SpawnManager {

    private final ChunkProtectPlugin plugin;
    private Location spawnLocation;
    private File dataFile;

    public SpawnManager(ChunkProtectPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "spawn.yml");
    }

    public void load() {
        if (!dataFile.exists()) return;
        FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
        if (!data.contains("spawn.world")) return;

        String worldName = data.getString("spawn.world");
        double x = data.getDouble("spawn.x");
        double y = data.getDouble("spawn.y");
        double z = data.getDouble("spawn.z");
        float yaw = (float) data.getDouble("spawn.yaw");
        float pitch = (float) data.getDouble("spawn.pitch");
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            this.spawnLocation = new Location(world, x, y, z, yaw, pitch);
        }
    }

    public void save() {
        FileConfiguration data = new YamlConfiguration();
        if (spawnLocation != null) {
            data.set("spawn.world", spawnLocation.getWorld().getName());
            data.set("spawn.x", spawnLocation.getX());
            data.set("spawn.y", spawnLocation.getY());
            data.set("spawn.z", spawnLocation.getZ());
            data.set("spawn.yaw", spawnLocation.getYaw());
            data.set("spawn.pitch", spawnLocation.getPitch());
        }
        try { data.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public Location getSpawn() { return spawnLocation; }

    public void setSpawn(Location location) {
        this.spawnLocation = location;
        save();
    }

    public boolean hasSpawn() { return spawnLocation != null; }
}

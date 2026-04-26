package com.chunkprotect;

import com.chunkprotect.commands.*;
import com.chunkprotect.listeners.*;
import com.chunkprotect.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class ChunkProtectPlugin extends JavaPlugin {

    private static ChunkProtectPlugin instance;
    private ClaimManager claimManager;
    private HomeManager homeManager;
    private SpawnManager spawnManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.claimManager = new ClaimManager(this);
        this.homeManager = new HomeManager(this);
        this.spawnManager = new SpawnManager(this);

        claimManager.load();
        homeManager.load();
        spawnManager.load();

        // Comandos
        getCommand("claim").setExecutor(new ClaimCommand(this));
        getCommand("unclaim").setExecutor(new UnclaimCommand(this));
        getCommand("claimlist").setExecutor(new ClaimListCommand(this));
        getCommand("trust").setExecutor(new TrustCommand(this));
        getCommand("untrust").setExecutor(new UntrustCommand(this));
        getCommand("claiminfo").setExecutor(new ClaimInfoCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("delhome").setExecutor(new DelHomeCommand(this));
        getCommand("homes").setExecutor(new HomesCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));

        // Listeners
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        getLogger().info("§aChunkProtect activado correctamente!");
    }

    @Override
    public void onDisable() {
        if (claimManager != null) claimManager.save();
        if (homeManager != null) homeManager.save();
        if (spawnManager != null) spawnManager.save();
        getLogger().info("§cChunkProtect desactivado.");
    }

    public static ChunkProtectPlugin getInstance() { return instance; }
    public ClaimManager getClaimManager() { return claimManager; }
    public HomeManager getHomeManager() { return homeManager; }
    public SpawnManager getSpawnManager() { return spawnManager; }
}

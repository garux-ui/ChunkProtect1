package com.chunkprotect.listeners;

import com.chunkprotect.ChunkProtectPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final ChunkProtectPlugin plugin;

    public PlayerJoinListener(ChunkProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§a§l✦ ChunkProtect §r§7| Usa §e/claim <radio> §7para proteger tu territorio.");
        event.getPlayer().sendMessage("§7Usa §e/spawn §7para ir al spawn y §e/home §7para tus casas.");
    }
}

package com.chunkprotect.commands;

import com.chunkprotect.ChunkProtectPlugin;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private final ChunkProtectPlugin plugin;

    public SpawnCommand(ChunkProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSolo jugadores.");
            return true;
        }

        if (!plugin.getSpawnManager().hasSpawn()) {
            player.sendMessage("§cEl spawn no está configurado. Un admin debe usar §e/setspawn§c.");
            return true;
        }

        Location spawn = plugin.getSpawnManager().getSpawn();
        player.teleport(spawn);
        player.sendMessage("§a✔ Teletransportado al §eSpawn§a.");
        return true;
    }
}

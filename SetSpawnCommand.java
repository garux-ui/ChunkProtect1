package com.chunkprotect.commands;

import com.chunkprotect.ChunkProtectPlugin;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

    private final ChunkProtectPlugin plugin;

    public SetSpawnCommand(ChunkProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSolo jugadores.");
            return true;
        }

        plugin.getSpawnManager().setSpawn(player.getLocation());
        player.sendMessage("§a✔ Spawn establecido en tu posición actual.");
        plugin.getServer().broadcastMessage("§6[ChunkProtect] §fEl spawn ha sido actualizado por §e" + player.getName() + "§f.");
        return true;
    }
}

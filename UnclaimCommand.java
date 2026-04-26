package com.chunkprotect.commands;

import com.chunkprotect.ChunkProtectPlugin;
import com.chunkprotect.model.ClaimedChunk;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class UnclaimCommand implements CommandExecutor {

    private final ChunkProtectPlugin plugin;

    public UnclaimCommand(ChunkProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSolo jugadores.");
            return true;
        }

        boolean success = plugin.getClaimManager().unclaimChunk(player.getUniqueId(), player.getLocation().getChunk());
        if (!success) {
            ClaimedChunk claim = plugin.getClaimManager().getClaim(player.getLocation().getChunk());
            if (claim == null) {
                player.sendMessage("§cEste chunk no está reclamado.");
            } else {
                player.sendMessage("§cNo eres el dueño de este chunk.");
            }
        } else {
            player.sendMessage("§a✔ Chunk liberado correctamente.");
        }
        return true;
    }
}

package com.chunkprotect.commands;

import com.chunkprotect.ChunkProtectPlugin;
import com.chunkprotect.model.ClaimedChunk;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class ClaimListCommand implements CommandExecutor {

    private final ChunkProtectPlugin plugin;

    public ClaimListCommand(ChunkProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSolo jugadores.");
            return true;
        }

        List<ClaimedChunk> claims = plugin.getClaimManager().getOwnerClaims(player.getUniqueId());
        if (claims.isEmpty()) {
            player.sendMessage("§7No tienes ningún territorio reclamado. Usa §e/claim <radio>§7.");
            return true;
        }

        player.sendMessage("§6§l── Tus Territorios ──");
        for (ClaimedChunk claim : claims) {
            String[] parts = claim.getId().split(":");
            String world = parts[0];
            String cx = parts[1], cz = parts[2];
            int members = claim.getMembers().size();
            player.sendMessage("§e• §f" + claim.getName() + " §7[" + world + " " + cx + "," + cz + "] §8(" + members + " miembros)");
        }
        player.sendMessage("§7Total: §e" + claims.size() + "/" + plugin.getClaimManager().getMaxClaims() + " §7chunks");
        return true;
    }
}

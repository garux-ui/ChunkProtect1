package com.chunkprotect.commands;

import com.chunkprotect.ChunkProtectPlugin;
import com.chunkprotect.model.ClaimedChunk;
import org.bukkit.Chunk;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class ClaimCommand implements CommandExecutor {

    private final ChunkProtectPlugin plugin;

    public ClaimCommand(ChunkProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSolo jugadores pueden usar este comando.");
            return true;
        }

        int radius = 0; // 0 = solo el chunk actual (1x1)
        if (args.length >= 1) {
            try {
                radius = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage("§cUsa: §e/claim <radio> §7(ej: /claim 0 = 1 chunk, /claim 2 = 5x5)");
                return true;
            }
        }

        int maxRadius = plugin.getClaimManager().getMaxRadius();
        if (radius < 0 || radius > maxRadius) {
            player.sendMessage("§cEl radio debe ser entre §e0 §cy §e" + maxRadius + "§c.");
            return true;
        }

        // Check límite de claims
        int currentClaims = plugin.getClaimManager().getOwnerClaims(player.getUniqueId()).size();
        int totalChunks = (radius * 2 + 1) * (radius * 2 + 1);
        int maxClaims = plugin.getClaimManager().getMaxClaims();
        if (currentClaims + totalChunks > maxClaims) {
            player.sendMessage("§cNo puedes reclamar más de §e" + maxClaims + " §cchunks en total.");
            player.sendMessage("§7Actualmente tienes §e" + currentClaims + "§7 chunks reclamados.");
            return true;
        }

        Chunk center = player.getLocation().getChunk();

        boolean success = plugin.getClaimManager().claimChunks(player.getUniqueId(), center, radius);
        if (!success) {
            player.sendMessage("§c✗ Uno o más chunks en esa área ya están reclamados por otro jugador.");
            return true;
        }

        int side = radius * 2 + 1;
        player.sendMessage("§a✔ ¡Territorio reclamado! §7(" + side + "x" + side + " chunks)");
        player.sendMessage("§7Usa §e/trust <jugador> <ADMIN|MEMBER|VISITOR> §7para añadir miembros.");
        player.sendMessage("§7Usa §e/sethome §7para guardar tu posición aquí.");
        return true;
    }
}

package com.chunkprotect.commands;

import com.chunkprotect.ChunkProtectPlugin;
import com.chunkprotect.model.ClaimRole;
import com.chunkprotect.model.ClaimedChunk;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class ClaimInfoCommand implements CommandExecutor {

    private final ChunkProtectPlugin plugin;

    public ClaimInfoCommand(ChunkProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSolo jugadores.");
            return true;
        }

        ClaimedChunk claim = plugin.getClaimManager().getClaim(player.getLocation().getChunk());
        if (claim == null) {
            player.sendMessage("§7Este chunk no está protegido.");
            return true;
        }

        OfflinePlayer owner = Bukkit.getOfflinePlayer(claim.getOwner());
        player.sendMessage("§6§l── Info del Territorio ──");
        player.sendMessage("§eNombre: §f" + claim.getName());
        player.sendMessage("§eDueño: §f" + (owner.getName() != null ? owner.getName() : "Desconocido"));
        player.sendMessage("§eChunk: §f" + claim.getId());

        ClaimRole myRole = claim.getRoleOf(player.getUniqueId());
        player.sendMessage("§eTu rol: §f" + (myRole != null ? myRole.name() : "Ninguno (solo paso)"));

        if (myRole != null && (myRole == ClaimRole.OWNER || myRole == ClaimRole.ADMIN)) {
            player.sendMessage("§6Miembros:");
            for (Map.Entry<UUID, ClaimRole> entry : claim.getMembers().entrySet()) {
                OfflinePlayer member = Bukkit.getOfflinePlayer(entry.getKey());
                player.sendMessage("  §7• §f" + (member.getName() != null ? member.getName() : entry.getKey()) + " §8[" + entry.getValue().name() + "]");
            }
        }

        player.sendMessage("§6Permisos según rol:");
        player.sendMessage("§8OWNER §7> Construir, abrir todo, gestionar miembros");
        player.sendMessage("§cADMIN §7> Construir, abrir cofres, invitar");
        player.sendMessage("§eADMIN §7> Construir, abrir cofres, invitar");
        player.sendMessage("§aMEMBER §7> Abrir cofres, interactuar con entidades");
        player.sendMessage("§bVISITOR §7> Pasar y abrir puertas");
        player.sendMessage("§7Sin rol §7> Solo pasar");
        return true;
    }
}

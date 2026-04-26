package com.chunkprotect.commands;

import com.chunkprotect.ChunkProtectPlugin;
import com.chunkprotect.model.ClaimRole;
import com.chunkprotect.model.ClaimedChunk;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class TrustCommand implements CommandExecutor {

    private final ChunkProtectPlugin plugin;

    public TrustCommand(ChunkProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSolo jugadores.");
            return true;
        }
        if (args.length < 2) {
            player.sendMessage("§cUso: §e/trust <jugador> <ADMIN|MEMBER|VISITOR>");
            return true;
        }

        ClaimedChunk claim = plugin.getClaimManager().getClaim(player.getLocation().getChunk());
        if (claim == null) {
            player.sendMessage("§cEste chunk no está reclamado. Párate en tu territorio.");
            return true;
        }

        ClaimRole myRole = claim.getRoleOf(player.getUniqueId());
        if (!claim.canManageMembers(myRole)) {
            player.sendMessage("§cNecesitas ser OWNER o ADMIN para añadir miembros.");
            return true;
        }

        String targetName = args[0];
        String roleName = args[1].toUpperCase();

        ClaimRole role;
        try {
            role = ClaimRole.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cRol inválido. Usa: §eADMIN§c, §eMEMBER §co §eVISITOR§c.");
            return true;
        }

        if (role == ClaimRole.OWNER) {
            player.sendMessage("§cNo puedes asignar el rol OWNER.");
            return true;
        }

        // Un ADMIN solo puede añadir MEMBER y VISITOR
        if (myRole == ClaimRole.ADMIN && role == ClaimRole.ADMIN) {
            player.sendMessage("§cComo ADMIN no puedes añadir otros ADMINs. Solo el OWNER puede hacerlo.");
            return true;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage("§cNo puedes añadirte a ti mismo.");
            return true;
        }

        claim.addMember(target.getUniqueId(), role);
        plugin.getClaimManager().save();

        player.sendMessage("§a✔ §f" + targetName + " §aañadido como §e" + role.name() + "§a.");
        if (target.isOnline()) {
            ((Player) target.getPlayer()).sendMessage("§a✔ Fuiste añadido como §e" + role.name() + " §aen el territorio de §f" + player.getName() + "§a.");
        }
        return true;
    }
}

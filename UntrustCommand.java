package com.chunkprotect.commands;

import com.chunkprotect.ChunkProtectPlugin;
import com.chunkprotect.model.ClaimRole;
import com.chunkprotect.model.ClaimedChunk;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class UntrustCommand implements CommandExecutor {

    private final ChunkProtectPlugin plugin;

    public UntrustCommand(ChunkProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSolo jugadores.");
            return true;
        }
        if (args.length < 1) {
            player.sendMessage("§cUso: §e/untrust <jugador>");
            return true;
        }

        ClaimedChunk claim = plugin.getClaimManager().getClaim(player.getLocation().getChunk());
        if (claim == null) {
            player.sendMessage("§cEste chunk no está reclamado.");
            return true;
        }

        ClaimRole myRole = claim.getRoleOf(player.getUniqueId());
        if (!claim.canManageMembers(myRole)) {
            player.sendMessage("§cNecesitas ser OWNER o ADMIN para quitar miembros.");
            return true;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        ClaimRole targetRole = claim.getRoleOf(target.getUniqueId());

        if (targetRole == null || targetRole == ClaimRole.OWNER) {
            player.sendMessage("§cEse jugador no está en tu territorio o es el dueño.");
            return true;
        }

        // ADMIN no puede quitar a otro ADMIN
        if (myRole == ClaimRole.ADMIN && targetRole == ClaimRole.ADMIN) {
            player.sendMessage("§cNo puedes quitar a otro ADMIN. Solo el OWNER puede.");
            return true;
        }

        claim.removeMember(target.getUniqueId());
        plugin.getClaimManager().save();
        player.sendMessage("§a✔ §f" + args[0] + " §aquitado del territorio.");

        if (target.isOnline()) {
            ((Player) target.getPlayer()).sendMessage("§c✗ Fuiste quitado del territorio de §f" + player.getName() + "§c.");
        }
        return true;
    }
}

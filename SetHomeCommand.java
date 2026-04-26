package com.chunkprotect.commands;

import com.chunkprotect.ChunkProtectPlugin;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {

    private final ChunkProtectPlugin plugin;

    public SetHomeCommand(ChunkProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSolo jugadores.");
            return true;
        }

        String homeName = args.length >= 1 ? args[0] : "home";

        // Validar nombre
        if (!homeName.matches("[a-zA-Z0-9_]{1,16}")) {
            player.sendMessage("§cEl nombre solo puede tener letras, números y _, máximo 16 caracteres.");
            return true;
        }

        boolean success = plugin.getHomeManager().setHome(player.getUniqueId(), homeName, player.getLocation());
        if (!success) {
            int max = plugin.getHomeManager().getMaxHomes();
            player.sendMessage("§cNo puedes tener más de §e" + max + " §chomes.");
        } else {
            player.sendMessage("§a✔ Home §e" + homeName + " §aguardado.");
        }
        return true;
    }
}

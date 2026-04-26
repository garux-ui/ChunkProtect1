package com.chunkprotect.commands;

import com.chunkprotect.ChunkProtectPlugin;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class DelHomeCommand implements CommandExecutor {

    private final ChunkProtectPlugin plugin;

    public DelHomeCommand(ChunkProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSolo jugadores.");
            return true;
        }

        String homeName = args.length >= 1 ? args[0] : "home";
        boolean success = plugin.getHomeManager().deleteHome(player.getUniqueId(), homeName);

        if (!success) {
            player.sendMessage("§cNo tienes un home llamado §e" + homeName + "§c.");
        } else {
            player.sendMessage("§a✔ Home §e" + homeName + " §aeliminado.");
        }
        return true;
    }
}

package com.chunkprotect.commands;

import com.chunkprotect.ChunkProtectPlugin;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {

    private final ChunkProtectPlugin plugin;

    public HomeCommand(ChunkProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSolo jugadores.");
            return true;
        }

        String homeName = args.length >= 1 ? args[0] : "home";
        Location loc = plugin.getHomeManager().getHome(player.getUniqueId(), homeName);

        if (loc == null) {
            player.sendMessage("§cNo tienes un home llamado §e" + homeName + "§c.");
            player.sendMessage("§7Usa §e/homes §7para ver tus homes o §e/sethome §7para crear uno.");
            return true;
        }

        player.teleport(loc);
        player.sendMessage("§a✔ Teletransportado a §e" + homeName + "§a.");
        return true;
    }
}

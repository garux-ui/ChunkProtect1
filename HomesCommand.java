package com.chunkprotect.commands;

import com.chunkprotect.ChunkProtectPlugin;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Map;

public class HomesCommand implements CommandExecutor {

    private final ChunkProtectPlugin plugin;

    public HomesCommand(ChunkProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSolo jugadores.");
            return true;
        }

        Map<String, Location> homes = plugin.getHomeManager().getHomes(player.getUniqueId());
        if (homes.isEmpty()) {
            player.sendMessage("§7No tienes homes. Usa §e/sethome [nombre]§7.");
            return true;
        }

        player.sendMessage("§6§l── Tus Homes ──");
        int max = plugin.getHomeManager().getMaxHomes();
        for (Map.Entry<String, Location> entry : homes.entrySet()) {
            Location loc = entry.getValue();
            player.sendMessage("§e• §f" + entry.getKey() + " §7[" + loc.getWorld().getName() + " " +
                (int)loc.getX() + "," + (int)loc.getY() + "," + (int)loc.getZ() + "]");
        }
        player.sendMessage("§7Total: §e" + homes.size() + "/" + max);
        return true;
    }
}

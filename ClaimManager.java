package com.chunkprotect.managers;

import com.chunkprotect.ChunkProtectPlugin;
import com.chunkprotect.model.ClaimRole;
import com.chunkprotect.model.ClaimedChunk;
import org.bukkit.Chunk;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ClaimManager {

    private final ChunkProtectPlugin plugin;
    private final Map<String, ClaimedChunk> claims = new HashMap<>(); // chunkKey -> ClaimedChunk
    private final Map<UUID, List<String>> ownerClaims = new HashMap<>(); // ownerUUID -> list of chunkKeys
    private File dataFile;
    private FileConfiguration data;

    public ClaimManager(ChunkProtectPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "claims.yml");
    }

    public void load() {
        if (!dataFile.exists()) {
            try { dataFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection claimsSection = data.getConfigurationSection("claims");
        if (claimsSection == null) return;

        for (String key : claimsSection.getKeys(false)) {
            String safeKey = key.replace("_DOT_", ".");
            String ownerStr = claimsSection.getString(key + ".owner");
            String name = claimsSection.getString(key + ".name", "claim");
            if (ownerStr == null) continue;

            UUID owner = UUID.fromString(ownerStr);
            ClaimedChunk chunk = new ClaimedChunk(safeKey, owner, name);

            ConfigurationSection membersSection = claimsSection.getConfigurationSection(key + ".members");
            if (membersSection != null) {
                for (String memberStr : membersSection.getKeys(false)) {
                    UUID memberUUID = UUID.fromString(memberStr);
                    ClaimRole role = ClaimRole.valueOf(membersSection.getString(memberStr, "VISITOR"));
                    chunk.addMember(memberUUID, role);
                }
            }

            claims.put(safeKey, chunk);
            ownerClaims.computeIfAbsent(owner, k -> new ArrayList<>()).add(safeKey);
        }
        plugin.getLogger().info("Cargadas " + claims.size() + " protecciones.");
    }

    public void save() {
        data = new YamlConfiguration();
        for (Map.Entry<String, ClaimedChunk> entry : claims.entrySet()) {
            String safeKey = entry.getKey().replace(".", "_DOT_");
            ClaimedChunk chunk = entry.getValue();
            String path = "claims." + safeKey;
            data.set(path + ".owner", chunk.getOwner().toString());
            data.set(path + ".name", chunk.getName());
            for (Map.Entry<UUID, ClaimRole> member : chunk.getMembers().entrySet()) {
                data.set(path + ".members." + member.getKey().toString(), member.getValue().name());
            }
        }
        try { data.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public ClaimedChunk getClaim(Chunk chunk) {
        return claims.get(ClaimedChunk.getKey(chunk));
    }

    public ClaimedChunk getClaim(String key) {
        return claims.get(key);
    }

    public boolean isClaimed(Chunk chunk) {
        return claims.containsKey(ClaimedChunk.getKey(chunk));
    }

    public boolean claimChunks(UUID owner, Chunk center, int radius) {
        List<String> keys = new ArrayList<>();
        int startX = center.getX() - radius;
        int endX = center.getX() + radius;
        int startZ = center.getZ() - radius;
        int endZ = center.getZ() + radius;

        // Check if any chunk is already claimed
        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                String key = ClaimedChunk.getKey(center.getWorld().getName(), x, z);
                if (claims.containsKey(key)) return false; // ya reclamado
            }
        }

        // Claim all chunks
        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                String key = ClaimedChunk.getKey(center.getWorld().getName(), x, z);
                ClaimedChunk claimed = new ClaimedChunk(center.getWorld().getName(), x, z, owner);
                claims.put(key, claimed);
                keys.add(key);
            }
        }

        ownerClaims.computeIfAbsent(owner, k -> new ArrayList<>()).addAll(keys);
        save();
        return true;
    }

    public boolean unclaimChunk(UUID owner, Chunk chunk) {
        String key = ClaimedChunk.getKey(chunk);
        ClaimedChunk claimed = claims.get(key);
        if (claimed == null) return false;
        if (!claimed.getOwner().equals(owner)) return false;

        claims.remove(key);
        List<String> owned = ownerClaims.get(owner);
        if (owned != null) owned.remove(key);
        save();
        return true;
    }

    public List<ClaimedChunk> getOwnerClaims(UUID owner) {
        List<String> keys = ownerClaims.getOrDefault(owner, Collections.emptyList());
        List<ClaimedChunk> result = new ArrayList<>();
        for (String key : keys) {
            ClaimedChunk c = claims.get(key);
            if (c != null) result.add(c);
        }
        return result;
    }

    public int getMaxClaims() {
        return plugin.getConfig().getInt("max-claims-per-player", 10);
    }

    public int getMaxRadius() {
        return plugin.getConfig().getInt("max-claim-radius", 5);
    }
}

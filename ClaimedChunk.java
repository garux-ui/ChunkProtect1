package com.chunkprotect.model;

import org.bukkit.Chunk;

import java.util.*;

public class ClaimedChunk {

    private final String id; // world:x:z
    private final UUID owner;
    private final Map<UUID, ClaimRole> members = new HashMap<>();
    private String name;

    public ClaimedChunk(String world, int chunkX, int chunkZ, UUID owner) {
        this.id = world + ":" + chunkX + ":" + chunkZ;
        this.owner = owner;
        this.name = "claim_" + chunkX + "_" + chunkZ;
    }

    public ClaimedChunk(String id, UUID owner, String name) {
        this.id = id;
        this.owner = owner;
        this.name = name;
    }

    public static String getKey(Chunk chunk) {
        return chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
    }

    public static String getKey(String world, int x, int z) {
        return world + ":" + x + ":" + z;
    }

    public String getId() { return id; }
    public UUID getOwner() { return owner; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Map<UUID, ClaimRole> getMembers() { return members; }

    public ClaimRole getRoleOf(UUID uuid) {
        if (uuid.equals(owner)) return ClaimRole.OWNER;
        return members.getOrDefault(uuid, null);
    }

    public boolean isMember(UUID uuid) {
        return uuid.equals(owner) || members.containsKey(uuid);
    }

    public void addMember(UUID uuid, ClaimRole role) {
        members.put(uuid, role);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    // Permisos por rol
    public boolean canInteractContainers(ClaimRole role) {
        if (role == null) return false;
        return role == ClaimRole.OWNER || role == ClaimRole.ADMIN || role == ClaimRole.MEMBER;
    }

    public boolean canInteractEntities(ClaimRole role) {
        if (role == null) return false;
        return role == ClaimRole.OWNER || role == ClaimRole.ADMIN || role == ClaimRole.MEMBER;
    }

    public boolean canKillEntities(ClaimRole role) {
        if (role == null) return false;
        return role == ClaimRole.OWNER || role == ClaimRole.ADMIN || role == ClaimRole.MEMBER;
    }

    public boolean canBreakBlocks(ClaimRole role) {
        if (role == null) return false;
        return role == ClaimRole.OWNER || role == ClaimRole.ADMIN;
    }

    public boolean canPlaceBlocks(ClaimRole role) {
        if (role == null) return false;
        return role == ClaimRole.OWNER || role == ClaimRole.ADMIN;
    }

    public boolean canManageMembers(ClaimRole role) {
        if (role == null) return false;
        return role == ClaimRole.OWNER || role == ClaimRole.ADMIN;
    }

    public boolean canOpenDoors(ClaimRole role) {
        // Visitors solo pueden abrir puertas
        if (role == null) return false;
        return true; // todos los roles pueden abrir puertas
    }
}

package com.chunkprotect.listeners;

import com.chunkprotect.ChunkProtectPlugin;
import com.chunkprotect.model.ClaimRole;
import com.chunkprotect.model.ClaimedChunk;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;

import java.util.Set;
import java.util.UUID;

public class ProtectionListener implements Listener {

    private final ChunkProtectPlugin plugin;

    // Materiales considerados "contenedores"
    private static final Set<Material> CONTAINERS = Set.of(
        Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL,
        Material.HOPPER, Material.DROPPER, Material.DISPENSER,
        Material.FURNACE, Material.BLAST_FURNACE, Material.SMOKER,
        Material.SHULKER_BOX, Material.WHITE_SHULKER_BOX, Material.ORANGE_SHULKER_BOX,
        Material.MAGENTA_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX,
        Material.YELLOW_SHULKER_BOX, Material.LIME_SHULKER_BOX, Material.PINK_SHULKER_BOX,
        Material.GRAY_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX, Material.CYAN_SHULKER_BOX,
        Material.PURPLE_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX,
        Material.GREEN_SHULKER_BOX, Material.RED_SHULKER_BOX, Material.BLACK_SHULKER_BOX,
        Material.ENDER_CHEST, Material.BREWING_STAND, Material.BEACON,
        Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL,
        Material.ENCHANTING_TABLE, Material.CRAFTING_TABLE,
        Material.LOOM, Material.CARTOGRAPHY_TABLE, Material.STONECUTTER, Material.GRINDSTONE,
        Material.LECTERN
    );

    // Materiales de puertas/trampillas (VISITOR puede usar)
    private static final Set<Material> DOORS = Set.of(
        Material.OAK_DOOR, Material.SPRUCE_DOOR, Material.BIRCH_DOOR,
        Material.JUNGLE_DOOR, Material.ACACIA_DOOR, Material.DARK_OAK_DOOR,
        Material.MANGROVE_DOOR, Material.CHERRY_DOOR, Material.BAMBOO_DOOR,
        Material.CRIMSON_DOOR, Material.WARPED_DOOR, Material.IRON_DOOR,
        Material.OAK_TRAPDOOR, Material.SPRUCE_TRAPDOOR, Material.BIRCH_TRAPDOOR,
        Material.JUNGLE_TRAPDOOR, Material.ACACIA_TRAPDOOR, Material.DARK_OAK_TRAPDOOR,
        Material.MANGROVE_TRAPDOOR, Material.CHERRY_TRAPDOOR, Material.BAMBOO_TRAPDOOR,
        Material.CRIMSON_TRAPDOOR, Material.WARPED_TRAPDOOR, Material.IRON_TRAPDOOR,
        Material.OAK_FENCE_GATE, Material.SPRUCE_FENCE_GATE, Material.BIRCH_FENCE_GATE,
        Material.JUNGLE_FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.DARK_OAK_FENCE_GATE,
        Material.MANGROVE_FENCE_GATE, Material.CHERRY_FENCE_GATE, Material.BAMBOO_FENCE_GATE,
        Material.CRIMSON_FENCE_GATE, Material.WARPED_FENCE_GATE,
        Material.OAK_BUTTON, Material.SPRUCE_BUTTON, Material.BIRCH_BUTTON,
        Material.JUNGLE_BUTTON, Material.ACACIA_BUTTON, Material.DARK_OAK_BUTTON,
        Material.STONE_BUTTON, Material.POLISHED_BLACKSTONE_BUTTON,
        Material.OAK_PRESSURE_PLATE, Material.SPRUCE_PRESSURE_PLATE,
        Material.STONE_PRESSURE_PLATE, Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
        Material.HEAVY_WEIGHTED_PRESSURE_PLATE
    );

    public ProtectionListener(ChunkProtectPlugin plugin) {
        this.plugin = plugin;
    }

    private boolean hasBypass(Player player) {
        return player.hasPermission("chunkprotect.bypass");
    }

    private ClaimRole getRoleInChunk(Player player, ClaimedChunk claim) {
        return claim.getRoleOf(player.getUniqueId());
    }

    // ========== ROMPER BLOQUES ==========
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (hasBypass(player)) return;
        ClaimedChunk claim = plugin.getClaimManager().getClaim(event.getBlock().getChunk());
        if (claim == null) return;

        ClaimRole role = getRoleInChunk(player, claim);
        if (!claim.canBreakBlocks(role)) {
            event.setCancelled(true);
            player.sendMessage("§c✗ No tienes permiso para romper bloques aquí.");
        }
    }

    // ========== COLOCAR BLOQUES ==========
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (hasBypass(player)) return;
        ClaimedChunk claim = plugin.getClaimManager().getClaim(event.getBlock().getChunk());
        if (claim == null) return;

        ClaimRole role = getRoleInChunk(player, claim);
        if (!claim.canPlaceBlocks(role)) {
            event.setCancelled(true);
            player.sendMessage("§c✗ No tienes permiso para colocar bloques aquí.");
        }
    }

    // ========== INTERACCIÓN CON BLOQUES ==========
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (hasBypass(player)) return;
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;

        Material mat = event.getClickedBlock().getType();
        ClaimedChunk claim = plugin.getClaimManager().getClaim(event.getClickedBlock().getChunk());
        if (claim == null) return;

        ClaimRole role = getRoleInChunk(player, claim);

        if (CONTAINERS.contains(mat)) {
            if (!claim.canInteractContainers(role)) {
                event.setCancelled(true);
                player.sendMessage("§c✗ No puedes abrir contenedores en esta zona.");
            }
        } else if (DOORS.contains(mat)) {
            if (!claim.canOpenDoors(role)) {
                event.setCancelled(true);
                player.sendMessage("§c✗ No puedes interactuar con esto aquí.");
            }
        } else {
            // Cualquier otro bloque interactivo: solo OWNER/ADMIN/MEMBER
            if (role == null) {
                event.setCancelled(true);
            }
        }
    }

    // ========== INTERACCIÓN CON ENTIDADES ==========
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (hasBypass(player)) return;

        Entity entity = event.getRightClicked();
        ClaimedChunk claim = plugin.getClaimManager().getClaim(entity.getLocation().getChunk());
        if (claim == null) return;

        ClaimRole role = getRoleInChunk(player, claim);

        // Villagers, animales, marcos, etc.
        if (entity instanceof Villager || entity instanceof Merchant ||
            entity instanceof Animals || entity instanceof ItemFrame ||
            entity instanceof ArmorStand || entity instanceof NPC) {
            if (!claim.canInteractEntities(role)) {
                event.setCancelled(true);
                player.sendMessage("§c✗ No puedes interactuar con entidades en esta zona.");
            }
        }
    }

    // ========== DAÑO A ENTIDADES ==========
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Player player = null;

        if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile proj = (Projectile) event.getDamager();
            if (proj.getShooter() instanceof Player) {
                player = (Player) proj.getShooter();
            }
        }

        if (player == null) return;
        if (hasBypass(player)) return;

        Entity entity = event.getEntity();
        // No proteger a otros jugadores (PvP libre, se puede cambiar)
        if (entity instanceof Player) return;

        ClaimedChunk claim = plugin.getClaimManager().getClaim(entity.getLocation().getChunk());
        if (claim == null) return;

        ClaimRole role = getRoleInChunk(player, claim);
        if (!claim.canKillEntities(role)) {
            event.setCancelled(true);
            player.sendMessage("§c✗ No puedes atacar entidades en esta zona.");
        }
    }

    // ========== EXPLOSIONES ==========
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block ->
            plugin.getClaimManager().isClaimed(block.getChunk())
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(org.bukkit.event.entity.EntityExplodeEvent event) {
        event.blockList().removeIf(block ->
            plugin.getClaimManager().isClaimed(block.getChunk())
        );
    }

    // ========== PISOTEAR CULTIVOS ==========
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTrample(PlayerInteractEvent event) {
        if (event.getAction() != org.bukkit.event.block.Action.PHYSICAL) return;
        Player player = event.getPlayer();
        if (hasBypass(player)) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.FARMLAND) return;

        ClaimedChunk claim = plugin.getClaimManager().getClaim(event.getClickedBlock().getChunk());
        if (claim == null) return;
        ClaimRole role = getRoleInChunk(player, claim);
        if (!claim.canBreakBlocks(role)) {
            event.setCancelled(true);
        }
    }

    // ========== MARCOS DE ITEM ==========
    @EventHandler(priority = EventPriority.HIGH)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player player)) return;
        if (hasBypass(player)) return;

        ClaimedChunk claim = plugin.getClaimManager().getClaim(event.getEntity().getLocation().getChunk());
        if (claim == null) return;
        ClaimRole role = getRoleInChunk(player, claim);
        if (!claim.canBreakBlocks(role)) {
            event.setCancelled(true);
            player.sendMessage("§c✗ No puedes romper esto en esta zona.");
        }
    }
}

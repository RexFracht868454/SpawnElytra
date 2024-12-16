package rexfracht868454.spawnelytra;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.KeybindComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.ArrayList;
import java.util.List;

public class Listener implements org.bukkit.event.Listener {

    private rexfracht868454.spawnelytra.SpawnElytra plugin;
    private final int multiplyValue;
    private final int useRadius;
    private final String world;
    private final List<Player> isFlying = new ArrayList<>();
    private final List<Player> hasBoosted = new ArrayList<>();

    public Listener(SpawnElytra plugin) {
        this.plugin = plugin;
        this.multiplyValue = plugin.getConfig().getInt("multiplyValue");
        this.useRadius = plugin.getConfig().getInt("useRadius");
        this.world = plugin.getConfig().getString("world");

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (Bukkit.getWorld(world) == null) {
                Bukkit.getLogger().warning("Can´t find the world: '" + world + "'. Pleas check the config.yml!");
                return;
            }

            Bukkit.getWorld(world).getPlayers().forEach(player -> {
                if (player.getGameMode() != GameMode.SURVIVAL) return;
                player.setAllowFlight(isInUseRadius(player));
                if (isFlying.contains(player) && !player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isAir()) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.setGliding(false);
                    hasBoosted.remove(player);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> isFlying.remove(player), 5);
                }
            });
        }, 0, 3);
    }

    @EventHandler
    public void onPlayerFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SURVIVAL) return;
        if (!isInUseRadius(player)) return;
        event.setCancelled(true);
        event.getPlayer().setGliding(true);
        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new ComponentBuilder("§2Use ")
                        .append(new KeybindComponent("key.swapOffhand"))
                        .append("§2 to boost").create());
        isFlying.add(player);
    }
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Player player = (Player) event.getEntity();
        if (event.getEntityType() == EntityType.PLAYER && (event.getCause() == EntityDamageEvent.DamageCause.FALL || event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL) && isFlying.contains(player)) event.setCancelled(true);
    }
    @EventHandler
    public void onPlayerSwapItem(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (player.isGliding()) {
            if (hasBoosted.contains(player)) {
                event.setCancelled(true);
            } else {
                player.setVelocity(player.getLocation().getDirection().multiply(multiplyValue));
                hasBoosted.add(player);
            }
        }
    }
    @EventHandler
    public void onEntityToggleGlide(EntityToggleGlideEvent event) {
        if (event.getEntityType() == EntityType.PLAYER && isFlying.contains(event.getEntity())) event.setCancelled(true);
    }

    private boolean isInUseRadius(Player player) {
        if (!player.getWorld().getName().equals(world)) return false;
        return player.getWorld().getSpawnLocation().distance(player.getLocation()) <= useRadius;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isFlying.contains(player)) isFlying.remove(player);
        if (hasBoosted.contains(player)) hasBoosted.remove(player);
    }
}

package com.lunex.lauth.listeners;

import com.lunex.lauth.LauthPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class PlayerListener implements Listener {
    
    private LauthPlugin plugin;
    
    public PlayerListener(LauthPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        
        event.setJoinMessage("§c[LunexVanilla] §f" + playerName + " §cbağlandı!");
        
        if (!plugin.getAuthManager().isAuthenticated(player)) {
            // Lobby dünyasına TP et
            Location lobbySpawn = getLobbySpawnPoint();
            player.teleport(lobbySpawn);
            
            if (plugin.getAuthManager().isRegistered(playerName)) {
                player.sendMessage("§c════════════════════════════════════════");
                player.sendMessage("§c╔════════════ LunexVanilla ════════════╗");
                player.sendMessage("§c║");
                player.sendMessage("§c║  §fHoş geldiniz! §c" + playerName);
                player.sendMessage("§c║");
                player.sendMessage("§c║  §cGiriş yapmak için:");
                player.sendMessage("§c║  §f/login <şifre>");
                player.sendMessage("§c║");
                player.sendMessage("§c╚════════════════════════════════════════╝");
                player.sendMessage("§c════════════════════════════════════════");
            } else {
                player.sendMessage("§c════════════════════════════════════════");
                player.sendMessage("§c╔════════════ LunexVanilla ════════════╗");
                player.sendMessage("§c║");
                player.sendMessage("§c║  §fHoş geldiniz! §c" + playerName);
                player.sendMessage("§c║");
                player.sendMessage("§c║  §cKayıt olmak için:");
                player.sendMessage("§c║  §f/register <şifre> <şifre>");
                player.sendMessage("§c║");
                player.sendMessage("§c╚════════════════════════════════════════╝");
                player.sendMessage("§c════════════════════════════════════════");
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getAuthManager().logout(player);
        event.setQuitMessage("§c[LunexVanilla] §f" + player.getName() + " §cayrıldı!");
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getAuthManager().isAuthenticated(player)) {
            Location from = event.getFrom();
            event.setTo(from);
            player.sendMessage("§cGiriş yapmak için §f/login <şifre> §cyaznız!");
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getAuthManager().isAuthenticated(player)) {
            event.setCancelled(true);
            player.sendMessage("§cÖnce giriş yapmanız gerekir!");
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getAuthManager().isAuthenticated(player)) {
            event.setCancelled(true);
            player.sendMessage("§cÖnce giriş yapmanız gerekir!");
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getAuthManager().isAuthenticated(player)) {
            event.setCancelled(true);
            player.sendMessage("§cÖnce giriş yapmanız gerekir!");
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            
            if (!plugin.getAuthManager().isAuthenticated(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            
            if (!plugin.getAuthManager().isAuthenticated(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    private Location getLobbySpawnPoint() {
        org.bukkit.World world = plugin.getLobbyWorld();
        if (world != null) {
            return new Location(world, 0.5, 64, 0.5, 0, 0);
        }
        return null;
    }
}

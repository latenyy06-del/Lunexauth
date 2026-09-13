package com.lunex.lauth;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.lunex.lauth.listeners.PlayerListener;
import com.lunex.lauth.commands.LoginCommand;
import com.lunex.lauth.commands.RegisterCommand;
import com.lunex.lauth.managers.AuthManager;

public class LauthPlugin extends JavaPlugin {
    
    private static LauthPlugin instance;
    private AuthManager authManager;
    private World lobbyWorld;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Config oluştur
        saveDefaultConfig();
        
        // AuthManager başlat
        authManager = new AuthManager(this);
        
        // Event listener'ları kaydet
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        
        // Komutları kaydet
        getCommand("login").setExecutor(new LoginCommand(this));
        getCommand("register").setExecutor(new RegisterCommand(this));
        
        // Lobby dünyasını oluştur/yükle
        createLobbyWorld();
        
        getLogger().info("§c[LunexVanilla] §flauth plugini aktif edildi!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("§c[LunexVanilla] §flauth plugini devre dışı bırakıldı!");
    }
    
    private void createLobbyWorld() {
        String worldName = getConfig().getString("lobby-world", "lauth_lobby");
        World world = Bukkit.getWorld(worldName);
        
        if (world == null) {
            getLogger().info("Lobby dünyası oluşturuluyor...");
            // Yeni dünya oluştur
            world = Bukkit.getWorldCreator(worldName)
                    .environment(World.Environment.NORMAL)
                    .createWorld();
            
            if (world != null) {
                world.setAutoSave(false);
                world.setGameRuleValue("showDeathMessages", "false");
                world.setGameRuleValue("pvp", "false");
                world.setGameRuleValue("doMobSpawning", "false");
                getLogger().info("Lobby dünyası oluşturuldu: " + worldName);
            }
        }
        
        lobbyWorld = world;
    }
    
    public static LauthPlugin getInstance() {
        return instance;
    }
    
    public AuthManager getAuthManager() {
        return authManager;
    }
    
    public World getLobbyWorld() {
        return lobbyWorld;
    }
}

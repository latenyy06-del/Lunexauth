package com.lunex.lauth.managers;

import com.lunex.lauth.LauthPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AuthManager {
    
    private LauthPlugin plugin;
    private File authFile;
    private FileConfiguration authConfig;
    private Map<String, Long> loginAttempts;
    private Set<String> authenticatedPlayers;
    
    public AuthManager(LauthPlugin plugin) {
        this.plugin = plugin;
        this.loginAttempts = new HashMap<>();
        this.authenticatedPlayers = new HashSet<>();
        initializeAuthFile();
    }
    
    private void initializeAuthFile() {
        authFile = new File(plugin.getDataFolder(), "users.yml");
        
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        if (!authFile.exists()) {
            try {
                authFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("users.yml dosyası oluşturulamadı!");
                e.printStackTrace();
            }
        }
        
        authConfig = YamlConfiguration.loadConfiguration(authFile);
    }
    
    public boolean isRegistered(String playerName) {
        reloadConfig();
        return authConfig.contains("users." + playerName);
    }
    
    public boolean isAuthenticated(Player player) {
        return authenticatedPlayers.contains(player.getUniqueId().toString());
    }
    
    public boolean register(String playerName, String password) {
        if (isRegistered(playerName)) {
            return false;
        }
        
        reloadConfig();
        String hashedPassword = hashPassword(password);
        authConfig.set("users." + playerName + ".password", hashedPassword);
        authConfig.set("users." + playerName + ".registered-date", System.currentTimeMillis());
        
        try {
            authConfig.save(authFile);
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("Kayıt verisi kaydedilemedi: " + playerName);
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean login(String playerName, String password) {
        // Brute force koruması
        if (isRateLimited(playerName)) {
            return false;
        }
        
        reloadConfig();
        
        if (!isRegistered(playerName)) {
            recordLoginAttempt(playerName);
            return false;
        }
        
        String storedHash = authConfig.getString("users." + playerName + ".password");
        String providedHash = hashPassword(password);
        
        if (storedHash.equals(providedHash)) {
            return true;
        } else {
            recordLoginAttempt(playerName);
            return false;
        }
    }
    
    public void setAuthenticated(Player player) {
        authenticatedPlayers.add(player.getUniqueId().toString());
        loginAttempts.remove(player.getName().toLowerCase());
    }
    
    public void logout(Player player) {
        authenticatedPlayers.remove(player.getUniqueId().toString());
    }
    
    private void recordLoginAttempt(String playerName) {
        long currentTime = System.currentTimeMillis();
        loginAttempts.put(playerName.toLowerCase(), currentTime);
    }
    
    private boolean isRateLimited(String playerName) {
        String key = playerName.toLowerCase();
        if (!loginAttempts.containsKey(key)) {
            return false;
        }
        
        long lastAttempt = loginAttempts.get(key);
        long timeDiff = System.currentTimeMillis() - lastAttempt;
        
        // 30 saniye bekleme süresi
        return timeDiff < 30000;
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(password.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void reloadConfig() {
        authConfig = YamlConfiguration.loadConfiguration(authFile);
    }
    
    public void saveConfig() {
        try {
            authConfig.save(authFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Yapılandırma kaydedilemedi!");
            e.printStackTrace();
        }
    }
}

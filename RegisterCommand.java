package com.lunex.lauth.commands;

import com.lunex.lauth.LauthPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {
    
    private LauthPlugin plugin;
    
    public RegisterCommand(LauthPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cBu komut sadece oyuncular tarafından kullanılabilir!");
            return true;
        }
        
        Player player = (Player) sender;
        String playerName = player.getName();
        
        if (args.length != 2) {
            player.sendMessage("§c❌ Kullanım: /register <şifre> <şifre-tekrar>");
            return true;
        }
        
        // Zaten kayıtlı mı?
        if (plugin.getAuthManager().isRegistered(playerName)) {
            player.sendMessage("§c❌ Bu kullanıcı adı zaten kayıtlı!");
            return true;
        }
        
        // Zaten giriş yapmışsa
        if (plugin.getAuthManager().isAuthenticated(player)) {
            player.sendMessage("§c❌ Zaten giriş yapmışsınız!");
            return true;
        }
        
        String password1 = args[0];
        String password2 = args[1];
        
        // Şifre kontrolü
        if (!password1.equals(password2)) {
            player.sendMessage("§c════════════════════════════════════════");
            player.sendMessage("§c╔════════════ LunexVanilla ════════════╗");
            player.sendMessage("§c║");
            player.sendMessage("§c║  ❌ §fŞifreler eşleşmiyor!");
            player.sendMessage("§c║");
            player.sendMessage("§c║  §cLütfen aynı şifreyi yazınız.");
            player.sendMessage("§c║");
            player.sendMessage("§c╚════════════════════════════════════════╝");
            player.sendMessage("§c════════════════════════════════════════");
            return true;
        }
        
        // Şifre uzunluğu kontrolü
        if (password1.length() < 3) {
            player.sendMessage("§c❌ Şifre en az 3 karakter olmalıdır!");
            return true;
        }
        
        if (password1.length() > 32) {
            player.sendMessage("§c❌ Şifre en fazla 32 karakter olabilir!");
            return true;
        }
        
        // Kayıt yap
        if (plugin.getAuthManager().register(playerName, password1)) {
            plugin.getAuthManager().setAuthenticated(player);
            
            // Spawn noktasına TP et
            Location spawn = Bukkit.getWorld("world").getSpawnLocation();
            player.teleport(spawn);
            
            // Mesajları gönder
            player.sendMessage("§c════════════════════════════════════════");
            player.sendMessage("§c╔════════════ LunexVanilla ════════════╗");
            player.sendMessage("§c║");
            player.sendMessage("§c║  ✓ §fKayıt başarılı!");
            player.sendMessage("§c║");
            player.sendMessage("§c║  §fHoş geldiniz, §c" + playerName);
            player.sendMessage("§c║");
            player.sendMessage("§c╚════════════════════════════════════════╝");
            player.sendMessage("§c════════════════════════════════════════");
            
            // Broadcast
            Bukkit.broadcastMessage("§c[LunexVanilla] §f" + playerName + " §ckayıt oldu!");
            
        } else {
            player.sendMessage("§c❌ Kayıt sırasında hata oluştu!");
        }
        
        return true;
    }
}

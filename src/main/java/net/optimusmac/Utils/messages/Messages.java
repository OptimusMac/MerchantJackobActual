package net.optimusmac.Utils.messages;

import net.optimusmac.Merchant;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class Messages {
    public static void writeAll(String message){
        CompletableFuture.runAsync(() -> Bukkit.getOnlinePlayers().forEach(e -> e.sendMessage(alternate(message))));
    }
    public static String alternate(String t){
        return ChatColor.translateAlternateColorCodes('&', t);
    }
    public static void sendMessageDepositMoney(Player player, int money, int amount){
        player.sendMessage(alternate(String.format(Merchant.getInstance().getMessageDepositMoney(), money, amount)));
    }
}

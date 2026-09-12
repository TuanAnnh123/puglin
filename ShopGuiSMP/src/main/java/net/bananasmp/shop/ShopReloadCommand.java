package net.bananasmp.shop;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShopReloadCommand implements CommandExecutor {

    private final ShopManager shopManager;

    public ShopReloadCommand(ShopManager shopManager) {
        this.shopManager = shopManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        shopManager.load();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aĐã tải lại cấu hình shop!"));
        return true;
    }
}

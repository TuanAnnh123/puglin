package net.bananasmp.shop;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.Locale;

public class SellCommand implements CommandExecutor {

    private final ShopManager shopManager;

    public SellCommand(ShopManager shopManager) {
        this.shopManager = shopManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Lệnh này chỉ dùng được trong game.");
            return true;
        }

        Economy econ = EconomyHook.get();
        if (econ == null) {
            player.sendMessage(color("&cLỗi: chưa kết nối được hệ thống kinh tế (Vault)."));
            return true;
        }

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType().isAir()) {
            player.sendMessage(color("&cBạn phải cầm vật phẩm muốn bán trên tay!"));
            return true;
        }

        ShopItem shopItem = shopManager.findMatch(hand);
        if (shopItem == null) {
            player.sendMessage(color("&cVật phẩm này không thể bán tại shop!"));
            return true;
        }

        double shopPricePerSingle = shopItem.getPricePerSingle();
        double sellPricePerSingle = shopPricePerSingle;

        if (args.length >= 1) {
            double customPrice;
            try {
                customPrice = Double.parseDouble(args[0]);
            } catch (NumberFormatException ex) {
                player.sendMessage(color("&cGiá không hợp lệ!"));
                return true;
            }
            if (customPrice <= 0) {
                player.sendMessage(color("&cGiá phải lớn hơn 0!"));
                return true;
            }
            if (customPrice > shopPricePerSingle) {
                player.sendMessage(color("&cBạn không được tự đặt giá cao hơn giá shop! (Tối đa &e"
                        + format(shopPricePerSingle) + "$&c / cái)"));
                return true;
            }
            sellPricePerSingle = customPrice;
        }

        int amount = hand.getAmount();
        double total = sellPricePerSingle * amount;

        player.getInventory().setItemInMainHand(null);
        econ.depositPlayer(player, total);
        player.sendMessage(color("&aĐã bán &e" + amount + "x " + ChatColor.stripColor(shopItem.getDisplayName())
                + " &avới giá &e" + format(total) + "$"));
        return true;
    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private String format(double value) {
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }
}

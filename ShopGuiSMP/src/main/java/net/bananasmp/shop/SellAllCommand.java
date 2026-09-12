package net.bananasmp.shop;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.text.NumberFormat;
import java.util.Locale;

public class SellAllCommand implements CommandExecutor {

    private final ShopManager shopManager;

    public SellAllCommand(ShopManager shopManager) {
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

        PlayerInventory inv = player.getInventory();
        double total = 0;
        int totalItems = 0;

        // chi quet 36 o chinh (0-35), khong dong armor/offhand
        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = inv.getItem(slot);
            if (stack == null || stack.getType().isAir()) continue;

            ShopItem shopItem = shopManager.findMatch(stack);
            if (shopItem == null) continue;

            double value = shopItem.getPricePerSingle() * stack.getAmount();
            total += value;
            totalItems += stack.getAmount();
            inv.setItem(slot, null);
        }

        if (totalItems == 0) {
            player.sendMessage(color("&cKhông có vật phẩm nào trong kho đồ có thể bán!"));
            return true;
        }

        econ.depositPlayer(player, total);
        player.sendMessage(color("&aĐã bán tổng cộng &e" + totalItems + " &avật phẩm, nhận được &e"
                + format(total) + "$"));
        return true;
    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private String format(double value) {
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }
}

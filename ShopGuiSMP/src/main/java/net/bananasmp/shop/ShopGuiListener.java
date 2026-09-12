package net.bananasmp.shop;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ShopGuiListener implements Listener {

    private final ShopGuiSMP plugin;
    private final ShopManager shopManager;
    private final ShopGui shopGui;

    public ShopGuiListener(ShopGuiSMP plugin, ShopManager shopManager, ShopGui shopGui) {
        this.plugin = plugin;
        this.shopManager = shopManager;
        this.shopGui = shopGui;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory topInv = e.getView().getTopInventory();
        InventoryHolder holder = topInv.getHolder();

        if (holder instanceof MenuHolders.MainMenuHolder) {
            e.setCancelled(true);
            handleMainMenuClick(e);
        } else if (holder instanceof MenuHolders.CategoryMenuHolder catHolder) {
            e.setCancelled(true);
            handleCategoryClick(e, catHolder.getCategory());
        }
    }

    private void handleMainMenuClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || e.getClickedInventory() != e.getView().getTopInventory()) return;
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        Player player = (Player) e.getWhoClicked();

        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        for (ShopCategory category : ShopCategory.values()) {
            if (clicked.getType() == category.getIcon()) {
                shopGui.openCategory(player, category);
                return;
            }
        }
    }

    private void handleCategoryClick(InventoryClickEvent e, ShopCategory category) {
        if (e.getClickedInventory() == null || e.getClickedInventory() != e.getView().getTopInventory()) return;
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        Player player = (Player) e.getWhoClicked();

        if (clicked.getType() == Material.ARROW) {
            shopGui.openMain(player);
            return;
        }

        ShopItem matched = findClickedShopItem(category, clicked);
        if (matched == null) return;

        int multiplier = e.isShiftClick() ? 10 : 1;
        buy(player, matched, multiplier);
    }

    private ShopItem findClickedShopItem(ShopCategory category, ItemStack clicked) {
        for (ShopItem item : shopManager.getByCategory(category)) {
            ItemStack built = shopManager.buildItemStack(item, item.getUnitSize());
            ItemMeta builtMeta = built.getItemMeta();
            ItemMeta clickedMeta = clicked.getItemMeta();
            if (built.getType() != clicked.getType()) continue;
            if (builtMeta != null && clickedMeta != null
                    && builtMeta.getDisplayName().equals(clickedMeta.getDisplayName())) {
                return item;
            }
        }
        return null;
    }

    private void buy(Player player, ShopItem item, int multiplier) {
        Economy econ = EconomyHook.get();
        if (econ == null) {
            player.sendMessage(color("&cLỗi: chưa kết nối được hệ thống kinh tế (Vault)."));
            return;
        }

        double totalPrice = item.getPrice() * multiplier;
        int totalAmount = item.getUnitSize() * multiplier;

        if (econ.getBalance(player) < totalPrice) {
            player.sendMessage(color("&cBạn không đủ tiền! Cần &e" + format(totalPrice) + "$"));
            return;
        }

        // kiem tra du cho trong tui do
        ItemStack toGive = shopManager.buildItemStack(item, totalAmount);
        java.util.Map<Integer, ItemStack> leftover = player.getInventory().addItem(toGive.clone());
        if (!leftover.isEmpty()) {
            // hoan lai neu khong the them (do da cho di 1 phan) -> xoa phan da them va bao loi
            for (ItemStack given : leftover.values()) {
                player.getInventory().removeItem(given);
            }
            player.sendMessage(color("&cTúi đồ của bạn không đủ chỗ trống!"));
            return;
        }

        econ.withdrawPlayer(player, totalPrice);
        player.sendMessage(color("&aĐã mua &e" + totalAmount + "x " + ChatColor.stripColor(item.getDisplayName())
                + " &avới giá &e" + format(totalPrice) + "$"));
    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private String format(double value) {
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }
}

package net.bananasmp.shop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ShopGui {

    private final ShopGuiSMP plugin;
    private final ShopManager shopManager;

    public ShopGui(ShopGuiSMP plugin, ShopManager shopManager) {
        this.plugin = plugin;
        this.shopManager = shopManager;
    }

    public void openMain(Player player) {
        MenuHolders.MainMenuHolder holder = new MenuHolders.MainMenuHolder();
        String title = ShopManager.color(plugin.getConfig().getString("gui-title", "&6&l🍌 BANANASMP SHOP"));
        Inventory inv = Bukkit.createInventory(holder, 54, title);
        holder.setInventory(inv);

        inv.setItem(10, categoryIcon(ShopCategory.BLOCKS));
        inv.setItem(12, categoryIcon(ShopCategory.ORES));
        inv.setItem(14, categoryIcon(ShopCategory.FOOD));
        inv.setItem(19, categoryIcon(ShopCategory.GEAR));
        inv.setItem(21, categoryIcon(ShopCategory.COMBAT));
        inv.setItem(23, categoryIcon(ShopCategory.FARM));

        fillBorder(inv);
        inv.setItem(49, closeButton());

        player.openInventory(inv);
    }

    public void openCategory(Player player, ShopCategory category) {
        MenuHolders.CategoryMenuHolder holder = new MenuHolders.CategoryMenuHolder(category);
        String title = ShopManager.color("&8" + category.getDisplayName());
        Inventory inv = Bukkit.createInventory(holder, 54, title);
        holder.setInventory(inv);

        List<ShopItem> items = shopManager.getByCategory(category);
        int slot = 0;
        for (ShopItem item : items) {
            if (slot >= 45) break; // 5 hang dau danh cho item, hang cuoi la dieu huong
            inv.setItem(slot, buildDisplayItem(item));
            slot++;
        }

        inv.setItem(49, backButton());
        player.openInventory(inv);
    }

    private ItemStack buildDisplayItem(ShopItem item) {
        ItemStack stack = shopManager.buildItemStack(item, Math.max(1, item.getUnitSize()));
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            java.util.List<String> lore = new java.util.ArrayList<>();
            lore.add(ShopManager.color("&7Giá: &a" + format(item.getPrice()) + "$ &7/ " + item.getUnitSize() + " cái"));
            if (item.getUnitSize() == 1) {
                lore.set(0, ShopManager.color("&7Giá: &a" + format(item.getPrice()) + "$ &7/ cái"));
            }
            lore.add("");
            lore.add(ShopManager.color("&eClick trái: &fMua 1 lần (" + item.getUnitSize() + " cái)"));
            lore.add(ShopManager.color("&eShift + Click trái: &fMua x10"));
            meta.setLore(lore);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private ItemStack categoryIcon(ShopCategory category) {
        ItemStack stack = new ItemStack(category.getIcon());
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ShopManager.color("&e&l" + category.getDisplayName()));
            meta.setLore(List.of(ShopManager.color("&7Click để xem các mặt hàng")));
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private ItemStack closeButton() {
        ItemStack stack = new ItemStack(Material.BARRIER);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ShopManager.color("&c&l❌ ĐÓNG"));
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private ItemStack backButton() {
        ItemStack stack = new ItemStack(Material.ARROW);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ShopManager.color("&e&l⬅ QUAY LẠI"));
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private void fillBorder(Inventory inv) {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            filler.setItemMeta(meta);
        }
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                // chi vien nhe hang tren/duoi
                if (i < 9 || i >= 45) {
                    inv.setItem(i, filler);
                }
            }
        }
    }

    private String format(double value) {
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }
}

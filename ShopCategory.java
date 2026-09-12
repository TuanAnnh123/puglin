package net.bananasmp.shop;

import org.bukkit.Material;

public enum ShopCategory {

    BLOCKS("⛏ BLOCKS", Material.BRICKS),
    ORES("💎 ORES", Material.DIAMOND_ORE),
    FOOD("🍎 FOOD", Material.COOKED_BEEF),
    GEAR("🛠 GEAR", Material.NETHERITE_PICKAXE),
    COMBAT("⚔ COMBAT", Material.DIAMOND_SWORD),
    FARM("🌾 FARM", Material.WHEAT);

    private final String displayName;
    private final Material icon;

    ShopCategory(String displayName, Material icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIcon() {
        return icon;
    }
}

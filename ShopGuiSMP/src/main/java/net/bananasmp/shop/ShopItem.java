package net.bananasmp.shop;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.Collections;
import java.util.Map;

public class ShopItem {

    private final String id;
    private final Material material;
    private final ShopCategory category;
    private final String displayName;
    private final double price;
    private final int unitSize;
    private final boolean sellable;
    private final Map<Enchantment, Integer> enchantments;
    private final String potionType; // nullable

    public ShopItem(String id, Material material, ShopCategory category, String displayName,
                     double price, int unitSize, boolean sellable,
                     Map<Enchantment, Integer> enchantments, String potionType) {
        this.id = id;
        this.material = material;
        this.category = category;
        this.displayName = displayName;
        this.price = price;
        this.unitSize = Math.max(1, unitSize);
        this.sellable = sellable;
        this.enchantments = enchantments == null ? Collections.emptyMap() : enchantments;
        this.potionType = potionType;
    }

    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public ShopCategory getCategory() {
        return category;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Giá cho 1 lần mua/bán = unitSize vật phẩm. */
    public double getPrice() {
        return price;
    }

    public int getUnitSize() {
        return unitSize;
    }

    public boolean isSellable() {
        return sellable;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public String getPotionType() {
        return potionType;
    }

    /** Giá quy đổi cho 1 vật phẩm đơn lẻ (dùng khi bán số lượng bất kỳ). */
    public double getPricePerSingle() {
        return price / (double) unitSize;
    }
}

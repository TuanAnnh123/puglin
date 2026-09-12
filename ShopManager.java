package net.bananasmp.shop;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.*;

public class ShopManager {

    private final ShopGuiSMP plugin;
    private final Map<String, ShopItem> itemsById = new LinkedHashMap<>();
    private final Map<ShopCategory, List<ShopItem>> byCategory = new EnumMap<>(ShopCategory.class);

    public ShopManager(ShopGuiSMP plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        itemsById.clear();
        byCategory.clear();
        for (ShopCategory cat : ShopCategory.values()) {
            byCategory.put(cat, new ArrayList<>());
        }

        plugin.reloadConfig();
        ConfigurationSection itemsSection = plugin.getConfig().getConfigurationSection("items");
        if (itemsSection == null) return;

        for (String id : itemsSection.getKeys(false)) {
            ConfigurationSection sec = itemsSection.getConfigurationSection(id);
            if (sec == null) continue;

            try {
                Material material = Material.matchMaterial(sec.getString("material", ""));
                if (material == null) {
                    plugin.getLogger().warning("Bo qua item '" + id + "': material khong hop le.");
                    continue;
                }
                ShopCategory category = ShopCategory.valueOf(sec.getString("category", "BLOCKS").toUpperCase());
                String displayName = sec.getString("display-name", id);
                double price = sec.getDouble("price", 0);
                int unitSize = sec.getInt("unit-size", 1);
                boolean sellable = sec.getBoolean("sellable", true);
                String potionType = sec.getString("potion-type", null);

                Map<Enchantment, Integer> enchants = new HashMap<>();
                ConfigurationSection enchSec = sec.getConfigurationSection("enchantments");
                if (enchSec != null) {
                    for (String enchName : enchSec.getKeys(false)) {
                        Enchantment ench = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchName.toLowerCase()));
                        if (ench == null) {
                            plugin.getLogger().warning("Enchant '" + enchName + "' khong ton tai (item " + id + ")");
                            continue;
                        }
                        enchants.put(ench, enchSec.getInt(enchName));
                    }
                }

                ShopItem shopItem = new ShopItem(id, material, category, displayName, price,
                        unitSize, sellable, enchants, potionType);
                itemsById.put(id, shopItem);
                byCategory.get(category).add(shopItem);
            } catch (Exception ex) {
                plugin.getLogger().warning("Loi doc item '" + id + "': " + ex.getMessage());
            }
        }
    }

    public List<ShopItem> getByCategory(ShopCategory category) {
        return byCategory.getOrDefault(category, Collections.emptyList());
    }

    public Collection<ShopItem> getAllItems() {
        return itemsById.values();
    }

    public ShopItem getById(String id) {
        return itemsById.get(id);
    }

    /**
     * Tim ShopItem khop voi 1 ItemStack (dung khi ban).
     * Uu tien khop chinh xac enchant, sau do khop item khong yeu cau enchant.
     */
    public ShopItem findMatch(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) return null;

        ShopItem best = null;
        for (ShopItem item : itemsById.values()) {
            if (item.getMaterial() != stack.getType()) continue;
            if (!item.isSellable()) continue;

            if (item.getPotionType() != null) {
                if (!matchesPotion(stack, item.getPotionType())) continue;
            }

            if (!item.getEnchantments().isEmpty()) {
                if (matchesEnchants(stack, item.getEnchantments())) {
                    return item; // khop chinh xac -> uu tien tra ve ngay
                }
            } else {
                // item khong yeu cau enchant -> chi khop neu stack cung khong co enchant dac biet
                // (tranh viec ban nham vu khi da enchant voi gia thuong)
                if (best == null) {
                    best = item;
                }
            }
        }
        return best;
    }

    private boolean matchesEnchants(ItemStack stack, Map<Enchantment, Integer> required) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return false;
        Map<Enchantment, Integer> actual = meta.getEnchants();
        if (actual.size() != required.size()) return false;
        for (Map.Entry<Enchantment, Integer> e : required.entrySet()) {
            Integer lvl = actual.get(e.getKey());
            if (lvl == null || !lvl.equals(e.getValue())) return false;
        }
        return true;
    }

    private boolean matchesPotion(ItemStack stack, String potionTypeName) {
        ItemMeta meta = stack.getItemMeta();
        if (!(meta instanceof PotionMeta potionMeta)) return false;
        try {
            PotionType wanted = PotionType.valueOf(potionTypeName.toUpperCase());
            return potionMeta.getBasePotionType() == wanted;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    /** Tao ItemStack de dua vao GUI hoac giao cho nguoi choi khi mua. */
    public ItemStack buildItemStack(ShopItem item, int amount) {
        ItemStack stack = new ItemStack(item.getMaterial(), amount);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color(item.getDisplayName()));
            for (Map.Entry<Enchantment, Integer> e : item.getEnchantments().entrySet()) {
                meta.addEnchant(e.getKey(), e.getValue(), true);
            }
            if (item.getPotionType() != null && meta instanceof PotionMeta potionMeta) {
                try {
                    potionMeta.setBasePotionType(PotionType.valueOf(item.getPotionType().toUpperCase()));
                } catch (IllegalArgumentException ignored) {
                }
            }
            stack.setItemMeta(meta);
        }
        return stack;
    }

    public static String color(String s) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', s);
    }
}

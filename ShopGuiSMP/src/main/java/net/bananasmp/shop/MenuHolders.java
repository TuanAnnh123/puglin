package net.bananasmp.shop;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MenuHolders {

    public static class MainMenuHolder implements InventoryHolder {
        private Inventory inventory;
        @Override
        public Inventory getInventory() {
            return inventory;
        }
        public void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }
    }

    public static class CategoryMenuHolder implements InventoryHolder {
        private Inventory inventory;
        private final ShopCategory category;

        public CategoryMenuHolder(ShopCategory category) {
            this.category = category;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }

        public void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }

        public ShopCategory getCategory() {
            return category;
        }
    }
}

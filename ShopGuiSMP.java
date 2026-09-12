package net.bananasmp.shop;

import org.bukkit.plugin.java.JavaPlugin;

public class ShopGuiSMP extends JavaPlugin {

    private ShopManager shopManager;
    private ShopGui shopGui;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        EconomyHook.init(this);
        if (!EconomyHook.isReady()) {
            getLogger().warning("Chua tim thay plugin kinh te (Vault Economy) ngay luc bat.");
            getLogger().warning("Neu ban da cai EssentialsX/CMI... thi khong sao, plugin se tu dong");
            getLogger().warning("tim lai kinh te moi khi co nguoi dung /shop, /sell hoac /sellall.");
        }

        this.shopManager = new ShopManager(this);
        this.shopGui = new ShopGui(this, shopManager);

        getServer().getPluginManager().registerEvents(
                new ShopGuiListener(this, shopManager, shopGui), this);

        getCommand("shop").setExecutor(new ShopCommand(shopGui));
        getCommand("sell").setExecutor(new SellCommand(shopManager));
        getCommand("sellall").setExecutor(new SellAllCommand(shopManager));
        getCommand("shopreload").setExecutor(new ShopReloadCommand(shopManager));

        getLogger().info("BananaSMP ShopGUI da duoc bat!");
    }

    @Override
    public void onDisable() {
        getLogger().info("BananaSMP ShopGUI da tat.");
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public ShopGui getShopGui() {
        return shopGui;
    }
}

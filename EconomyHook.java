package net.bananasmp.shop;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyHook {

    private static Economy economy;
    private static ShopGuiSMP plugin;

    /** Goi 1 lan luc onEnable, chi luu tham chieu plugin, KHONG bat buoc kinh te phai san sang ngay. */
    public static void init(ShopGuiSMP p) {
        plugin = p;
    }

    /**
     * Lay Economy hien tai. Neu chua co (do plugin kinh te nap sau ShopGuiSMP)
     * thi thu do tim lai moi lan goi, tranh loi do thu tu nap plugin.
     */
    public static Economy get() {
        if (economy != null) return economy;
        if (plugin == null) return null;
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) return null;

        RegisteredServiceProvider<Economy> rsp = plugin.getServer()
                .getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return null;

        economy = rsp.getProvider();
        return economy;
    }

    /** True neu da co the lay duoc kinh te ngay luc goi. */
    public static boolean isReady() {
        return get() != null;
    }
}

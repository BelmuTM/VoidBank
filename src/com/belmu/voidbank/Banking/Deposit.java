package com.belmu.voidbank.Banking;

import com.belmu.voidbank.Main;
import com.belmu.voidbank.Utils.NumberUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Deposit implements Listener {

    private static String path = "Item_values";
    private static String file_name = "deposit_item_values.yml";

    private static Plugin plugin;

    public Deposit(Plugin plugin){
        this.plugin = plugin;
        initConfig();

        initDepositValues();
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    static FileConfiguration config;
    static File file;

    public static void initConfig(){

        File f = new File("plugins/" + Main.pluginName);

        if(!f.exists()) f.mkdirs();
        file = new File(f, file_name);

        if(!file.exists()) {
            try {
                file.createNewFile();

            } catch (IOException ioe) { ioe.printStackTrace();}

        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public static void saveConfig(){
        try { config.save(file); } catch (IOException ioe) { ioe.printStackTrace(); }
    }

    public static void set(String sectionPath, Object object) {
        String mainPath = path + sectionPath;
        getConfig().set(mainPath, object);
        saveConfig();
    }

    public static void initDepositValues() {

        if(getConfig().get(path) == null) set(".STONE", 5);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {

        Player p = e.getEntity();
        List<ItemStack> inventory = getAllItems(p);
        registerAll(inventory);

        if (p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.VOID) {

            if(p.hasPermission("voidbank.player.deposit")) {

                if(!Interest.isRegistered(p)) Interest.registerPlayer(p);

                double amount = getTotalCost(inventory);
                double purse = VoidBank.getPurse(p);

                if(purse > 0) {

                    amount += purse;
                    VoidBank.removePurse(p, purse);
                }

                if (amount <= 0) return;

                VoidBank.addBalance(p, amount);

                String bc = "§7[" + BankMenu.getFormattedBalance(p, false) + "]";

                p.sendMessage(Main.bankPrefix + "§fYou earned §6" + NumberUtil.withSuffix(amount) + " coins§f from your §ddeath§f. " + bc);
                p.playSound(p.getLocation(), Main.success, Main.successPitch, Integer.MAX_VALUE);

            }

        }

    }

    public static Double getTotalCost(List<ItemStack> items) {

        double totalCost = 0;

        if(!items.isEmpty()) {

            for (String key : getConfig().getKeys(true)) {

                key = key.toUpperCase().replaceAll(path.toUpperCase() + ".", "");
                for (ItemStack item : items) {

                    if(item != null) {
                        String itemMaterial = item.getType().toString().toUpperCase();

                        if (itemMaterial.equals(key)) {

                            try {

                                double itemCost = getConfig().getDouble(path + "." + key);
                                if (itemCost >= 1) {

                                    if (item.getAmount() > 1) itemCost += (itemCost * item.getAmount());

                                    totalCost += itemCost;
                                }

                            } catch(NumberFormatException nfe) {

                                System.out.println("ERROR in '" + file_name + "': a numeral value is incorrect.");
                            }

                        }

                    }

                }

            }

        }

        return totalCost;
    }

    public static List<ItemStack> getAllItems(Player player) {
        List<ItemStack> allItems = new ArrayList<>();

        for(ItemStack item : player.getInventory().getContents()) {

            if(item != null) {

                allItems.add(item);
            }

        }

        for(ItemStack item : player.getInventory().getArmorContents()) {

            if(item != null) {

                allItems.add(item);
            }

        }

        return allItems;
    }

    public static void registerAll(List<ItemStack> items) {

        if(!items.isEmpty()) {

            for (ItemStack item : items) {

                if(item != null) {

                    String name = item.getType().toString().toUpperCase();

                    if(!getConfig().contains(path + "." + name)){

                        getConfig().set(path + "." + name, 0);
                        saveConfig();
                    }

                }

            }

        }

    }

}

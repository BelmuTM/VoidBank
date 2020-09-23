package com.belmu.voidbank.Banking;

import com.belmu.voidbank.Main;
import com.belmu.voidbank.Utils.NumberUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("ALL")
public class VoidBank {

    static String path = "Bank.";
    private static String file_name = "player_banking.yml";

    static String name = ".name";
    static String purse = ".purse";
    static String balance = ".balance";
    static String interest = ".interest_percentage";
    static String limit = ".bank_limit";
    static String tier = ".bank_tier";

    static String initiated = ".initiated";

    private static Plugin plugin;

    public VoidBank(Plugin plugin){
        this.plugin = plugin;
        initConfig();
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

    public static boolean isInitiated(Player player) {
        if(getConfig().getBoolean(path + player.getUniqueId() + initiated) == true) { return true; } else { return false; }
    }

    public static void initPlayer(Player player) {

        String uuid = player.getUniqueId().toString();
        if(VoidBank.isInitiated(player)) return;

        set(uuid + name, player.getName());

        set(uuid + purse, 0);
        set(uuid + balance, 0);

        set(uuid + interest, Main.defaultInt);
        set(uuid + limit, Main.defaultLim);
        set(uuid + tier, 1);

        set(uuid + initiated, true);
    }

    public static void removeBalance(Player player, Double coins) {

        double balance = getBalance(player);

        if(balance < coins) throw new IllegalArgumentException();

        double minus = balance - coins;
        setBalance(player, minus);
    }

    public static void addBalance(Player player, Double coins) {
        double balance = getBalance(player);
        double add = balance + coins;
        setBalance(player, add);
    }

    public static void setBalance(Player player, Double coins) {

        String uuid = player.getUniqueId().toString();
        Double count = NumberUtil.formatDouble(coins);

        if(count <= getLimit(player)) { set(uuid + balance, count); }
        else { set(uuid + balance, getLimit(player)); }
    }

    public static Double getBalance(Player player) {

        String uuid = player.getUniqueId().toString();
        return getConfig().getDouble(path + uuid + balance);
    }

    public static Double getInterest(Player player) {

        String uuid = player.getUniqueId().toString();
        return getConfig().getDouble(path + uuid + interest);
    }

    public static void setInterest(Player player, Double interestValue) {

        String uuid = player.getUniqueId().toString();
        set(uuid + interest, interestValue);
    }

    public static Double calculateInterest(Player player) {

        double interest = getInterest(player);
        double balance = getBalance(player);

        return (balance * (interest / 100.0f));
    }

    public static void applyInterest(Player player) {

        double finalValue = calculateInterest(player) + getBalance(player);
        if(finalValue > 0) { setBalance(player, finalValue); }
    }

    public static Double getLimit(Player player) {

        String uuid = player.getUniqueId().toString();
        return getConfig().getDouble(path + uuid + limit);
    }

    public static void setLimit(Player player, Double limitValue) {

        String uuid = player.getUniqueId().toString();
        Double count = NumberUtil.formatDouble(limitValue);
        set(uuid + limit, count);
    }

    public static void removePurse(Player player, Double coins) {

        double purse = getPurse(player);

        if(purse < coins) throw new IllegalArgumentException();

        double minus = purse - coins;
        setPurse(player, minus);
    }

    public static void addPurse(Player player, Double coins) {
        double purse = getPurse(player);
        double add = purse + coins;
        setPurse(player, add);
    }

    public static void setPurse(Player player, Double coins) {

        String uuid = player.getUniqueId().toString();
        Double count = NumberUtil.formatDouble(coins);
        set(uuid + purse, count);
    }

    public static Double getPurse(Player player) {

        String uuid = player.getUniqueId().toString();
        return getConfig().getDouble(path + uuid + purse);
    }

    public static void setTier(Player player, int tierValue) {

        String uuid = player.getUniqueId().toString();
        set(uuid + tier, tierValue);
    }

    public static int getTier(Player player) {

        String uuid = player.getUniqueId().toString();
        return getConfig().getInt(path + uuid + tier);
    }

}

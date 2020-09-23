package com.belmu.voidbank;

import com.belmu.voidbank.Banking.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("ALL")
public class Main extends JavaPlugin implements Listener {

    ////////////////////////////////////////
    //                                    //
    //       *Plugin developed by*        //
    //        *Twitter : @Belmu_*         //
    //                                    //
    ////////////////////////////////////////

    /*

        Don't ever try to steal my plugins you fool ʕ•ᴥ•ʔ

     */

    public static String pluginName = "VoidBank";

    private static Main instance;
    public static Main getInstance() {
        return instance;
    }

    public static String bankPrefix = "§7[§dVoidBank§7]§5 •§7 ";

    //CONFIG PATH
    public static String interestPercent = "interest_upgrade_percentage";
    public static String limitPercent = "bank_limit_upgrade_percentage";
    public static String defaultInterest = "default_interest";
    public static String defaultLimit = "default_bank_limit";

    public static Double defaultInt;
    public static Double defaultLim;

    public static Double interestPer;
    public static Double limitPer;

    //ERROR MESSAGES
    public static String noPerm = "§5You are not attuned to the void.";
    public static String unkPlayer = "§7Unknown player.";
    public static String notNumb = "§d'§7%§d'§7 is not a valid number!";
    public static String wrongUsage = Main.bankPrefix + "§dWrong usage! §7Try /void bank (#add-remove-set-reset | balance) [#player] [#amount]";
    public static String notEnoughCoins = Main.bankPrefix + "§cYou do not have enough coins!";

    //SOUNDS
    public static Sound success = Sound.BLOCK_NOTE_BLOCK_PLING;
    public static Sound fail = Sound.ENTITY_VILLAGER_NO;

    //SOUNDS PITCH
    public static float failPitch = 0.1f;
    public static float successPitch = 0.2f;

    private VoidBank bank;

    public final void onLoad() {
        bank = new VoidBank(this);
    }

    @Override
    public void onEnable() {

        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(this, this);
        instance = this;

        getCommand("void").setExecutor(new BankCommand());

        pm.registerEvents(new PlayerJoin(), this);
        pm.registerEvents(new Deposit(this), this);
        pm.registerEvents(new Upgrade(), this);
        pm.registerEvents(new WithdrawMenu(), this);

        if(getConfig().get(interestPercent) == null) getConfig().set(interestPercent, 25);
        if(getConfig().get(limitPercent) == null) getConfig().set(limitPercent, 55);

        if(getConfig().get(defaultInterest) == null) getConfig().set(defaultInterest, 1.2);
        if(getConfig().get(defaultLimit) == null) getConfig().set(defaultLimit, 10000000); //ten millions
        saveConfig();

        defaultInt = getConfig().getDouble(defaultInterest);
        defaultLim = getConfig().getDouble(defaultLimit);

        interestPer = getConfig().getDouble(interestPercent);
        limitPer = getConfig().getDouble(limitPercent);
    }

}
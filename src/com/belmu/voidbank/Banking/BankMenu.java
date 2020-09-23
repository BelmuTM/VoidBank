package com.belmu.voidbank.Banking;

import com.belmu.voidbank.Utils.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

@SuppressWarnings("ALL")
public class BankMenu {

    static String click = "§e§lCLICK!";

    public static Inventory menu(Player player) {

        String balance = getFormattedBalance(player, true);
        String playerLimit = NumberUtil.format(VoidBank.getLimit(player));
        String limit = "§6Bank Limit:";

        Inventory inv = Bukkit.createInventory(null, 36, "Void Bank");

        ItemStack b = new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE, 1);
        ItemMeta bM = b.getItemMeta();

        bM.setDisplayName(" ");
        b.setItemMeta(bM);

        Integer slots[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, /*lower*/27, 28, 29, 30, 31, 32, 33, 34, 35, /*sides*/9, 18, 17, 26};
        addMultipleTimes(inv, b, slots);

        ItemStack d = new ItemStack(Material.DISPENSER, 1);
        ItemMeta dM = d.getItemMeta();

        dM.setDisplayName("§dCoins Withdraw");
        dM.setLore(Arrays.asList(balance, limit + " §a" + playerLimit, " ", "§7Takes the §6coins§7 earned", "§7from the §dvoid§7 out of", "§7your bank.", " ", click));
        d.setItemMeta(dM);

        ItemStack a = new ItemStack(Material.ANVIL, 1);
        ItemMeta aM = a.getItemMeta();

        aM.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        aM.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        String cost = NumberUtil.format(Upgrade.calculateUpgradeBankCost(player));
        Double cinterest = Upgrade.calculateUpgradeBank(player)[0];
        String climit = NumberUtil.format(Upgrade.calculateUpgradeBank(player)[1]);

        Integer slotsU[] = {30, 32};
        String arrow = "→";

        if(VoidBank.getPurse(player) >= NumberUtil.formatDouble(Upgrade.calculateUpgradeBankCost(player))) {

            ItemStack s = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
            ItemMeta sM = b.getItemMeta();

            sM.setDisplayName("§aUPGRADE!");
            s.setItemMeta(sM);

            aM.setLore(Arrays.asList("§6Cost: §e" + cost, "", "§7Increases the max. §climit", "§7of §6coins§7 you can store", "§7and the §binterest§7 percentage.",
                    " ", "§6Interest:§7 " + VoidBank.getInterest(player) + "%§b " + arrow + "§a " + cinterest + "%",
                    limit + " §7" + playerLimit + "§b " + arrow + "§a " + climit, " ", click));

            addMultipleTimes(inv, s, slotsU);
        } else {

            ItemStack s = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
            ItemMeta sM = b.getItemMeta();

            sM.setDisplayName("§cNot enough coins!");
            s.setItemMeta(sM);

            aM.setLore(Arrays.asList("§6Cost: §e" + cost, "", "§7Increases the max. §climit", "§7of §6coins§7 you can store", "§7and the §binterest§7 percentage.",
                    " ", "§6Interest:§7 " + VoidBank.getInterest(player) + "%§b " + arrow + "§a " + cinterest + "%",
                    limit + " §7" + playerLimit + "§b " + arrow + "§a " + climit, " ", "§cYou do not have enough coins!"));

            addMultipleTimes(inv, s, slotsU);
        }

        aM.setDisplayName("§dBank Upgrade §a[§e" + NumberUtil.toRoman(VoidBank.getTier(player)) + "§a]");
        a.setItemMeta(aM);

        ItemStack i = new ItemStack(Material.EMERALD, 1);
        ItemMeta iM = i.getItemMeta();

        iM.setDisplayName("§r§fInformation");
        iM.setLore(Arrays.asList(" ", "§aItems§7 are converted into §6coins§7.", "§7Those are sent to the bank", "§7alongside with your §bpurse§7."));
        i.setItemMeta(iM);

        inv.setItem(35, i);
        inv.setItem(13, d);
        inv.setItem(31, a);

        return inv;
    }

    public static void addMultipleTimes(Inventory inv, ItemStack stack, Integer[] slots) {

        for(int slot : slots) inv.setItem(slot, stack);

    }

    public static String getFormattedBalance(Player player, Boolean title) {
        if(!title) { return NumberUtil.format(VoidBank.getBalance(player)); } else { return "§6Current Balance:§e " + NumberUtil.format(VoidBank.getBalance(player)); }
    }

}

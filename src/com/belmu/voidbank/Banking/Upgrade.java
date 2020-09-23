package com.belmu.voidbank.Banking;

import com.belmu.voidbank.Main;
import com.belmu.voidbank.Utils.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Upgrade implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        Inventory inv = e.getClickedInventory();
        ItemStack cur = e.getCurrentItem();
        HumanEntity p = e.getWhoClicked();

        if (inv == null) return;
        if (cur == null) return;

        if (p instanceof Player) {

            Player player = (Player) p;

            if (p.getOpenInventory().getTitle().equals("Void Bank")) {

                if (!cur.hasItemMeta()) return;
                e.setCancelled(true);

                if (cur.getType() == Material.ANVIL) {

                    if(!player.hasPermission("voidbank.player.upgrade")) {

                        player.playSound(player.getLocation(), Main.fail, Main.failPitch, Integer.MAX_VALUE);
                        player.sendMessage(Main.bankPrefix + Main.noPerm);
                        return;
                    }

                    double cost = NumberUtil.formatDouble(Upgrade.calculateUpgradeBankCost(player));

                    if(VoidBank.getPurse(player) < cost || VoidBank.getPurse(player) <= 0) {

                        player.playSound(player.getLocation(), Main.fail, Main.failPitch, Integer.MAX_VALUE);
                        player.sendMessage(Main.notEnoughCoins);

                        Inventory menu = BankMenu.menu(player);
                        player.openInventory(menu);
                        return;
                    }

                    Inventory menu = confirmation();
                    player.openInventory(menu);

                }

            } else if (p.getOpenInventory().getTitle().equals("Confirmation")) {

                if (!cur.hasItemMeta()) return;
                e.setCancelled(true);

                if (cur.getType() == Material.GREEN_TERRACOTTA) {

                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, Main.successPitch, Integer.MAX_VALUE);
                    upgradeBank(player);

                    player.sendMessage(Main.bankPrefix + "§6§lSUCCESS!§r§f Your §dbank account§f is now §atier " + VoidBank.getTier(player) + "§f!");

                    Inventory menu = BankMenu.menu(player);
                    player.openInventory(menu);

                } else if (cur.getType() == Material.RED_TERRACOTTA) {

                    player.playSound(player.getLocation(), Main.fail, Main.failPitch, Integer.MAX_VALUE);

                    Inventory menu = BankMenu.menu(player);
                    player.openInventory(menu);

                }

            }

        }

    }

    public static Inventory confirmation() {

        Inventory inv = Bukkit.createInventory(null, 27, "Confirmation");

        ItemStack y = new ItemStack(Material.GREEN_TERRACOTTA, 1);
        ItemMeta yM = y.getItemMeta();

        yM.setDisplayName("§aAccept");
        y.setItemMeta(yM);

        ItemStack n = new ItemStack(Material.RED_TERRACOTTA, 1);
        ItemMeta nM = n.getItemMeta();

        nM.setDisplayName("§cDecline");
        nM.setLore(Arrays.asList("§8§oNOTE: Changes are irreversible."));
        n.setItemMeta(nM);

        inv.setItem(11, y);
        inv.setItem(15, n);

        return inv;

    }

    public static Double[] calculateUpgradeBank(Player player) {

        double interestPercent = Main.interestPer;
        double limitPercent = Main.limitPer;

        double interest = VoidBank.getInterest(player);
        double newInterest = NumberUtil.formatDouble((interest / 100) * interestPercent);

        double bankLimit = VoidBank.getLimit(player);
        double newBankLimit = NumberUtil.formatDouble((bankLimit / 100) * limitPercent);

        newInterest += interest;
        newBankLimit += bankLimit;

        return new Double[] { newInterest, newBankLimit };
    }

    public static Double calculateUpgradeBankCost(Player player) {

        return (VoidBank.getLimit(player) / 100) * 90;
    }

    public static void upgradeBank(Player player) {

        double interest = calculateUpgradeBank(player)[0];
        double limit = calculateUpgradeBank(player)[1];

        VoidBank.setInterest(player, interest);
        VoidBank.setLimit(player, limit);
        VoidBank.setTier(player, VoidBank.getTier(player) + 1);
    }

}

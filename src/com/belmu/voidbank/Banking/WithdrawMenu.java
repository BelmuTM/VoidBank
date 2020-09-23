package com.belmu.voidbank.Banking;

import com.belmu.voidbank.Main;
import com.belmu.voidbank.Utils.Countdown;
import com.belmu.voidbank.Utils.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("ALL")
public class WithdrawMenu implements Listener {

    List<UUID> onCooldown = new ArrayList<>();
    Integer[] percentages = { 100, 50, 25 };

    private int cooldown = 20; //seconds

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

                if (cur.getType() == Material.DISPENSER) {

                    Inventory menu = withdraw(player);
                    player.openInventory(menu);

                }

            } else if (p.getOpenInventory().getTitle().equals("Withdraw")) {

                if (!cur.hasItemMeta()) return;
                e.setCancelled(true);

                if(cur.getType() == Material.ARROW) {

                    Inventory menu = BankMenu.menu(player);
                    player.openInventory(menu);

                } else if(cur.getType() == Material.DISPENSER) {

                    if(cur.getEnchantments().isEmpty()) {

                        for(Integer per : percentages) {

                            if (cur.getItemMeta().getDisplayName().contains(per.toString())) {

                                try {
                                    int percent = Integer.parseInt(per.toString());
                                    double amount = VoidBank.getBalance(player) / 100 * percent;

                                    if(amount <= 2.5) {

                                        player.playSound(player.getLocation(), Main.fail, Main.failPitch, Integer.MAX_VALUE);
                                        player.sendMessage(Main.bankPrefix + "§cYou don't have enough coins!");
                                        return;
                                    }

                                    VoidBank.addPurse(player, amount);
                                    VoidBank.removeBalance(player, amount);

                                    String bc = "§7[" + BankMenu.getFormattedBalance(player, false) + "]";

                                    player.sendMessage(Main.bankPrefix + "§fWithdrawn§6 " +
                                            NumberUtil.withSuffix(amount) + " coins§f from your account. " + bc);

                                    player.playSound(player.getLocation(), Main.success, Main.successPitch, Integer.MAX_VALUE);

                                    player.closeInventory();
                                    Inventory menu = withdraw(player);
                                    player.openInventory(menu);

                                } catch(NumberFormatException nfe) {
                                    System.out.println("ERROR: Withdraw's percentage isn't an Integer. Contact Belmu#4066");
                                }

                            }

                        }

                    } else {

                        player.closeInventory();
                        player.sendMessage("§6Please type a§e custom amount§6 of coins. §a§lvvv");

                        UUID uuid = player.getUniqueId();
                        Countdown border = new Countdown(Main.getInstance(),
                                cooldown,

                                () -> onCooldown.add(uuid),

                                () -> {

                                    if(onCooldown.contains(uuid)) {
                                        onCooldown.remove(uuid);
                                        player.sendMessage("§7§oNo answer, cancelled custom amount.");
                                    }
                                },
                                (t) -> {}
                        );
                        border.scheduleTimer();


                    }

                }

            }

        }

    }

    public static Inventory withdraw(Player player) {

        Inventory inv = Bukkit.createInventory(null, 45, "Withdraw");

        ItemStack b = new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE, 1);
        ItemMeta bM = b.getItemMeta();

        bM.setDisplayName(" ");
        b.setItemMeta(bM);

        Integer slots[] = {0, 9, 18, 27, 36, 8, 17, 26, 35, 44, 1, 2, 3, 4,
                5, 6, 7, 8, 37, 38, 39, 40, 41, 42, 43};
        BankMenu.addMultipleTimes(inv, b, slots);

        ///////////////////WITHDRAW OPTIONS///////////////////

        ItemStack w1 = new ItemStack(Material.DISPENSER, 1);
        ItemMeta w1M = w1.getItemMeta();

        w1M.setDisplayName("§dWithdraw 100%");
        w1M.setLore(Arrays.asList(BankMenu.getFormattedBalance(player, true), " ", BankMenu.click));
        w1.setItemMeta(w1M);
        inv.setItem(19, w1);

        ItemStack w2 = new ItemStack(Material.DISPENSER, 1);
        ItemMeta w2M = w2.getItemMeta();

        w2M.setDisplayName("§dWithdraw 50%");
        w2M.setLore(Arrays.asList(BankMenu.getFormattedBalance(player, true), " ", BankMenu.click));
        w2.setItemMeta(w2M);
        inv.setItem(21, w2);

        ItemStack w3 = new ItemStack(Material.DISPENSER, 1);
        ItemMeta w3M = w3.getItemMeta();

        w3M.setDisplayName("§dWithdraw 25%");
        w3M.setLore(Arrays.asList(BankMenu.getFormattedBalance(player, true), " ", BankMenu.click));
        w3.setItemMeta(w3M);
        inv.setItem(23, w3);

        ItemStack w4 = new ItemStack(Material.DISPENSER, 1);
        ItemMeta w4M = w4.getItemMeta();

        w4M.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        w4M.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        w4M.setDisplayName("§dWithdraw Custom Amount");
        w4M.setLore(Arrays.asList(BankMenu.getFormattedBalance(player, true), " ", "§7Takes away a §bcustom amount§7 of", "§6coins §7from your bank.", " ", BankMenu.click));
        w4.setItemMeta(w4M);
        inv.setItem(25, w4);

        ///////////////////OTHER///////////////////

        ItemStack ba = new ItemStack(Material.ARROW, 1);
        ItemMeta baM = ba.getItemMeta();

        baM.setDisplayName("§r§fGo Back");
        baM.setLore(Arrays.asList(" "));
        ba.setItemMeta(baM);

        ItemStack i = new ItemStack(Material.EMERALD, 1);
        ItemMeta iM = i.getItemMeta();

        iM.setDisplayName("§r§fInformation");
        iM.setLore(Arrays.asList(" ", "§aItems§7 are converted into §6coins§7.", "§7Those are sent to the bank", "§7alongside with your §bpurse§7."));
        i.setItemMeta(iM);
        inv.setItem(44, i);
        inv.setItem(4, ba);

        return inv;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String[] msg = e.getMessage().split(" ");

        if(onCooldown.contains(uuid)) {

            e.setCancelled(true);
            if(msg.length == 1) {

                try {
                    double amount = Double.parseDouble(msg[0]);

                    if(amount > VoidBank.getBalance(player)) {

                        onCooldown.remove(uuid);
                        player.playSound(player.getLocation(), Main.fail, Main.failPitch, Integer.MAX_VALUE);
                        player.sendMessage(Main.bankPrefix + "§cYou don't have enough coins!");

                        return;
                    }

                    VoidBank.addPurse(player, amount);
                    VoidBank.removeBalance(player, amount);

                    String bc = "§7[" + BankMenu.getFormattedBalance(player, false) + "]";

                    player.sendMessage(Main.bankPrefix + "§fWithdrawn§6 " +
                            NumberUtil.withSuffix(amount) + " coins§f from your account. " + bc);

                    player.playSound(player.getLocation(), Main.success, Main.successPitch, Integer.MAX_VALUE);
                    onCooldown.remove(uuid);

                } catch (NumberFormatException nfe) {

                    onCooldown.remove(uuid);
                    player.playSound(player.getLocation(), Main.fail, Main.failPitch, Integer.MAX_VALUE);
                    player.sendMessage(Main.bankPrefix + Main.notNumb.replaceAll("%", msg[0]));

                    Inventory menu = withdraw(player);
                    player.openInventory(menu);
                }

            } else {

                onCooldown.remove(uuid);
                player.playSound(player.getLocation(), Main.fail, Main.failPitch, Integer.MAX_VALUE);
                player.sendMessage(Main.bankPrefix + "§dWrong amount!§7 Try again.");

                Inventory menu = withdraw(player);
                player.openInventory(menu);
            }

        }

    }

}

package com.belmu.voidbank.Banking;

import com.belmu.voidbank.Main;
import com.belmu.voidbank.Utils.NumberUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();
        if(!VoidBank.isInitiated(player)) VoidBank.initPlayer(player);

        new BukkitRunnable() {

            @Override
            public void run() {

                long millis = System.currentTimeMillis();

                if(Interest.isRegistered(player)) {

                    if(Interest.getMillis(player) <= millis) {

                        double amount = VoidBank.calculateInterest(player);
                        String bc = "§7[" + BankMenu.getFormattedBalance(player, false) + "]";

                        long hours = Interest.milliseconds / 3600000;
                        double hd = (double) hours;
                        double finalHD = NumberUtil.formatDouble(hd);

                        if(amount < 1) return;

                        VoidBank.applyInterest(player);
                        Interest.setMillis(player, millis + Interest.milliseconds);

                        player.sendMessage(Main.bankPrefix + "§fYou earned§6 " + NumberUtil.withSuffix(amount)
                                + " coins§f of §binterest§f during the last §a" + finalHD + "hours§f. " + bc);
                    }

                } else {

                    Interest.registerPlayer(player);
                }

                if(!player.isOnline()) this.cancel();

            }
        }.runTaskTimer(Main.getInstance(), 0, 60);

    }

}

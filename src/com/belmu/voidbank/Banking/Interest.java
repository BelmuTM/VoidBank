package com.belmu.voidbank.Banking;

import com.belmu.voidbank.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Interest {

    public static long milliseconds = 172800000;
    public static String path = "Interest.";
    public static String ms = ".milliseconds";

    public static void registerPlayer(Player player) {

        UUID uuid = player.getUniqueId();
        FileConfiguration c = Main.getInstance().getConfig();
        long millis = System.currentTimeMillis();
        c.set(path + uuid + ms, millis + milliseconds);
        Main.getInstance().saveConfig();
    }

    public static boolean isRegistered(Player player) {

        UUID uuid = player.getUniqueId();
        FileConfiguration c = Main.getInstance().getConfig();

        if(c.get(path + uuid) != null) {
            return true;
        } else {
            return false;
        }

    }

    public static void setMillis(Player player, long milliseconds) {

        UUID uuid = player.getUniqueId();
        FileConfiguration c = Main.getInstance().getConfig();

        c.set(path + uuid + ms, milliseconds);
        Main.getInstance().saveConfig();
    }

    public static long getMillis(Player player) {

        UUID uuid = player.getUniqueId();
        FileConfiguration c = Main.getInstance().getConfig();
        if(!isRegistered(player)) return 0;
        return c.getLong(path + uuid + ms);
    }

}

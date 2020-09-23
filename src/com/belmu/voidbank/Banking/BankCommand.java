package com.belmu.voidbank.Banking;

import com.belmu.voidbank.Main;
import com.belmu.voidbank.Utils.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@SuppressWarnings("NullableProblems")
public class BankCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("void")) {

                if(args.length > 0) {

                    if(args[0].equalsIgnoreCase("bank")) {

                        if(args.length == 1) {

                            if(player.hasPermission("voidbank.player.menu")) {

                                if(!VoidBank.isInitiated(player)) VoidBank.initPlayer(player);
                                Inventory inv = BankMenu.menu(player);
                                player.openInventory(inv);

                            } else {

                                player.sendMessage(Main.bankPrefix + Main.noPerm);

                            }

                        } else if(args.length == 4) {

                            String numb = Main.notNumb.replaceAll("%", args[3]);

                            if(args[1].equalsIgnoreCase("add")) {

                                if(player.hasPermission("voidbank.admin.add")) {

                                    Player target = Bukkit.getPlayerExact(args[2]);

                                    if (target != null) {

                                        try {
                                            double amount = Double.parseDouble(args[3]);
                                            double balance = VoidBank.getBalance(target);

                                            if ((amount + balance) > VoidBank.getLimit(player)) {
                                                player.sendMessage(Main.bankPrefix + "§dYou can't go over the specified player's limit!");
                                                return false;
                                            }

                                            VoidBank.addBalance(player, amount);

                                            String bc = "§7[" + BankMenu.getFormattedBalance(player, false) + "]";

                                            player.sendMessage(Main.bankPrefix + "§fAdded§6 " +
                                                    NumberUtil.withSuffix(amount) + " coins§f to §a" + target.getName() + "§f's account. " + bc);

                                        } catch (NumberFormatException nfe) {

                                            player.sendMessage(Main.bankPrefix + numb);
                                        }

                                    } else {

                                        player.sendMessage(Main.bankPrefix + Main.unkPlayer);
                                    }

                                } else {

                                    player.sendMessage(Main.bankPrefix + Main.noPerm);
                                }

                            } else if(args[1].equalsIgnoreCase("remove")) {

                                if (player.hasPermission("voidbank.admin.remove")) {

                                    Player target = Bukkit.getPlayerExact(args[2]);

                                    if (target != null) {

                                        try {

                                            double amount = Double.parseDouble(args[3]);
                                            double balance = VoidBank.getBalance(target);

                                            if (amount <= balance) {
                                                VoidBank.removeBalance(player, amount);

                                                String bc = "§7[" + BankMenu.getFormattedBalance(player, false) + "]";

                                                player.sendMessage(Main.bankPrefix + "§fRemoved§6 " +
                                                        NumberUtil.withSuffix(amount) + " coins§f from §a" + target.getName() + "§f's account. " + bc);

                                            } else {
                                                player.sendMessage(Main.bankPrefix + "§dThe amount needs to be smaller than the specified player's balance!");
                                            }

                                        } catch (NumberFormatException nfe) {

                                            player.sendMessage(Main.bankPrefix + numb);
                                        }

                                    } else {

                                        player.sendMessage(Main.bankPrefix + Main.unkPlayer);

                                    }

                                } else {

                                    player.sendMessage(Main.bankPrefix + Main.noPerm);

                                }

                            } else if(args[1].equalsIgnoreCase("set")) {

                                if (player.hasPermission("voidbank.admin.set")) {

                                    Player target = Bukkit.getPlayerExact(args[2]);

                                    if (target != null) {

                                        try {

                                            double amount = Double.parseDouble(args[3]);

                                            VoidBank.setBalance(player, amount);
                                            player.sendMessage(Main.bankPrefix + "§fSet §a" + target.getName() + "§f's balance to§6 " +
                                                    NumberUtil.format(amount) + " coins§f.");

                                        } catch (NumberFormatException nfe) {

                                            player.sendMessage(Main.bankPrefix + numb);
                                        }

                                    } else {

                                        player.sendMessage(Main.bankPrefix + Main.unkPlayer);
                                    }

                                } else {

                                    player.sendMessage(Main.bankPrefix + Main.noPerm);
                                }

                            } else {

                                player.sendMessage(Main.wrongUsage);
                            }

                        } else if(args.length == 3) {

                            if(args[1].equalsIgnoreCase("reset")) {

                                if (player.hasPermission("voidbank.admin.reset")) {

                                    Player target = Bukkit.getPlayerExact(args[2]);

                                    if (target != null) {

                                        VoidBank.setBalance(target, 0D);
                                        VoidBank.setPurse(target, 0D);
                                        VoidBank.setInterest(target, Main.defaultInt);
                                        VoidBank.setLimit(target, Main.defaultLim);
                                        VoidBank.setTier(target, 1);

                                        player.sendMessage(Main.bankPrefix + "§eSuccessfully§f reset §a" + target.getName() + "§f's bank account.");

                                    } else {

                                        player.sendMessage(Main.bankPrefix + Main.unkPlayer);
                                    }

                                } else {

                                    player.sendMessage(Main.bankPrefix + Main.noPerm);
                                }

                            } else {

                                player.sendMessage(Main.wrongUsage);
                            }

                        } else if(args.length == 2) {

                            if(args[1].equalsIgnoreCase("balance")) {

                                player.sendMessage(Main.bankPrefix + BankMenu.getFormattedBalance(player, true) + "§7 [§b" + VoidBank.getInterest(player) + "%§f interest§7]");
                            } else {

                                player.sendMessage(Main.wrongUsage);
                            }

                        } else {

                            player.sendMessage(Main.wrongUsage);
                        }

                    } else {

                            player.sendMessage(Main.wrongUsage);
                    }

                } else {

                    player.sendMessage(Main.wrongUsage);
                }

            }

        }

        return false;
    }

}

package de.articdive.lwckeys;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;


public class LWCKeysCommands implements CommandExecutor {
	private LWCKeysMain main = LWCKeysMain.getInstance();
	private HashMap<Integer, Key> keys = main.getKeys();

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		String[] index = new String[]{ChatColor.YELLOW + "=====   " + ChatColor.GOLD + "LWCKeys" + ChatColor.YELLOW + "   =====", ChatColor.YELLOW + "/" + label + " give [Key] [Player] [Amount]"};
		if (args.length == 0) {
			cs.sendMessage(index);
			return true;
		}
		if (args.length != 0) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("give")) {
					if (cs.hasPermission("lwckeys.give")) {
						cs.sendMessage(ChatColor.RED + "You need to specify a key!");
						return true;
					} else {
						cs.sendMessage(ChatColor.RED + "Sorry you don't have permission to use this command!");
						return true;
					}
				} else {
					cs.sendMessage(index);
					return true;
				}
			}
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("give")) {
					if (cs.hasPermission("lwckeys.give")) {
						for (int j = 0; j < keys.size(); j++) {
							if (args[1].equalsIgnoreCase(keys.get(j).getName())) {
								if (cs instanceof Player) {
									Player player = (Player) cs;
									player.getInventory().addItem(keys.get(j).createItemStack(1));
									cs.sendMessage(ChatColor.GREEN + "You've given yourself 1 " + args[1] + " Key!");
									return true;
								} else {
									cs.sendMessage(ChatColor.RED + "You need to specify a player to give the key!");
									return true;
								}
							}
						}
						cs.sendMessage(ChatColor.RED + args[1] + " is not a valid keyname!");
						return true;
					} else {
						cs.sendMessage(ChatColor.RED + "Sorry you don't have permission to use this command!");
						return true;
					}
				} else {
					cs.sendMessage(index);
					return true;
				}
			}
			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("give")) {
					if (cs.hasPermission("lwckeys.give")) {
						for (int j = 0; j < keys.size(); j++) {
							if (args[1].equalsIgnoreCase(keys.get(j).getName())) {
								if (isPlayer(args[2])) {
									Player target = Bukkit.getPlayerExact(args[2]);
									target.getInventory().addItem(keys.get(j).createItemStack(1));
									cs.sendMessage(ChatColor.GREEN + "You've given " + args[2] + " 1 " + args[1] + " Key!");
									return true;
								} else {
									cs.sendMessage(ChatColor.RED + "Player " + args[2] + " couldn't be found or is not online!");
									return true;
								}
							}
						}
						cs.sendMessage(ChatColor.RED + args[1] + " is not a valid keyname!");
						return true;
					} else {
						cs.sendMessage(ChatColor.RED + "Sorry you don't have permission to use this command!");
						return true;
					}
				} else {
					cs.sendMessage(index);
					return true;
				}
			}
			if (args.length == 4) {
				if (args[0].equalsIgnoreCase("give")) {
					if (cs.hasPermission("lwckeys.give")) {
						for (int j = 0; j < keys.size(); j++) {
							if (args[1].equalsIgnoreCase(keys.get(j).getName())) {
								if (isPlayer(args[2])) {
									Player target = Bukkit.getPlayerExact(args[2]);
									if (isInt(args[3])) {
										target.getInventory().addItem(keys.get(j).createItemStack(Integer.parseInt(args[3])));
										cs.sendMessage(ChatColor.GREEN + "You've given " + args[2] + " " + args[3] + " " + args[1] + " Key(s)!");
										return true;
									} else {
										cs.sendMessage(ChatColor.RED + args[3] + " is not a valid number!");
										return true;
									}
								} else {
									cs.sendMessage(ChatColor.RED + "Player " + args[2] + " couldn't be found or is not online!");
									return true;
								}
							}
							cs.sendMessage(ChatColor.RED + args[1] + " is not a valid keyname!");
							return true;
						}
					} else {
						cs.sendMessage(ChatColor.RED + "Sorry you don't have permission to use this command!");
						return true;
					}
				} else {
					cs.sendMessage(index);
					return true;
				}
			}
		}
		cs.sendMessage(index);
		return true;
	}

	public boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public boolean isPlayer(String s) {
		Player john = Bukkit.getPlayerExact(s);
		if (john != null) {
			return true;
		}
		if (john == null) {
			return false;
		}
		return false;
	}
}

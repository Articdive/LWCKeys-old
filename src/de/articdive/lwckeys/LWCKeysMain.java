package de.articdive.lwckeys;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LWCKeysMain extends JavaPlugin {
    private static LWCKeysMain instance;
    private static Plugin plugin;
    private LWC lwc;
    public static String displayname;
    private List<String> lore;
    private Material keymaterial = Material.TRIPWIRE_HOOK;
    private boolean removeProtection;
    private String configversion;
    private HashMap<Enchantment, Integer> enchantments = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        plugin = this;
        if (!(this.getServer().getPluginManager().isPluginEnabled("LWC"))) {
            getLogger().info("LWCKeys couldn't find LWC, please get LWC before running LWCKeys");
            this.getPluginLoader().disablePlugin(this);
        } else {
            createConfig();
            Plugin lwcp = Bukkit.getPluginManager().getPlugin("LWC");
            lwc = ((LWCPlugin) lwcp).getLWC();
            getLogger().info("Hooked into LWC version " + lwcp.getDescription().getVersion() + " successfully");
            registerEvents(this, new LWCKeysListener());
            getLogger().info("LWCKeys has been enabled!");
        }
    }

    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("Config.yml not found, creating!");
                saveDefaultConfig();
                loadConfig();
            } else {
                getLogger().info("Config.yml found, loading!");
                loadConfig();
                if (configversion == null || !(configversion.equals("1.3.0"))) {
                    File old = new File(getDataFolder(), "configold.yml");
                    if (old.exists()) {
                        old.delete();
                    }
                    file.renameTo(old);
                    getLogger().info("Older config found, renaming it to configold.yml!");
                    createConfig();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadConfig() {
        configversion = plugin.getConfig().getString("config-version");
        displayname = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("displayname"));
        lore = translateList(plugin.getConfig().getStringList("lore"));
        removeProtection = plugin.getConfig().getBoolean("remove-protection");
        try {
            keymaterial = Material.valueOf(plugin.getConfig().getString("material").toUpperCase());
        } catch (Exception e) {
            keymaterial = Material.STONE;
            displayname = ChatColor.RED + "invalid Material";
            this.getLogger().info("invalid material for LWCKey!");
        }
        enchantments = getEnchantmentsFromConfig();
    }

    private List<String> translateList(List<String> list) {
        List<String> newlist = new ArrayList<>();
        for (int j = 0; j < list.size(); j++) {
            newlist.add(ChatColor.translateAlternateColorCodes('&', list.get(j)));
        }
        return newlist;
    }

    @Override
    public void onDisable() {
        plugin = null;
        getLogger().info("LWCKeys has been disabled!");
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    private static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    public LWC getLWC() {
        return lwc;
    }

    public static LWCKeysMain getInstance() {
        return instance;
    }

    public ItemStack createkey(int amount) {
        ItemStack key = new ItemStack(keymaterial, amount);
        ItemMeta keymeta = key.getItemMeta();
        keymeta.setDisplayName(displayname);
        keymeta.setLore(lore);
            for (Enchantment enchantment : enchantments.keySet()) {
                int level = enchantments.get(enchantment);
                keymeta.addEnchant(enchantment, level, true);
            }
        key.setItemMeta(keymeta);
        return key;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String[] index = new String[]{ChatColor.YELLOW + "/" + label + " give [player] [amount]"};
        if (cmd.getName().equalsIgnoreCase("lwckeys")) {
            if (args.length == 0) {
                sender.sendMessage(index);
                return true;
            }
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("give")) {
                    if (sender.hasPermission("lwckeys.give")) {
                        int amount = 1;
                        if (args.length == 1) {
                            if (sender instanceof Player) {
                                Player player = (Player) sender;
                                player.getInventory().addItem(createkey(1));
                                sender.sendMessage(ChatColor.GREEN + "You have given yourself 1 LWCKey");
                                return true;
                            } else {
                                sender.sendMessage(ChatColor.RED + "This command is only available to players");
                                return true;
                            }
                        }
                        if (args.length == 2) {
                            if (isInt(args[1]) && sender instanceof Player && !isPlayer(args[1])) {
                                Player player = (Player) sender;
                                player.getInventory().addItem(createkey(Integer.parseInt(args[1])));
                                sender.sendMessage(ChatColor.GREEN + "You have given yourself " + Integer.parseInt(args[1]) + " LWCKey(s)");
                                return true;
                            }
                            Player target = Bukkit.getPlayerExact(args[1]);
                            if (target != null) {
                                target.getInventory().addItem(createkey(amount));
                                sender.sendMessage(ChatColor.GREEN + "You have added " + Integer.toString(amount) + " LWCKey(s) to " + args[1] + "'s inventory");
                                return true;
                            }
                            if (target == null && !isPlayer(args[1])) {
                                sender.sendMessage(ChatColor.RED + "Player " + args[1] + " couldn't be found or is not online!");
                                return true;
                            }
                        }
                        if (args.length == 3) {
                            try {
                                amount = getInt(args[2]);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number!");
                                return true;
                            }
                            Player target = Bukkit.getPlayerExact(args[1]);
                            if (target != null) {
                                target.getInventory().addItem(createkey(amount));
                                sender.sendMessage(ChatColor.GREEN + "You have added " + Integer.toString(amount) + " LWCKey(s) to " + args[1] + "'s inventory");
                                return true;
                            } else {
                                sender.sendMessage(ChatColor.RED + "Player " + args[1] + " couldn't be found or is not online!");
                                return true;
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Sorry you don't have permission to use this command!");
                    }
                } else {
                    sender.sendMessage(index);
                }
            }
        }
        return false;
    }

    public Material getKeymaterial() {
        return keymaterial;
    }

    public String getDisplayname() {
        return displayname;
    }

    public List<String> getLore() {
        return lore;
    }

    public boolean getremoveProtection() {
        return removeProtection;
    }

    public Integer getInt(String s) {
        int i = 1;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw e;

        }
        return i;
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

    public HashMap<Enchantment, Integer> getEnchantmentsFromConfig() {
        HashMap<Enchantment, Integer> configenchants = new HashMap<>();
        List<String> configlist = getConfig().getStringList("enchantments");
        for (int j = 0; j < configlist.size(); j++) {
            String line = configlist.get(j);
            String[] enchantmentlevel = line.split(",");
            if (enchantmentlevel.length <= 2 && enchantmentlevel.length != 0) {
                try {
                    configenchants.put(Enchantment.getByName(enchantmentlevel[0].toUpperCase()), Integer.parseInt(enchantmentlevel[1]));
                } catch (IllegalArgumentException e) {
                    getLogger().info(enchantmentlevel[0] + " is not a valid enchantment or " + enchantmentlevel[1] + " is not a valid Integer, skipping!");
                }
            }
        }
        return configenchants;
    }

    public HashMap<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }
}

package de.articdive.lwckeys;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class LWCKeysMain extends JavaPlugin {
    private static LWCKeysMain instance;
    private static Plugin plugin;
    private LWC lwc;
    private boolean removeProtection;
    private boolean sinceowneronline;
    private String configversion;
    private Set<String> keysconfig;
    public HashMap<Integer, Key> keys = new HashMap<>();

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
            getCommand("lwckeys").setExecutor(new LWCKeysCommands());
            getLogger().info("LWCKeys has been enabled!");
        }
    }

    @Override
    public void onDisable() {
        plugin = null;
        getLogger().info("LWCKeys has been disabled!");
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
                if (configversion == null || !(configversion.equals("2.1.0"))) {
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
        removeProtection = plugin.getConfig().getBoolean("remove-protection");
        sinceowneronline = plugin.getConfig().getBoolean("since-owner-online");
        keysconfig = plugin.getConfig().getConfigurationSection("keys").getKeys(false);
        createKeys();
    }

    public void createKeys() {
        int j = -1;
        for (String key : keysconfig) {
            j = j + 1;
            ConfigurationSection var = getConfig().getConfigurationSection("keys." + key);
            String name = key;
            String displayname = ChatColor.translateAlternateColorCodes('&', var.getString("displayname"));
            List<String> lore = translateList(var.getStringList("lore"));
            Material mat;
            try {
                mat = Material.valueOf(var.getString("material"));
            } catch (Exception e) {
                getLogger().info("Invalid Material");
                mat = Material.TRIPWIRE_HOOK;
                displayname = "invalid";
            }
            HashMap<Enchantment, Integer> configenchants = new HashMap<>();
            List<String> configlist = var.getStringList("enchantments");
            for (int k = 0; k < configlist.size(); k++) {
                String line = configlist.get(k).toUpperCase();
                String[] enchantmentlevel = line.split(",");
                if (enchantmentlevel.length <= 2 && enchantmentlevel.length != 0) {
                    try {
                        configenchants.put(Enchantment.getByName(enchantmentlevel[0].toUpperCase()), Integer.parseInt(enchantmentlevel[1]));
                    } catch (IllegalArgumentException e) {
                        getLogger().info(enchantmentlevel[0] + " is not a valid enchantment or " + enchantmentlevel[1] + " is not a valid Integer, skipping!");
                    }
                }
            }
            long time = TimeUtil.parseTime(var.getString("time"));
            Key test = new Key(name, displayname, lore, mat, configenchants, time);
            keys.put(j, test);
        }
    }

    private List<String> translateList(List<String> list) {
        List<String> newlist = new ArrayList<>();
        for (int j = 0; j < list.size(); j++) {
            newlist.add(ChatColor.translateAlternateColorCodes('&', list.get(j)));
        }
        return newlist;
    }

    public LWC getLWC() {
        return lwc;
    }

    public static LWCKeysMain getInstance() {
        return instance;
    }

    public boolean getremoveProtectionBoolean() {
        return removeProtection;
    }


    public boolean getSinceOwnerOnlineBoolean() {
        return sinceowneronline;
    }

    private static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    public HashMap<Integer, Key> getKeys() {
        return keys;
    }
}

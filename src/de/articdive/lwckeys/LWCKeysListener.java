package de.articdive.lwckeys;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LWCKeysListener implements Listener {
    private LWCKeysMain main = LWCKeysMain.getInstance();
    private LWC lwc = main.getLWC();
    private HashMap<Integer, Key> keys = main.getKeys();
    private boolean sinceowneronline = main.getSinceOwnerOnlineBoolean();

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            // do nothing
        }
        if (event.hasItem() && event.hasBlock() && event.getHand() == EquipmentSlot.HAND && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() != Material.AIR && event.getItem().getType() != Material.AIR) {
            ItemStack item = event.getItem();
            for (int j = 0; j < keys.size(); j++) {
                String displayname = keys.get(j).getDisplayname(); //checked
                Material mat = keys.get(j).getMat(); //checked
                List<String> lore = keys.get(j).getLore(); //checked
                HashMap<Enchantment, Integer> enchants = keys.get(j).getEnchantments(); //checked
                long time = keys.get(j).getTime();
                if (matchesDescription(item, displayname, lore, enchants, mat)) {
                    if (event.getPlayer().hasPermission("lwckeys.use")) {
                        Protection protection = null;
                        try {
                            protection = lwc.findProtection(event.getClickedBlock().getLocation());
                        } catch (NullPointerException e) {
                        }
                        if (protection == null) {
                            return;
                        }
                        if (!(protection == null)) {
                            if (!(protection.getOwner().equals(event.getPlayer().getUniqueId().toString()))) {
                                if (!(lwc.canAccessProtection(event.getPlayer(), protection))) {
                                    OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(protection.getOwner()));
                                    if (owner.isOnline()) {
                                        player.sendMessage(ChatColor.RED + owner.getName() + " is online, container is not unlocked!");
                                        return;
                                    }
                                    if (timeandownermatchesup(owner, time, player, protection)) {
                                        if (main.getremoveProtectionBoolean()) {
                                            protection.remove();
                                            event.getPlayer().sendMessage(ChatColor.GREEN + "You've unlocked the chest belonging to " + owner.getName() + "!");
                                            return;
                                        } else {
                                            protection.setOwner(event.getPlayer().getUniqueId().toString());
                                            event.getPlayer().sendMessage(ChatColor.GREEN + "You've unlocked the chest belonging to " + owner.getName() + "!");
                                            return;
                                        }
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "Why would you unlock something you're able to access???");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean timeandownermatchesup(OfflinePlayer owner, long delay, Player player, Protection protection) {
        long servertime = System.currentTimeMillis();
        if (sinceowneronline) {
            long sincelogin = owner.getLastPlayed();

            if (servertime >= sincelogin + delay) {
                return true;
            }
            if (servertime < sincelogin + delay) {
                player.sendMessage(ChatColor.RED + "You must wait " + timeToString((servertime - (sincelogin + delay)) / 1000L) + " before you can unlock this container!");
                return false;
            }
        }
        if (!sinceowneronline) {
            long sinceopened = protection.getLastAccessed() * 1000;
            if (servertime >= sinceopened + delay) {
                return true;
            }
            if (servertime < sinceopened + delay) {
                player.sendMessage(ChatColor.RED + "You must wait " + timeToString((servertime - (sinceopened + delay)) / 1000L) + " before you can unlock this container!");
                return false;
            }
        }
        return false;
    }

    public boolean matchesDescription(ItemStack item, String displayname, List<String> lore, HashMap<Enchantment, Integer> enchants, Material mat) {
        if (item.getType() == mat) {
            if ((displayname.isEmpty() || item.getItemMeta().hasDisplayName())) {
                if (lore.isEmpty() || item.getItemMeta().hasLore()) {
                    if (enchants.isEmpty() || item.getItemMeta().hasEnchants()) {
                        if (displayname.equals(item.getItemMeta().getDisplayName())) {
                            if (lore.equals(item.getItemMeta().getLore())) {
                                if (enchants.equals(item.getEnchantments())) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    // Code from LWC, altered to work with minus numbers all of the below code except line 132-134 and text changes on line 137 and 168 are belonging to the plugin LWC : https://github.com/Hidendra/LWC
    public static String timeToString(long time) {
        String str = "";

        if (time < 0) {
            time = time * -1;
        }

        if ((System.currentTimeMillis() / 1000L) - time == 0) {
            return "now";
        }

        long days = time / 86400;
        time -= days * 86400;

        long hours = time / 3600;
        time -= hours * 3600;

        long minutes = time / 60;
        time -= minutes * 60;

        long seconds = time;

        if (days > 0) {
            str += days + " day" + (days == 1 ? "" : "s") + " ";
        }

        if (hours > 0) {
            str += hours + " hour" + (hours == 1 ? "" : "s") + " ";
        }

        if (minutes > 0) {
            str += minutes + " minute" + (minutes == 1 ? "" : "s") + " ";
        }

        if (seconds > 0) {
            str += seconds + " second" + (seconds == 1 ? "" : "s") + " ";
        }

        if (str.equals("")) {
            return "until now";
        }

        return str.trim();
    }
}

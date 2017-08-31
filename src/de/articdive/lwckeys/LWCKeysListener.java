package de.articdive.lwckeys;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

public class LWCKeysListener implements Listener {
    private LWCKeysMain main = LWCKeysMain.getInstance();
    private LWC lwc = main.getLWC();
    private String unlockaccessable = ChatColor.RED + "Why would you unlock, something you can already access??";

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            // do nothing
        }
        if (event.getHand() == EquipmentSlot.HAND && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() != Material.AIR) {
            if (event.getItem() != null && event.getItem().getType() != Material.AIR && event.getItem().getType() == main.getKeymaterial()) {
                if (((main.getLore() == null || main.getLore().isEmpty()) || (event.getItem().getItemMeta().hasLore() && event.getItem().getItemMeta().getLore().equals(main.getLore()))) && ((main.getDisplayname() == null || main.getDisplayname().isEmpty())|| (event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().getDisplayName().equals(main.getDisplayname()))) && ((main.getEnchantments() == null || main.getEnchantments().isEmpty()) || (event.getItem().getItemMeta().hasEnchants() && event.getItem().getEnchantments().equals(main.getEnchantments())))) {
                    if (event.getPlayer().hasPermission("lwckeys.use")){
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
                                String owner = protection.getOwner();
                                if (!(lwc.canAccessProtection(event.getPlayer(), protection))) {
                                    if (main.getremoveProtection()) {
                                        protection.remove();
                                        event.getPlayer().sendMessage(ChatColor.GREEN + "You've unlocked the chest belonging to " + Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName() +"!");
                                        return;
                                    } else {
                                        protection.setOwner(event.getPlayer().getUniqueId().toString());
                                        event.getPlayer().sendMessage(ChatColor.GREEN + "You've unlocked the chest belonging to " + Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName() +"!");
                                    }
                                } else {
                                    player.sendMessage(unlockaccessable);
                                }
                            } else {
                            }
                        }
                    } else {
                        event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to use LWCKeys!");
                    }
                }
            }
        }
    }
}

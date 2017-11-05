package de.articdive.lwckeys;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class Key {
	private String name;
	private String displayname;
	private List<String> lore;
	private Material mat;
	private HashMap<Enchantment, Integer> enchantments;
	private long time;

	public Key(String name, String displayname, List<String> lore, Material mat, HashMap<Enchantment, Integer> enchants, long time) {
		this.name = name;
		this.displayname = displayname;
		this.lore = lore;
		this.mat = mat;
		this.enchantments = enchants;
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public String getDisplayname() {
		return displayname;
	}

	public List<String> getLore() {
		return lore;
	}

	public HashMap<Enchantment, Integer> getEnchantments() {
		return enchantments;
	}

	public long getTime() {
		return time * 1000L;
	}

	public Material getMat() {
		return mat;
	}

	public ItemStack createItemStack(int amount) {
		ItemStack key = new ItemStack(mat, amount);
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
}
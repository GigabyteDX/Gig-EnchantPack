package com.gigabytedx.gigenchantments.enchantments;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gigabytedx.gigenchantments.Main;
import com.gigabytedx.gigenchantments.constants.ConfigPaths;
import com.gigabytedx.gigenchantments.constants.ErrorsAndMessages;
import com.rit.sucy.CustomEnchantment;

public class BoostDamage extends CustomEnchantment {
	static final Material[] STRENGTH_ITEMS = new Material[] { Material.BLAZE_POWDER };
	static HashMap<UUID, Long> playerCooldown = new HashMap<>();
	private int maxLevel;
	private int cooldown;
	private int duration;

	public BoostDamage(Main plugin) {
		
		super(plugin.getConfig().getString(ConfigPaths.STRENTH_SPELL_PATH.getPath() + "." + ConfigPaths.NAME_PATH.getPath()), STRENGTH_ITEMS,
				2);

		this.maxLevel = plugin.getConfig().getInt(ConfigPaths.STRENTH_SPELL_PATH.getPath() + "." + ConfigPaths.MAX_LEVEL_PATH.getPath());
		this.cooldown = plugin.getConfig().getInt(ConfigPaths.STRENTH_SPELL_PATH.getPath() + "." + ConfigPaths.COOLDOWN_PATH.getPath());
		this.duration = plugin.getConfig().getInt(ConfigPaths.STRENTH_SPELL_PATH.getPath() + "." + ConfigPaths.DURATION_PATH.getPath());

		this.max = maxLevel;
		this.base = 1;
	}

	@Override
	public void applyMiscEffect(Player player, int enchantLevel, PlayerInteractEvent event) {
		if (enchantLevel > maxLevel) {
			enchantLevel = maxLevel;
		}

		if (isOnCooldown(player, enchantLevel)) {
			player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 100, 50);
			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration * 20, enchantLevel), true);
			player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation(), 100);
			player.getWorld().spawnParticle(Particle.LAVA, player.getLocation().getX(),
					player.getLocation().getY(), player.getLocation().getZ(), 150, 0.25, 1, 0.25);
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_BLAST, 100, 50);
		}

	}
	
	private boolean isOnCooldown(Player player, int enchantLevel) {
		if (playerCooldown.containsKey(player.getUniqueId())) {
			if (System.currentTimeMillis() < playerCooldown.get(player.getUniqueId())) {
				// user is still on cool down
				int secondsRemaining = (int) (playerCooldown.get(player.getUniqueId()) - System.currentTimeMillis())
						/ 1000;
				String response = ErrorsAndMessages.ON_COOLDOWN.getErrorMessage().replace("&S", secondsRemaining + "");
				player.sendMessage(response);
				return false;
			}
		}
		// user is not on cool down
		long cooldownTime = (cooldown - (enchantLevel * 5)) * 1000;
		playerCooldown.put(player.getUniqueId(), System.currentTimeMillis() + cooldownTime);

		return true;
	}
}

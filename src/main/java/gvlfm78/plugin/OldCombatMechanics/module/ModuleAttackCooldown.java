package gvlfm78.plugin.OldCombatMechanics.module;

import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import gvlfm78.plugin.OldCombatMechanics.OCMMain;
import gvlfm78.plugin.OldCombatMechanics.utilities.Config;

/**
 * Created by Rayzr522 on 6/25/16.
 */
public class ModuleAttackCooldown extends Module {

	public ModuleAttackCooldown(OCMMain plugin) {
		super(plugin, "disable-attack-cooldown");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerJoinEvent e) {
		checkAttackSpeed(e);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldChange(PlayerChangedWorldEvent e) {
		checkAttackSpeed(e);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent e){
		Player player = e.getPlayer();
		AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
		double baseValue = attribute.getBaseValue();
		if (baseValue != 4){ //If basevalue is not 1.9 default, set it back
			attribute.setBaseValue(4);
			player.saveData();
		}
	}

	private void checkAttackSpeed(PlayerEvent e) {
		Player p = e.getPlayer();
		World world = p.getWorld();

        //If module is disabled, set attack speed to 1.9 default
		double attackSpeed = Config.moduleEnabled("disable-attack-cooldown", world) ? module().getDouble("generic-attack-speed") : 4;

		AttributeInstance attribute = p.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
		double baseValue = attribute.getBaseValue();

		if(baseValue!=attackSpeed){
			attribute.setBaseValue(attackSpeed);
			p.saveData();
		}
	}

}

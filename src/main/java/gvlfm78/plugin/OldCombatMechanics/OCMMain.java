package gvlfm78.plugin.OldCombatMechanics;

import com.codingforcookies.armourequip.ArmourListener;
import gvlfm78.plugin.OldCombatMechanics.module.*;
import gvlfm78.plugin.OldCombatMechanics.updater.ModuleUpdateChecker;
import gvlfm78.plugin.OldCombatMechanics.utilities.Config;
import gvlfm78.plugin.OldCombatMechanics.utilities.Messenger;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OCMMain extends JavaPlugin {

	private Logger logger = getLogger();
	private OCMConfigHandler CH = new OCMConfigHandler(this);
	private static OCMMain INSTANCE;

	@Override
	public void onEnable() {
	    INSTANCE = this;

		PluginDescriptionFile pdfFile = this.getDescription();

		// Setting up config.yml
		CH.setupConfig();

		// Initialise ModuleLoader utility
		ModuleLoader.initialise(this);

		// Register every event class (as well as our command handler)
		registerAllEvents();

		// Initialise the Messenger utility
		Messenger.initialise(this);

		// Initialise Config utility
		Config.initialise(this);

		// Initialise the team if it doesn't already exist
		createTeam();

		// Disabling player collision
		/*if (Config.moduleEnabled("disable-player-collision"))
			// Even though it says "restart", it works for just starting it too
			restartTask();*/

		//Remove scoreboard
		String name = "ocmInternal";
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		scoreboard.getTeam(name).unregister();

		// MCStats Metrics
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats
		}

		//BStats Metrics
		Metrics metrics = new Metrics(this);

		metrics.addCustomChart(
				new Metrics.SimpleBarChart(
						"enabled_modules",
						() -> ModuleLoader.getModules().stream()
								.filter(Module::isEnabled)
								.collect(Collectors.toMap(Module::toString, module -> 1))
				)
		);

		// Logging to console the enabling of OCM
		logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " has been enabled");

	}

	@Override
	public void onDisable() {

		PluginDescriptionFile pdfFile = this.getDescription();

		//if (task != null) task.cancel();

		// Logging to console the disabling of OCM
		logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " has been disabled");
	}

	private void registerAllEvents() {

		// Update Checker (also a module so we can use the dynamic registering/unregistering)
		ModuleLoader.addModule(new ModuleUpdateChecker(this, this.getFile()));

		// Module listeners
		ModuleLoader.addModule(new ArmourListener(this));
		ModuleLoader.addModule(new ModuleAttackCooldown(this));
		ModuleLoader.addModule(new ModulePlayerCollisions(this));

		//Apparently listeners registered after get priority
		ModuleLoader.addModule(new ModuleOldToolDamage(this));
		ModuleLoader.addModule(new ModuleSwordSweep(this));

		ModuleLoader.addModule(new ModuleGoldenApple(this));
		ModuleLoader.addModule(new ModuleFishingKnockback(this));
		ModuleLoader.addModule(new ModulePlayerRegen(this));
		ModuleLoader.addModule(new ModuleSwordBlocking(this));
		ModuleLoader.addModule(new ModuleOldArmourStrength(this));
		ModuleLoader.addModule(new ModuleDisableCrafting(this));
		ModuleLoader.addModule(new ModuleDisableOffHand(this));
		ModuleLoader.addModule(new ModuleOldBrewingStand(this));
		ModuleLoader.addModule(new ModuleDisableElytra(this));
		ModuleLoader.addModule(new ModuleDisableProjectileRandomness(this));
		ModuleLoader.addModule(new ModuleDisableBowBoost(this));
		ModuleLoader.addModule(new ModuleProjectileKnockback(this));
		ModuleLoader.addModule(new ModuleNoLapisEnchantments(this));
		ModuleLoader.addModule(new ModuleDisableEnderpearlCooldown(this));

		getCommand("OldCombatMechanics").setExecutor(new OCMCommandHandler(this, this.getFile()));// Firing commands listener
	}

	private void createTeam() {
		String name = "ocmInternal";
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

		Team team = scoreboard.getTeams().stream()
                .filter(t -> t.getName().equals(name))
                .findFirst()
                .orElse(scoreboard.registerNewTeam(name));

		team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
		team.setAllowFriendlyFire(true);
	}

	public void upgradeConfig() {
		CH.upgradeConfig();
	}

	public boolean doesConfigExist() {
		return CH.doesConfigExist();
	}

	public static OCMMain getInstance(){
	    return INSTANCE;
    }
}
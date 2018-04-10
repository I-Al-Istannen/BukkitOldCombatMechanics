package gvlfm78.plugin.OldCombatMechanics.module;

import gvlfm78.plugin.OldCombatMechanics.OCMMain;
import gvlfm78.plugin.OldCombatMechanics.utilities.ToolDamage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rayzr522 on 25/06/16.
 */
public class ModuleSwordSweep extends Module {

    private BukkitRunnable task;
    private List<Location> sweepLocations = new ArrayList<>();

    public ModuleSwordSweep(OCMMain plugin){
        super(plugin, "disable-sword-sweep");
    }

    @Override
    public void reload(){
        if(task != null){
            task.cancel();
        }

        task = new BukkitRunnable() {
            @Override
            public void run(){
                sweepLocations.clear();
            }
        };
        task.runTaskTimer(plugin, 0, 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamaged(EntityDamageByEntityEvent e){
        World world = e.getDamager().getWorld();

        if(!isEnabled(world)) return;

        if(!(e.getDamager() instanceof Player)) return;

        Player p = (Player) e.getDamager();
        ItemStack weapon = p.getInventory().getItemInMainHand();

        if(isHoldingSword(weapon.getType()))
            onSwordAttack(e, p, weapon);
    }

    private void onSwordAttack(EntityDamageByEntityEvent e, Player p, ItemStack weapon){
        //Disable sword sweep
        Location location = p.getLocation(); // ATTACKER

        int level = 0;

        try{ //In a try catch for servers that haven't updated
            level = weapon.getEnchantmentLevel(Enchantment.SWEEPING_EDGE);
        } catch(NoSuchFieldError ignored){
        }

        float damage = ToolDamage.getDamage(weapon.getType()) * level / (level + 1) + 1;

        if(e.getDamage() == damage){
            // Possibly a sword-sweep attack
            if(sweepLocations.contains(location)){
                debug("Cancelling sweep...", p);
                e.setCancelled(true);
            }
        } else {
            sweepLocations.add(location);
        }

        ModuleOldToolDamage.onAttack(e);
    }

    private boolean isHoldingSword(Material mat){
        return mat.toString().endsWith("_SWORD");
    }

}
package gvlfm78.plugin.OldCombatMechanics.tasks;

import gvlfm78.plugin.OldCombatMechanics.OCMMain;
import gvlfm78.plugin.OldCombatMechanics.utilities.Config;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.List;

public class CollisionTask extends BukkitRunnable {

    private OCMMain plugin;

    public CollisionTask(OCMMain instance) {
        this.plugin = instance;
    }

    public void run() {
        Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
        for (Player p : players) {
            addPlayerToScoreboard(p);
        }
    }

    private void addPlayerToScoreboard(Player p) {

        String name = p.getName();
        if (p.getScoreboard().getEntryTeam(p.getName()) != null) return;

        World w = p.getWorld();
        List<?> worlds = Config.getWorlds("disable-player-collision");

        if (worlds.isEmpty() || worlds.contains(w.getName())) {
            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("ocmInternal");
            if (!team.getEntries().contains(name)) {
                team.addEntry(name);
            }
        } else if (!worlds.contains(w.getName())) {
            removePlayerFromScoreboard(p);
        }
    }

    private void removePlayerFromScoreboard(Player p) {

        if (p.getScoreboard().getEntryTeam(p.getName()) != null) return;

        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("ocmInternal");
        if (team.getEntries().contains(p.getName())) {
            team.removeEntry(p.getName());
        }
    }
}
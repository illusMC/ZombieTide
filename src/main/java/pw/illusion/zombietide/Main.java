package pw.illusion.zombietide;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pw.illusion.randevent.EventManager;
import pw.illusion.randevent.events.EventStart;

import java.util.Random;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("ZombieTide loaded successfully!");
        EventManager.getInstance().registerRandEvent("zombie_tide", () -> new Random().nextInt(30) == 1 && getServer().getWorld(getConfig().getString("world")).getTime() >= getConfig().getInt("time"));
        Bukkit.getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        getLogger().info("ZombieTide disabled.");
    }

    @EventHandler
    void onRandomEvent(EventStart event) {
        if ("zombie_tide".equals(event.getEventName())) {
            Bukkit.getOnlinePlayers().stream().filter(s -> getConfig().getString("world").equals(s.getWorld().getName()) && new Random().nextInt(10) == 1).forEach(this::summonZombies);
        }
    }

    void summonZombies(Player player) {
        Location loc = player.getLocation();
        loc.add(new Random().nextInt(40) - 20, 0, new Random().nextInt(40) - 20);
    }

}
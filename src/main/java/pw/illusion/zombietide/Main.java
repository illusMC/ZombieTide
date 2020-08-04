package pw.illusion.zombietide;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pw.illusion.randevent.EventManager;
import pw.illusion.randevent.events.EventStart;

import java.util.Objects;
import java.util.Random;

/**
 * @author Yoooooory
 */
public class Main extends JavaPlugin implements Listener {

    private final Random random = new Random();
    private BukkitTask process = null;
    private final String world = Objects.requireNonNull(getConfig().getString("world"));

    @Override
    public void onEnable() {
        saveDefaultConfig();
        EventManager.getInstance().registerRandEvent("zombie_tide", () -> {
            if (process != null && !process.isCancelled()) return false;
            return (random.nextInt(30) == 1 && Objects.requireNonNull(Bukkit.getWorld(world)).getTime() > getConfig().getLong("time"));
        });
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("ZombieTide loaded successfully!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ZombieTide disabled.");
    }

    @EventHandler
    void onRandomEvent(EventStart event) {
        if ("zombie_tide".equals(event.getEventName())) {
            World omgWorld = Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("world")));
            assert omgWorld != null : String.format("Unknown world '%s'", getConfig().getString("world"));
            process = new BukkitRunnable() {
                @Override
                public void run() {
                    if (omgWorld.getTime() < getConfig().getLong("time")) {
                        cancel();
                    }
                    omgWorld.getPlayers().forEach(s -> pinniganjuequxie(s));
                }
            }.runTaskTimer(this, 0L, getConfig().getInt("interval") * 20L);
        }
    }

    /**
     * Summon some zombies near a player.
     * ( Method name: summonZombies )
     *
     * @param player The player.
     */
    void pinniganjuequxie(Player player) {
        Location center = getValidLocation(player.getLocation().add(random.nextInt(20), 0, random.nextInt(20)));
        if (Math.abs(center.getY() - player.getLocation().getY()) > 8) return;
        World world = player.getWorld();
        world.spawnEntity(center, EntityType.ZOMBIE);
        // Try to summon zombies.
        for (int i = 0; i < random.nextInt(20); i++) {
            if (random.nextInt(4) != 1) continue;
            Location loc = getValidLocation(center.clone().add(random.nextInt(6), 0, random.nextInt(6)));
            if (Math.abs(loc.getY() - center.getY()) <= 4) {
                world.spawnEntity(center, EntityType.ZOMBIE);
            }
        }
        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, Float.MAX_VALUE, 1);
        player.sendTitle("", "§r一个大波僵尸正在接近", 2, 60, 15);
    }

    Location getValidLocation(Location loc) {
        // Magic code, don't delete! (神奇代码 勿删)
        if (isLocationEmpty(loc) && isLocationEmpty(loc.clone().add(0, 1, 0))) {
            // A valid location, but may be floating.
            do {
                loc.add(0, -1, 0);
            } while (isLocationEmpty(loc));
            return loc.add(0, 1, 0);
        } else {
            // An invalid location.
            do {
                loc.add(0, 1, 0);
            } while (!isLocationEmpty(loc));
            return loc;
        }
    }

    boolean isLocationEmpty(Location loc) {
        return loc.getBlock().isEmpty();
    }

}
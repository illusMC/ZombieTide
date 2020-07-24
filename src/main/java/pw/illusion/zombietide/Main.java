package pw.illusion.zombietide;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import pw.illusion.randevent.EventManager;
import pw.illusion.randevent.events.EventStart;

import java.util.Objects;
import java.util.Random;

public class Main extends JavaPlugin implements Listener {
    private static final Random random=new Random();
    private boolean started=false;
    private BukkitTask process;
    @Override
    public void onEnable() {
        getLogger().info("ZombieTide loaded successfully!");
        saveDefaultConfig();
        EventManager.getInstance().registerRandEvent("zombie_tide", () -> {
            if(started) return false;
            return (random.nextInt(30)==1 && Bukkit.getWorld(getConfig().getString("world")).getTime()>getConfig().getLong("time"));
        });
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("ZombieTide disabled.");
    }

    @EventHandler
    void onRandomEvent(EventStart event) {
        if ("zombie_tide".equals(event.getEventName())) {
            started=true;
            process = Bukkit.getScheduler().runTaskTimer(this,()->{
                World omgWorld=Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("world")));
                if(omgWorld.getTime()<getConfig().getLong("time")){
                    started=false;
                    process.cancel();
                }
                omgWorld.getPlayers().stream().filter(s->!s.isSleeping()).forEach(p->{
                    summonZombies(p);
                });
            },0L,getConfig().getInt("interval")*20L);
        }
    }

    void summonZombies(Player player) {
        Location loc = player.getLocation();
        int debug=0;
        long time=System.currentTimeMillis();
        do {
            debug++;
            loc.add(Vector.getRandom()).setY(getY(loc, player));
        }
        while(loc.distance(player.getLocation())<5 || loc.distance(player.getLocation())>15 && canBeOnGround(loc));
        getLogger().info("DEBUGGING : loop for "+debug+" times! Take "+(System.currentTimeMillis()-time)+"ms.");
        loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
    }
    double getY(Location loc,Player player){
        if(loc.getBlock().getType()== Material.AIR && isHeadAir(loc)){
            return loc.getY();
        }
        //else..
        Location loc2=loc;
        for(int i=0;i<=3;i++){ //search up..
            loc2.setY(loc2.getY()+1);
            if(loc2.getBlock().getType()==Material.AIR && isHeadAir(loc2)){
                return loc2.getY();
            }
        }
        loc2=loc;
        for(int i=0;i<=3;i++){ //search down..
            loc2.setY(loc2.getY()-1);
            if(loc2.getBlock().getType()==Material.AIR && isHeadAir(loc2)){
                return loc2.getY();
            }
        }
        return loc.getY();
    }
    boolean isHeadAir(Location loc){
        Location head=loc;
        head.setY(head.getY()+1);
        return head.getBlock().getType()==Material.AIR;
    }
    boolean canBeOnGround(Location loc){
        Location ground=loc;
        ground.setY(loc.getY()-1);
        Material type=ground.getBlock().getType();
        return type!=Material.AIR && !type.name().contains("GATE") && !type.name().contains("DOOR");
    }


}
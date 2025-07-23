package com.j3ly.deathnotif;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathMessagesPlugin extends JavaPlugin {

    private final EntityListener entityListener = new EntityListener() {
        @Override
        public void onEntityDeath(EntityDeathEvent event) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                String message = player.getName() + " has died! RIP.";
                getServer().broadcastMessage(message);
            }
        }
    };

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
        System.out.println("[DeathMessages] Plugin enabled.");
    }

    @Override
    public void onDisable() {
        System.out.println("[DeathMessages] Plugin disabled.");
    }
}

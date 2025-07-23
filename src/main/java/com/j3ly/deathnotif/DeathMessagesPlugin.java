package com.j3ly.deathnotif;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DeathMessagesPlugin extends JavaPlugin {

    private final Random random = new Random();
    private List<String> messages = new ArrayList<String>();

    private final EntityListener entityListener = new EntityListener() {
        @Override
        public void onEntityDeath(EntityDeathEvent event) {
            if (event.getEntity() instanceof Player && messages != null && !messages.isEmpty()) {
                Player player = (Player) event.getEntity();
                String randomMessage = messages.get(random.nextInt(messages.size()));
                String message = player.getName() + randomMessage;
                getServer().broadcastMessage(message);
            }
        }
    };

    @Override
    public void onEnable() {
        createDefaultConfig();
        loadMessagesFromConfig();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);

        System.out.println("[DeathMessages] Plugin enabled.");
    }

    @Override
    public void onDisable() {
        System.out.println("[DeathMessages] Plugin disabled.");
    }

    private void createDefaultConfig() {
        try {
            File folder = getDataFolder();
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File configFile = new File(folder, "config.yml");
            if (!configFile.exists()) {
                FileWriter writer = new FileWriter(configFile);
                writer.write("death-messages:\n");
                writer.write("  - \" has died! RIP.\"\n");
                writer.write("  - \" died LMFAOO\"\n");
                writer.write("  - \" killed them self\"\n");
                writer.write("  - \" died in a hole\"\n");
                writer.write("  - \" jumped\"\n");
                writer.write("  - \" did a Epstein\"\n");
                writer.close();
            }
        } catch (Exception e) {
            System.out.println("[DeathMessages] Error creating default config: " + e.getMessage());
        }
    }

    private void loadMessagesFromConfig() {
        try {
            File configFile = new File(getDataFolder(), "config.yml");
            if (!configFile.exists()) return;

            InputStream input = new FileInputStream(configFile);
            Yaml yaml = new Yaml();
            Object rawData = yaml.load(input);
            if (!(rawData instanceof Map)) {
                System.out.println("[DeathMessages] Invalid config format.");
                return;
            }
            Map<?, ?> rawMap = (Map<?, ?>) rawData;
            Object listObj = rawMap.get("death-messages");

            if (listObj instanceof List<?>) {
                for (Object o : (List<?>) listObj) {
                    if (o instanceof String) {
                        messages.add((String) o);
                    }
                }
            }

            if (messages.isEmpty()) {
                System.out.println("[DeathMessages] No messages found in config.");
            }

        } catch (Exception e) {
            System.out.println("[DeathMessages] Failed to load messages: " + e.getMessage());
        }
    }
}

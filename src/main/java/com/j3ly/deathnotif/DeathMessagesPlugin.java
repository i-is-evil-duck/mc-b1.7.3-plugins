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
import java.io.FileWriter;
import java.io.InputStream;
import java.util.*;

public class DeathMessagesPlugin extends JavaPlugin {

    private final Random random = new Random();
    private final List<String> defaultMessages = new ArrayList<String>();
    private final Map<String, List<String>> playerMessages = new HashMap<String, List<String>>();

    private final EntityListener entityListener = new EntityListener() {
        @Override
        public void onEntityDeath(EntityDeathEvent event) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                String playerName = player.getName().toLowerCase();

                List<String> chosenMessages = playerMessages.getOrDefault(playerName, defaultMessages);
                if (chosenMessages == null || chosenMessages.isEmpty()) return;

                String msg = chosenMessages.get(random.nextInt(chosenMessages.size()));
                getServer().broadcastMessage(player.getName() + msg);
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
                writer.write("default-messages:\n");
                writer.write("  - \" has died! RIP.\"\n");
                writer.write("  - \" died LMFAOO\"\n");
                writer.write("  - \" killed them self\"\n");
                writer.write("  - \" died in a hole\"\n");
                writer.write("  - \" jumped\"\n");
                writer.write("  - \" did a Epstein\"\n");
                writer.write("\n");
                writer.write("players:\n");
                writer.write("  mikey:\n");
                writer.write("    - \" slipped on a banana\"\n");
                writer.write("    - \" farted too hard\"\n");
                writer.write("    - \" has died! RIP.\"\n");
                writer.write("    - \" did a Epstein\"\n");
                writer.write("    - \" tripped over their ego\"\n");
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
            input.close();

            if (!(rawData instanceof Map)) {
                System.out.println("[DeathMessages] Invalid config format.");
                return;
            }

            Map<?, ?> root = (Map<?, ?>) rawData;

            // Load default messages
            Object defMsgObj = root.get("default-messages");
            if (defMsgObj instanceof List<?>) {
                for (Object o : (List<?>) defMsgObj) {
                    if (o instanceof String) {
                        defaultMessages.add((String) o);
                    }
                }
            }

            // Load per-player messages
            Object playersObj = root.get("players");
            if (playersObj instanceof Map<?, ?>) {
                Map<?, ?> playerMap = (Map<?, ?>) playersObj;
                for (Map.Entry<?, ?> entry : playerMap.entrySet()) {
                    String playerName = entry.getKey().toString().toLowerCase();
                    Object msgList = entry.getValue();
                    if (msgList instanceof List<?>) {
                        List<String> customList = new ArrayList<String>();
                        for (Object o : (List<?>) msgList) {
                            if (o instanceof String) {
                                customList.add((String) o);
                            }
                        }
                        if (!customList.isEmpty()) {
                            playerMessages.put(playerName, customList);
                        }
                    }
                }
            }

            if (defaultMessages.isEmpty()) {
                System.out.println("[DeathMessages] Warning: No default messages found!");
            }

        } catch (Exception e) {
            System.out.println("[DeathMessages] Failed to load config: " + e.getMessage());
        }
    }
}

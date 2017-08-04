package net.goldiriath.plugin.game.questing.quest;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.questing.quest.requirement.RequirementList;
import net.goldiriath.plugin.game.questing.quest.requirement.RequirementParser;
import net.goldiriath.plugin.game.questing.script.Script;
import net.goldiriath.plugin.game.questing.script.ScriptContext;
import net.goldiriath.plugin.game.questing.script.ScriptParser;
import net.goldiriath.plugin.util.PrefixFileFilter;
import net.goldiriath.plugin.util.service.AbstractService;
import net.pravian.aero.config.YamlConfig;
import net.pravian.aero.util.Plugins;
import org.apache.commons.lang.exception.ExceptionUtils;

public class QuestManager extends AbstractService {

    @Getter
    private final File questContainer;
    @Getter
    private final Map<String, Quest> questMap;
    private final Map<String, RequirementList> globalRequirements;
    private final Map<String, Script> globalScripts;

    public QuestManager(Goldiriath plugin) {
        super(plugin);
        this.questContainer = new File(Plugins.getPluginDataFolder(plugin), "quests");
        this.questMap = new HashMap<>();
        this.globalRequirements = new HashMap<>();
        this.globalScripts = new HashMap<>();
    }

    @Override
    public void onInit() {
        // Ensure folder is present
        if (questContainer.isFile()) {
            if (!questContainer.delete()) {
                logger.severe("Not loading quests! Could not delete file: " + questContainer.getAbsolutePath());
                return;
            }
        }
        if (!questContainer.exists()) {
            questContainer.mkdirs();
        }

        // Preload quests
        questMap.clear();
        for (File file : questContainer.listFiles(new PrefixFileFilter(plugin, "quest"))) {
            String id = parseQuestId(file);

            if (id.isEmpty() || questMap.containsKey(id)) {
                logger.warning("Skipping quest file: " + file.getName() + ". Invalid quest ID!");
                continue;
            }

            questMap.put(id, new Quest(this, id));
        }
    }

    @Override
    public void onStart() {
        if (questContainer.isFile()) {
            return;
        }

        // Load globals
        final File globalFile = new File(questContainer, "global.yml");
        if (globalFile.exists()) {
            final YamlConfig globals = new YamlConfig(plugin, globalFile, false);
            globals.load();

            // Requirements
            globalRequirements.clear();
            if (!globals.isConfigurationSection("requirements")) {
                logger.warning("Not loading global requirements: global.yml does not contain section: 'requirements'");
            } else {
                for (String id : globals.getConfigurationSection("requirements").getKeys(false)) {
                    final RequirementParser parser = new RequirementParser(plugin, "global");
                    globalRequirements.put(id, parser.parse(globals.getStringList("requirements." + id)));
                }
            }

            // Actions
            globalScripts.clear();
            if (!globals.isConfigurationSection("actions")) {
                logger.warning("Not loading global actions: global.yml does not contain section: 'actions'");
            } else {
                for (String id : globals.getConfigurationSection("script").getKeys(false)) {
                    final ScriptParser parser = new ScriptParser(plugin, new ScriptContext());
                    globalScripts.put(id, parser.parse(globals.getStringList("script." + id)));
                }
            }
        }

        // Load questMap
        for (File file : questContainer.listFiles(new PrefixFileFilter(plugin, "quest"))) {
            final String id = parseQuestId(file);
            final Quest quest = questMap.get(id);
            if (quest == null) {
                // Assume onInit() already printed an error
                continue;
            }

            final YamlConfig config = new YamlConfig(plugin, file, false);
            config.load();

            try {
                quest.loadFrom(config);
            } catch (Exception ex) {
                logger.warning("Skipping quest: " + id + ". Exception loading quest!");
                logger.severe(ExceptionUtils.getFullStackTrace(ex));
            }

            if (!quest.isValid()) {
                logger.warning("Skipping quest: " + id + ". Invalid quest! (Are there missing entries?)");
                continue;
            }

            questMap.put(id, quest);
        }

        int stages = 0;
        for (Quest quest : questMap.values()) {
            stages += quest.getStages().size();
        }
        logger.info("Loaded " + stages + " stages for " + questMap.size() + " quests");
    }

    @Override
    public void onStop() {
    }

    private String parseQuestId(File file) {
        return file.getName().replace("quest_", "").replace(".yml", "").trim().toLowerCase();
    }

    public Map<String, Script> getGlobalScripts() {
        return Collections.unmodifiableMap(globalScripts);
    }

    public Map<String, RequirementList> getGlobalRequirements() {
        return Collections.unmodifiableMap(globalRequirements);
    }

    public Quest getQuest(String id) {
        return questMap.get(id);
    }

}

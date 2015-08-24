package me.dirkjan.goldiriath.quest;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.quest.action.ActionList;
import me.dirkjan.goldiriath.quest.action.ActionParser;
import me.dirkjan.goldiriath.quest.requirement.RequirementList;
import me.dirkjan.goldiriath.quest.requirement.RequirementParser;
import me.dirkjan.goldiriath.util.service.AbstractService;
import net.pravian.bukkitlib.config.YamlConfig;
import net.pravian.bukkitlib.util.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

public class QuestManager extends AbstractService {

    private final File questContainer;
    private final Map<String, Quest> quests;
    private final Map<String, RequirementList> globalRequirements;
    private final Map<String, ActionList> globalActions;

    public QuestManager(Goldiriath plugin) {
        super(plugin);
        this.questContainer = new File(FileUtils.getPluginDataFolder(plugin), "quests");
        this.quests = new HashMap<>();
        this.globalRequirements = new HashMap<>();
        this.globalActions = new HashMap<>();
    }

    @Override
    public void onStart() {

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
            globalActions.clear();
            if (!globals.isConfigurationSection("actions")) {
                logger.warning("Not loading global actions: global.yml does not contain section: 'actions'");
            } else {
                for (String id : globals.getConfigurationSection("actions").getKeys(false)) {
                    final ActionParser parser = new ActionParser(plugin, "global");
                    globalActions.put(id, parser.parse(globals.getStringList("actions." + id)));
                }
            }
        }

        // Load quests
        quests.clear();
        for (File file : questContainer.listFiles(new QuestFileFilter(plugin))) {

            final String id = file.getName().replace("quest_", "").replace(".yml", "").trim().toLowerCase();

            if (id.isEmpty() || quests.containsKey(id)) {
                logger.warning("Skipping quest file: " + file.getName() + ". Invalid quest ID!");
                continue;
            }

            final YamlConfig config = new YamlConfig(plugin, file, false);
            config.load();

            final Quest quest = new Quest(this, id);

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

            quests.put(id, quest);
        }
    }

    @Override
    public void onStop() {
    }

    public Map<String, ActionList> getGlobalActions() {
        return Collections.unmodifiableMap(globalActions);
    }

    public Map<String, RequirementList> getGlobalRequirements() {
        return Collections.unmodifiableMap(globalRequirements);
    }

    public Quest getQuest(String id) {
        return quests.get(id);
    }

}

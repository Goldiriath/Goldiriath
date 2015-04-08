package me.dirkjan.goldiriath.skills;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import me.dirkjan.goldiriath.Goldriath;
import net.pravian.bukkitlib.config.YamlConfig;
import org.bukkit.configuration.ConfigurationSection;

public class SkillManager {

    private final Goldriath plugin;
    public final YamlConfig skillConfig;
    private final HashMap<UUID, Set<Skill>> skillmap = new HashMap<>();

    public SkillManager(Goldriath plugin) {
        this.plugin = plugin;
        skillConfig = new YamlConfig(plugin, "skill.yml", false);
    }

    public void save() {
        skillConfig.clear();
        for (UUID uuid : skillmap.keySet()) {
            ConfigurationSection section = skillConfig.createSection(uuid.toString());
            for (Skill skill : skillmap.get(uuid)) {
                section.set(skill.getType().getName() + ".lvl", skill.getLvl());
                    String skillstring = skill.getClass().toString();
                    int lvlString = skill.getLvl();
                    String typestring = skill.getType().toString();
                    section.set("skillname.", skillstring);
                    section.set("skill lvl.", lvlString);
                    section.set("skilltype.", typestring);
            }
            
        }
        skillConfig.save();

    }

    public void load() {
        skillmap.clear();
        skillConfig.load();
        
    }
}

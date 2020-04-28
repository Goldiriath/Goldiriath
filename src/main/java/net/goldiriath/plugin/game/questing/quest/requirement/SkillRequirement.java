package net.goldiriath.plugin.game.questing.quest.requirement;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.Message;
import net.goldiriath.plugin.game.questing.script.ParseException;
import net.goldiriath.plugin.game.skill.SkillType;
import org.bukkit.entity.Player;

public class SkillRequirement extends AbstractRequirement {

    private final SkillType skill;

    public SkillRequirement(Goldiriath plugin, String[] args) {
        super(plugin, Message.QUEST_SKILL_NOT_OWNED);
        skill = SkillType.fromName(args[1]);
        if (skill == null) {
            throw new ParseException("Skill '" + args[1] + "' is not known.");
        }
    }

    @Override
    public boolean has(Player player) {
        return plugin.pym.getData(player).getSkills().getSkills().containsKey(skill);
    }

}

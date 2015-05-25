package me.dirkjan.goldiriath.quest.requirement;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.Message;
import me.dirkjan.goldiriath.quest.ParseException;
import me.dirkjan.goldiriath.skill.SkillType;
import org.bukkit.entity.Player;

public class SkillRequirement extends AbstractRequirement{

    private final SkillType skill;
    
    public SkillRequirement(Goldiriath plugin, String[] args) {
        super(plugin, Message.QUEST_SKILL_NOT_OWNED);
        skill = SkillType.fromName(args[1]);
        if (skill == null){
            throw new ParseException("Skill '" + args[1] + "' is not known.");
        }
    }

    @Override
    public boolean has(Player player) {
         return plugin.pm.getData(player).hasSkill(skill);
    }
        

}
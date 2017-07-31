package net.goldiriath.plugin.command;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.Message;
import net.goldiriath.plugin.game.skill.SkillMeta;
import net.goldiriath.plugin.game.skill.SkillType;
import net.goldiriath.plugin.game.skill.type.Skill;
import net.goldiriath.plugin.player.data.DataSkills;
import net.pravian.aero.command.CommandOptions;
import net.pravian.aero.command.SimpleCommand;
import net.pravian.aero.command.SourceType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandOptions(
        description = "Manage player skills",
        usage = "/<command> <list | <player> <list | set <skill> <level>>>",
        subPermission = "gskill",
        source = SourceType.ANY,
        aliases = "gsk")
public class Command_gskill extends SimpleCommand<Goldiriath> {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equals("list")) {
            for (SkillType skill : SkillType.values()) {
                msg(
                        ChatColor.DARK_AQUA + "-> "
                        + ChatColor.GOLD + skill.getName());

            }
            return true;
        }

        if (args.length < 2) {
            return showUsage();
        }

        Player player = getPlayer(args[0]);
        if (player == null) {
            msg(Message.COMMAND_PLAYER_NOT_FOUND);
            return true;
        }

        if (args[1].equals("list")) {
            DataSkills data = plugin.pm.getData(player).getSkills();
            if (data.getSkills().isEmpty()) {
                msg(ChatColor.GOLD + "That player has no skills (He's a scrub).");
                return true;
            }

            for (Skill skill : data.getSkills().values()) {
                msg(
                        ChatColor.DARK_AQUA + "-> "
                        + ChatColor.GOLD + skill.getType().getName()
                        + ChatColor.WHITE + ": "
                        + ChatColor.GREEN + skill.getMeta().level);

            }
            return true;
        }

        if (args.length != 4) {
            return showUsage();
        }

        if (!args[1].equals("set")) {
            return showUsage();
        }

        SkillType type;
        try {
            type = SkillType.valueOf(args[2]);
        } catch (IllegalArgumentException ex) {
            msg(Message.COMMAND_SKILL_NOT_FOUND);
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[3]);
        } catch (NumberFormatException ex) {
            msg(ChatColor.RED + "Invalid Level");
            return true;
        }

        DataSkills data = plugin.pm.getData(player).getSkills();

        if (level <= 0) {
            data.getSkills().remove(type);
            player.sendMessage(ChatColor.DARK_GREEN + "Removed " + player.getName() + "'s " + type.getName() + " skill");
            return true;
        }

        Skill skill;
        if (data.getSkills().containsKey(type)) {
            skill = data.getSkills().get(type);
        } else {
            skill = type.create(player, new SkillMeta(type));
            data.getSkills().put(type, skill);
        }

        skill.getMeta().level = level;
        player.sendMessage(ChatColor.DARK_GREEN + "Set " + player.getName() + "'s " + type.getName() + " skill level to " + level);

        return true;
    }

}

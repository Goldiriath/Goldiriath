package net.goldiriath.plugin.command;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.questing.quest.Quest;
import net.goldiriath.plugin.game.questing.quest.Stage;
import net.goldiriath.plugin.player.data.DataQuests;
import net.pravian.aero.command.CommandOptions;
import net.pravian.aero.command.SimpleCommand;
import net.pravian.aero.command.SourceType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandOptions(
        description = "View and set a player's current quest stage",
        usage = "/<command> <player> <list | <quest> [newstage]>",
        subPermission = "gquest",
        source = SourceType.PLAYER,
        aliases = "gq")
public class Command_gquest extends SimpleCommand<Goldiriath> {

    // /<command> <player> <list | <quest> [newstage]>
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length == 0) {
            return false;
        }

        Player player = getPlayer(args[0]);
        if (player == null) {
            msg(ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }

        final DataQuests quests = plugin.pym.getData(player).getQuests();

        if (args[1].equals("list")) {
            for (Quest quest : plugin.qst.getQuestMap().values()) {
                final Stage stage = quests.getStage(quest);
                msg(ChatColor.GREEN + "+ " + ChatColor.GOLD + quest.getId() + ChatColor.GRAY + " - " + getDisplay(quest, stage));
            }
            return true;
        }

        Quest quest = plugin.qst.getQuest(args[1]);
        if (quest == null) {
            msg(ChatColor.RED + "Quest not found: " + args[1]);
            return true;
        }

        if (args[2].equals("reset")) {
            quests.reset(quest);
            msg(ChatColor.GREEN + "Reset player's " + quest.getId() + " quest");
            return true;
        }

        final Stage newStage = quest.getStageMap().get(args[2]);
        if (newStage == null) {
            msg(ChatColor.RED + "Stage not found: " + args[2]);
            return true;
        }

        quests.setStage(quest, newStage);
        msg(ChatColor.GREEN
                + "Set player's quest stage to "
                + newStage == null ? "unstarted" : newStage.getId()
                        + " for " + quest.getId());
        return true;
    }

    private String getDisplay(Quest quest, Stage stage) {
        if (stage == null) {
            return ChatColor.RED + "unstarted";
        } else if (stage.equals(quest.getCancelStage())) {
            return ChatColor.RED + "cancelled";
        } else if (stage.equals(quest.getCompleteStage())) {
            return ChatColor.GREEN + "complete";
        } else if (stage.equals(quest.getEntryStage())) {
            return ChatColor.AQUA + "entry";
        } else {
            return ChatColor.GOLD + stage.getId();
        }
    }

}

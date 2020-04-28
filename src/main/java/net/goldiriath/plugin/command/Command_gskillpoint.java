package net.goldiriath.plugin.command;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.player.PlayerData;
import net.pravian.aero.command.CommandOptions;
import net.pravian.aero.command.SimpleCommand;
import net.pravian.aero.command.SourceType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandOptions(
        description = "Manage player skillpoints",
        usage = "/<command> <set | add> <amount>",
        subPermission = "gskillpoint",
        source = SourceType.PLAYER,
        aliases = "gsp")
public class Command_gskillpoint extends SimpleCommand<Goldiriath> {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerData data = plugin.pm.getData(playerSender);
        if (args.length < 2 || args.length > 2) {
            return showUsage();
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (Exception NumberFormatException) {
            msg("Invalid amount!");
            return true;
        }

        if (amount < 0) {
            msg("Skillpoints cannot be lower than 0");
            return true;
        }

        if (args[0].equals("add")) {
            data.setSkillPoints(data.getSkillPoints() + amount);
            msg("set skillpoints to " + data.getSkillPoints());
            return true;
        }
        if (args[0].equals("set")) {
            data.setSkillPoints(amount);
            msg("set skillpoints to " + amount);
            return true;
        }

        return showUsage();
    }
}

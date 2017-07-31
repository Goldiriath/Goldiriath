package net.goldiriath.plugin.command;

import net.goldiriath.plugin.Goldiriath;
import net.pravian.aero.command.CommandOptions;
import net.pravian.aero.command.SimpleCommand;
import net.pravian.aero.command.SourceType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandOptions(
        description = "Enable and disable development mode",
        subPermission = "gdev",
        usage = "/<command> <on | off>",
        source = SourceType.ANY)
public class Command_gdev extends SimpleCommand<Goldiriath> {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length != 1) {
            return false;
        }

        if (args[0].equals("on")) {
            msg("Turning development mode on.");
            plugin.dev.setDevMode(true);
            return true;
        }

        msg("Turning development mode off.");
        plugin.dev.setDevMode(false);
        return true;

    }

}

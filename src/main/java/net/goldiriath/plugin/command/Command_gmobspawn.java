package net.goldiriath.plugin.command;

import net.goldiriath.plugin.Goldiriath;
import net.pravian.aero.command.CommandOptions;
import net.pravian.aero.command.SimpleCommand;
import net.pravian.aero.command.SourceType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandOptions(
        description = "Manages mobspawns",
        usage = "/<command> dev <on|off>",
        subPermission = "gmobspawn",
        source = SourceType.PLAYER,
        aliases = "gmob,gms")
public class Command_gmobspawn extends SimpleCommand<Goldiriath> {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length == 0) {
            return false;
        }

        if (args[0].equals("dev")) {

            if (args.length == 1) {
                msg("MobSpawn development mode is " + (plugin.msm.isDevMode() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
                return true;
            }

            // TODO: Fix its own command
            if (args[1].equals("on")) {
                plugin.msm.setDevMode(true);
                plugin.lm.setDevMode(true);
            } else {
                plugin.msm.setDevMode(false);
                plugin.lm.setDevMode(false);
            }

            msg("MobSpawn development mode " + (plugin.msm.isDevMode() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
            return true;
        }

        return false;
    }

}

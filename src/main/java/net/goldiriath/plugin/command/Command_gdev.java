package net.goldiriath.plugin.command;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.player.PlayerData;
import net.pravian.aero.command.CommandOptions;
import net.pravian.aero.command.SimpleCommand;
import net.pravian.aero.command.SourceType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandOptions(description = "Development command", subPermission = "gdev", usage = "/<command> <values...>", source = SourceType.PLAYER)
public class Command_gdev extends SimpleCommand<Goldiriath> {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length != 1) {
            return false;
        }

        try {
            final PlayerData data = plugin.pm.getData(playerSender);
            int newMoney = Integer.parseInt(args[0]);
            msg("Prev: " + data.getMoney());
            data.setMoney(newMoney);
            msg("New: " + data.getMoney());
            plugin.pm.saveAll();
        } catch (NumberFormatException nex) {
            msg("Can't parse number!");
            return true;
        }

        return true;

    }

}

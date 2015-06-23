package me.dirkjan.goldiriath.command;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.player.PlayerData;
import net.pravian.bukkitlib.command.BukkitCommand;
import net.pravian.bukkitlib.command.CommandPermissions;
import net.pravian.bukkitlib.command.SourceType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(permission = "goldiriath.godev", source = SourceType.PLAYER)
public class Command_godev extends BukkitCommand<Goldiriath> {

    @Override
    protected boolean run(CommandSender sender, Command command, String commandLabel, String[] args) {

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

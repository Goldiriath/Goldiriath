package me.dirkjan.goldiriath.command;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.dialog.NPCDialogHandler;
import me.dirkjan.goldiriath.dialog.OptionSet;
import me.dirkjan.goldiriath.dialog.OptionSet.Option;
import me.dirkjan.goldiriath.player.PlayerData;
import net.pravian.bukkitlib.command.BukkitCommand;
import net.pravian.bukkitlib.command.CommandPermissions;
import net.pravian.bukkitlib.command.SourceType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(source = SourceType.PLAYER)
public class Command_option extends BukkitCommand<Goldiriath> {

    @Override
    protected boolean run(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length != 3) {
            return false;
        }

        // Args: DialogHandler ID, OptionSet ID, Option ID
        final NPCDialogHandler handler = plugin.dm.getHandlers().get(args[0]);
        if (handler == null) {
            msg("Could not find dialog handler: " + args[0], ChatColor.RED);
            return true;
        }

        final OptionSet optionSet = handler.getOptionsMap().get(args[1]);
        if (optionSet == null) {
            msg("Could not find option set: " + args[1], ChatColor.RED);
            return true;
        }

        Option option = null;
        for (Option loopOption : optionSet.getOptions()) {
            if (loopOption.getId().equals(args[2])) {
                option = loopOption;
                break;
            }
        }

        final PlayerData gp = plugin.pm.getData(playerSender);
        if (!optionSet.equals(gp.getCurrentOption())) {
            msg("Choice is no longer available.", ChatColor.RED);
            return true;
        }

        if (option == null) {
            msg("Could not find option: " + args[2], ChatColor.RED);
            return true;
        }

        gp.endOption();
        gp.startDialog(option.getDialog());
        return true;

    }

}

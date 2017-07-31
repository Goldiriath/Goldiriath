package net.goldiriath.plugin.command;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.questing.dialog.NPCDialogHandler;
import net.goldiriath.plugin.game.questing.dialog.OptionSet;
import net.goldiriath.plugin.game.questing.dialog.OptionSet.Option;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.player.info.InfoDialogs;
import net.pravian.aero.command.CommandOptions;
import net.pravian.aero.command.SimpleCommand;
import net.pravian.aero.command.SourceType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandOptions(
        description = "Not for manual use! Selects a dialog option",
        usage = "/<command> <dialog> <optionset> <option>",
        source = SourceType.PLAYER,
        aliases = "gopt")
public class Command_goption extends SimpleCommand<Goldiriath> {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length != 3) {
            return false;
        }

        // Args: DialogHandler ID, OptionSet ID, Option ID
        final NPCDialogHandler handler = plugin.dlm.getHandlers().get(args[0]);
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
        final InfoDialogs info = gp.getDialogs();
        if (!optionSet.equals(info.getCurrentOption())) {
            msg("Choice is no longer available.", ChatColor.RED);
            return true;
        }

        if (option == null) {
            msg("Could not find option: " + args[2], ChatColor.RED);
            return true;
        }

        info.endOption();
        info.start(option.getDialog());
        return true;

    }

}

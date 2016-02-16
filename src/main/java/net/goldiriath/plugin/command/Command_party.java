package net.goldiriath.plugin.command;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.chat.Party;
import net.goldiriath.plugin.player.info.InfoChat;
import net.pravian.bukkitlib.command.BukkitCommand;
import net.pravian.bukkitlib.command.CommandPermissions;
import net.pravian.bukkitlib.command.SourceType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(source = SourceType.PLAYER)
public class Command_party extends BukkitCommand<Goldiriath> {

    @Override
    protected boolean run(CommandSender sender, Command command, String commandLabel, String[] args) {

        InfoChat chat = plugin.pm.getData(playerSender).getChat();
        Party party = chat.getParty();
        if (args.length == 1) {
            if (party == null) {
                msg("You have to be in party to perform this action");
                return true;
            }
            if (args[0].equals("leave")) {
                party.removeMember(playerSender);
                return true;
            }
            if (args[0].equals("disband")) {
                if (!party.getLeader().equals(playerSender)) {
                    playerSender.sendMessage("You can only disband a party if you are the leader");
                    return true;
                }
                party.disband();

            }

            return false;
        }
        if (args.length == 2) {
            if (args[0].equals("invite")) {
                Player invited = getPlayer(args[1]);
                if (invited == null) {
                    msg("Could not find player");
                    return true;
                }
                if (party == null) {
                    party = new Party(playerSender);
                }
                party.addMember(invited);
                msg("Succesfully invited " + invited.getName());
                return true;

            }
            if (args[0].equals("kick")) {
                if (!(party == null || party.getLeader().equals(playerSender))) {
                    playerSender.sendMessage("You need to be party leader to kick someone");
                    return true;
                }
                Player invited = getPlayer(args[1]);
                if (invited == null) {
                    msg("Could not find player");
                    return true;
                }
                party.removeMember(invited);
                msg(invited + " succesfully kicked from the party");

                return true;
            }

        }
        return false;
    }
}

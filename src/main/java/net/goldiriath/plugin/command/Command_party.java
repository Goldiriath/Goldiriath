package net.goldiriath.plugin.command;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.chat.Party;
import net.goldiriath.plugin.chat.PartyOptionSet;
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
                PartyOptionSet optionSet = new PartyOptionSet(playerSender);
                Player invited = getPlayer(args[1]);
                if (invited == null) {
                    msg("Could not find player" + invited.getName());
                    return true;
                }
                if (party == null) {
                    party = chat.createParty();
                }
                party.invite(invited);
                msg("Succesfully invited " + invited.getName());
                optionSet.getMessage().send(invited);
                return true;

            }
            if (args[0].equals("kick")) {
                if (party == null || !party.getLeader().equals(playerSender)) {
                    playerSender.sendMessage("You need to be party leader to kick someone");
                    return true;
                }
                Player kicked = getPlayer(args[1]);
                if (kicked == null) {
                    msg("Could not find player");
                    return true;
                }
                party.removeMember(kicked);
                msg(kicked + " succesfully kicked from the party");

                return true;
            }

            if (args[0].equals("accept")) {
                Player player = getPlayer(args[1]);
                Party invitedParty = plugin.pm.getData(player).getChat().getParty();
                if (player == null) {
                    playerSender.sendMessage("Player " + player.getName() +  " is not online right now");
                    return true;
                }

                if (invitedParty == null) {
                    playerSender.sendMessage("Player doesnt have a party");
                    return true;
                }
                
                if (!(invitedParty.getInvited().contains(playerSender.getUniqueId()))) {
                    playerSender.sendMessage("You were not invited to this party");
                    return true;
                }
                
                if (party != null) {
                    playerSender.sendMessage("Please leave your current party before joining a new one");
                    return true;
                }
                invitedParty.acceptInvite(playerSender);
                return true;
            }
            
            if (args[0].equals("deny")) {
                Player player = getPlayer(args[1]);
                InfoChat ic = plugin.pm.getData(player).getChat();
                if (!ic.isInParty()) {
                    playerSender.sendMessage("This person hasn't invited you (yet)");
                    return true;
                }
                ic.getParty().denyInvite(player);
                playerSender.sendMessage("Invitation succesfully declined");
            }

        }
        return false;
    }
}

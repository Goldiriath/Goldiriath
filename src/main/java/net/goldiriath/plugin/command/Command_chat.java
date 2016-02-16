package net.goldiriath.plugin.command;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.chat.ChatChannel;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.player.info.InfoChat;
import net.pravian.bukkitlib.command.BukkitCommand;
import net.pravian.bukkitlib.command.CommandPermissions;
import net.pravian.bukkitlib.command.SourceType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(source = SourceType.PLAYER)
public class Command_chat extends BukkitCommand<Goldiriath> {

    @Override
    protected boolean run(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length <= 1) {
            return false;
        }
        InfoChat chat = plugin.pm.getData(playerSender).getChat();
        switch (args[0].toLowerCase()) {
            case "send":
                switch (args[1].toLowerCase()) {
                    case "local":
                        chat.setCurrentChannel(ChatChannel.LOCAL);
                        break;

                    case "party":
                        if (chat.getParty() == null) {
                            playerSender.sendMessage("You have to be in a party to talk in party chat");
                            break;
                        }
                        chat.setCurrentChannel(ChatChannel.PARTY);
                        break;
                }
                msg("Now sending to " + args[1]);
                break;

            case "ignore":
                ChatChannel channel = ChatChannel.fromString(args[1]);
                chat.ignore(channel);
                msg("You are now ignoring: " + args[1]);
                break;

            case "unignore":
                ChatChannel unignoreChannel = ChatChannel.fromString(args[1]);
                chat.unIgnore(unignoreChannel);
                msg("You are now not ignoring: " + args[1]);
                break;
        }
        return true;
    }
}

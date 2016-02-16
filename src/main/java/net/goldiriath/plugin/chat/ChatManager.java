package net.goldiriath.plugin.chat;

import java.util.Iterator;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.player.info.InfoChat;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChatEvent;

public class ChatManager extends AbstractService {

    public ChatManager(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @EventHandler
    public void onPlayerChatEvent(PlayerChatEvent event) {
        InfoChat chat = plugin.pm.getData(event.getPlayer()).getChat();

        // Formatting message
        event.setFormat(chat.getCurrentChannel().getPrefix() + event.getFormat());

        // Channel handeling
        switch (chat.getCurrentChannel()) {

            case PARTY:
                event.getRecipients().clear();
                event.getRecipients().addAll(chat.getParty().getPlayerMembers());
                break;

            case LOCAL:
                Iterator<Player> it = event.getRecipients().iterator();
                while (it.hasNext()) {
                    if (event.getPlayer().getLocation().distanceSquared(it.next().getLocation()) >= 62500) {
                        it.remove();
                    }
                }
                break;
        }

        // Ignore handeling
        Iterator<Player> it = event.getRecipients().iterator();
        while (it.hasNext()) {
            if (plugin.pm.getData(it.next()).getChat().getIgnoredChannels().contains(chat.getCurrentChannel())) {
                it.remove();
            }
        }
    }

}

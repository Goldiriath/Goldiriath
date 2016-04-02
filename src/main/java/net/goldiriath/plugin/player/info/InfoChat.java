package net.goldiriath.plugin.player.info;

import com.google.common.collect.Sets;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import net.goldiriath.plugin.chat.ChatChannel;
import net.goldiriath.plugin.chat.Party;
import net.goldiriath.plugin.player.PlayerData;

public class InfoChat extends Info {

    public InfoChat(PlayerData data) {
        super(data);
    }

    @Getter
    @Setter
    private ChatChannel currentChannel = ChatChannel.LOCAL;
    @Getter
    private final Set<ChatChannel> ignoredChannels = Sets.newHashSet();
    @Getter
    @Setter
    private Party party;

    public void ignore(ChatChannel channel) {
        ignoredChannels.add(channel);
    }

    public void unIgnore(ChatChannel channel) {
        ignoredChannels.remove(channel);
    }

    public boolean isInParty() {
        return party != null;
    }

    public void leaveParty() {
        if (party == null) {
            return;
        }

        party.removeMember(data.getPlayer());
        party = null;
    }

    public Party createParty() {
        leaveParty();
        return party = new Party(data.getPlayer());
    }

}

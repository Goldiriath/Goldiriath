package net.goldiriath.plugin.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class Party {

    private final List<UUID> members = new ArrayList<>();
    @Getter
    private final List<UUID> invited = new ArrayList<>();

    public Party(Player leader) {
        invited.add(leader.getUniqueId());
        acceptInvite(leader);
    }

    private void removeGone() {
        Iterator<UUID> it = members.iterator();
        while (it.hasNext()) {
            if (Bukkit.getPlayer(it.next()) == null) {
                it.remove();
            }
        }
    }

    public Player getLeader() {
        removeGone();
        if (members.isEmpty()) {
            return null;
        }
        return Bukkit.getPlayer(members.get(0));

    }

    public void acceptInvite(Player member) {
        if (!(invited.contains(member.getUniqueId()))) {
            return;
        }
        members.add(member.getUniqueId());
        Goldiriath.instance().pm.getData(member).getChat().setParty(this);
        invited.remove(member.getUniqueId());
    }

    public void denyInvite(Player member) {
        if (invited.contains(member.getUniqueId())) {
            invited.remove(member.getUniqueId());
        }
    }

    public void removeMember(Player member) {
        members.remove(member.getUniqueId());
        Goldiriath.instance().pm.getData(member).getChat().setParty(null);
    }

    public List<UUID> getMembers() {
        removeGone();
        return Collections.unmodifiableList(members);
    }

    public List<Player> getPlayerMembers() {
        removeGone();
        List<Player> playerMembers = new ArrayList<>();
        for (UUID member : members) {
            playerMembers.add(Bukkit.getPlayer(member));
        }
        return playerMembers;
    }

    public void disband() {
        for (Player player : getPlayerMembers()) {
            removeMember(player);
        }
    }

    public void invite(Player player) {
        invited.add(player.getUniqueId());
    }
}

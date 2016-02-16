package net.goldiriath.plugin.chat;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import net.goldiriath.plugin.Goldiriath;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class Party {

    private final SortedSet<UUID> members;

    public Party(Player leader) {
        members = new TreeSet<>();
        addMember(leader);
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
        try {
            return Bukkit.getPlayer(members.first());
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    public void addMember(Player member) {
        members.add(member.getUniqueId());
        Goldiriath.instance().pm.getData(member).getChat().setParty(this);
    }

    public void removeMember(Player member) {
        members.remove(member.getUniqueId());
        Goldiriath.instance().pm.getData(member).getChat().setParty(null);
    }

    public SortedSet<UUID> getMembers() {
        removeGone();
        return Collections.unmodifiableSortedSet(members);
    }

    public SortedSet<Player> getPlayerMembers() {
        removeGone();
        SortedSet<Player> playerMembers = new TreeSet<>();
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
}

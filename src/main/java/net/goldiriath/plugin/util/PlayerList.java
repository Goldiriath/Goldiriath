package net.goldiriath.plugin.util;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerList implements Iterable<Player> {

    private final List<UUID> uuids = Lists.newArrayList();

    public PlayerList() {
    }

    @Override
    public Iterator<Player> iterator() {
        return new Iterator<Player>() {
            final Iterator<UUID> currentUuids = uuids.iterator();

            @Override
            public boolean hasNext() {
                return currentUuids.hasNext();
            }

            @Override
            public Player next() {
                return getPlayer(currentUuids.next());
            }

            @Override
            public void remove() {
                currentUuids.remove();
            }
        };
    }

    public void add(Player member) {
        uuids.add(member.getUniqueId());
    }

    public void remove(Player member) {
        uuids.remove(member.getUniqueId());
    }

    public List<UUID> getMembers() {
        removeGone();
        return Collections.unmodifiableList(uuids);
    }

    public List<Player> getPlayerMembers() {
        removeGone();
        List<Player> playerMembers = Lists.newArrayList();
        for (UUID member : uuids) {
            playerMembers.add(getPlayer(member));
        }
        return playerMembers;
    }

    public int size() {
        int size = 0;
        for (UUID uuid : uuids) {
            size += getPlayer(uuid) == null ? 0 : 1;
        }
        return size;
    }

    public void clear() {
        uuids.clear();
    }

    private Player getPlayer(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }

    private void removeGone() {
        Iterator<UUID> it = uuids.iterator();
        while (it.hasNext()) {
            if (getPlayer(it.next()) == null) {
                it.remove();
            }
        }
    }

}

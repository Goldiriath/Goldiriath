package me.dirkjan.goldiriath.commands;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.MobSpawn;
import net.pravian.bukkitlib.command.BukkitCommand;
import net.pravian.bukkitlib.command.CommandPermissions;
import net.pravian.bukkitlib.command.SourceType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

@CommandPermissions(permission = "goldriathpluginquests.newmobspawn", source = SourceType.PLAYER)
public class Command_newmobspawn extends BukkitCommand<Goldiriath> {

    @Override
    protected boolean run(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length < 3) {
            return false;
        }

        String name = args[0];
        for (MobSpawn mobspawn : plugin.mobSpawns) {
            if (mobspawn.getName().equals(name)) {
                sender.sendMessage(ChatColor.RED + "that name is already used");
                return true;
            }
        }

        String stringtype = args[1];
        String stringlvl = args[2];

        EntityType type = EntityType.fromName(stringtype);
        if (type == null || type.getEntityClass().isAssignableFrom(LivingEntity.class)) {
            sender.sendMessage(ChatColor.RED + "type incorect");
            return true;
        }

        int lvl;
        try {
            lvl = Integer.parseInt(stringlvl);
        } catch (IllegalArgumentException exception) {
            sender.sendMessage(ChatColor.RED + "lvl is a number");
            return true;
        }
        final Location location = playerSender.getLocation();

        MobSpawn mobspawn = new MobSpawn();
        mobspawn.setName(name);
        mobspawn.setLocation(location);
        mobspawn.setEntityType(type);
        mobspawn.setLvl(lvl);
        sender.sendMessage(ChatColor.GREEN + "done");
        plugin.mobSpawns.add(mobspawn);
        mobspawn.startspawning();
        plugin.saveConfig();
        return true;

    }
}

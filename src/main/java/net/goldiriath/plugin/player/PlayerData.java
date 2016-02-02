package net.goldiriath.plugin.player;

import net.goldiriath.plugin.player.info.InfoSidebar;
import net.goldiriath.plugin.player.data.DataQuests;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.math.XPMath;
import net.goldiriath.plugin.mobspawn.MobSpawn;
import net.goldiriath.plugin.mobspawn.citizens.MobSpawnTrait;
import net.goldiriath.plugin.player.data.DataFlags;
import net.goldiriath.plugin.player.data.DataSkills;
import net.goldiriath.plugin.player.info.InfoDialogs;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class PlayerData {

    @Getter
    private final Goldiriath plugin;
    @Getter
    private final PlayerManager manager;
    @Getter
    private final Player player;
    @Getter
    private final PersistentData persistent;
    //
    @Getter
    private final InfoSidebar sidebar;
    @Getter
    private final InfoDialogs dialogs;
    //
    public PlayerData(PlayerManager manager, Player player) {
        this.plugin = manager.getPlugin();
        this.manager = manager;
        this.player = player;
        this.persistent = new PersistentData(this);
        //
        this.sidebar = new InfoSidebar(this);
        this.dialogs = new InfoDialogs(this);
    }

    //
    //
    //
    public void recordKill(LivingEntity killed) {

        NPC npc = plugin.msm.getBridge().getNPC(killed);
        if (npc == null) {
            return;
        }

        MobSpawnTrait mobSpawnTrait = npc.getTrait(MobSpawnTrait.class);
        if (mobSpawnTrait == null) {
            manager.getPlugin().logger.warning(player.getName() + " killed NPC without MobSpawn trait!");
            return;
        }

        final MobSpawn mobSpawn = mobSpawnTrait.getSpawn();
        final int mobLevel = mobSpawn.getProfile().getLevel();

        int level = XPMath.xpToLevel(persistent.xp);
        double diff = Math.abs(level - mobLevel);

        int xp = 1;
        if (diff <= 1) {
            xp = 5;
        }
        if (diff >= 2 && diff <= 3 && mobLevel >= level) {
            xp = 7;
        }
        if (diff >= 2 && diff <= 3 && level >= mobLevel) {
            xp = 2;
        }
        addXp(xp);
    }

    public int getXp() {
        return persistent.xp;
    }

    public void addXp(int amt) {
        int currentlevel = XPMath.xpToLevel(persistent.xp);
        persistent.xp += amt;
        int newlevel = XPMath.xpToLevel(persistent.xp);
        while (currentlevel < newlevel) {
            gainLevel();
            currentlevel++;
        }
    }

    public void removeXp(int amt) {
        persistent.xp -= amt;
    }

    public void gainLevel() {
        Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta meta = fw.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().flicker(true).with(FireworkEffect.Type.STAR).withColor(Color.RED).withTrail().withFade(Color.WHITE).build();
        meta.addEffect(effect);
        meta.setPower(0);
        fw.setFireworkMeta(meta);
        addSkillPoints(1);

        int xp = persistent.xp;
        int newLevel = XPMath.xpToLevel(xp);

        // TODO: These shouldn't be levelled this way
        persistent.maxHealth += (100 * newLevel);
        persistent.maxMana += (100 * newLevel);
        player.sendMessage(ChatColor.YELLOW + "Congratulations on reaching level " + newLevel);
    }

    public DataFlags getFlags() {
        return persistent.flags;
    }

    public DataQuests getQuests() {
        return persistent.quest;
    }

    public DataSkills getSkills() {
        return persistent.skills;
    }

    public int addMoney(int added) {
        persistent.money += added;
        return persistent.money;
    }

    public int removeMoney(int remove) {
        persistent.money -= remove;
        return persistent.money;
    }

    public boolean hasMoney(int has) {
        return persistent.money >= has;
    }

    public boolean hasHealth(int health) {
        return persistent.health >= health;
    }

    public int getMaxMana() {
        return persistent.maxMana;
    }

    public int getMana() {
        return persistent.mana;
    }

    public void setMana(int mana) {
        persistent.mana = mana;
    }

    public boolean hasMana(int mana) {
        return persistent.mana >= mana;
    }

    public void addSkillPoints(int toadd) {
        persistent.skillPoints += toadd;
    }

    public void removeSkillPoints(int toremove) {
        persistent.skillPoints -= toremove;
    }

    public boolean hasSkillPoints(int has) {
        return persistent.skillPoints >= has;
    }

    public int getMoney() {
        return persistent.money;
    }

    public void setMoney(int money) {
        persistent.money = money;
    }

    public int getHealth() {
        return persistent.health;
    }

    public void setHealth(int health) {
        persistent.health = health;
    }

    public int getMaxHealth() {
        return persistent.maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        persistent.maxHealth = maxHealth;
    }

}

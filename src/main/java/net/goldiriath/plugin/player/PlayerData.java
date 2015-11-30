package net.goldiriath.plugin.player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import net.goldiriath.plugin.ConfigPaths;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.dialog.Dialog;
import net.goldiriath.plugin.dialog.OptionSet;
import net.goldiriath.plugin.dialog.script.ScriptRunner;
import net.goldiriath.plugin.mobspawn.MobSpawn;
import net.goldiriath.plugin.skill.Skill;
import net.goldiriath.plugin.skill.SkillType;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class PlayerData {

    @Getter
    private final Goldiriath plugin;
    @Getter
    private final PlayerManager manager;
    @Getter
    private final Player player;
    @Getter
    private final PersistentData persistent;
    @Getter
    private final SidebarData sidebar;
    //
    @Getter
    private OptionSet currentOption;
    private BukkitTask currentOptionTimeout;
    private ScriptRunner scriptRunner;

    public PlayerData(PlayerManager manager, Player player) {
        this.plugin = manager.getPlugin();
        this.manager = manager;
        this.player = player;
        this.persistent = new PersistentData(this);
        this.sidebar = new SidebarData(player);
    }

    //
    //
    //
    public void recordKill(LivingEntity killed) {
        final List<MetadataValue> metadataList = killed.getMetadata(MobSpawn.METADATA_ID);
        if (metadataList.isEmpty()) {
            return;
        }

        final MobSpawn mobSpawn = (MobSpawn) metadataList.get(0).value();
        final int mobLevel = mobSpawn.getProfile().getLevel();

        int level = calculateLevel();
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

    public int calculateLevel() {
        return (int) Math.floor(0.1 * (Math.sqrt(2 * persistent.xp + 25) + 5));
    }

    public int calculateNextXp() {
        int x = (int) Math.floor(0.1 * (Math.sqrt(2 * persistent.xp + 25) + 5) + 1);
        return 50 * (x - 1) * x;
    }

    public int getXp() {
        return persistent.xp;
    }

    public void addXp(int amt) {
        int currentlevel = calculateLevel();
        persistent.xp += amt;
        int newlevel = calculateLevel();
        if (currentlevel != newlevel) {
            gainLevel();
        }
    }

    public void gainLevel() {
        Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta meta = fw.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().flicker(true).with(FireworkEffect.Type.STAR).withColor(Color.RED).withTrail().withFade(Color.WHITE).build();
        meta.addEffect(effect);
        meta.setPower(0);
        fw.setFireworkMeta(meta);

        addSkillPoints(1);
        persistent.maxHealth += (100 * calculateLevel());
        persistent.maxMana += (100 * calculateLevel());
        player.sendMessage(ChatColor.YELLOW + "Congratulations on reaching level " + calculateLevel());
    }

    public boolean isShowingOption() {
        return getCurrentOption() != null;
    }

    public void showOption(final OptionSet option) {
        endOption();
        this.currentOption = option;
        option.getMessage().send(player);

        this.currentOptionTimeout = new BukkitRunnable() {
            @Override
            public void run() {
                // TODO improve?
                if (getCurrentOption().equals(option)) {
                    endOption();
                }
                player.sendMessage(ChatColor.YELLOW + "Note" + ChatColor.WHITE + ": You've stopped speaking to this character.");
                endOption();
            }
        }.runTaskLater(manager.getPlugin(), manager.getPlugin().config.getInt(ConfigPaths.DIALOG_TIMEOUT));
    }

    public void endOption() {
        this.currentOption = null;
        try {
            this.currentOptionTimeout.cancel();
        } catch (Exception ignored) {
        } finally {
            this.currentOptionTimeout = null;
        }
    }

    public void endDialog() {
        this.scriptRunner.stop();
        this.scriptRunner = null;
    }

    public void startDialog(Dialog dialog) {
        if (scriptRunner != null) {
            endDialog();
        }

        recordDialog(dialog.getId());

        final ScriptRunner sr = new ScriptRunner(dialog.getScript(), player);
        sr.start();
        this.scriptRunner = sr;
    }

    public boolean isInDialog() {
        return getScriptRunner() != null;
    }

    public boolean hasHadDialog(String id) {
        return persistent.dialogs.containsKey(id) && persistent.dialogs.get(id) > 0;
    }

    public ScriptRunner getScriptRunner() {
        if (scriptRunner != null && !scriptRunner.isStarted()) {
            scriptRunner = null;
        }

        return scriptRunner;
    }

    public Set<Skill> getSkills() {
        return Collections.unmodifiableSet(persistent.skills);
    }

    public void addSkill(Skill skill) {
        persistent.skills.add(skill);
    }

    public void removeSkill(Skill skill) {
        persistent.skills.remove(skill);
    }

    public boolean hasSkill(SkillType type) {
        for (Skill loopSkill : persistent.skills) {
            if (loopSkill.getType() == type) {
                return true;
            }
        }

        return false;
    }

    public Map<String, Integer> getFlags() {
        return Collections.unmodifiableMap(persistent.flags);
    }

    public boolean hasFlag(String flag) {
        return hasFlag(flag, 1);
    }

    public boolean hasFlag(String flag, int amount) {
        return persistent.flags.containsKey(flag) && persistent.flags.get(flag) >= amount;
    }

    public int getFlag(String flag) {
        return persistent.flags.containsKey(flag) ? persistent.flags.get(flag) : 0;
    }

    public void setFlag(String flag, int amount) {
        persistent.flags.put(flag, amount);
    }

    public void addFlag(String flag) {
        addFlag(flag, 1);
    }

    public void addFlag(String flag, int amount) {
        if (persistent.flags.containsKey(flag)) {
            persistent.flags.put(flag, persistent.flags.get(flag) + amount);
        } else {
            persistent.flags.put(flag, amount);
        }
    }

    public void removeFlag(String flag) {
        removeFlag(flag, 1);
    }

    public void removeFlag(String flag, int amount) {
        if (!persistent.flags.containsKey(flag)) {
            return;
        }

        int newAmount = persistent.flags.get(flag) - amount;

        if (newAmount > 0) {
            persistent.flags.put(flag, newAmount);
        } else {
            persistent.flags.remove(flag);
        }
    }

    public void deleteFlag(String flag) {
        persistent.flags.remove(flag);
    }

    public int getDialogCount(String id) {
        return persistent.dialogs.get(id);
    }

    public void recordDialog(String id) {
        if (persistent.dialogs.containsKey(id)) {
            persistent.dialogs.put(id, persistent.dialogs.get(id) + 1);
        } else {
            persistent.dialogs.put(id, 1);
        }
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

    public QuestData getQuestData() {
        return persistent.questData;
    }

}

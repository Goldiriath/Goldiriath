package net.goldiriath.plugin.game.damage;

import java.util.Iterator;
import java.util.List;
import net.goldiriath.plugin.ConfigPath;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.inventory.InventoryUtil;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class DeathManager extends AbstractService {

    private int deathCost;
    private double multiplier;

    public DeathManager(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
        this.deathCost = plugin.config.getInt(ConfigPath.DEATH_MONEY_COST);
        this.multiplier = plugin.config.getDouble(ConfigPath.DEATH_MONEY_MULTIPLIER);
    }

    @Override
    protected void onStop() {
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {

        final Player player = event.getEntity();

        event.setKeepLevel(true);
        event.setDroppedExp(0);

        if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
            return;
        }

        PlayerData data = plugin.pym.getData(player);
        int money = data.getMoney();

        money *= multiplier;
        data.setMoney(money);

        if (deathCost > 0) {
            if (money >= deathCost) {
                money -= deathCost;
                data.setMoney(money);
                return;
            }

            player.sendMessage(ChatColor.RED + "You lost your items because you didn't have enough money.");
            event.setKeepInventory(false);

            // Filter skills and skillbooks
            List<ItemStack> stacks = event.getDrops();
            Iterator<ItemStack> it = stacks.iterator();
            while (it.hasNext()) {
                ItemStack s = it.next();
                if (InventoryUtil.isSkill(s)
                        || InventoryUtil.isSkillBook(s)) {
                    it.remove();
                }
            }

            return;
        }

        // Keep the player's inventory
        event.setKeepInventory(true);
        event.getDrops().clear();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        PlayerData data = plugin.pym.getData(event.getPlayer());
        data.setHealth(data.getMaxHealth());
    }

}

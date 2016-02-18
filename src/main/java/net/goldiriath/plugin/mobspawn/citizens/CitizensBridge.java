package net.goldiriath.plugin.mobspawn.citizens;

import java.util.UUID;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.npc.ai.NPCHolder;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.service.AbstractService;
import net.pravian.bukkitlib.util.LocationUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class CitizensBridge extends AbstractService {

    private NPCRegistry globalReg;
    private NPCRegistry memoryReg;

    public CitizensBridge(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
        try {
            globalReg = CitizensAPI.getNPCRegistry();
        } catch (Exception ex) {
            logger.severe("Could not obtain Citizens API!", ex);
            return;
        }

        if (globalReg == null) {
            logger.severe("No Citizens API found!");
            return;
        }

        memoryReg = CitizensAPI.createNamedNPCRegistry("Goldiriath", new DummyNPCDataStore());
        if (memoryReg == null) {
            logger.severe("Could not create named NPC registry");
        }

        logger.info("Hooked into Citizens NPC registry");
    }

    @Override
    protected void onStop() {
        // Despawn and remove all NPCs in the memory registry
        memoryReg.deregisterAll();
    }

    public NPC createPlayer(String name) {
        return memoryReg.createNPC(EntityType.PLAYER, name);
    }

    public NPC createMob(EntityType type) {
        return memoryReg.createNPC(type, "");
    }

    public NPC createMob(EntityType type, String name) {
        return memoryReg.createNPC(type, name);
    }

    public NPC getNPC(LivingEntity entity) {
        if (!entity.hasMetadata("NPC")) {
            return null;
        }

        if (!(entity instanceof NPCHolder)) {
            logger.warning("Could not obtain NPCHolder from NPC type at " + LocationUtils.format(entity.getLocation()));
            return null;
        }

        return ((NPCHolder) entity).getNPC();
    }

    public NPC getNPC(UUID uuid) {
        return memoryReg.getByUniqueId(uuid);
    }

    public boolean isNPC(LivingEntity entity) {
        return getNPC(entity) != null;
    }

}

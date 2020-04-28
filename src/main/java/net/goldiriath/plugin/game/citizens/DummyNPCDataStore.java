package net.goldiriath.plugin.game.citizens;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;

public class DummyNPCDataStore implements NPCDataStore {

    public static int NPC_ID_OFFSET = 2000;

    private int counter = 0;

    @Override
    public void clearData(NPC npc) {
    }

    @Override
    public int createUniqueNPCId(NPCRegistry npcr) {
        return NPC_ID_OFFSET + counter++;
    }

    @Override
    public void loadInto(NPCRegistry npcr) {
    }

    @Override
    public void saveToDisk() {
    }

    @Override
    public void saveToDiskImmediate() {
    }

    @Override
    public void store(NPC npc) {
    }

    @Override
    public void storeAll(NPCRegistry npcr) {
    }

    @Override
    public void reloadFromSource() {
    }

}

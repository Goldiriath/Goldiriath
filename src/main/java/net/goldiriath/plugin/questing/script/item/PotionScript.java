package net.goldiriath.plugin.questing.script.item;

import net.goldiriath.plugin.questing.script.ParseException;
import net.goldiriath.plugin.questing.script.Script;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionScript extends ScriptItem {

    private final PotionEffect effect;

    public PotionScript(Script script, String[] args) {
        super(script);

        if (args[1].equals("clear")) {
            effect = null;
        } else {
            PotionEffectType type = PotionEffectType.getByName(args[1]);
            if (type == null) {
                throw new ParseException("Unknown potion effect type: " + args[1].toUpperCase());
            }

            int duration = parseInt(args[2]);
            int amplifier = parseInt(args[3]);

            boolean ambient = true;
            if (args.length == 5) {
                ambient = args[4].equals("true");
                if (!ambient && !args[4].equals("false")) {
                    throw new ParseException("Invalid ambient value (true/false): " + args[4]);
                }
            }

            effect = new PotionEffect(type, duration, amplifier, ambient);
        }
    }

    @Override
    public void execute(Player player) {
        if (effect == null) {
            for (PotionEffectType type : PotionEffectType.values()) {
                player.removePotionEffect(type);
            }
        } else {
            player.addPotionEffect(effect, true);
        }
    }

}

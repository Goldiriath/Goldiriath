package net.goldiriath.plugin.math;

import net.goldiriath.plugin.Goldiriath;
import org.bukkit.inventory.ItemStack;

public class ArmorMath {
    Goldiriath plugin = new Goldiriath();
    
    public double baseArmor(ItemStack item){
        int l = 0;
        double t = 1;
        int h = 1;
        
        if (plugin.im.getItemStorage().getItemMap().containsValue(item)) {
            return 1 + 10*Math.pow(1.08306, l-1)*t*h;
        }
        
        //level determination
        l = plugin.im.getMeta(item).getLevel();
        
        // tier determination
        switch(plugin.im.getMeta(item).getTier()){
            case BATTERED:
                t = 0.7;
                break;
            case CRAFTED:
                t = 1.3;
                break;
            case RARE:
                t = 1.6;
                break;
            case LEGENDARY:
                t = 2;
        }
        
        //TODO: implement heavyness multipliers
        
        
        return 1 + 10*Math.pow(1.08306, l-1)*t*h;
    }
    
    public double armorModifier(ItemStack helmet, ItemStack chestplate, ItemStack pants, ItemStack boots){
        double b = baseArmor(helmet)
                   + baseArmor(chestplate)
                   + baseArmor(pants)
                   + baseArmor(boots);
        return 1 - (b / (b + 2000));
    }
}
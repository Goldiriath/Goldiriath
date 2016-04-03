package net.goldiriath.plugin.math;

import net.goldiriath.plugin.Goldiriath;
import org.bukkit.inventory.ItemStack;

public class DamageMath {
    Goldiriath plugin = new Goldiriath();
    ArmorMath armor = new ArmorMath();
    

    public double baseDamage(ItemStack item){
        double m = 1;
        double t = 1;
        int l = 0;
        
        // material determination
        switch(item.getType()){
            case BOW:
                m = 1.1;
                break;
            case SHEARS:
                m = 0.8;
                break;
            case STICK:
                m = 1.025;
                break;
        }
        
        if(!plugin.im.getItemStorage().getItemMap().containsValue(item)){
            return 10+m*t*Math.pow(1.1, l);
        }
        
        // tier determination
        switch(plugin.im.getMeta(item).getTier()){
            case BATTERED:
                t = 0.8;
                break;
            case CRAFTED:
                t = 1.1;
                break;
            case RARE:
                t = 1.21;
                break;
            case LEGENDARY:
                t = 1.33;
        
        }
        //level determination
        l = plugin.im.getMeta(item).getLevel();
        
        
        return 10+m*t*Math.pow(1.1, l) ;
    }
    
    public double attackDamage(ItemStack item){
        double b = baseDamage(item);
        double s = 1;
        double i = 1;
        double e = 1;
        
        //TODO: implement skill modifiers
        //TODO: implement inventory modifiers
        //TODO: implement enviromental modifiers
        
        return b*s*i*e;
    }
    
    public double effectiveDamage(ItemStack weapon, ItemStack helmet, ItemStack chestplate, ItemStack pants, ItemStack boots){
        double a = attackDamage(weapon);
        double d = armor.armorModifier(helmet, chestplate, pants, boots);
        double i = 1;
        
        //TODO: implement inventory modifiers
        
        return Math.max(a*d*i, 1);
    }
}
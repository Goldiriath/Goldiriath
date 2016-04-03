package net.goldiriath.plugin.math;

import com.google.common.annotations.VisibleForTesting;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.item.meta.GItemMeta;
import org.bukkit.inventory.ItemStack;

public class DamageMath {
    
    @VisibleForTesting
    static double b(double m, double t, double l){
        return 10+m*t*Math.pow(1.1, l) ;
    }
    
    @VisibleForTesting
    static double a(double b, double s, double i, double e){
        return b*s*i*e;
    }
    
    @VisibleForTesting
    static double e(double a, double d, double i){
        return Math.max(a*d*i, 1);
    }
    

    public static double baseDamage(ItemStack item){
        GItemMeta meta = Goldiriath.instance().im.getMeta(item, false);
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
            case BLAZE_ROD:
                m = 1.025;
                break;
        }
        
        if(meta == null){
            return b(m, t, t);
        }
        
        // tier determination
        t = meta.getTier().getWeaponMulti();
        
        
        //level determination
        l = meta.getLevel();
        
        
        return b(m, t, t);
    }
    
    public static double attackDamage(ItemStack item){
        double b = baseDamage(item);
        double s = 1;
        double i = 1;
        double e = 1;
        
        //TODO: implement skill modifiers
        //TODO: implement inventory modifiers
        //TODO: implement enviromental modifiers
        
        return a(b, s, i, e);
    }
    
    public static double effectiveDamage(ItemStack weapon, ItemStack helmet, ItemStack chestplate, ItemStack pants, ItemStack boots){
        double a = attackDamage(weapon);
        double d = ArmorMath.armorModifier(helmet, chestplate, pants, boots);
        double i = 1;
        
        //TODO: implement inventory modifiers
        
        return e(a, d, i);
    }
}
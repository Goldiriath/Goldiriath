
package net.goldiriath.plugin.item.meta;

public enum ArmorType {
    Heavy(1.5),
    LIGHT(1);

    private final double multi;
    private ArmorType(double multi) {
        this.multi = multi;
    }
    
    public double getMulti(){
        return multi;
    }
    
    
}

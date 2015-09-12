package net.goldiriath.plugin.util;

import java.util.ArrayList;
import java.util.Collection;

public class SafeArrayList<T> extends ArrayList<T> {

    private static final long serialVersionUID = 273717122922L;

    // TODO low: Implement the rest of the add actions
    @Override
    public boolean add(T elem) {
        // Sanity check
        if (elem == this) {
            throw new IllegalArgumentException("Can not add self to element list.");
        }
        return super.add(elem);
    }

    @Override
    public boolean addAll(Collection<? extends T> elems) {
        // Sanity check
        for (T elem : elems) {
            if (elem == this) {
                throw new IllegalArgumentException("Can not add self to element list.");
            }
        }
        return super.addAll(elems);
    }

}

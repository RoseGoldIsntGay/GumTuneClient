package rosegold.gumtuneclient.utils.objects;

import java.util.ArrayList;

public class ModuleArrayList<E> extends ArrayList<E> {
    @SafeVarargs
    public final void addAll(E... e) {
        for (E entry : e) {
            add(entry);
        }
    }
}

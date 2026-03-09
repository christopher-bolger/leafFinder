package leafFinder.utility;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public final class Utility {

    public static <E> List<E> nearestNeighbour(List<E> list, Predicate<E> p) {

        return list;
    }

    public static <E> void swap(int one, int two, List<E> list){
        E temp = list.get(one);
        list.set(one, list.get(two));
        list.set(two, temp);
    }
}

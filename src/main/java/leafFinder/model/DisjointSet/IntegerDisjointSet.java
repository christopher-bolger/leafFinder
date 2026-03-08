package leafFinder.model.DisjointSet;

public class IntegerDisjointSet {
    private final int[] array;

    public IntegerDisjointSet() {
        array = new int[10];
    }

    public IntegerDisjointSet(int size){
        array = new int[size];
    }

    public void add(int value, int index) {
        array[index] = value;
    }

    private boolean isValidIndex(int index){
        return index >= 0 && index < array.length;
    }

    public int indexOf(int value){
        for(int i = 0; i < array.length; i++)
            if(array[i] == value)
                return i;
        return -1;
    }

    public int size(){
        return array.length;
    }

    public boolean hasParent(int index){
        return array[index] != index;
    }

    public void union(int parentIndex, int childIndex){
        if(!isValidIndex(parentIndex) || !isValidIndex(childIndex))
            return;
        set(find(parentIndex), find(childIndex));
    }

    public int find(int index){
        if(!isValidIndex(index))
            return -1;
        if(array[index] == index)
            return index;
        else array[index] = find(array[index]);
        return array[index];
    }

    public void set(int value, int index){
        array[index] = value;
    }

    public int get(int index){
        if(isValidIndex(index))
            return array[index];
        return -1;
    }
}

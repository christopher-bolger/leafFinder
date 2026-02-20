package leafFinder.model.DisjointSet;

import java.util.ArrayList;

public class DisjointSet<E> {
    private ArrayList<DisjointSetNode<E>> array;

    public DisjointSet() {
        array = new ArrayList<>();
    }

    public DisjointSet(int size){
        array = new ArrayList<>(size);
    }

    public void add(E e){
        array.add(new DisjointSetNode<>(e));
    }

    public E get(int index){
        return array.get(index).getElement();
    }

    public void insert(int index, E e){
        array.get(index).setElement(e);
    }

    public ArrayList<DisjointSetNode<E>> getArray() {
        return array;
    }

    public void setArray(ArrayList<DisjointSetNode<E>> array) {
        this.array = array;
    }
}

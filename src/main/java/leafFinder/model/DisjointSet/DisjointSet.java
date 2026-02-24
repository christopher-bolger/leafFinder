package leafFinder.model.DisjointSet;

import java.util.ArrayList;

public class DisjointSet<E> {
    private DisjointSetNode<E>[] array;

    public DisjointSet() {
        array = new DisjointSetNode[10];
    }

    public DisjointSet(int size){
        array = new DisjointSetNode[size];
    }

    private boolean isValidIndex(int index){
        return index >= 0 && index < array.length;
    }

    public int size(){
        return array.length;
    }

    public E get(int index){
        if(isValidIndex(index))
            return array[index].getElement();
        return null;
    }

    public void insert(int index, E e){
        if(isValidIndex(index))
            array[index].setElement(e);
    }

    public void union(int parentIndex, int childIndex){
        if(!isValidIndex(parentIndex) || !isValidIndex(childIndex))
            return;
        array[childIndex].setParent(find2(array[parentIndex]));
    }

    private DisjointSetNode<E> find2(DisjointSetNode<E> child){
        if(child.getParent() == null)
            return child;
        else child.setParent(find2(child.getParent()));
        return child.getParent();
    }

    public void set(E value, int index){
        array[index] = new DisjointSetNode<>(value);
    }

    public E find(int index){
        return array[index].find().getElement();
    }

    public DisjointSetNode<E>[] getArray() {
        return array;
    }

    public void setArray(DisjointSetNode<E>[] array) {
        this.array = array;
    }
}

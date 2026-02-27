package leafFinder.model.DisjointSet;

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

    public int indexOf(DisjointSetNode<E> node){
        for(int i = 0; i < array.length; i++){
            if(array[i] == node){
                return i;
            }
        }
        return -1;
    }

    public int size(){
        return array.length;
    }

    public DisjointSetNode<E> get(int index){
        if(isValidIndex(index))
            return array[index];
        return null;
    }

    public DisjointSetNode<E> getNode(int index){
        return array[index];
    }

    public void insert(int index, E e){
        if(isValidIndex(index))
            array[index].setElement(e);
    }

    public void union(DisjointSetNode<E> parentNode, DisjointSetNode<E> childNode){
        if(indexOf(parentNode) == -1 || indexOf(childNode) == -1)
            return;
        array[indexOf(childNode)].setParent(find(array[indexOf(parentNode)]));
    }

    public DisjointSetNode<E> find(DisjointSetNode<E> child){
        if(child.getParent() == null)
            return child;
        else child.setParent(find(child.getParent()));
        return child.getParent();
    }

    public void set(E value, int index){
        array[index] = new DisjointSetNode<>(value);
    }

//    public E find(int index){
//        return array[index].find().getElement();
//    }

    public DisjointSetNode<E>[] getArray() {
        return array;
    }

    public void setArray(DisjointSetNode<E>[] array) {
        this.array = array;
    }
}

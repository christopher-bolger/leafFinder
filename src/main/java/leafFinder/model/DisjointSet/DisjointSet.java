package leafFinder.model.DisjointSet;

public class DisjointSet<E> {
    private DisjointSetNode<E>[] array;

    public DisjointSet() {
        array = new DisjointSetNode[10];
    }

    public DisjointSet(int size){
        array = new DisjointSetNode[size];
    }

    public void add(E e, int index) {
        array[index] = new DisjointSetNode<>(e);
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

    public E get(int index){
        if(isValidIndex(index))
            return array[index].getElement();
        return null;
    }

    public DisjointSetNode<E> getNode(int index){
        return array[index];
    }

    public void setParent(int parentIndex, int childIndex){
        array[childIndex].setParent(array[parentIndex]);
    }

    public boolean hasParent(int index){
        return array[index].getParent() != null;
    }

    public void insert(int index, E e){
        if(isValidIndex(index))
            array[index].setElement(e);
    }

    public void union(int parentIndex, int childNode){
        if(!isValidIndex(parentIndex) || !isValidIndex(childNode))
            return;
        array[find(childNode)].setParent(array[find(parentIndex)]);
    }

    public int find(int nodeIndex){
        if(!isValidIndex(nodeIndex))
            return -1;
        DisjointSetNode<E> node = array[nodeIndex];
        while(node.getParent() != null)
            node = node.getParent();
        return indexOf(node);
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

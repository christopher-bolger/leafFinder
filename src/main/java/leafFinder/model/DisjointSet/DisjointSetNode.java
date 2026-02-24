package leafFinder.model.DisjointSet;

public class DisjointSetNode<E> {
    private E element;
    private DisjointSetNode<E> parent;

    public DisjointSetNode(E element, DisjointSetNode<E> parent) {
        this.element = element;
        this.parent = parent;
    }

    public DisjointSetNode(E element) {
        this.element = element;
    }

    public void union(DisjointSetNode<E> parent, DisjointSetNode<E> child) {
        child.parent = parent;
    }

    public DisjointSetNode<E> find(){
        if(parent == null)
            return this;
        parent = parent.find();
        return parent;
    }

    public E getElement() {
        return element;
    }
    public void setElement(E element) {
        this.element = element;
    }
    public DisjointSetNode<E> getParent() {
        return parent;
    }
    public void setParent(DisjointSetNode<E> parent) {
        this.parent = parent;
    }
}

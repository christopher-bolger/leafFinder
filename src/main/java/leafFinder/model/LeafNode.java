package leafFinder.model;

public class LeafNode {
    public int minX, minY, maxX, maxY;
    public final int size, root;

    public LeafNode(int size, int root) {
        this.size = size;
        this.root = root;
        minX = minY = maxX = maxY = 0;
    }

    public int size(LeafNode leafNode) {
        return size;
    }
}

package leafFinder.model;

public class TreeNode {
    private int minX, minY, maxX, maxY, size;

    public TreeNode(int minX, int minY){
        this.minX = minX;
        this.minY = minY;
        this.maxX = minX + 1;
        this.maxY = minY + 1;
        this.size = 1;
    }

    public void combineNodes(TreeNode other){
        this.size += other.size;
        this.minX = Math.min(this.minX, other.minX);
        this.minY = Math.min(this.minY, other.minY);
        this.maxX = Math.max(this.maxX, other.maxX);
        this.maxY = Math.max(this.maxY, other.maxY);
    }

    public void incrementSize(){
        size++;
    }

    public int getMinX() {
        return minX;
    }

    public void setX(int x) {
        if(x < minX) minX = x;
        else if(x > maxX) maxX = x;
    }

    public int getMinY() {
        return minY;
    }

    public void setY(int y) {
        if(y < minY) minY = y;
        else if(y > maxY) maxY = y;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getSize() {
        return size;
    }
}

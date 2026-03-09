package leafFinder.model;

public class TreeNode {
    private static final int Y_AXIS_WEIGHT = 128;
    private int minX, minY, maxX, maxY, size;
    private final int origin;
    private int centerX, centerY;

    public TreeNode(int minX, int minY, int origin){
        this.origin = origin;
        this.minX = minX;
        this.minY = minY;
        this.maxX = minX + 1;
        this.maxY = minY + 1;
        this.size = 1;
        centerX = centerY = -1;
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

    public int getOrigin() {
        return origin;
    }

    public int[] getCenter(){
        if(centerX == -1 || centerY == -1)
            calculateCenter();
        return new int[]{centerX, centerY};
    }

    private void calculateCenter(){
        centerX = minX + (maxX - minX) / 2;
        centerY = minY + (maxY - minY) / 2;
    }

    public int distanceBetweenNodes(TreeNode other){
        int dx = getCenter()[0] - other.getCenter()[0];
        int dy = getCenter()[1] - other.getCenter()[1];
        return dx * dx + Y_AXIS_WEIGHT * dy * dy;
    }
}

package leafFinder.model;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ImageProcessor {
    public static final String[] COMPUTE_SIZE = {"1/1", "1/2", "1/4", "1/8"};
    private Settings settings;
    private final Image image;
    private final PixelReader originalPixelReader;
    private WritableImage computeImage, blackAndWhiteImage, previewImage, colouredImage;
    private PixelReader computePixelReader;
    private PixelWriter computePixelWriter, blackAndWhitePixelWriter, previewPixelWriter, colouredImagePixelWriter;
    private final int height, width;
    private int computeHeight;
    private int computeWidth;
    private IntegerDisjointSet nodeTree;
    private HashMap<Integer, TreeNode> distinctTreeNodes;
    private final LinkedList<Color> nodeColours = new LinkedList<>();
    private int division;

    private final double[] hslMinMaxValues = {0, 360, 0, 1, 0, 1}; //defaults
                                    //hueMin, hueMax, SaturationMin, saturationMax, BrightnessMin, BrightnessMax
                                    // >=0,     <=360,       >=0     ,   <=1      ,    >=0     ,      <=1

    public ImageProcessor(Image image, Settings settings) {
        this.image = image;
        originalPixelReader = image.getPixelReader();
        height = (int) image.getHeight();
        width = (int) image.getWidth();
        this.settings = settings;
        computeImages(image);
    }

    public void computeImages(Image image){
        drawNewComputeImage();
        computeDisjointSet();
        computeBAndW();
        computePreview();
    }

    private void initializeWritableImages(){
        switch(settings.computeRatio()){
            case "1/1" -> division = 1;
            case "1/4" -> division = 4;
            case "1/8" -> division = 8;
            default -> division = 2;
        }
        computeHeight = height / division;
        computeWidth = width / division;
        System.out.println(computeWidth + " " + computeHeight);

        computeImage = new WritableImage(computeWidth, computeHeight);
        blackAndWhiteImage = new WritableImage(computeWidth, computeHeight);
        colouredImage = new WritableImage(computeWidth, computeHeight);
        previewImage = new WritableImage(computeWidth, computeHeight);

        computePixelWriter = computeImage.getPixelWriter();
        blackAndWhitePixelWriter = blackAndWhiteImage.getPixelWriter();
        previewPixelWriter = previewImage.getPixelWriter();
        colouredImagePixelWriter = colouredImage.getPixelWriter();
    }

    private void drawNewComputeImage(){
        initializeWritableImages();
        if(division == 1)
            computePixelReader = originalPixelReader;
        else {
            Color colour;
            int srcX, srcY;
            for (int cy = 0; cy < computeHeight; cy++) {
                for (int cx = 0; cx < computeWidth; cx++) {
                    srcX = cx * division;
                    srcY = cy * division;
                    if (srcX >= width || srcY >= height)
                        continue;
                    colour = originalPixelReader.getColor(srcX, srcY);
                    computePixelWriter.setColor(cx, cy, colour);
                }
            }
            computePixelReader = computeImage.getPixelReader();
        }
    }

    public void setComputeArguments(double... values){
        if(values.length != 6)
            return;
        for(int x = 0; x < values.length; x++) {
            if(x < 3) {
                if (values[x] < 0 || values[x] > 360)
                    return;
            }else {
                if (values[x] < 0 || values[x] > 1)
                    return;
            }
        }
        System.arraycopy(values, 0, hslMinMaxValues, 0, values.length);
        compute();
    }

    public void computeBAndW(){
        Color black = Color.BLACK, white = Color.WHITE;
        int x, y;
        boolean isSelected;
        for(int i = 0; i < nodeTree.size(); i++){
            x = i % computeWidth;
            y = i / computeWidth;
            isSelected = distinctTreeNodes.containsKey(nodeTree.find(i));
            if(isSelected)
                blackAndWhitePixelWriter.setColor(x, y, white);
            else
                blackAndWhitePixelWriter.setColor(x, y, black);
        }
    }

    public void colourBoxes(List<TreeNode> selection, List<TreeNode> from){
        computeBAndW();
        int rootID, index, position;
        for (TreeNode treeNode : selection) {
            rootID = nodeTree.find(treeNode.getOrigin());
            index = from.indexOf(treeNode);
            for (int y = treeNode.getMinY(); y <= treeNode.getMaxY(); y++) {
                for (int x = treeNode.getMinX(); x <= treeNode.getMaxX(); x++) {
                    position = y * computeWidth + x;
                    if (nodeTree.find(position) == rootID)
                        blackAndWhitePixelWriter.setColor(x, y, nodeColours.get(index));
                }
            }
        }
    }

    public Image getColouredImage(){
        return colouredImage;
    }

    public void colourBW(){
        computeBAndW();
        int rootID, position;
        for(int y = 0; y < computeHeight; y++){
            for(int x = 0; x < computeWidth; x++){
                colouredImagePixelWriter.setColor(x, y, Color.BLACK);
            }
        }
        for (TreeNode treeNode : distinctTreeNodes.values()) {
            rootID = nodeTree.find(treeNode.getOrigin());
            for (int y = treeNode.getMinY(); y <= treeNode.getMaxY(); y++) {
                for (int x = treeNode.getMinX(); x <= treeNode.getMaxX(); x++) {
                    position = y * computeWidth + x;
                    if (nodeTree.find(position) == rootID)
                        if(x < computeWidth && y < computeHeight)
                            colouredImagePixelWriter.setColor(x, y, nodeColours.get(rootID % nodeColours.size()));
                }
            }
        }
    }

    private void computeDisjointSet(){
        if(computePixelReader == null)
            return;

        nodeTree = new IntegerDisjointSet(computeWidth * computeHeight);
        Color colour;
        int index;
        double hue, saturation, brightness;
        boolean withinHue, withinSaturation, withinBrightness;
        for(int y = 0; y < computeHeight; y++) {
            for (int x = 0; x < computeWidth; x++) {
                index = (y * computeWidth) + x;

                colour = computePixelReader.getColor(x, y);
                hue = colour.getHue();
                saturation = colour.getSaturation();
                brightness = colour.getBrightness();
                withinHue = hslMinMaxValues[0] <= hue && hue <= hslMinMaxValues[1];
                withinSaturation = hslMinMaxValues[2] <= saturation && saturation <= hslMinMaxValues[3];
                withinBrightness = hslMinMaxValues[4] <= brightness && brightness <= hslMinMaxValues[5];

                if (withinHue && withinSaturation && withinBrightness) {
                    setDisjointIndex(index, index);
                }else {
                    setDisjointIndex(index, -1);
                }
            }
        }
        joinDisjointSet();
        filterSets();
        generateSetColours();
    }

    private void setDisjointIndex(int index, int value){
        nodeTree.set(value, index);
    }

    private void computePreview(){
        int x, y;
        Color colour = settings.previewColour();
        for(int i = 0; i < nodeTree.size(); i++){
            x = i % computeWidth;
            y = i / computeWidth;
            if(distinctTreeNodes.containsKey(nodeTree.find(i)))
                previewPixelWriter.setColor(x, y, colour);
            else
                previewPixelWriter.setColor(x, y, computePixelReader.getColor(x, y));
        }
    }

    public void compute(){
        computeDisjointSet();
        computeBAndW();
        computePreview();
        colourBW();
    }

    private void joinDisjointSet(){
        distinctTreeNodes = new HashMap<>();
        int indexValue, indexParent, nextIndexX, nextIndexY;
        for(int index = 0; index < nodeTree.size(); index++) {
            indexValue = nodeTree.get(index);
            if(indexValue == -1)
                continue;
            //checking if index is contained within set of trees
            if(!nodeTree.hasParent(index)) {
                int x = indexValue % computeWidth;
                int y = indexValue / computeWidth;
                distinctTreeNodes.put(index, new TreeNode(x ,y, index));
                indexParent = index;
            }else {
                indexParent = nodeTree.find(index);
            }
            // +1 on x
            nextIndexX = index + 1;
            if(nextIndexX % computeWidth != 0 && nodeTree.get(nextIndexX) != -1){ //within width & valid pixel
                distinctTreeNodes.get(indexParent).setX(nextIndexX % computeWidth);
                joinToExistingSet(indexParent, nodeTree.find(nextIndexX));
            }
            // +1 on y
            nextIndexY = index + computeWidth;
            if(nextIndexY < nodeTree.size() - computeWidth && nodeTree.get(nextIndexY) != -1){ //within height & valid pixel
                indexParent = nodeTree.find(indexParent);
                distinctTreeNodes.get(indexParent).setY(nextIndexY / computeWidth);
                joinToExistingSet(indexParent, nodeTree.find(nextIndexY));
            }
        }
    }

    private void filterSets(){
        LinkedList<Integer> keys = new LinkedList<>(distinctTreeNodes.keySet());
        //removing sets smaller than user defined value
        for(int i : keys)
            if(distinctTreeNodes.get(i).getSize() < settings.minSetSize())
                distinctTreeNodes.remove(i);
    }

    private void generateSetColours(){
        nodeColours.clear();
        int noOfColours = distinctTreeNodes.keySet().size();
        double colourDistance =  (double) 360 / noOfColours;
        for(int i = 0; i < noOfColours; i++){
            nodeColours.add(Color.hsb(i * colourDistance, 0.75, 0.75));
        }
    }

    private void joinToExistingSet(int thisPixel, int nextPixel){
        if(nodeTree.get(thisPixel) == nodeTree.get(nextPixel))
            return;
       if(!distinctTreeNodes.containsKey(nodeTree.find(nextPixel))) {
            nodeTree.union(thisPixel, nextPixel);
            distinctTreeNodes.get(thisPixel).incrementSize();
       }else{
           if(distinctTreeNodes.get(thisPixel).getSize() < distinctTreeNodes.get(nextPixel).getSize()){
               nodeTree.union(nextPixel, thisPixel);
               distinctTreeNodes.get(nextPixel).combineNodes(distinctTreeNodes.get(thisPixel));
               distinctTreeNodes.remove(thisPixel);
           }else{
               nodeTree.union(thisPixel, nodeTree.find(nextPixel));
               distinctTreeNodes.get(thisPixel).combineNodes(distinctTreeNodes.get(nextPixel));
               distinctTreeNodes.remove(nextPixel);
           }
       }
    }

    public HashMap<Integer, TreeNode> getDistinctTreeNodes() {
        return distinctTreeNodes;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        computeImages(image);
    }

    public Image getComputeImage(){
        return computeImage;
    }

    public Image getBlackAndWhiteImage() {
        return blackAndWhiteImage;
    }

    public Image getPreviewImage(){
        if(previewImage == null)
            computePreview();
        return previewImage;
    }

    public String getCompute() {
        return settings.computeRatio();
    }

    public void setSettings(Settings settings){
        if(!settings.computeRatio().equals(this.settings.computeRatio())){
            this.settings = settings;
            drawNewComputeImage();
        }else{
            this.settings = settings;
        }
    }

    public Settings getSettings(){
        return settings;
    }
}

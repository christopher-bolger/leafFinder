package leafFinder.model;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import leafFinder.model.DisjointSet.DisjointSet;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

public class ImageProcessor {
    public static final String[] COMPUTE_SIZE = {"1/1", "1/2", "1/4", "1/8"};
    private Settings settings;
    private final Image image;
    private final PixelReader originalPixelReader;
    private WritableImage computeImage, blackAndWhiteImage, highlightImage, previewImage;
    private PixelReader computePixelReader;
    private PixelWriter computePixelWriter, blackAndWhitePixelWriter, highlightPixelWriter, previewPixelWriter;
    private final int height, width;
    private int computeHeight;
    private int computeWidth;
    private DisjointSet<Integer> nodeTree;
    private HashMap<Integer, Integer> distinctTreeSizes;
    private int division;

    private final double[] hslMinMaxValues = {0, 360, 0, 1, 0, 1}; //defaults
                                    //hueMin, hueMax, SaturationMin, saturationMax, BrightnessMin, BrightnessMax
                                    // >=0,     <360,       >=0     ,   <=1      ,    >=0     ,      <=1

    public ImageProcessor(Image image) {
        int minSize = 50, borderSize = 1; Color borderColour = Color.BLUE, previewColour = Color.RED; String compute = COMPUTE_SIZE[2];
        settings = new Settings(compute, minSize, borderSize, borderColour, previewColour);

        this.image = image;
        originalPixelReader = image.getPixelReader();
        height = (int) image.getHeight();
        width = (int) image.getWidth();
        computeImages(image);
    }

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
        if(computeHeight % 2 == 1 && division > 1)
            computeHeight++;
        if(computeWidth % 2 == 1 && division > 1)
            computeWidth++;

        //System.out.println("ComputeWidth: " + computeWidth + " ComputeHeight: " + computeHeight);
        computeImage = new WritableImage(computeWidth, computeHeight);
        blackAndWhiteImage = new WritableImage(computeWidth, computeHeight);
        highlightImage = new WritableImage(computeWidth, computeHeight); //final image -> node tree filtering applied
        previewImage = new WritableImage(computeWidth, computeHeight);

        computePixelWriter = computeImage.getPixelWriter();
        blackAndWhitePixelWriter = blackAndWhiteImage.getPixelWriter();
        highlightPixelWriter = highlightImage.getPixelWriter();
        previewPixelWriter = previewImage.getPixelWriter();
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
        //System.out.println("Checking values");
        for(int x = 0; x < values.length; x++) {
            //System.out.println(values[x]);
            if(x < 3) {
                if (values[x] < 0 || values[x] > 360)
                    return;
            }else {
                if (values[x] < 0 || values[x] > 1)
                    return;
            }
        }
        //System.out.println("Updated values");
        System.arraycopy(values, 0, hslMinMaxValues, 0, values.length);
        computeDisjointSet();
        computeBAndW();
        computePreview();
    }

    //updating settings to make the ratio smaller seems to cause an exception here - it'll only render 1 line of the image
    public void computeBAndW(){
        Color black = Color.BLACK, white = Color.WHITE;
        int x, y;
        boolean isSelected;
        for(int i = 0; i < nodeTree.size(); i++){
            x = i % computeWidth;
            y = (i - 1) / computeWidth;
            isSelected = nodeTree.get(i) != -1;
            //System.out.println("x: " + x + " y: " + y);
            if(isSelected)
                blackAndWhitePixelWriter.setColor(x, y, white);
            else
                blackAndWhitePixelWriter.setColor(x, y, black);
        }
    }

    private void computeDisjointSet(){
        if(computePixelReader == null)
            return;

        nodeTree = new DisjointSet<>(computeWidth * computeHeight);
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
        //System.out.println("Array created, total size: " + nodeTree.size() + " Equal to height * width: " + (nodeTree.size() == (computeWidth * computeHeight)));
    }

    private void setDisjointIndex(int index, int value){
        nodeTree.set(value, index);
    }

    private void computePreview(){
        for(int y = 0; y < computeHeight; y++) {
            for(int x = 0; x < computeWidth; x++) {
                if(nodeTree.get((y * computeWidth) + x) != -1){
                    previewPixelWriter.setColor(x, y, settings.previewColour());
                }else{
                    previewPixelWriter.setColor(x, y, computePixelReader.getColor(x, y));
                }
            }
        }
    }

    public Image computeFinal(){
        computeDisjointSet();
        joinDisjointSet();
        filterSets();
        computeBAndW();
        computeHighlight();
        return highlightImage;
    }

    private void joinDisjointSet(){
        distinctTreeSizes = new HashMap<>();
        int indexValue, indexParent, nextIndexX, nextIndexY;
        for(int index = 0; index < nodeTree.size(); index++) {
            indexValue = nodeTree.get(index);
            if(indexValue == -1)
                continue;
            //checking if index is contained within set of trees
            if(!nodeTree.hasParent(index)) {
                distinctTreeSizes.put(index, 1);
                indexParent = index;
            }else {
                indexParent = nodeTree.find(index);
            }
            //System.out.println("Index: " + index + " Total nodes: " + nodeTree.size());
            // +1 on x
//            System.out.println("+1 on x");
            nextIndexX = index + 1;
            if(nextIndexX % computeWidth != 0 && nodeTree.get(nextIndexX) != -1){ //within width & valid pixel
                joinToExistingSet(indexParent, nodeTree.find(nextIndexX));
            }
            // +1 on y
//            System.out.println("+1 on y");
            nextIndexY = index + computeWidth;
            if(nextIndexY < nodeTree.size() - computeWidth && nodeTree.get(nextIndexY) != -1){ //within height & valid pixel
                indexParent = nodeTree.find(indexParent);
                joinToExistingSet(indexParent, nodeTree.find(nextIndexY));
            }
        }
    }

    private void filterSets(){
        //removing sets smaller than user defined value
        for(int i : distinctTreeSizes.keySet())
            if(distinctTreeSizes.get(i) < settings.minSetSize())
                distinctTreeSizes.remove(i);
    }

    private void joinToExistingSet(int thisPixel, int nextPixel){
        if(nodeTree.get(thisPixel) == nodeTree.get(nextPixel))
            return;
//        System.out.println("This pixel: " + thisPixel + " nextPixel: " + nextPixel);
//        System.out.println("This pixel isContained: " + distinctTreeSizes.containsKey(thisPixel));
//        System.out.println("Next pixel isContained: " + distinctTreeSizes.containsKey(nextPixel));
       if(!distinctTreeSizes.containsKey(nodeTree.find(nextPixel))) {
            nodeTree.union(thisPixel, nextPixel);
            distinctTreeSizes.put(thisPixel, distinctTreeSizes.get(thisPixel) + 1);
       }else{
    //           System.out.println("This pixel value: " + distinctTreeSizes.get(thisPixel));
    //           System.out.println("Next pixel value: " + distinctTreeSizes.get(nextPixel));
           if(distinctTreeSizes.get(thisPixel) < distinctTreeSizes.get(nextPixel)){
               nodeTree.setParent(nextPixel, thisPixel);
               distinctTreeSizes.put(nextPixel, distinctTreeSizes.get(thisPixel) + distinctTreeSizes.get(nextPixel));
               distinctTreeSizes.remove(thisPixel);
           }else{
               nodeTree.setParent(thisPixel, nodeTree.find(nextPixel));
               distinctTreeSizes.put(thisPixel, distinctTreeSizes.get(thisPixel) + distinctTreeSizes.get(nextPixel));
               distinctTreeSizes.remove(nextPixel);
           }
       }
    }

    public void computeHighlight(){
        int index, indexParent;
        for(int y = 0; y < computeHeight; y++) {
            for(int x = 0; x < computeWidth; x++) {
                index = ((y * computeWidth) + x);
                indexParent = nodeTree.find(index);
                if(nodeTree.get(index) != -1 && distinctTreeSizes.containsKey(indexParent) && distinctTreeSizes.get(indexParent) > settings.minSetSize()){
                    highlightPixelWriter.setColor(x, y, settings.previewColour());
                }else{
                    highlightPixelWriter.setColor(x, y, computePixelReader.getColor(x, y));
                }
            }
        }
    }

    public LinkedList<LeafNode> computeRects(){
        LinkedList<LeafNode> rects = new LinkedList<>();
        for(int key : distinctTreeSizes.keySet()){
            rects.add(new LeafNode(distinctTreeSizes.get(key), key));
        }
        rects.sort(LeafNode::size);
        for(int index = 0; index < nodeTree.size(); index++){
            if(nodeTree.get(index) == -1)
                continue;
            if(!distinctTreeSizes.containsKey(nodeTree.find(index)))
                continue;

        }
        //TODO
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

    public Image getHighlightImage() {
        return highlightImage;
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
        computeDisjointSet();
        computeBAndW();
        computePreview();
    }

    public Settings getSettings(){
        return settings;
    }
}

package leafFinder.model;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import leafFinder.model.DisjointSet.DisjointSet;

import java.util.Arrays;

public class ImageProcessor {
    public static final String[] COMPUTE_SIZE = {"1/1", "1/2", "1/4", "1/8"};
    private Settings settings;
    private Image image;
    private PixelReader originalPixelReader;
    private WritableImage computeImage, blackAndWhiteImage, highlightImage, previewImage;
    private PixelReader computePixelReader, blackAndWhitePixelReader;
    private PixelWriter computePixelWriter, blackAndWhitePixelWriter, highlightPixelWriter;
    private int height, width, computeHeight, computeWidth;
    private DisjointSet<Integer> nodeTree;
    private int division;

    private double[] hslMinMaxValues = {0, 359.99, 0, 1, 0, 1};
                                    //hueMin, hueMax, SaturationMin, saturationMax, BrightnessMin, BrightnessMax
                                    // >=0,     <360,       >=0     ,   <=1      ,    >=0     ,      <=1

    public ImageProcessor(Image image) {
        int minSize = 50, borderSize = 1;
        Color borderColour = Color.BLUE;
        String compute = COMPUTE_SIZE[1];
        settings = new Settings(compute, minSize, borderSize, borderColour);
        computeImages(image);
    }

    public ImageProcessor(Image image, Settings settings) {
        setSettings(settings);
        computeImages(image);
    }

    private void initialize(Image image){
        this.image = image;
        originalPixelReader = image.getPixelReader();
        height = (int) image.getHeight();
        width = (int) image.getWidth();
        Arrays.fill(hslMinMaxValues, -1);
    }

    public void computeImages(Image image){
        initialize(image);
        initializeWritableImages();
        drawNewComputeImage();
        computeBAndW();
        computeDisjointSet();
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
        if(height % 2 == 1)
            computeHeight++;
        if(width % 2 == 1)
            computeWidth++;

        computeImage = new WritableImage(computeWidth, computeHeight);
        blackAndWhiteImage = new WritableImage(computeWidth, computeHeight);
        highlightImage = new WritableImage(computeWidth, computeHeight);

        computePixelWriter = computeImage.getPixelWriter();
        blackAndWhitePixelWriter = blackAndWhiteImage.getPixelWriter();
        highlightPixelWriter = highlightImage.getPixelWriter();
    }

    private void drawNewComputeImage(){
        if(division == 1) {
            computePixelReader = originalPixelReader;
            return;
        }
        double hue, saturation, brightness;
        Color colour, colour2;
        for(int y = 0; y < height; y += division){
            for(int x = 0; x < width; x += division){
                colour = originalPixelReader.getColor(x, y);
                hue = colour.getHue(); saturation = colour.getSaturation(); brightness = colour.getBrightness();
                colour2 = Color.hsb(hue, saturation, brightness);
                computePixelWriter.setColor(x / division, y / division, colour2);
            }
        }
        computePixelReader = computeImage.getPixelReader();
    }

    public void setComputeArguments(double... values){
        if(values.length != 6)
            return;
        for(int x = 0; x < values.length; x++) {
            if (values[x] < 0)
                return;
            if(x <= 1)
                if(values[x] >= 360.0)
                    return;
            else
                if(values[x] > 1.0)
                    return;
        }
        System.arraycopy(values, 0, hslMinMaxValues, 0, values.length);
    }

    public boolean isComputeReady(){
        return computePixelReader != null;
    }

    public boolean computeBAndW(){
        if(!isComputeReady())
            return false;
        Color colour;
        double hue, saturation, brightness;
        boolean withinHue, withinSaturation, withinBrightness;
        for(int y = 0; y < computeHeight; y++)
            for(int x = 0; x < computeWidth; x++){
                colour = computePixelReader.getColor(x, y);
                hue = colour.getHue(); saturation = colour.getSaturation(); brightness = colour.getBrightness();
                withinHue = hslMinMaxValues[0] <= hue && hue <= hslMinMaxValues[1];
                withinSaturation = hslMinMaxValues[2] <= saturation && saturation <= hslMinMaxValues[3];
                withinBrightness = hslMinMaxValues[4] <= brightness && brightness <= hslMinMaxValues[5];
                if(withinHue && withinSaturation && withinBrightness)
                    blackAndWhitePixelWriter.setColor(x, y, Color.WHITE);
                else
                    blackAndWhitePixelWriter.setColor(x, y, Color.BLACK);
            }
        blackAndWhitePixelReader = blackAndWhiteImage.getPixelReader();
        return true;
    }

    private void deNoiseBAndW(){

    }

    private void computeDisjointSet(){
        nodeTree = new DisjointSet<>(computeWidth * computeHeight);
        for(int y = 0; y < computeHeight; y++)
            for(int x = 0; x < computeWidth; x++) {
                int index = (y * computeWidth) + x;
                if (blackAndWhitePixelReader.getColor(x, y) == Color.WHITE) {
                    nodeTree.set(index, index);
                } else nodeTree.set(-1, index);
            }
        System.out.println("NodeTree length = " + nodeTree.size() + " width * height: " + computeWidth * computeHeight);
    }

    private void computePreview(){
        WritableImage preview = new WritableImage(computePixelReader, computeWidth, computeHeight);
        PixelWriter writer = preview.getPixelWriter();
        for(int y = 0; y < computeHeight; y++)
            for(int x = 0; x < computeWidth; x++) {
                writer.setColor(x, y, computePixelReader.getColor(x, y));
                System.out.println("NodeTree length = " + nodeTree.size() + " position: " + (y * computeWidth) + x);
                if(nodeTree.get((y * computeWidth) + x) >= 0)
                    writer.setColor(x, y, Color.RED);
            }
        previewImage = preview;
    }

    public boolean computeHighlight(){
        if(!isComputeReady())
            return false;

        return true;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        drawNewComputeImage();
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
        return previewImage;
    }

    public String getCompute() {
        return settings.computeRatio();
    }

    public void setSettings(Settings settings){
        this.settings = settings;
    }

    public Settings getSettings(){
        return settings;
    }
}

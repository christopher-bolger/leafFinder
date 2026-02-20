package leafFinder.model;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import leafFinder.model.DisjointSet.DisjointSet;

import java.util.Arrays;

public class ImageProcessor {
    public static final String[] COMPUTE_SIZE = {"1/2", "1/4", "1/8"};
    private Settings settings;
    private Image image;
    private PixelReader originalPixelReader;
    private WritableImage computeImage, blackAndWhiteImage, highlightImage;
    private PixelReader computePixelReader, blackAndWhitePixelReader;
    private PixelWriter computePixelWriter, blackAndWhitePixelWriter, highlightPixelWriter;
    private double height, width, computeHeight, computeWidth;
    private DisjointSet<Integer> nodeTree;
    private int division;

    private double[] hslMinMaxValues = new double[6];
                                    //hueMin, hueMax, SaturationMin, saturationMax, LuminanceMin, LuminanceMax
                                    // >=0,     <360,       >=0     ,   <=1      ,    >=0     ,      <=1
    private boolean isValuesSet = false, isBlackAndWhiteOld = true;

    public ImageProcessor(Image image) {
        initialize(image);
        int minSize = 50, borderSize = 1;
        Color borderColour = Color.BLUE;
        String compute = COMPUTE_SIZE[0];
        settings = new Settings(compute, minSize, borderSize, borderColour);
        drawNewComputeImage();
    }

    public ImageProcessor(Image image, Settings settings) {
        initialize(image);
        setSettings(settings);
        drawNewComputeImage();
    }

    private void initialize(Image image){
        this.image = image;
        originalPixelReader = image.getPixelReader();
        height = image.getHeight();
        width = image.getWidth();
        Arrays.fill(hslMinMaxValues, -1);
    }

    private void initializeWritableImages(){
        switch(settings.computeRatio()){
            case "1/4" -> division = 4;
            case "1/8" -> division = 8;
            default -> division = 2;
        }
        computeHeight = height / division;
        computeWidth = width / division;

        computeImage = new WritableImage((int) computeWidth, (int) computeHeight);
        blackAndWhiteImage = new WritableImage((int) computeWidth, (int) computeHeight);
        highlightImage = new WritableImage((int) computeWidth, (int) computeHeight);
        computePixelWriter = computeImage.getPixelWriter();
        blackAndWhitePixelWriter = blackAndWhiteImage.getPixelWriter();
        highlightPixelWriter = highlightImage.getPixelWriter();
    }

    private void drawNewComputeImage(){
        initializeWritableImages();
        double averageHue, averageSaturation, averageBrightness;
        Color colour, colour2;
        for(int y = 0; y < height; y += division){
            for(int x = 0; x < width; x += division){
                averageHue = 0; averageSaturation = 0; averageBrightness = 0;
                for(int y2 = 0; y2 < division; y2++){
                    for(int x2 = 0; x2 < division; x2++){
                        colour = originalPixelReader.getColor(x + x2, y + y2);
                        averageHue += colour.getHue();
                        averageSaturation += colour.getSaturation();
                        averageBrightness += colour.getBrightness();
                    }
                }
                averageHue /= division^2; averageSaturation /= division^2; averageBrightness /= division^2;
                colour2 = Color.hsb(averageHue, averageSaturation, averageBrightness);
                computePixelWriter.setColor(x, y, colour2);
            }
        }
        computePixelReader = computeImage.getPixelReader();
    }

    public void setComputeArguements(double... values){
        if(values.length > hslMinMaxValues.length)
            return;
        System.arraycopy(values, 0, hslMinMaxValues, 0, values.length);
        boolean set = true;
        for(double d : values)
            if(d < 0) {
                set = false;
                break;
            }
        isValuesSet = set;
    }

    public boolean isComputeReady(){
        return isValuesSet && computePixelReader != null;
    }

    public boolean computeBAndW(){
        if(!isComputeReady())
            return false;
        if(!isBlackAndWhiteOld)
            return true;
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
        computeDisjointSet();
        isBlackAndWhiteOld = false;
        return true;
    }

    private void deNoiseBAndW(){

    }

    private void computeDisjointSet(){
        nodeTree = new DisjointSet<>(((int) computeWidth) * ((int) computeHeight));
        for(int y = 0; y < computeHeight; y++)
            for(int x = 0; x < computeWidth; x++)
                if(blackAndWhitePixelReader.getColor(x, y) == Color.WHITE) {
                    int index = (int) (y * computeWidth) + x;
                    nodeTree.insert(index, index);
                }
    }

    public boolean computeHighlight(){
        if(!isComputeReady() || isBlackAndWhiteOld)
            return false;
        //do the highlighting - we need to do some sort of edge detection and highlight a pixel distance from that
        return true;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        drawNewComputeImage();
    }

    public Image getBlackAndWhiteImage() {
        return blackAndWhiteImage;
    }

    public Image getHighlightImage() {
        return highlightImage;
    }

    public String getCompute() {
        return settings.computeRatio();
    }

    public void setSettings(Settings settings){
        //need to trigger change here
        this.settings = settings;
    }
}

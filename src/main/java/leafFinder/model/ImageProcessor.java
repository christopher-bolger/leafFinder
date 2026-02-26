package leafFinder.model;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import leafFinder.model.DisjointSet.DisjointSet;

import java.util.Objects;

public class ImageProcessor {
    public static final String[] COMPUTE_SIZE = {"1/1", "1/2", "1/4", "1/8"};
    private Settings settings;
    private Image image;
    private PixelReader originalPixelReader;
    private WritableImage computeImage, blackAndWhiteImage, highlightImage, previewImage;
    private PixelReader computePixelReader, blackAndWhitePixelReader;
    private PixelWriter computePixelWriter, blackAndWhitePixelWriter, highlightPixelWriter, previewPixelWriter;
    private int height, width, computeHeight, computeWidth;
    private DisjointSet<Integer> nodeTree;
    private int division;

    private double[] hslMinMaxValues = {0, 359.99, 0, 1, 0, 1}; //defaults
                                    //hueMin, hueMax, SaturationMin, saturationMax, BrightnessMin, BrightnessMax
                                    // >=0,     <360,       >=0     ,   <=1      ,    >=0     ,      <=1

    public ImageProcessor(Image image) {
        int minSize = 50, borderSize = 1; Color borderColour = Color.BLUE, previewColour = Color.RED; String compute = COMPUTE_SIZE[1];
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
        computeBAndW();
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
        if(height % 2 == 1 && division > 1)
            computeHeight++;
        if(width % 2 == 1 && division > 1)
            computeWidth++;

        computeImage = new WritableImage(computeWidth, computeHeight);
        blackAndWhiteImage = new WritableImage(computeWidth, computeHeight);
        highlightImage = new WritableImage(computeWidth, computeHeight);
        previewImage = new WritableImage(computeWidth, computeHeight);

        computePixelWriter = computeImage.getPixelWriter();
        blackAndWhitePixelWriter = blackAndWhiteImage.getPixelWriter();
        highlightPixelWriter = highlightImage.getPixelWriter();
        previewPixelWriter = previewImage.getPixelWriter();

        nodeTree = new DisjointSet<>(computeWidth * computeHeight);
    }

    private void drawNewComputeImage(){
        initializeWritableImages();
        if(division == 1)
            computePixelReader = originalPixelReader;
        else {
            Color colour;
            for (int y = 0; y < height; y += division)
                for (int x = 0; x < width; x += division) {
                    colour = originalPixelReader.getColor(x, y);
                    computePixelWriter.setColor(x / division, y / division, colour);
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
        computeBAndW();
        computePreview();
    }

    //updating settings to make the ratio smaller seems to cause an exception here - it'll only render 1 line of the image
    public void computeBAndW(){
        Color colour;
        double hue, saturation, brightness;
        boolean withinHue, withinSaturation, withinBrightness;
        for(int y = 0; y < computeHeight; y++) {
            for (int x = 0; x < computeWidth; x++) {
                colour = computePixelReader.getColor(x, y);
                hue = colour.getHue();
                saturation = colour.getSaturation();
                brightness = colour.getBrightness();

                withinHue = hslMinMaxValues[0] <= hue && hue <= hslMinMaxValues[1];
                withinSaturation = hslMinMaxValues[2] <= saturation && saturation <= hslMinMaxValues[3];
                withinBrightness = hslMinMaxValues[4] <= brightness && brightness <= hslMinMaxValues[5];
                int index = (y * computeWidth) + x;
                if (withinHue && withinSaturation && withinBrightness) {
                    blackAndWhitePixelWriter.setColor(x, y, Color.WHITE);
                    setDisjointIndex(index, index);
                }else {
                    blackAndWhitePixelWriter.setColor(x, y, Color.BLACK);
                    setDisjointIndex(index, -1);
                }
            }
        }
        blackAndWhitePixelReader = blackAndWhiteImage.getPixelReader();
    }

    private void setDisjointIndex(int index, int value){
        nodeTree.set(value, index);
    }

    private void computePreview(){
        for(int y = 0; y < computeHeight; y++) {
            for(int x = 0; x < computeWidth; x++) {
                int index = (y * computeWidth) + x;
                if(nodeTree.get(index) != -1){
                    previewPixelWriter.setColor(x, y, settings.previewColour());
                }else{
                    previewPixelWriter.setColor(x, y, computePixelReader.getColor(x, y));
                }
            }
        }
    }

    public void computeHighlight(){
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
        computeBAndW();
        computePreview();
    }

    public Settings getSettings(){
        return settings;
    }
}

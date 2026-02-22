package leafFinder.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import leafFinder.model.ImageProcessor;
import leafFinder.model.Settings;
import org.controlsfx.control.RangeSlider;

public class ImageViewer {
    public AnchorPane anchor;
    public SplitPane splitPane;
    public AnchorPane imageAnchor;
    public StackPane imagePane;
    public ImageView imageView;
    public AnchorPane controlsAnchor;
    public RangeSlider hueSlider;
    public RangeSlider saturationSlider;
    public RangeSlider lightnessSlider;
    public Label contrastLabel;
    public Slider contrast;
    public Label redChannelLabel;
    public Slider redChannel;
    public Label greenChannelLabel;
    public Slider greenChannel;
    public Label blueChannelLabel;
    public Slider blueChannel;
    public Button applyButton;

    private ImageProcessor processor;
    private Settings settings;
    private Image image;

    public void initialize(){
        imageView.fitWidthProperty().bind(imagePane.widthProperty());
        imageView.fitHeightProperty().bind(imagePane.heightProperty());
    }

    public void startUp(){
        if(image == null)
            return;
        if(settings == null)
            settings = new Settings("1/2", 50,1, Color.BLUE);
        processor = new ImageProcessor(image, settings);
        imageView.setImage(image);
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
        if(processor != null){
            processor.setSettings(settings);
        }
    }

    public Settings getSettings() {
        return settings;
    }

    public void setActiveImage(Image image) {
        imageView.setImage(image);
    }

    public Image getDownscaled(){
        return processor.getComputeImage();
    }

    public Image getHighlight(){
        return processor.getHighlightImage();
    }

    public Image getBW(){
        return processor.getBlackAndWhiteImage();
    }

    public Image original(){
        return image;
    }

    public void updateHue(DragEvent dragEvent) {
    }

    public void updateSaturation(DragEvent dragEvent) {
    }

    public void updateLightness(DragEvent dragEvent) {
    }

    public void processImage(MouseEvent mouseEvent) {
    }

    public void applyChanges(ActionEvent actionEvent) {
    }
}

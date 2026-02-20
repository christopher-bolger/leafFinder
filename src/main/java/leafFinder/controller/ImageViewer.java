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

    public void startUp(){
        if(image == null)
            return;
        if(settings == null)
            processor = new ImageProcessor(image);
        else
            processor = new ImageProcessor(image, settings);
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

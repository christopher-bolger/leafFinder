package leafFinder.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import leafFinder.model.ImageProcessor;
import leafFinder.model.RectWithMessage;
import leafFinder.model.Settings;
import leafFinder.model.TreeNode;
import org.controlsfx.control.RangeSlider;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ImageViewer {
    public AnchorPane anchor;
    public SplitPane splitPane;
    public AnchorPane imageAnchor;
    public Pane imagePane;
    public ImageView imageView;
    public AnchorPane controlsAnchor;
    public RangeSlider hueSlider;
    public RangeSlider saturationSlider;
    public RangeSlider lightnessSlider;
    public Button applyButton;
    public Group boxOverlay;
    public Button clearBoxesButton;
    private final List<RectWithMessage> listOfBoxes = new LinkedList<>();
    private final List<TreeNode> listOfSelectedBoxes = new LinkedList<>();
    private List<TreeNode> treeNodes = new LinkedList<>();
    private boolean boxesVisible = false;

    private ImageProcessor processor;
    private Settings settings;
    private Image image;

    public void initialize(){
        imageView.fitWidthProperty().bind(imagePane.widthProperty());
        imageView.fitHeightProperty().bind(imagePane.heightProperty());
        Platform.runLater(() -> hueSlider.requestLayout());
    }

    public void startUp(){;
        if(settings == null)
            settings = new Settings("1/4", 50,1, Color.BLUE, Color.RED, Color.LIME);
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

    public Image getBW(){
        if(listOfSelectedBoxes.isEmpty())
            return processor.getBlackAndWhiteImage();
        processor.colourBoxes(listOfSelectedBoxes, treeNodes);
        return processor.getBlackAndWhiteImage();
    }

    public Image original(){
        return image;
    }

    public Image getPreview(){
        return processor.getPreviewImage();
    }

    public void updateValues(MouseEvent dragEvent) {
        double minHue, maxHue, minSat, maxSat, minLight, maxLight;
        minHue = hueSlider.getLowValue(); maxHue = hueSlider.getHighValue();
        minSat = saturationSlider.getLowValue(); maxSat = saturationSlider.getHighValue();
        minLight = lightnessSlider.getLowValue(); maxLight = lightnessSlider.getHighValue();
        processor.setComputeArguments(minHue, maxHue, minSat, maxSat, minLight, maxLight);
       }

    public void showBoxes() {
        boxOverlay.getChildren().clear();
        listOfBoxes.clear();
        treeNodes = new LinkedList<>(processor.getDistinctTreeNodes().values());
        treeNodes.sort(Comparator.comparingInt(TreeNode::getSize)); // smallest first
        treeNodes = treeNodes.reversed();

        Image img = processor.getComputeImage();
        double scaleX = imageView.getBoundsInParent().getWidth()  / img.getWidth();
        double scaleY = imageView.getBoundsInParent().getHeight() / img.getHeight();

        for (TreeNode node : treeNodes) {
            double x   = node.getMinX() * scaleX;
            double y   = node.getMinY() * scaleY;
            double w   = (node.getMaxX() - node.getMinX()) * scaleX;
            double h   = (node.getMaxY() - node.getMinY()) * scaleY;
            String message = "Index: " + listOfBoxes.size() + "\n" +
                             "Size: " + node.getSize();

            Rectangle box = new Rectangle(x, y, w, h);
            box.setFill(Color.TRANSPARENT);
            box.setStroke(settings.boxColour());
            box.setStrokeWidth(settings.borderSize());
            listOfBoxes.add(new RectWithMessage(listOfBoxes.size(), message, box, node, listOfSelectedBoxes, settings));
            boxOverlay.getChildren().add(box);
        }
        boxesVisible = true;
    }

    public void applyChanges(ActionEvent actionEvent) {
        processor.computeFinal();
        showBoxes();
        clearBoxesButton.setDisable(false);
    }

    public void clearBoxes(ActionEvent actionEvent) {
        boxOverlay.getChildren().clear();
        listOfSelectedBoxes.clear();
        boxesVisible = false;
        clearBoxesButton.setDisable(true);
    }
}

package leafFinder.controller;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import leafFinder.model.ImageProcessor;
import leafFinder.model.RectWithMessage;
import leafFinder.model.Settings;
import leafFinder.model.TreeNode;
import leafFinder.utility.Utility;
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
    public Button animatePathButton;
    private List<TreeNode> treeNodes = new LinkedList<>();

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
            settings = new Settings("1/2", 20,1, Color.BLUE, Color.RED, Color.LIME);
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
        minHue = hueSlider.getLowValue() * 360; maxHue = hueSlider.getHighValue() * 360;
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
    }

    public void applyChanges(ActionEvent actionEvent) {
        processor.computeFinal();
        showBoxes();
        clearBoxesButton.setDisable(false);
        animatePathButton.setDisable(false);
    }

    public void clearBoxes(ActionEvent actionEvent) {
        boxOverlay.getChildren().clear();
        listOfSelectedBoxes.clear();
        clearBoxesButton.setDisable(true);
        animatePathButton.setDisable(true);
    }


    public void animatePath(ActionEvent actionEvent) {
        sortListByDistance();

        Image img = processor.getComputeImage();
        double scaleX = imageView.getBoundsInParent().getWidth()  / img.getWidth();
        double scaleY = imageView.getBoundsInParent().getHeight() / img.getHeight();

        // Start position
        double x0 = treeNodes.getFirst().getCenter()[0] * scaleX;
        double y0 = treeNodes.getFirst().getCenter()[1] * scaleY;

        Circle circle = new Circle(settings.borderSize() * 3, settings.selectionColour());
        circle.setCenterX(x0);
        circle.setCenterY(y0);

        Polyline trail = new Polyline();                       // committed path
        trail.setStroke(settings.boxColour());
        trail.setStrokeWidth(settings.borderSize());
        trail.getPoints().addAll(x0, y0);

        Line active = new Line(x0, y0, x0, y0);
        active.setStroke(settings.boxColour());
        active.setStrokeWidth(settings.borderSize());
        active.endXProperty().bind(circle.centerXProperty());
        active.endYProperty().bind(circle.centerYProperty());

        if (!boxOverlay.getChildren().contains(trail))
            boxOverlay.getChildren().addAll(trail, active, circle);

        Timeline timeline = new Timeline();
        Duration total = Duration.millis(5000);
        int hops = Math.max(1, treeNodes.size() - 1);
        Duration step = total.divide(hops);
        Duration t = Duration.ZERO;

        for (int i = 0; i < treeNodes.size() - 1; i++) {
            TreeNode next = treeNodes.get(i + 1);
            double xTo = next.getCenter()[0] * scaleX;
            double yTo = next.getCenter()[1] * scaleY;

            Duration at = t.add(step);
            KeyFrame kf = new KeyFrame(
                    at,
                    e -> {
                        trail.getPoints().addAll(xTo, yTo);
                        active.setStartX(xTo);
                        active.setStartY(yTo);
                    },
                    new KeyValue(circle.centerXProperty(), xTo, Interpolator.LINEAR),
                    new KeyValue(circle.centerYProperty(), yTo, Interpolator.LINEAR)
            );
            timeline.getKeyFrames().add(kf);
            t = at;
        }

        timeline.play(); // must be on FX Application Thread
    }

    private void sortListByDistance(){
        TreeNode selected;
        if(listOfSelectedBoxes.isEmpty())
            selected = treeNodes.getFirst();
        else
            selected = listOfSelectedBoxes.getLast();
        treeNodes.remove(selected);
        treeNodes.addFirst(selected);
        TreeNode from, to;
        for(int i = 0; i < treeNodes.size() - 1; i++){
            from = treeNodes.get(i);
            int minDist = Integer.MAX_VALUE, currentDist, smallestIndex = i+1;

            for(int j = i+1; j < treeNodes.size(); j++){
                to = treeNodes.get(j);
                currentDist = from.distanceBetweenNodes(to);

                if(currentDist < minDist){
                    minDist = currentDist;
                    smallestIndex = j;
                }
            }
            Utility.swap(i + 1, smallestIndex, treeNodes);
            Utility.swap(i + 1, smallestIndex, listOfBoxes); //need to keep them in the same order, no direct connection between.
        }
    }
}

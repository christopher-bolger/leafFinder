package leafFinder.controller;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.text.Text;
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
    public Text nodeCount;
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
            settings = new Settings("1/2", 20,1, Color.BLUE, Color.RED, Color.LIME, Color.MAGENTA, 1, Color.BLACK, 5, 5);
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

    public Image getColoured(){
        processor.colourBW();
        return processor.getColouredImage();
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
        nodeCount.setText(String.valueOf(processor.getDistinctTreeNodes().values().size()));
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
        processor.compute();
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
        double x = treeNodes.getFirst().getCenter()[0] * scaleX;
        double y = treeNodes.getFirst().getCenter()[1] * scaleY;

        Circle circle = createCircle(x, y);
        Polyline trail = createLine(x, y);
        Line active = createLineStart(x, y, circle);

        if (!boxOverlay.getChildren().contains(trail))
            boxOverlay.getChildren().addAll(trail, active, circle);

        Timeline timeline = new Timeline();
        Duration total = Duration.millis((settings.animationTimeSeconds() * 1000));
        int hops = Math.max(1, treeNodes.size() - 1);
        Duration step = total.divide(hops);
        Duration t = Duration.ZERO;

        for (int i = 0; i < treeNodes.size() - 1; i++) {
            TreeNode next = treeNodes.get(i + 1);
            double xTo = next.getCenter()[0] * scaleX;
            double yTo = next.getCenter()[1] * scaleY;

            Duration at = t.add(step);
            int finalI = i;
            KeyFrame kf = new KeyFrame(
                    at,
                    e -> {
                        trail.getPoints().addAll(xTo, yTo);
                        active.setStartX(xTo);
                        active.setStartY(yTo);
                        listOfBoxes.get(finalI).resetBoxColour();
                        listOfBoxes.get(finalI + 1).highlight();
                    },
                    new KeyValue(circle.centerXProperty(), xTo, Interpolator.LINEAR),
                    new KeyValue(circle.centerYProperty(), yTo, Interpolator.LINEAR)
            );
            timeline.getKeyFrames().add(kf);
            t = at;
        }
        timeline.getKeyFrames().add(new KeyFrame(t.add(step), e -> listOfBoxes.get(treeNodes.size() - 1).resetBoxColour() ));
        timeline.play();
    }

    private void sortListByDistance(){
        TreeNode selected;
        if(listOfSelectedBoxes.isEmpty())
            selected = treeNodes.getFirst();
        else
            selected = listOfSelectedBoxes.getLast();
        listOfSelectedBoxes.clear();
        int index = treeNodes.indexOf(selected);
        Utility.swap(index, 0, treeNodes);
        Utility.swap(index, 0, listOfBoxes); //need to keep them in the same order, no direct connection between.

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
            Utility.swap(i + 1, smallestIndex, listOfBoxes);
        }
    }

    private Circle createCircle(double x, double y){
        Circle circle = new Circle(settings.circleRadius(), settings.circleColor());
        circle.setCenterX(x);
        circle.setCenterY(y);
        return circle;
    }

    private Polyline createLine(double x, double y){
        Polyline trail = new Polyline();
        trail.setStroke(settings.lineColour());
        trail.setStrokeWidth(settings.lineSize());
        trail.getPoints().addAll(x, y);
        return trail;
    }

    private Line createLineStart(double x, double y, Circle circle){
        Line active = new Line(x, y, x, y);
        active.setStroke(settings.lineColour());
        active.setStrokeWidth(settings.lineSize());
        active.endXProperty().bind(circle.centerXProperty());
        active.endYProperty().bind(circle.centerYProperty());
        return active;
    }
}

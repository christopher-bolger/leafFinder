package leafFinder.model;

import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class RectWithMessage {
    private final int index;
    private final TreeNode node;
    private final Rectangle rectangle;
    private final Settings settings;

    public RectWithMessage(int index, String message, Rectangle rectangle, TreeNode source, List<TreeNode> selection, Settings settings) {
        this.index = index;
        this.node = source;
        this.rectangle = rectangle;
        this.settings = settings;
        // info box
        Tooltip tooltip = new Tooltip(message);
        rectangle.setOnMouseMoved(e -> tooltip.show(rectangle, e.getScreenX() + 10, e.getScreenY() + 10));
        rectangle.setOnMouseExited(e -> tooltip.hide());

        //adding to selection list in ImageViewer
        rectangle.setOnMouseClicked(e -> {
            if(!selection.contains(source) && e.getButton() == MouseButton.PRIMARY)
                selection.add(source);
            if(e.getButton() == MouseButton.SECONDARY)
                selection.remove(source);
            if(selection.contains(source))
                rectangle.setStroke(settings.selectionColour());
            else
                rectangle.setStroke(settings.boxColour());
        });
    }

    public void highlight() {
        rectangle.setStroke(settings.selectionColour());
    }

    public void resetBoxColour(){
        rectangle.setStroke(settings.boxColour());
    }

    public int getIndex() {
        return index;
    }

    public TreeNode getNode() {
        return node;
    }
}

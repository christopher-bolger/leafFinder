package leafFinder.model;

import javafx.scene.paint.Color;

public record Settings(String computeRatio, int minSetSize, int borderSize, Color boxColour, Color previewColour, Color selectionColour, Color circleColor, int lineSize, Color lineColour, int circleRadius, int animationTimeSeconds, boolean showLabels) {
}
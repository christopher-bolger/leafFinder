package leafFinder.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import leafFinder.model.ImageProcessor;
import leafFinder.model.Settings;

public class SettingsDialog{
    public Button saveButton;
    public Button cancelButton;
    public ChoiceBox<String> computeSizeField;
    public Spinner<Integer> minAreaSpinner;
    public Spinner<Integer> borderSizeSpinner ;
    public AnchorPane anchor;
    public ColorPicker borderColour;
    public ColorPicker highlightColor;
    public ColorPicker selectionColor;
    public ColorPicker circleColour;
    public Spinner<Integer> lineSizeSpinner;
    public ColorPicker lineColour;
    public Spinner<Integer> animationTimeSpinner;
    public Spinner<Integer> circleRadiusSpinner;
    private boolean isCancelled = false;
    private Settings settings;

    public void initialize(){
        borderSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 1, 1));
        minAreaSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 20, 10));
        lineSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1, 1));
        circleRadiusSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 5, 1));
        animationTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 5, 1));
        computeSizeField.getItems().addAll(ImageProcessor.COMPUTE_SIZE);
        computeSizeField.getSelectionModel().select(1);
    }

    public void setSettings(Settings settings){
        this.settings = settings;
        Platform.runLater(this::setValues);
    }

    private void setValues(){
        borderSizeSpinner.getValueFactory().setValue(settings.borderSize());
        minAreaSpinner.getValueFactory().setValue(settings.minSetSize());
        computeSizeField.getSelectionModel().select(settings.computeRatio());
        borderColour.valueProperty().setValue(settings.boxColour());
        highlightColor.valueProperty().setValue(settings.previewColour());
        selectionColor.valueProperty().setValue(settings.selectionColour());
        circleColour.valueProperty().setValue(settings.circleColor());
        lineColour.valueProperty().setValue(settings.lineColour());
        lineSizeSpinner.getValueFactory().setValue(settings.lineSize());
        circleRadiusSpinner.getValueFactory().setValue(settings.circleRadius());
        animationTimeSpinner.getValueFactory().setValue(settings.animationTimeSeconds());
    }

    public void save(ActionEvent actionEvent) {
        settings = new Settings(computeSizeField.getValue(),
                                minAreaSpinner.getValue(),
                                borderSizeSpinner.getValue(),
                                borderColour.getValue(),
                                highlightColor.getValue(),
                                selectionColor.getValue(),
                                circleColour.getValue(),
                                lineSizeSpinner.getValue(),
                                lineColour.getValue(),
                                circleRadiusSpinner.getValue(),
                                animationTimeSpinner.getValue());
        close();
    }

    public void cancel(ActionEvent actionEvent) {
        isCancelled = true;
        close();
    }

    private void close(){
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public Settings getSettings() {
        return settings;
    }

    public AnchorPane getAnchor() {
        return anchor;
    }
}

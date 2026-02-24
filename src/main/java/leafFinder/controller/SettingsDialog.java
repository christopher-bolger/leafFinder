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
    private boolean isCancelled = false;
    private Settings settings;

    public void initialize(){
        borderSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 1, 1));
        minAreaSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 50, 10));
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
        borderColour.valueProperty().setValue(settings.color());
    }

    public void save(ActionEvent actionEvent) {
        settings = new Settings(computeSizeField.getValue(), minAreaSpinner.getValue(), borderSizeSpinner.getValue(), borderColour.getValue());
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

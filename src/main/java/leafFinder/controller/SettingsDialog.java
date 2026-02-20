package leafFinder.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import leafFinder.model.ImageProcessor;
import leafFinder.model.Settings;

public class SettingsDialog extends AnchorPane {
    public Button saveButton;
    public Button cancelButton;
    public ChoiceBox<String> computeSizeField;
    public Spinner<Integer> minAreaSpinner, borderSizeSpinner;
    public AnchorPane anchor;
    public ColorPicker borderColour;
    private boolean isCancelled = false;
    private Settings settings;

    public void initialize(){
        borderSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 1, 1));
        minAreaSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 50, 10));
        computeSizeField.getItems().addAll(ImageProcessor.COMPUTE_SIZE);
        computeSizeField.getSelectionModel().clearAndSelect(0);
    }

    public void setValues(String computeRatio, int minSetSize){
        int index = 0;
        for(int i = 0; i < ImageProcessor.COMPUTE_SIZE.length; i++){
            if(ImageProcessor.COMPUTE_SIZE[i].equals(computeRatio)){
                index = i;
            }
        }
        computeSizeField.getSelectionModel().clearAndSelect(index);
        minAreaSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, minSetSize, 10));
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

package leafFinder.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class FoundationWindow {
    public MenuBar menuBar;
    public MenuItem loadImage;
    public MenuItem saveImage;
    public MenuItem closeWindow;
    public Menu viewOption;
    public MenuItem viewOriginalAction;
    public MenuItem bAndWAction;
    public MenuItem viewSelectionAction;
    public TabPane tabPane;
    public Menu settings;
    public MenuItem viewDownscaledAction;
    public MenuItem EditSettings;
    private char tick = '✓';

    private LinkedList<ImageViewer> imageViewers;

    public void initialize(){
        imageViewers = new LinkedList<>();
    }

    @FXML
    public void loadImage(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
        if (selectedFile != null) {
            FXMLLoader editor = new FXMLLoader(getClass().getResource("/imageViewerControls.fxml"));
            Node controller = editor.load();
            ImageViewer editorController = editor.getController();
            editorController.setImage(new Image(selectedFile.toURI().toString()));
            editorController.startUp();
            //editorController.initialize(ImageEditor::canRedo, redo);

            Tab tab = new Tab();
            tab.setContent(controller);
            tab.setText(selectedFile.getName());

            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
            settings.setDisable(false);
            viewOption.setDisable(false);
            imageViewers.add(editorController);
        }
    }

    public void saveImage(ActionEvent actionEvent) {
    }

    public void closeWindow(ActionEvent actionEvent) {
        Stage stage = (Stage) menuBar.getScene().getWindow();
        stage.close();
    }

    public void openSettingsDialog(ActionEvent actionEvent) throws IOException {
        FXMLLoader insertLoader = new FXMLLoader(getClass().getResource("/settings.fxml"));
        ImageViewer selectedController = getSelectedTabController();
        SettingsDialog settingsController = new SettingsDialog();
        settingsController.setAnchor(insertLoader.load());
        settingsController.setSettings(selectedController.getSettings());

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(settingsController.getAnchor()));
        stage.showAndWait();
        if(!settingsController.isCancelled() )
            selectedController.setSettings(settingsController.getSettings());
    }

    public void viewOriginal(ActionEvent actionEvent) {
        ImageViewer viewer = imageViewers.get(tabPane.getSelectionModel().getSelectedIndex());
        viewer.setActiveImage(viewer.original());
        clearViewActive();
        viewOption.getItems().getFirst().setText(tick + " Original");
    }

    public void viewBAndW(ActionEvent actionEvent) {
        ImageViewer viewer = imageViewers.get(tabPane.getSelectionModel().getSelectedIndex());
        viewer.setActiveImage(viewer.getHighlight());
        clearViewActive();
        viewOption.getItems().get(1).setText(tick + " B&W");
    }

    public void viewSelection(ActionEvent actionEvent) {
        ImageViewer viewer = imageViewers.get(tabPane.getSelectionModel().getSelectedIndex());
        viewer.setActiveImage(viewer.getHighlight());
        clearViewActive();
        viewOption.getItems().get(2).setText(tick + " Selection");
    }

    public void viewDownscaled(ActionEvent actionEvent) {
        ImageViewer viewer = imageViewers.get(tabPane.getSelectionModel().getSelectedIndex());
        viewer.setActiveImage(viewer.getDownscaled());
        clearViewActive();
        viewOption.getItems().get(3).setText(tick + " Downscaled");
    }

    public void clearViewActive() {
        viewOption.getItems().getFirst().setText("Original");
        viewOption.getItems().get(1).setText("B&W");
        viewOption.getItems().get(2).setText("Selection");
        viewOption.getItems().get(3).setText("Downscaled");
    }

    private ImageViewer getSelectedTabController(){
        return imageViewers.get(tabPane.getSelectionModel().getSelectedIndex());
    }
}

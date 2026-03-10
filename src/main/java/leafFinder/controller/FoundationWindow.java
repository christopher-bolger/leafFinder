package leafFinder.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
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
    public TabPane tabPane;
    public Menu settings;
    public MenuItem viewFinalAction;
    public MenuItem EditSettings;
    public MenuItem viewPreviewAction;
    private final char TICK = '✓';
    public MenuItem colouredAction;

    private LinkedList<ImageViewer> imageViewers;

    public void initialize(){
        imageViewers = new LinkedList<>();
    }

    @FXML
    public void loadImage(ActionEvent actionEvent) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp"));
        File selectedFile = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
        if (selectedFile != null) {
            FXMLLoader editor = new FXMLLoader(getClass().getResource("/imageViewerControls.fxml"));
            Node controller = editor.load();
            ImageViewer editorController = editor.getController();
            editorController.setImage(new Image(selectedFile.toURI().toString()));
            editorController.startUp();

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/settings.fxml"));
        AnchorPane root = loader.load();

        SettingsDialog settingsController = loader.getController();
        ImageViewer selectedController = getSelectedTabController();

        settingsController.setSettings(selectedController.getSettings());

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();
        if (!settingsController.isCancelled())
            selectedController.setSettings(settingsController.getSettings());
    }

    public void viewOriginal(ActionEvent actionEvent) {
        ImageViewer viewer = imageViewers.get(tabPane.getSelectionModel().getSelectedIndex());
        viewer.setActiveImage(viewer.original());
        clearViewActive();
        viewOption.getItems().getFirst().setText(TICK + " Original");
    }

    public void viewPreview(ActionEvent actionEvent) {
        ImageViewer viewer = imageViewers.get(tabPane.getSelectionModel().getSelectedIndex());
        viewer.setActiveImage(viewer.getPreview());
        clearViewActive();
        viewOption.getItems().get(1).setText(TICK + " Preview");
    }

    public void viewBAndW(ActionEvent actionEvent) {
        ImageViewer viewer = imageViewers.get(tabPane.getSelectionModel().getSelectedIndex());
        viewer.setActiveImage(viewer.getBW());
        clearViewActive();
        viewOption.getItems().get(2).setText(TICK + " B&W");
    }

    public void clearViewActive() {
        viewOption.getItems().getFirst().setText("Original");
        viewOption.getItems().get(1).setText("Preview");
        viewOption.getItems().get(2).setText("B&W");
        viewOption.getItems().get(3).setText("Coloured");
    }

    private ImageViewer getSelectedTabController(){
        return imageViewers.get(tabPane.getSelectionModel().getSelectedIndex());
    }

    public void viewColouredBW(ActionEvent actionEvent) {
        ImageViewer viewer = imageViewers.get(tabPane.getSelectionModel().getSelectedIndex());
        viewer.setActiveImage(viewer.getColoured());
        clearViewActive();
        viewOption.getItems().get(3).setText(TICK + " Coloured");
    }
}

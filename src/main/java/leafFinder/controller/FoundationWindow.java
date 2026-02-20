package leafFinder.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
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
            FXMLLoader editor = new FXMLLoader(getClass().getResource("/ImageEditor.fxml"));
            Node controller = editor.load();
            ImageViewer editorController = editor.getController();
            editorController.setImage(new Image(selectedFile.toURI().toString()));
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

    public void openSettingsDialog(ActionEvent actionEvent) {

    }

    public void viewOriginal(ActionEvent actionEvent) {

    }

    public void viewBAndW(ActionEvent actionEvent) {

    }

    public void viewSelection(ActionEvent actionEvent) {

    }
}

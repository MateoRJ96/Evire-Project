/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Properties;

import View_Student.StudentController;
import View_Teacher.TeacherController;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
/**
 *
 * @author Mateo
 */
public class SceneProperty {
    private double xOffset = 0;
    private double yOffset = 0;
    private static class Delta {
        double x, y;
    }
    
    final Delta dragDelta = new Delta();
    final BooleanProperty inDrag = new SimpleBooleanProperty(false);  
    
    public void centerScene(Stage stage){
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }
    
    public void showNewScene(ActionEvent event, String pathFXML, String titleWindow, String[] user, int tUser){
         try{            
            FXMLLoader loader = new FXMLLoader();
            Pane root1 = loader.load(getClass().getResource(pathFXML).openStream());   
            if(tUser != 1){
                StudentController sc = (StudentController) loader.getController();
                sc.loadUser(user);
            }else{
                TeacherController tc = (TeacherController) loader.getController();
                tc.loadUser(user);
            }
            Stage stage = new Stage();              
            stage.getIcons().add(new Image("/Resources/icon.png"));            
            stage.setTitle(titleWindow);            
            allowDragged(root1, stage);
            Scene scene = new Scene(root1);
            scene.getStylesheets().add(getClass().getResource("/Resources/Style00.css").toExternalForm());
            stage.setScene(scene);              
            ((Node)event.getSource()).getScene().getWindow().hide();
            stage.setResizable(false);            
            stage.show();            
            centerScene(stage);
          }catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "No se pudo cargar el recurso.", e);
        }
    }        
    
    public void allowDragged(Parent root, Stage stage){
            root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dragDelta.x = stage.getX() - event.getScreenX();
                dragDelta.y = stage.getY() - event.getScreenY();
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() + dragDelta.x);
                stage.setY(event.getScreenY() + dragDelta.y);
                stage.getWidth();
                stage.getHeight();
                stage.getX();
                stage.getY();
                inDrag.set(true);

            }
        });
    }
    
    public void setMessageDialog(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "",ButtonType.OK);
        alert.setTitle("Evire Information");
        alert.setHeaderText(msg);        
        alert.getDialogPane().setPrefSize(400, 60);
        alert.show();
    }
    
    public String setInputDialog(String titulo, String headerText, String contentText){
        TextInputDialog inputDiag = new TextInputDialog();
        inputDiag.setTitle(titulo);
        inputDiag.setHeaderText(headerText);
        inputDiag.setContentText(contentText);
        Optional<String> result = inputDiag.showAndWait();
        String text = "";
        if(result.isPresent())
            text = result.get();        
        return text;
    }        
}

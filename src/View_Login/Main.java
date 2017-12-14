/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import Properties.SceneProperty;
/**
 *
 * @author Mateo
 */
public class Main extends Application{
    @Override
    public void start(final Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        stage.initStyle(StageStyle.UNDECORATED);
        
        SceneProperty ps = new SceneProperty();
        ps.allowDragged(root, stage);
        stage.getIcons().add(new Image("/Resources/icon.png"));
        Scene scene = new Scene(root);                        
        stage.setScene(scene);       
        stage.setTitle("Evire Login");
        stage.show();
                
        ps.centerScene(stage);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

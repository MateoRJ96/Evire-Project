/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Login;

import Controllers.LoginOperations;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import Properties.SceneProperty;
import View_Student.StudentController;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Mateo
 */
public class LoginController implements Initializable {
    private SceneProperty sp = new SceneProperty();    
    private LoginOperations lop = new LoginOperations();
    public String data[];   
    
    @FXML
    private Button btnClose; 
    @FXML
    public TextField txtUsername;
    @FXML
    private PasswordField txtPassword;        
    
    @FXML
    private void btnCloseAction(ActionEvent event){
        try {
            Stage stage = (Stage)btnClose.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            sp.setMessageDialog(e.getMessage());
        }
    }                
    
    @FXML
    private void btnIniciarSesionAction(ActionEvent event){
        try {          
            StudentController sc = new StudentController();
            if(txtUsername.getText().trim().length() > 0 && txtPassword.getText().trim().length() > 0){                                                             
                lop.setId(txtUsername.getText().trim());
                lop.setPassword(txtPassword.getText().trim());
                data = lop.findUser();
                if( data != null){    
                    int tUser = Integer.parseInt(data[1].trim().substring(0, data[1].length() - 3));                    
                    if(tUser != 1){
                        sp.showNewScene(event, "/View_Student/Student.fxml", "Student Evire", new String[]{data[0].trim(), txtUsername.getText()}, tUser);                                                        
                    }else{
                        sp.showNewScene(event, "/View_Teacher/Teacher.fxml", "Teacher Evire", new String[]{data[0].trim(), txtUsername.getText()}, tUser);                                                        
                    }                    
                }else{                    
                    sp.setMessageDialog("¡El usuario o la contraseña son incorrectos!");
                }   
            }else{
                sp.setMessageDialog("¡Llene los campos, para inicar sesión!");
            }                                            
        } catch (Exception e) {
            sp.setMessageDialog("El formato usado es incorrecto");
        }
    }
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO               
    }    
    
}

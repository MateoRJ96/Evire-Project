/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Teacher;

import Controllers.LoginOperations;
import Controllers.StudentOperations;
import Properties.PasswordDialog;
import Properties.SceneProperty;
import View_Login.Main;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;


/**
 * FXML Controller class
 *
 * @author mateorj96
 */
public class TeacherController implements Initializable {
    
    private StudentOperations sop = new StudentOperations();
    private SceneProperty sp = new SceneProperty();
    private LoginOperations lop = new LoginOperations();    
    
    private String arrayTemp[];
    private ObjectId fileId;   
    private int idSemestreActual;  
    private String idTeacher;
    private String student[];
    private String idStudentActual;
    
    @FXML
    private TextField txtNumeroControl;
    @FXML
    private ComboBox cmbSemestre; 
    @FXML
    private ComboBox cmbMateria;
    @FXML
    private ListView listaArchivos;
    @FXML
    private ListView listaAlumnos;
    @FXML
    private Label lblUsername;
    @FXML
    private Button btnCerrarSesion;    
    
    public void loadUser(String [] user){
        lblUsername.setText(user[0]);        
        idTeacher = user[1].trim();
    }
    
    @FXML
    private void btnCerrarSesionAction(ActionEvent event){
        try {            
            Stage stage = (Stage)btnCerrarSesion.getScene().getWindow();
            stage.close();        
            Main m = new Main();
            m.start(new Stage());              
        } catch (Exception e) {
            sp.setMessageDialog(e.getMessage());
        }
    } 
    
    private void fillCmbSemestre(){
        List<String> lst = new ArrayList<String>();        
        for(Document item : sop.findSemestre()){                        
            String i = item.values().toString();
            String temp = i.substring(1, i.length() - 1);
            lst.add(temp);            
        }
        cmbSemestre.setItems(FXCollections.observableArrayList(lst));
    }
    
    @FXML
    private void fillCmbMateriaBySemestre(){  
        cmbMateria.setItems(null);        
        Document i = sop.findIdSemestreByName(cmbSemestre.getValue().toString());
        String s = i.values().toString();
        String temp = s.substring(1, s.length() - 1);
        String[] arrayT = temp.split(",");        
        idSemestreActual = Integer.parseInt(arrayT[0].substring(0, arrayT[0].length() - 2));
        
        List<String> lst = new ArrayList<String>();        
        for(Document item : sop.findMateriasBySemestre(idSemestreActual)){
            String s2 = item.values().toString();
            String temp2 = s2.substring(1, s2.length() - 1);            
            lst.add(temp2);
        }                
        cmbMateria.setItems(FXCollections.observableArrayList(lst)); 
        fillListaAlumnos();
    }
    
    private void fillListaAlumnos(){
        try {
            listaAlumnos.setItems(null);
            List<String> alumnos = new ArrayList<String>();
            for(Document i : sop.findStudentBySemestre(idSemestreActual)){
                String item = i.values().toString();
                String temp = item.substring(1, item.length() - 1);
                String []at = temp.split(",");
                
                alumnos.add(new Document()
                        .append("NC", at[0])
                        .append("Alumno", at[1])                    
                        .toJson()
                );
            }
            listaAlumnos.setItems(FXCollections.observableArrayList(alumnos));
        } catch (Exception e) {
        }
    }
    
    @FXML
    private void changePassword(){        
        try {            
            lop.setId(idTeacher);
            
            PasswordDialog pd = new PasswordDialog();
            Optional<String> result = pd.showAndWait();
            result.ifPresent(password -> lop.setPassword(password));

            String msg = "";
            
            if (lop.getPassword().isEmpty()) {
                sp.setMessageDialog("¡La contraseña no puede estar vacía!");
            } else {
                msg = lop.changePasswordForCurrentTeacher() != false ? "La contraseña se ha actualizado correctamente":"No se pudo actualizar la contraseña, intentelo de nuevo más tarde";
            }
            
            sp.setMessageDialog(msg);
        } catch (Exception e) {
        }
    }

    @FXML
    private void fillListFiles(){
        try {                 
            listaArchivos.setItems(null);
            List<String> files = new ArrayList<>();
            String arrayT[] = new String[4];
            for (Document item : sop.findFilesLoaded(idStudentActual)) {
                String i = item.values().toString();
                String temp = i.substring(1, i.length() - 1);
                arrayT = temp.split(",");
                float kb = Float.parseFloat(arrayT[2]) / 1024F;

                files.add(new Document()
                        .append("ObjectId", arrayT[0])
                        .append("Filename", arrayT[1])
                        .append("Size", kb + "KB")
                        .append("LoadDate", arrayT[3])
                        .toJson());
            }
            listaArchivos.setItems(FXCollections.observableArrayList(files));
        } catch (Exception e) {
            sp.setMessageDialog("Seleccione un alumno de la lista de alumnos");
        }
    }
    
    @FXML
    private void selectedStudentFromList(){
        try {
            Document item = Document.parse(listaAlumnos.getSelectionModel().getSelectedItem().toString());
            String ic = item.values().toString();
            student = ic.substring(1, ic.length() - 1).split(",");
            idStudentActual = student[0].trim();
            fillListFiles();
        } catch (Exception e) {
            sp.setMessageDialog(e.toString());
        }
    }
    
    @FXML
    private void selectedFileFromList(){
        try {
            Document item = Document.parse(listaArchivos.getSelectionModel().getSelectedItem().toString());
            String ic = item.values().toString();
            arrayTemp = ic.substring(1, ic.length() - 1).split(",");
            fileId = new ObjectId(arrayTemp[0]);            
        } catch (Exception e) {
            sp.setMessageDialog(e.toString());
        }
    }
    
    @FXML
    private void downloadFileToLocal(){
        if(arrayTemp != null){
            String msg = sop.downloadFileToMachine(
                    sop.setPathStorageLocalFile(
                            arrayTemp[1].trim()), 
                            fileId
            ) != false ? "¡La descarga fue exitosa!"
                    : "¡La descarga ha fallado!";
            sp.setMessageDialog(msg);
        }else{
            sp.setMessageDialog("Seleccione un archivo de la lista para descargarlo a su máquina");
        }                
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        fillCmbSemestre();
    }    
    
}

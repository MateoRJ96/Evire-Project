/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Student;

import Controllers.LoginOperations;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import java.io.IOException;

import Controllers.StudentOperations;
import Properties.PasswordDialog;
import Properties.SceneProperty;
import java.util.Optional;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.bson.Document;
import org.bson.types.ObjectId;

import View_Login.Main;
import javafx.stage.Stage;
/**
 * FXML Controller class
 *
 * @author Mateo
 */
public class StudentController implements Initializable{
    
    private StudentOperations sop = new StudentOperations();
    private SceneProperty sp = new SceneProperty();
    private LoginOperations lop = new LoginOperations();
    private callListFiles cl = new callListFiles();
    
    private String arrayTemp[];
    private ObjectId fileId;   
    private int idSemestreActual;
    
    @FXML
    private ComboBox cmbSemestre; 
    @FXML
    private ComboBox cmbMateria;
    @FXML
    private ListView listaArchivos;
    @FXML
    private TextField txtPathSelectedFile;
    @FXML
    private TextField txtNombreArchivo;  
    @FXML
    private TextField txtNumeroControl;
    @FXML
    private Label lblUsername;
    @FXML
    private Label lblFechaEvire;
    @FXML
    private Button btnCerrarSesion;
    
    @FXML
    private void btnCerrarSesionAction(ActionEvent event){
        try {
            cl.interrupt();
            Stage stage = (Stage)btnCerrarSesion.getScene().getWindow();
            stage.close();        
            Main m = new Main();
            m.start(new Stage());              
        } catch (Exception e) {
            sp.setMessageDialog(e.getMessage());
        }
    }  
    
    public void loadUser(String [] username){
        lblUsername.setText(username[0]);
        txtNumeroControl.setText(username[1]);
    }
    
    @FXML
    private void changePassword(){        
        try {
            lop.setId(txtNumeroControl.getText().trim());
            
            PasswordDialog pd = new PasswordDialog();
            Optional<String> result = pd.showAndWait();
            result.ifPresent(password -> lop.setPassword(password));

            String msg = "";
            
            if (lop.getPassword().isEmpty()) {
                sp.setMessageDialog("¡La contraseña no puede estar vacía!");
            } else {
                msg = lop.changePasswordForCurrentUser() != false ? "La contraseña se ha actualizado correctamente":"No se pudo actualizar la contraseña, intentelo de nuevo más tarde";
            }
            
            sp.setMessageDialog(msg);
        } catch (Exception e) {
        }
    }    
    
    @FXML
    private void openFileChooser(ActionEvent event) throws IOException{
        try {
            String [] filePath = sop.getFileFromLocal();
            txtPathSelectedFile.setText(filePath[0]);
            txtNombreArchivo.setText(filePath[1]);
        } catch (Exception e) {
        }
    }           
    
    public class callListFiles extends Thread{
        public void run(){
            try {                
                Thread.sleep(3000);
                fillListFiles();
            } catch (Exception e) {
            }
        }
    }           
    
    private void fillListFiles(){
        try {
            List<String> files = new ArrayList<>();
            String arrayT[] = new String[4];
            for (Document item : sop.findFilesLoaded(txtNumeroControl.getText().trim())) {
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
    }
    
    @FXML
    private void subirArchivoAlRepositorio(){        
        try {
            int idMateria = sop.getIdMateria(cmbMateria.getValue().toString(), idSemestreActual);                                  
            int dataStudent[] = new int[3];
            dataStudent[0] = Integer.parseInt(txtNumeroControl.getText());
            dataStudent[1] = idSemestreActual;
            dataStudent[2] = idMateria;
            ObjectId result = sop.loadFileToDataBase(txtPathSelectedFile.getText(), txtNombreArchivo.getText(), dataStudent);
            String msg = result != null ? "Operación exitosa, el archivo " + txtNombreArchivo.getText() + ", con el Id: " + result.toString() + " fue publicado" : "Error al subir el archivo";
            sp.setMessageDialog(msg); 
            fillListFiles();
        } catch (Exception e) {
            sp.setMessageDialog("Asegurese de haber cargado el archivo y seleccionado su semestre con su respectiva materia");
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
            sp.setMessageDialog(e.getMessage());
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
   
    @FXML
    private void deleteFileFromDB(){
        if(arrayTemp != null){
            String msg = sop.deleteFileToDataBase(fileId) != false ?
                    "El archivo fue eliminado satisfactoriamente":
                    "Hubo un problema al eliminar el archivo de la base de datos";
            sp.setMessageDialog(msg);
            fillListFiles();            
        }else{
            sp.setMessageDialog("Seleccione un archivo de la lista para eliminar un archivo");
        }
    }        
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO        
        fillCmbSemestre();        
        cl.start();
    }                
}

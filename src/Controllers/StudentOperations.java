/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import javafx.stage.FileChooser;
import java.io.*;
import javafx.stage.Stage;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

import com.mongodb.client.gridfs.*;
import com.mongodb.client.gridfs.model.*;
import org.bson.types.ObjectId;
/**
 *
 * @author mateorj96
 */
public class StudentOperations extends LoginOperations{
    public final GridFSBucket gf = GridFSBuckets.create(db, "ev");
    
    /**
     * Retorna un Array de tipo String cuyos valores son:
     * <p><pre>filePath[0] = Ruta del archivo<br>filePath[1] = Nombre del archivo</pre></p>
    */
    public String[] getFileFromLocal(){
        try {                                                
            String [] filePath = new String[2];
            
            FileChooser fc = new FileChooser();
            fc.setTitle("Seleccione un archivo");
            File selectFile = fc.showOpenDialog(new Stage());
            
            filePath[0] = selectFile.getAbsolutePath();
            filePath[1] = selectFile.getName();                        
            
            return filePath;
        } catch (Exception e) {
            return null;
        }
    }   
    
    public String setPathStorageLocalFile(String filename){
        try {            
            FileChooser fc = new FileChooser();
            fc.setTitle("Seleccione la ruta para descargar su archivo");
            fc.setInitialFileName(filename);            
            File dest = fc.showSaveDialog(new Stage());                       
            return dest.toPath().toString();
        } catch (Exception e) {
            return "";
        }
    }
    
    public List<Document> findFilesLoaded(String Id){
        try {
            MongoCollection<Document> c = db.getCollection("ev.files");
            Bson filtro = Filters.eq("metadata.Id_Alumno", Id);
            Bson pj = fields(include("filename", "length", "uploadDate"));
            List<Document> files = c.find()
                    .filter(filtro)
                    .projection(pj)
                    .into(new ArrayList<Document>());
            return files;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    public List<Document> findFilesLoaded(int [] dataStudent){
        try {
            MongoCollection<Document> c = db.getCollection("ev.files");
            Bson filtro = Filters.and(eq("metadata.Id_Alumno", dataStudent[0]), eq("metadata.Id_Semestre", dataStudent[1]), eq("metadata.Id_Materia", dataStudent[2]));
            Bson pj = fields(include("filename", "length", "uploadDate"));
            List<Document> files = c.find()
                    .filter(filtro)
                    .projection(pj)
                    .into(new ArrayList<Document>());
            return files;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Devuelve un listado de Documentos de la colección 'semestre'
     * <br>además de excluir el _Id de dicho documento y solo proyectar
     * <br>el campo con el valor del documento 'nombre_semestre'
     */
    public List<Document> findSemestre(){
        try {
            MongoCollection<Document> c = db.getCollection("semestre");
            Bson pj = fields(include("nombre_semestre"), excludeId());
            List<Document> carreras = c.find()
                    .projection(pj)
                    .into(new ArrayList<Document>());
            
            return carreras;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Devuelve un solo documento con el filtro 'Busqueda por nombre'
     * @param nombreSemestre
     * @return 
     */
    public Document findIdSemestreByName(String nombreSemestre){
        try {
            MongoCollection<Document> c = db.getCollection("semestre");
            Bson filtro = Filters.eq("nombre_semestre", nombreSemestre);            
            Document doc = c.find()
                    .filter(filtro)               
                    .first();
            return doc;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Devuelve una lista de documentos en base al Id del semestre a establecer
     * @param idSemestre
     * @return 
     */
    public List<Document> findMateriasBySemestre(int idSemestre){
        try {
            MongoCollection<Document> c = db.getCollection("materia");
            Bson filtro = Filters.and(eq("semestre", idSemestre), eq("carrera", 5));
            Bson pj = fields(include("nombre_materia"), excludeId());
            List<Document> materias = c.find()
                    .filter(filtro)
                    .projection(pj)
                    .into(new ArrayList<Document>());
            
            return materias;
        } catch (Exception e) {
            return null;
        }
    }
    
    public int getIdMateria(String nombreMateria, int idSemestre){
        try {
            MongoCollection<Document> c = db.getCollection("materia");
            Bson filtro = Filters.and(eq("semestre", idSemestre), eq("carrera", 5), eq("nombre_materia", nombreMateria));             
            Document doc = c.find()
                    .filter(filtro)               
                    .first();
            
            String s = doc.values().toString();
            String temp = s.substring(1, s.length() - 1);            
            String [] array = temp.split(",");
            return Integer.parseInt(array[0].trim().substring(0, array[0].length() - 2));
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Devuelve True en caso de que se haya cargado correctamente el archivo
     * <br>a la base de datos, en caso contrario, devuelve un false:
     * <p>Recibe tres parametros:
     * <br>pathFile : La ruta del archivo seleccionado
     * <br>filename : El nombre del archivo seleccionado
     * <br>dataStudent : Un array de enteros del conjunto de identificadores del alumno:
     * <pre>            
     * dataStudent[0] = Id Alumno o Numero de Control              
     * dataStudent[1] = Id Materia
     * </pre>
     * </p>
     * @param pathFile
     * @param filename
     * @param dataStudent
     * @return 
     */
    public ObjectId loadFileToDataBase(String pathFile, String filename, int[] dataStudent){
        try {                        
            InputStream s = new FileInputStream(new File(pathFile));
            GridFSUploadOptions op = new GridFSUploadOptions()
                    .chunkSizeBytes(261120)
                    .metadata(new Document("type", "Evidencia de Portafolio")
                            .append("Id_Alumno", String.valueOf(dataStudent[0]))
                            .append("Id_Semestre", dataStudent[1])
                            .append("Id_Materia", dataStudent[2])
                    );    
            ObjectId fileId = gf.uploadFromStream(filename, s, op);
            return fileId;
        } catch (Exception e) {
            return null;
        }
    }
    
    public boolean downloadFileToMachine(String path, ObjectId fileId){
        try {
            FileOutputStream download = new FileOutputStream(path);
            gf.downloadToStream(fileId, download);
            download.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean deleteFileToDataBase(ObjectId fileId){
        try {
            gf.delete(fileId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public List<Document> findStudentBySemestre(int idSemestre){
        try {
            MongoCollection<Document> alum = db.getCollection("alumno");           
            Bson filtro = Filters.eq("semestre", idSemestre);            
            Bson pj = fields(include("_id", "nombreCompleto"));            
            List<Document> students = alum.find()
                    .filter(filtro)
                    .projection(pj)
                    .into(new ArrayList<Document>());
            return students;
        } catch (Exception e) {
            return null;
        }
    }
}

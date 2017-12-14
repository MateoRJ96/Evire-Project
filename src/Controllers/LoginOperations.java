/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author mateorj96
 */
public class LoginOperations {    
    public final String host = "localhost";
    public final int port = 27017;
    public final String dbName = "demoEvire";
    
    public final MongoClient client = new MongoClient(host, port);
    public final MongoDatabase db = client.getDatabase(dbName);    
    
    private String id;
    private String password;     

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String[] findUser(){
        try {            
            String data[] = validateCollection("alumno");
            if(data == null){
                data = validateCollection("docente");
            }
            return data;
        } catch (Exception e) {
            return null;
        }
    }      
    
    private String[] validateCollection(String collectionName){
        try {
            MongoCollection<Document> alum = db.getCollection(collectionName);           
            Bson filtro = Filters.and(eq("_id", getId()), eq("userpass", getPassword()));            
            Bson pj = fields(include("nombreCompleto", "tipoUsuario"), excludeId());            
            Document doc = alum.find()
                    .filter(filtro)
                    .projection(pj)
                    .first();
            String temp = doc.values().toString();
            String values = temp.substring(1, temp.length() -1);
            String arrayT[] = values.split(",");
            return arrayT;
        } catch (Exception e) {
            return null;
        }
    }
    
    public boolean changePasswordForCurrentUser(){
        try {
            MongoCollection<Document> col = db.getCollection("alumno");           
            Bson filtro = Filters.eq("_id", getId());
            Bson update = combine(set("userpass", getPassword()));
            col.updateOne(filtro, update);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean changePasswordForCurrentTeacher(){
        try {
            MongoCollection<Document> col = db.getCollection("docente");           
            Bson filtro = Filters.eq("_id", getId());
            Bson update = combine(set("userpass", getPassword()));
            col.updateOne(filtro, update);
            return true;
        } catch (Exception e) {
            return false;
        }
    } 
}

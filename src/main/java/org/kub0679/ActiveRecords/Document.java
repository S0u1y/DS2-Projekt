package org.kub0679.ActiveRecords;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.kub0679.DatabaseGateway;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@Setter
@Builder
@ToString
public class Document extends DatabaseGateway {

    private int document_id;
    private String title;
    private Date dateAdded;
    private Date releaseYear;
    private String description;


    private static final String FIND_BY_ID = "SELECT * FROM document WHERE document_id = ?";
    public Document(){
        CREATE = "INSERT INTO document(title, dateAdded, releaseYear, description) VALUES(?, ?, ?, ?)";
        UPDATE = "UPDATE document SET title=?, dateAdded=?, releaseYear=?, description=? WHERE document_id = ?";
    }

    public Document(int document_id, String title, Date dateAdded, Date releaseYear, String description) {
        this();
        this.document_id = document_id;
        this.title = title;
        this.dateAdded = dateAdded;
        this.releaseYear = releaseYear;
        this.description = description;
    }

    public boolean create(){
        if(document_id!=0){
            Document found = findById(document_id);
            if(found != null){
                update();
                return true;
            }
        }
        try(ResultSet rs = executeQuery(CREATE, title, dateAdded, releaseYear, description)){
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean update() {
        if(document_id == 0) return false;

        try(ResultSet rs = executeQuery(CREATE, title, dateAdded, releaseYear, description)){
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Document findById(int documentId) {
        try(ResultSet rs = new Document().executeQuery(FIND_BY_ID, documentId)){
            if (rs == null) return null;
            if (!rs.next()) return null;
            return new Document(
                    rs.getInt("document_id"),
                    rs.getString("title"),
                    rs.getDate("dateAdded"),
                    rs.getDate("releaseYear"),
                    rs.getString("description")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isPersistent(){
        return document_id != 0;
    }

}

package org.kub0679.ActiveRecords;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.kub0679.Utility.DBField;
import org.kub0679.DatabaseGateway;
import org.kub0679.Utility.ReflectiveCloner;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@Setter
@Builder
@ToString
public class Document extends DatabaseGateway {

    protected final static String FIND_BY_ID = "SELECT * FROM document WHERE document_id = ?";

    @DBField(strategy = DBField.Strategy.Id)
    private Integer document_id;
    @DBField
    private String title;
    @DBField
    private Date dateAdded;
    @DBField
    private Date releaseYear;
    @DBField
    private String description;

    public Document(){
        super();
    }

    public Document(int document_id, String title, Date dateAdded, Date releaseYear, String description) {
        this();
        this.document_id = document_id;
        this.title = title;
        this.dateAdded = dateAdded;
        this.releaseYear = releaseYear;
        this.description = description;
    }

    public Document(ResultSet rs){
        super(rs);
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

        try(ResultSet rs = executeQuery(UPDATE, title, dateAdded, releaseYear, description, document_id)){
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document findById(int documentId) {
        try(
                Document document = new Document();
                ResultSet rs = document.executeQuery(FIND_BY_ID, documentId);
        ){
            if (rs == null) return null;
            if (!rs.next()) return null;

            return new Document(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Document findById(){
        return findById(document_id);
    }

    public boolean load(){
        if(!this.isPersistent()) return false;

        Document document = findById();

        ReflectiveCloner.clone(document, this);

        return true;
    }

    public boolean isPersistent(){
        return document_id != 0;
    }

}

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
public class Comment extends DatabaseGateway {

    protected final static String FIND_BY_ID = "SELECT * FROM \"Comment\" WHERE comment_id = ?";

    @DBField(strategy = DBField.Strategy.Id)
    private Integer comment_id;
    @DBField
    private Integer user_id;
    @DBField
    private Integer document_id;
    @DBField
    private Integer page_id;
    @DBField
    private Date commentDate;
    @DBField
    private String content;

    public Comment(){
        super();
    }
    public Comment(ResultSet rs){super(rs);}

    public Comment(Integer comment_id, Integer user_id, Integer document_id, Integer page_id, Date commentDate, String content) {
        this();
        this.comment_id = comment_id;
        this.user_id = user_id;
        this.document_id = document_id;
        this.page_id = page_id;
        this.commentDate = commentDate;
        this.content = content;
    }

    public boolean create(){
        if(comment_id != 0){
            Comment found = findById(comment_id);
            if(found != null){
                update();
                return true;
            }
        }

        try(ResultSet rs = executeQuery(CREATE, user_id, document_id, page_id, commentDate, content)){ //I could probably make this in a superclass using reflection too..!
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Comment[] getAll() {
        try (
                Comment comment = new Comment();
                ResultSet rs = comment.executeQuery(comment.SELECT);
                ResultSet sizeResult = comment.executeQuery("Select count(*) FROM \"Comment\"");
        ){
            if (rs == null) return null;
            sizeResult.next();
            int size = sizeResult.getInt(1);
            Comment[] output = new Comment[size];

            for (int i = 0; rs.next() && i < size; i++) {
                output[i] = new Comment(rs);
            }

            return output;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Comment findById(int comment_id){
        try(
                Comment comment = new Comment();
                ResultSet rs = comment.executeQuery(FIND_BY_ID, comment_id)
        ){
            if (rs == null) return null;
            if (!rs.next()) return null;

            return new Comment(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Comment findById(){
        return findById(comment_id);
    }

    public boolean load(){
        if(!this.isPersistent()) return false;

        Comment comment = findById();

        ReflectiveCloner.clone(comment, this);

        return true;
    }
    public boolean isPersistent(){
        return comment_id != 0;
    }

    public boolean update() {
        if(comment_id == 0) return false;

        try(ResultSet rs = executeQuery(UPDATE, user_id, document_id, page_id, commentDate, content, comment_id)){
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(){
        if(!isPersistent()) return;

        try(ResultSet rs = executeQuery(DELETE, comment_id)){
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

package org.kub0679;

import org.kub0679.ActiveRecords.Comment;
import org.kub0679.ActiveRecords.Document;
import org.kub0679.ActiveRecords.User;
import org.kub0679.Utility.FunctionPreparer;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//think about inheritance X composition for the future.
public class Searcher extends FunctionPreparer {

    public static List<User> searchUsers(String keyword, String option){

        List<User> output = new ArrayList<>();

        try{
            CallableStatement stmt = prepareFunction("SearchUsers(?, ?)", Types.REF_CURSOR, keyword, option);
            stmt.execute();

            ResultSet rs = (ResultSet) stmt.getObject(1);
            while(rs.next()){
                output.add(new User(rs));
            }

            rs.close();
            closeInternal();

            return output;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Document> searchDocuments(String keyword, String option){
        List<Document> output = new ArrayList<>();

        try{
            CallableStatement stmt = prepareFunction("SearchDocuments(?, ?)", Types.REF_CURSOR, keyword, option);
            stmt.execute();

            ResultSet rs = (ResultSet) stmt.getObject(1);
            while(rs.next()){
                output.add(new Document(rs));
            }

            rs.close();
            closeInternal();

            return output;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<User> searchStaff(String keyword, String option){
        List<User> output = new ArrayList<>();

        try{
            CallableStatement stmt = prepareFunction("SearchStaff(?, ?)", Types.REF_CURSOR, keyword, option);
            stmt.execute();

            ResultSet rs = (ResultSet) stmt.getObject(1);
            while(rs.next()){
                output.add(new User(rs));
            }

            rs.close();
            closeInternal();

            return output;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Comment> searchComments(String keyword, String option){
        List<Comment> output = new ArrayList<>();

        try{
            CallableStatement stmt = prepareFunction("SearchComments(?, ?)", Types.REF_CURSOR, keyword, option);
            stmt.execute();

            ResultSet rs = (ResultSet) stmt.getObject(1);
            while(rs.next()){
                output.add(new Comment(rs));
            }

            rs.close();
            closeInternal();

            return output;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}

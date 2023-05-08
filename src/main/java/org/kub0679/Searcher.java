package org.kub0679;

import org.kub0679.ActiveRecords.Document;
import org.kub0679.ActiveRecords.User;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Searcher implements Closeable {
    public static final DatabaseGateway db = new DatabaseGateway();

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
            Searcher.closeInternal();

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
            Searcher.closeInternal();

            return output;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    private static CallableStatement prepareFunction(String sql, int type, Object ... objects) throws SQLException {
        Connection connection = db.connect();
        CallableStatement stmt = connection.prepareCall("{? = CALL " + sql + "}");
        stmt.registerOutParameter(1, type); //this type is mental, why not use enums??
        for (int i = 2; i < objects.length+2; i++) {
            stmt.setObject(i, objects[i-2]);
        }

        return stmt;
    }

    @Override
    public void close() throws IOException {
        closeInternal();
    }
    public static void closeInternal(){
        db.close();
    }
}

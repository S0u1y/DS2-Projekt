package org.kub0679;

import java.io.Closeable;
import java.sql.*;

//Supertype for gateways
public class DatabaseGateway implements Closeable {

    private String URL;
    protected String CREATE;
    protected String SELECT;
    protected String FIND_BY_ID;
    protected String UPDATE;
    protected String DELETE;


    private Connection connection = null;
    protected Connection connect(){

        try {
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@dbsys.cs.vsb.cz:1521:oracle",
                    "kub0679",
                    "yP9LP7c9fa8s202z");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }
    protected ResultSet executeQuery(String sql){
        connection = connect();
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            if(e.getErrorCode() == 101){//sometimes queries don't return anything (insert, update, ..)
                return null;
            }
            System.out.println(e.getErrorCode());
            System.out.println(e.getMessage());
        }
        return null;
    }
    protected ResultSet executeQuery(String sql, Object ... args){
        connection = connect();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            int i = 1;
            for(Object object : args){
                if(object instanceof Integer integer){
                    statement.setInt(i, integer);
                } else if (object instanceof Double dubl) {
                    statement.setDouble(i, dubl);
                } else if (object instanceof String string) {
                    statement.setString(i, string);
                } else if(object == null){
                    statement.setString(i, null);
                }
                else{
                    System.out.println("Instance does not fall within bounds.");
                    return null;
                }
                i++;
            }
            if(sql.equals(CREATE) || sql.equals(UPDATE) || sql.equals(DELETE)){
                statement.executeUpdate();
                return null;
            }else{
                return statement.executeQuery();
            }


        } catch (SQLException e) {
            if(e.getErrorCode() == 0){//sometimes queries don't return anything (insert, update, ..)
                return null;
            }
            System.out.println(e.getErrorCode());
            System.out.println(e.getMessage());
        }
        return null;
    }



    public void close(){
        System.out.println("Closing Gateway...");
        try {
            if(this.connection != null && !this.connection.isClosed()){
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

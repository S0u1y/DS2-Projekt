package org.kub0679;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.sql.*;

//Supertype for gateways
public class DatabaseGateway implements Closeable {

    private String URL;

    private String className = this.getClass().getSimpleName();
    protected String CREATE;
    protected String SELECT;
    protected String FIND_BY_ID;
    protected String UPDATE;
    protected String DELETE;

    public DatabaseGateway(){
        if(className.equalsIgnoreCase("user") || className.equalsIgnoreCase("comment")){ //cannot use some names...
            className = "\""+className+"\"";
        }

        CREATE = "INSERT INTO " + className + "(";
        SELECT = "SELECT * FROM " + className;
        UPDATE = "UPDATE " + className + " SET ";
        DELETE = "DELETE FROM " + className + " WHERE ";

        Field[] fields = this.getClass().getDeclaredFields();

        //SETUP CREATE STRING
        for(int i = 0; i < fields.length; i++){
            Field field = fields[i];
            if(field.isAnnotationPresent(DBField.class) && field.getAnnotation(DBField.class).strategy() != DBField.Strategy.Id){
                if(i>0 && fields[i-1].getAnnotation(DBField.class).strategy() != DBField.Strategy.Id) CREATE += ", ";

                CREATE += field.getName();
            }
        }
        CREATE += ") VALUES(";

        for(int i = 0; i < fields.length; i++){
            Field field = fields[i];
            if(field.isAnnotationPresent(DBField.class) && field.getAnnotation(DBField.class).strategy() != DBField.Strategy.Id){
                if(i>0 && fields[i-1].getAnnotation(DBField.class).strategy() != DBField.Strategy.Id) CREATE += ", ";

                CREATE += "?";
            }
        }
        CREATE += ")";

        //SETUP UPDATE STRING
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if(field.isAnnotationPresent(DBField.class) && field.getAnnotation(DBField.class).strategy() != DBField.Strategy.Id){
                if(i>0 && fields[i-1].getAnnotation(DBField.class).strategy() != DBField.Strategy.Id) UPDATE += ", ";

                UPDATE += field.getName() + "= ?";
            }
        }
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if(field.isAnnotationPresent(DBField.class) && field.getAnnotation(DBField.class).strategy() == DBField.Strategy.Id){
                UPDATE += " WHERE " + field.getName() + "= ?";
                break; //only one identity field
            }
        }

        //SETUP DELETE STRING
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if(field.isAnnotationPresent(DBField.class) && field.getAnnotation(DBField.class).strategy() == DBField.Strategy.Id){
                DELETE += field.getName() + "= ?";
                break; //only one identity field
            }
        }

//        System.out.println(CREATE);
//        System.out.println(SELECT);
//        System.out.println(UPDATE);
//        System.out.println(DELETE); //Debugging is nice
    }



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
    //maybe this should be allowed to thro outside of itself...
    protected ResultSet executeQuery(String sql, Object ... args){
        connection = connect();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            int i = 1;
            for(Object object : args){
                statement.setObject(i, object);
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

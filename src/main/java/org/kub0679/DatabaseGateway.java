package org.kub0679;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.Arrays;

//Supertype for gateways
public class DatabaseGateway implements Closeable {

    private String URL;

    protected static String CREATE;
    protected static String SELECT;
    protected String FIND_BY_ID;
    protected static String UPDATE;
    protected static String DELETE;

    public DatabaseGateway(){
        if(CREATE != null) return;
        System.out.println("Creating Base");

        String className = this.getClass().getSimpleName();

        if(className.equalsIgnoreCase("user") || className.equalsIgnoreCase("comment")){ //cannot use some names...
            className = "\""+ className +"\"";
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

    public DatabaseGateway(ResultSet resultSet) {
        Field[] fields = this.getClass().getDeclaredFields();

        try {
            ResultSetMetaData metaData = resultSet.getMetaData();

            for(int i = 1; i <= metaData.getColumnCount(); i++){


                String colName = metaData.getColumnName(i);
                Field field = Arrays.stream(fields)
                        .filter(field1 -> colName.equalsIgnoreCase(field1.getName()))
                        .findFirst().orElse(null);

                assert field != null;
                if(!field.canAccess(this)){
                    field.setAccessible(true);

                    field.set(this, resultSet.getObject(colName, field.getType()));

                    field.setAccessible(false);
                }else{
                    field.set(this, resultSet.getObject(colName, field.getType()));
                }
            }
        } catch (IllegalAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //one connection per class,
    //might be worth considering to create a separate DBConnector class that'd be static for all.
    private static Connection connection = null;
    protected Connection connect(){
        if(connection == null)
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
    //maybe this should be allowed to throw outside of itself...
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
            if(connection != null && !connection.isClosed()){
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

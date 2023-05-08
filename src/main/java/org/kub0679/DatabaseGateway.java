package org.kub0679;

import org.kub0679.Utility.CRUDMAPPER;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.Arrays;

//Supertype for gateways
public class DatabaseGateway implements Closeable {

    private String URL;

    //perhaps it would be worth creating a third party mapper for class-CRUD
    protected String CREATE;
    protected String SELECT;
    protected String FIND_BY_ID;
    protected String UPDATE;
    protected String DELETE;

    public DatabaseGateway(){
        CRUDMAPPER.crud crud = CRUDMAPPER.cruds.get(this.getClass().getSimpleName());

        if(crud != null){
            crud = CRUDMAPPER.cruds.get(this.getClass().getSimpleName());

            CREATE = crud.CREATE();
            SELECT = crud.SELECT();
            UPDATE = crud.UPDATE();
            DELETE = crud.DELETE();

            return;
        }

        var strings = setupCRUD();
        CREATE = strings[0];
        SELECT = strings[1];
        UPDATE = strings[2];
        DELETE = strings[3];

        CRUDMAPPER.cruds.put(this.getClass().getSimpleName(), new CRUDMAPPER.crud(CREATE, SELECT, UPDATE, DELETE));

    }

    protected String[] setupCRUD(){
        String create, select, update, delete;

        String className = this.getClass().getSimpleName();

        if(className.equalsIgnoreCase("user") || className.equalsIgnoreCase("comment")){ //cannot use some names...
            className = "\""+ className +"\"";
        }

        create = "INSERT INTO " + className + "(";
        select = "SELECT * FROM " + className;
        update = "UPDATE " + className + " SET ";
        delete = "DELETE FROM " + className + " WHERE ";

        Field[] fields = this.getClass().getDeclaredFields();

        //SETUP CREATE STRING
        for(int i = 0; i < fields.length; i++){
            Field field = fields[i];
            if(field.isAnnotationPresent(DBField.class) && field.getAnnotation(DBField.class).strategy() != DBField.Strategy.Id){
                if(i>0 && fields[i-1].getAnnotation(DBField.class).strategy() != DBField.Strategy.Id) create += ", ";

                create += field.getName();
            }
        }
        create += ") VALUES(";

        for(int i = 0; i < fields.length; i++){
            Field field = fields[i];
            if(field.isAnnotationPresent(DBField.class) && field.getAnnotation(DBField.class).strategy() != DBField.Strategy.Id){
                if(i>0 && fields[i-1].getAnnotation(DBField.class).strategy() != DBField.Strategy.Id) create += ", ";

                create += "?";
            }
        }
        create += ")";

        //SETUP UPDATE STRING
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if(field.isAnnotationPresent(DBField.class) && field.getAnnotation(DBField.class).strategy() != DBField.Strategy.Id){
                if(i>0 && fields[i-1].getAnnotation(DBField.class).strategy() != DBField.Strategy.Id) update += ", ";

                update += field.getName() + "= ?";
            }
        }
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if(field.isAnnotationPresent(DBField.class) && field.getAnnotation(DBField.class).strategy() == DBField.Strategy.Id){
                update += " WHERE " + field.getName() + "= ?";
                break; //only one identity field
            }
        }

        //SETUP DELETE STRING
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if(field.isAnnotationPresent(DBField.class) && field.getAnnotation(DBField.class).strategy() == DBField.Strategy.Id){
                delete += field.getName() + "= ?";
                break; //only one identity field
            }
        }

        return new String[]{create, select, update, delete};
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

//                assert field != null;
                if(field == null) continue;
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
        closeInternal();
    }

    public static void closeInternal(){
        try {
            if(connection != null && !connection.isClosed()){
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCREATE() {
        return CREATE;
    }

    public String getSELECT() {
        return SELECT;
    }

    public String getUPDATE() {
        return UPDATE;
    }

    public String getDELETE() {
        return DELETE;
    }
}

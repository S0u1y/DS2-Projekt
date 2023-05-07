package org.kub0679.ActiveRecords;

import lombok.*;
import org.kub0679.DBField;
import org.kub0679.DatabaseGateway;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@Setter
@Builder
@ToString
public class User extends DatabaseGateway {
    @DBField(strategy = DBField.Strategy.Id)
    private Integer User_Id;
    @DBField
    public Integer Address_Id;
    @DBField
    public String Firstname;
    @DBField
    public String Lastname;
    @DBField
    public String Password;
    @DBField
    public String Email;
    @DBField
    public String Permission;
    @DBField
    public Date Activeuntil;
    @DBField
    public String Phone;

    private static final String FIND_BY_ID = "SELECT * FROM \"User\" WHERE user_id = ?";

    public User() {
        super();
    }

    public User(int user_Id, int address_Id, String firstname, String lastname, String password, String email, String permission, Date activeuntil, String phone) {
        this();
        User_Id = user_Id;
        Address_Id = address_Id;
        Firstname = firstname;
        Lastname = lastname;
        Password = password;
        Email = email;
        Permission = permission;
        Activeuntil = activeuntil;
        Phone = phone;
    }

    public User(ResultSet rs) {
        super(rs);
    }

    //Maybe make the sql functions close automatically...
    public boolean create(){
        if(User_Id != 0){
            User found = findById(User_Id);
            if(found != null){
                update();
                return true;
            }
        }

        try(ResultSet rs = executeQuery(CREATE, Address_Id, Firstname, Lastname, Password, Email, Permission, Activeuntil, Phone)){
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static User[] getAll() {
        try (
                User user = new User();
                ResultSet rs = user.executeQuery(SELECT);
                ResultSet sizeResult = user.executeQuery("Select count(*) FROM \"User\"");
        ){
            if (rs == null) return null;
            sizeResult.next();
            int size = sizeResult.getInt(1);
            User[] output = new User[size];

            for (int i = 0; rs.next(); i++) {
                output[i] = new User(rs);
            }

            return output;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static User findById(int user_id){
        try(ResultSet rs = new User().executeQuery(FIND_BY_ID, user_id)){
            if (rs == null) return null;
            if (!rs.next()) return null;

            return new User(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findById(){
        return findById(User_Id);
    }

    public boolean update() {
        if(User_Id == 0) return false;

        try(ResultSet rs = executeQuery(CREATE, Address_Id, Firstname, Lastname, Password, Email, Permission, Activeuntil, Phone, User_Id)){
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}


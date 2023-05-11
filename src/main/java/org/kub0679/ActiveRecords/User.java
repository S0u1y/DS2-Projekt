package org.kub0679.ActiveRecords;

import lombok.*;
import org.kub0679.Utility.DBField;
import org.kub0679.DatabaseGateway;
import org.kub0679.Utility.FunctionPreparer;
import org.kub0679.Utility.ReflectiveCloner;

import java.sql.*;

@Getter
@Setter
@ToString
public class User extends DatabaseGateway {

    protected final static String FIND_BY_ID = "SELECT * FROM \"User\" WHERE user_id = ?";

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


    public User() {
        super();
    }

    public User(int user_Id, int address_Id, String firstname, String lastname, String password, String email, String permission, Date activeuntil, String phone) {
        super();
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
                ResultSet rs = user.executeQuery(user.SELECT);
                ResultSet sizeResult = user.executeQuery("Select count(*) FROM \"User\"");
        ){
            if (rs == null) return null;
            sizeResult.next();
            int size = sizeResult.getInt(1);
            User[] output = new User[size];

            for (int i = 0; rs.next() && i < size; i++) {
                output[i] = new User(rs);
            }

            rs.close();
            sizeResult.close();

            return output;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static User findById(int user_id){
        try(
                User user = new User();
                ResultSet rs = user.executeQuery(FIND_BY_ID, user_id)
        ){
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

    public boolean load(){
        if(!this.isPersistent()) return false;

        User user = findById();

        ReflectiveCloner.clone(user, this);

        return true;
    }
    public boolean isPersistent(){
        return User_Id != 0;
    }

    public boolean update() {
        if(User_Id == 0) return false;

        try(ResultSet rs = executeQuery(UPDATE, Address_Id, Firstname, Lastname, Password, Email, Permission, Activeuntil, Phone, User_Id)){
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void edit(){//edit = update?
        try (CallableStatement stmt = FunctionPreparer.prepareFunction(
                "EditUser(?,?,?,?,?,?,?,?)", Types.NULL,
                User_Id, Firstname, Lastname, Email, Phone, Address_Id, Password, Activeuntil)
        )
        {
            stmt.execute();
            close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(){
        try (CallableStatement stmt = FunctionPreparer.prepareFunction(
                "DeleteUser(?)", Types.NULL, User_Id)
        )
        {
            stmt.execute();
            close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void changeRole(String newRole){
        try (CallableStatement stmt = FunctionPreparer.prepareFunction(
                "ChangeRole(?, ?)", Types.NULL, User_Id, newRole)
        )
        {
            stmt.execute();
            close();
            load();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}


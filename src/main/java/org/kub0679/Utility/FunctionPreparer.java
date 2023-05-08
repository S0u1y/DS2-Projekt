package org.kub0679.Utility;

import org.kub0679.DatabaseGateway;

import java.io.Closeable;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class FunctionPreparer implements Closeable {

    protected static final DatabaseGateway db = new DatabaseGateway();

    public static CallableStatement prepareFunction(String sql, int type, Object... objects) throws SQLException {
        Connection connection = db.connect();
        CallableStatement stmt;
        if(type != Types.NULL){
            stmt = connection.prepareCall("{? = CALL " + sql + "}");
            stmt.registerOutParameter(1, type); //this type is mental, why not use enums??
            for (int i = 2; i < objects.length+2; i++) {
                stmt.setObject(i, objects[i-2]);
            }
        }
        else{
            stmt = connection.prepareCall("{CALL " + sql + "}");
            for (int i = 1; i < objects.length+1; i++) {
                stmt.setObject(i, objects[i-1]);
            }
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

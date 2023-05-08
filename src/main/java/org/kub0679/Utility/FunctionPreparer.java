package org.kub0679.Utility;

import org.kub0679.DatabaseGateway;

import java.io.Closeable;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class FunctionPreparer implements Closeable {

    protected static final DatabaseGateway db = new DatabaseGateway();

    protected static CallableStatement prepareFunction(String sql, int type, Object... objects) throws SQLException {
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

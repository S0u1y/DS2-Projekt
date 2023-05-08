package org.kub0679;

import org.kub0679.ActiveRecords.User;
import org.kub0679.Utility.Encoding;

import java.sql.*;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        List<User> users = Searcher.searchUsers("", "first-name");

        for (User user: users) {
            System.out.println(user);
        }

    }

}
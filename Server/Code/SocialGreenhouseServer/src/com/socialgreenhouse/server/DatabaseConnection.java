/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialgreenhouse.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Roy
 */
public class DatabaseConnection {
    
    private Connection connection;
    private String url;
    private String user;
    private String password;
    
    public DatabaseConnection(String url, String user, String password) throws ClassNotFoundException
    {
        this.url = url;
        this.user = user;
        this.password = password;

        Class.forName("com.mysql.jdbc.Driver");
            
    }
    
    public Connection getConnection()
    {
        return connection;
    }
    
    public void openConnection() throws SQLException
    {
        connection = DriverManager.getConnection(url, user, password);
    }
    
    public void closeConnection() throws SQLException
    {
        connection.close();
    }
}

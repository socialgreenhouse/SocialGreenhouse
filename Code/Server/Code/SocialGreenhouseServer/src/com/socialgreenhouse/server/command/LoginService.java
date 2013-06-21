/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialgreenhouse.server.command;

import com.socialgreenhouse.server.DatabaseConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author justin
 */
public class LoginService {
    
    private DatabaseConnection connection;
    
    public LoginService(DatabaseConnection connection)
    {
        this.connection = connection;
    }
    
    public boolean login(String username, String userpass)
    {
        Statement st = null;
        ResultSet rs = null;
      
        
        try{
            connection.openConnection();
            st = connection.getConnection().createStatement();
            
            /*String strUserName = request.getParameter(username);
            String strPassWord = request.getParameter(userpass);
            
            PreparedStatement prepStmt = connection.prepareStatement("SELECT * FROM User WHERE UserName = '+strUserName+' AND UserPassword = '+strPassWord+'");
            
            rs = prepStmt.executeQuery();*/
            
            String selectStatement = "SELECT * FROM User WHERE UserName = ? AND UserPassword = ?";
            PreparedStatement prepStmt = connection.getConnection().prepareStatement(selectStatement);
            prepStmt.setString(1, username);
            prepStmt.setString(2, userpass);
            rs = prepStmt.executeQuery();

            if(rs.next())
            {
                return true;
            }
        }
        catch(SQLException ex)
        {
            System.out.println("Error : Error during connection with database server");
            System.out.println(ex.toString());
            return false;
        }
        finally
        {
            try {
                connection.closeConnection();
                
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error : Error during connection with database server");
            }
        }
    return false;
    }
}

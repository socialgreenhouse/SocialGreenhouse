/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialgreenhouse.server.command;

import com.socialgreenhouse.server.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Roy
 */
public class SaveValueService {
    private DatabaseConnection connection;
    
    public SaveValueService(DatabaseConnection connection)
    {
        this.connection = connection;
    }
    
    /**
     * saves new value of the module in the database
     * @param ModuleID
     * @param Value
     * @return true or false
     */
    public boolean save(String ModuleID, String Value)
    {
        Statement st = null;
        ResultSet rs = null;
        try{
            connection.openConnection();
            st = connection.getConnection().createStatement();
            
            long timestamp = System.currentTimeMillis()/1000;
            String updateStatement = "INSERT INTO `State` VALUES (NULL, ?, ?, ?)";
            PreparedStatement prepStmt = connection.getConnection().prepareStatement(updateStatement);
            prepStmt.setLong(1, timestamp);
            prepStmt.setString(2, Value);
            prepStmt.setString(3, ModuleID);
            
            prepStmt.executeUpdate();
            prepStmt.close();

            return true;
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
                System.out.println("Error : Error during connection with database server 1");
            }
            
            
        }
    }
}

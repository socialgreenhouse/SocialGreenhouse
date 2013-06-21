/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialgreenhouse.server.command;

import com.socialgreenhouse.server.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author justin
 */
public class ModuleService {
    
    private DatabaseConnection connection;
    private final static char UNIT_SEPARATOR = (char) 0x1F;
    private final static char RECORD_SEPARATOR = (char) 0x1E;
    
    public ModuleService(DatabaseConnection connection)
    {
        this.connection = connection;
    }
    
    /**
     * 
     * @return String with all the information of all modules
     */
    public String getAllModulesToSQL()
    {
        try {
            connection.openConnection();
            PreparedStatement st = connection.getConnection().prepareStatement("SELECT * FROM View_Tablet");
            ResultSet rs = st.executeQuery();
            String result  = "";
            while(rs.next())
            {
                result += rs.getString("SerialNo") + UNIT_SEPARATOR + rs.getString("Name") + UNIT_SEPARATOR + rs.getString("Sensor") + UNIT_SEPARATOR + rs.getString("Value") + UNIT_SEPARATOR + rs.getString("Time") + RECORD_SEPARATOR;
            }
            
            connection.closeConnection();
            
            return result + "\r\n";
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            System.out.println(ex.getCause());
        }
        return "Failed";
    }
    
    /**
     * Saves new module when not in databases
     * otherwise adds to database
     * @param input with values SerialNO,Name,Sensor
     * @return succes or failed
     */
    public String saveModule(String input)
    {
        try {
            String[] values = csvDecode(input);
            PreparedStatement ps = connection.getConnection().prepareStatement("SELECT * FROM Module WHERE SerialNo = ?");
            ps.setString(1, values[0]);
            ResultSet rs = ps.executeQuery();
            
            if(rs.next())
            {
                ps = connection.getConnection().prepareStatement("UPDATE Module SET Name = ? WHERE SerialNo = ?");
                ps.setString(1, values[1]);
                ps.setString(2, values[0]);
                
                ps.executeQuery();
            }
            else
            {
                ps = connection.getConnection().prepareStatement("INSERT INTO Module VALUES (? , ?, ?)");
                ps.setString(1, values[0]);
                ps.setString(2, values[1]);
                ps.setString(3, values[2]);
                
                ps.executeQuery();
            }
            
            return "Succes";
            
        } catch (SQLException ex) {
            //nothing happens
        }
        
        return "Failed";
    }
    
    /**
     * Split string on the unit_sperator
     * @param input String with values
     * @return array with all values
     */
    public static String[] csvDecode(String input)
    {
        return input.split(UNIT_SEPARATOR + "");
    }
}

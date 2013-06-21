/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialgreenhouse.server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is an class that contains all the data to create an twitter object. 
 */
public class TwitterData {

    private DatabaseConnection connection;
    private HashMap<String,String> settings = new HashMap<String,String>();

    public TwitterData(DatabaseConnection connection) {
        /*
         * Fetch data from the database server.
         */
        this.connection = connection;
        try {
            connection.openConnection();

            PreparedStatement statement = connection.getConnection().prepareStatement("SELECT * FROM `Settings`");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                settings.put(rs.getString("Name"), rs.getString("Value"));
            }
            connection.closeConnection();
        } catch (SQLException ex) {
            Logger.getLogger(TwitterData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getConsumerKey()
    {
       return settings.get("ConsumerKey");
    }
    
    public String getConsumerSecret()
    {
        return settings.get("ConsumerSecret");
    }
    
    public String getAccessToken()
    {
        return settings.get("AccessToken");
    }
    
    public String getAccessTokenSecret()
    {
        return settings.get("AccessTokenSecret");
    }
}

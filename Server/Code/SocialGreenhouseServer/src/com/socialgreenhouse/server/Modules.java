/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialgreenhouse.server;

import com.socialgreenhouse.server.condition.LessThanCondition;
import com.socialgreenhouse.server.TriggerParser.Factory;
import com.socialgreenhouse.server.condition.EqualStringCondition;
import com.socialgreenhouse.server.condition.GreaterThanCondition;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import twitter4j.Twitter;
import twitter4j.api.TweetsResources;
import twitter4j.internal.org.json.JSONArray;
import twitter4j.internal.org.json.JSONException;

/**
 *
 * @author justin
 */
public class Modules {

    private DatabaseConnection connection;
    private TweetsResources twitter;
    private String lastUpdate;
    private Map<String, Integer> previousState; // A map with the previous state of the trigger. (For updating)
    private TriggerParser triggerParser;
    private Map<String, Factory<Observer>> observerFactory = new HashMap<>(); // Factories for observer and condition
    private Map<String, Factory<Condition>> conditionFactory = new HashMap<>();

    public Modules(DatabaseConnection connection, TweetsResources twitter) {

        this.connection = connection;
        this.twitter = twitter;

        createFactories();

        previousState = new HashMap<>();
        triggerParser = new TriggerParser(observerFactory, conditionFactory);

    }
    /*
     * Set the twitter instance
     */
    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
        recreateFactories();
    }

    /*
     *THis method loads all the triggers from the database.
     */
    public Map<String, List<Observer>> loadTriggers() {
        try {
            Map<String, List<Observer>> triggers = new HashMap<>();
            connection.openConnection();

            PreparedStatement statement = connection.getConnection().prepareStatement("SELECT SerialNo, TriggerData FROM Module");
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String moduleId = result.getString("SerialNo");
                String triggerData = result.getString("TriggerData");
                JSONArray triggerArray = new JSONArray(triggerData);
                List<Observer> observers = triggerParser.parse(triggerArray);

                triggers.put(moduleId, observers);
                previousState.put(moduleId, triggerData.hashCode());
            }

            return triggers;
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println(e.getCause());
        }
        return null;
    }
    
    /*
     * This method checks if any trigger has changed, if so it wil update the trigger instantly
     */
    public Map<String, List<Observer>> refreshTriggers(Map<String, List<Observer>> triggers) {
        try {
            connection.openConnection();

            PreparedStatement statement = connection.getConnection().prepareStatement("SELECT SerialNo, TriggerData FROM Module");
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String triggerData = result.getString("TriggerData");
                JSONArray triggerArray = new JSONArray(triggerData);
                String moduleId = result.getString("SerialNo");
                
                if( ! previousState.containsKey(moduleId) || previousState.get(moduleId) != triggerData.hashCode())
                {
                    triggers.put(moduleId, triggerParser.parse(triggerArray));
                    previousState.put(moduleId, triggerData.hashCode());
                    System.out.println(triggerData);
                }
            }

            return triggers;
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println(e.getCause());
        }
        return triggers;
    }

    /*
     * THis method creates the factories use for the triggerparser
     */
    private void createFactories() {
        observerFactory.put("Tweet", new Factory<Observer>() {
            @Override
            public Observer create(JSONArray args) throws JSONException {
                return new Tweet(args.getString(0), twitter);
            }
        });

        conditionFactory.put("LessThan", new Factory<Condition>() {
            @Override
            public Condition create(JSONArray args) throws JSONException {
                return new LessThanCondition(args.getDouble(0));
            }
        });

        conditionFactory.put("Equals", new Factory<Condition>() {
            @Override
            public Condition create(JSONArray args) throws JSONException {
                return new EqualStringCondition(args.getString(0));
            }
        });

        conditionFactory.put("GreaterThan", new Factory<Condition>() {
            @Override
            public Condition create(JSONArray args) throws JSONException {
                return new GreaterThanCondition(args.getDouble(0));
            }
        });
    }
    
    /*
     * This method makes it possible to recreate the factories. This is used when the twitter instance has changed.
     */
    
    private void recreateFactories()
    {
        conditionFactory.clear();
        observerFactory.clear();
        createFactories();
    }
}

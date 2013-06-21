package com.socialgreenhouse.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import twitter4j.internal.org.json.JSONArray;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author justin
 */
public class TriggerParser {

    /*
     * This factory is used to connect an object with for example an condition. Therefore the server will know tha "Tweet" connects to the Tweet class
     */
    public interface Factory<T>{
        public T create(JSONArray args) throws JSONException;
    }
    
    private Map<String, Factory<Observer>> observerFactory  = new HashMap<>();
    private Map<String, Factory<Condition>> conditionFactory  = new HashMap<>();
    
    public TriggerParser(Map<String, Factory<Observer>> observerFactory,
                         Map<String, Factory<Condition>> conditionFactory) 
    {
        this.observerFactory = observerFactory;
        this.conditionFactory = conditionFactory;
    }

    //
    // This method parse a trigger and returns a observer
    // The trigger is parsed from a JSONArray in the database.
    // @return Observer object with
    // @exception throws a JSONException if a observer can't be made
    //
    public List<Observer> parse(JSONArray triggerArray) throws JSONException {
       
        List<Observer> observers = new ArrayList<Observer>();
        
        for(int i = 0; i < triggerArray.length(); i++)
        {
            JSONObject trigger = triggerArray.getJSONObject(i);
            
            if (!trigger.getBoolean("enabled")) {
                continue;
            }
            
            JSONObject action = trigger.getJSONObject("action");
            Observer observer = observerFactory.get(action.getString("name")).create(action.getJSONArray("arguments"));
            
            observer = new CooldownDecorator(trigger.getInt("cooldown")*60, observer, System.currentTimeMillis()/1000);
            
            JSONArray conditions = trigger.getJSONArray("conditions");
            
            for(int o = 0; o < conditions.length(); o++ )
            {
                JSONObject condition = conditions.getJSONObject(o);
                observer = new FilterDecorator(conditionFactory.get(condition.getString("name")).create(condition.getJSONArray("arguments")), observer);
            }
            
            observers.add(observer);
        }
        
        return observers;
    }
}

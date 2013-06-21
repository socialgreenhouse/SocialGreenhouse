/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialgreenhouse.server;

import com.socialgreenhouse.State;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.api.TweetsResources;
/**
 *
 * @author justin
 */
public class Tweet implements Observer
{
    private String message;
    private TweetsResources twitter;
    

    public Tweet(String message, TweetsResources twitter)
    {
        this.message = message;
        this.twitter = twitter;
    }

    public void update(State record)
    {
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Calendar cal = Calendar.getInstance();
            twitter.updateStatus(message.replace("{?}", record.getValue()) + " (" + dateFormat.format(cal.getTime()) + ")");
        } catch (TwitterException ex) {
            System.out.println(ex.toString());
            System.out.println(ex.getCause());
        }
    }
}

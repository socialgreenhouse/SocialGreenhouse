
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialgreenhouse.server;
import com.socialgreenhouse.State;


/**
 *
 * @author Social Greenhouse
 */
public class CooldownDecorator implements Observer
{
    private int seconds;
    private Observer delegate;
    private long lastUpdate;

    public CooldownDecorator(int seconds, Observer delegate, long lastUpdate)
    {
        this.seconds = seconds;
        this.delegate = delegate;
        this.lastUpdate = lastUpdate;
    }

    public void update(State record)
    {
        long time = record.getTime();        
        if ((time - lastUpdate) >= seconds) {
            delegate.update(record);
            lastUpdate = time;
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialgreenhouse.server;
import com.socialgreenhouse.State;

/**
 *
 * @author justin
 */
public class FilterDecorator implements Observer
{
    private Condition condition;
    private Observer delegate;

    public FilterDecorator(Condition condition, Observer delegate)
    {
        this.condition = condition;
        this.delegate = delegate;
    }

    public void update(State record)
    {
        if (condition.isMetBy(record))
        {
            delegate.update(record);
        }
    }
}

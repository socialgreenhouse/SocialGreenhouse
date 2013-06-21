/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialgreenhouse.server.condition;
import com.socialgreenhouse.State;
import com.socialgreenhouse.server.Condition;

/**
 *
 * @author justin
 */
public class NotCondition implements Condition
{
    private Condition delegate;

    public NotCondition(Condition delegate)
    {
        this.delegate = delegate;
    }

    public boolean isMetBy(State record)
    {
        return ! delegate.isMetBy(record);
    }
}

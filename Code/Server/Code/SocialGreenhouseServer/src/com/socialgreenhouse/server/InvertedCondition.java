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
public class InvertedCondition implements Condition
{
    private Condition delegate;

    public InvertedCondition(Condition delegate)
    {
        this.delegate = delegate;
    }

    public boolean isMetBy(State record)
    {
        return ! delegate.isMetBy(record);
    }
}

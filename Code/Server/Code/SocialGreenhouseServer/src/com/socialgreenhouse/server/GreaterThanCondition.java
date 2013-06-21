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
public class GreaterThanCondition implements Condition
{
    private int threshold;

    public GreaterThanCondition(int threshold)
    {
        this.threshold = threshold;
    }

    public boolean isMetBy(State record)
    {
        return Math.round(Float.parseFloat(record.getValue())) > threshold;
    }
}

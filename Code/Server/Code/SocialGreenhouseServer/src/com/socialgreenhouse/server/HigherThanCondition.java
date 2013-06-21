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
public class HigherThanCondition implements Condition
{
    private int threshold;

    public HigherThanCondition(int threshold)
    {
        this.threshold = threshold;
    }

    public boolean isMetBy(State record)
    {
        return Math.round(Float.parseFloat(record.getValue())) > threshold;
    }
}

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
public class LessThanCondition implements Condition
{
    private double threshold;

    public LessThanCondition(double threshold)
    {
        this.threshold = threshold;
    }

    public boolean isMetBy(State record)
    {
        return Float.parseFloat(record.getValue()) < threshold;
    }
}

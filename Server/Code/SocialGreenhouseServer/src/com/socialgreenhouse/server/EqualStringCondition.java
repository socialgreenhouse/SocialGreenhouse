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
public class EqualStringCondition implements Condition
{
    private String string;

    public EqualStringCondition(String string)
    {
        this.string = string;
    }

    public boolean isMetBy(State record)
    {
        return record.getValue().equals(string);
    }
}

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
public interface Condition
{
    public boolean isMetBy(State record);
}

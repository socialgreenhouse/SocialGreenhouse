/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialgreenhouse.server;

/**
 *
 * @author justin
 */

public class Attribute 
{
    private String key;
    private String value;
    
    public Attribute(String key, String value)
    {
        this.key = key;
        this.value = value;
    }
    
    public String getKey()
    {
        return key;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public String toString()
    {
        return key + "=" + value;
    }
}


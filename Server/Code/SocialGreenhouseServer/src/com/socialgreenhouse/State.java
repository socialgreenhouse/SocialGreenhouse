package com.socialgreenhouse;

import java.io.Serializable;

public class State implements Serializable {
    
    private static final long serialVersionUID = 123016087493617698L;
    
    private String moduleId;
    private long time;
    private String value;

    public State(String moduleId, long time, String value) {
	this.moduleId = moduleId;
	this.time = time;
	this.value = value;
    }
    
    public String getModuleId() {
	return moduleId;
    }

    public long getTime() {
	return time;
    }

    public String getValue() {
	return value;
    }
}

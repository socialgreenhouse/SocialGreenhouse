/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialgreenhouse.server.command;

import com.socialgreenhouse.server.Server;
import com.socialgreenhouse.State;
import com.socialgreenhouse.server.Observer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author justin
 */
public class CommandHandler {

    private LoginService loginService;
    private SaveValueService saveValueService;
    private ModuleService moduleService;
    private boolean logged;
    private boolean debugMode;

    public CommandHandler(LoginService loginService, SaveValueService saveValueService, ModuleService moduleService, boolean debugMode) {
        this.loginService = loginService;
        this.saveValueService = saveValueService;
        this.moduleService = moduleService;
        this.debugMode = debugMode;
    }

    /**
     * Saves data string with values (ModuleID|value) in database
     * Or returns String with all modules 
     * @param command
     * @param serverInstance
     * @return succes or failed / list with all modules
     */
    public String handle(String command, Server serverInstance) {

        //Save Values to the database
        if (command.contains("#save")) {
            //ModuleID, value
            String data[] = command.split("\\|");
		
	    boolean saved = false;
            for (int i = 1; i < data.length; i++) {
		//System.out.println(data[i]);
                String sensor[] = data[i].split(",");
                if (debugMode) {
                    System.out.println("Saving value | " + sensor[0] + " | " + sensor[1] + " | ");
                }

                saved = saveValueService.save(sensor[0], sensor[1]);
                serverInstance.updateTriggers(new State(sensor[0], System.currentTimeMillis()/1000, sensor[1]));
            }

            if (saved) {
                //System.out.println("save attempt succes");
                return "Succes";
            } else {
                //System.out.println("save attempt failed");
                return "Failed";
            }
        }
        
        if (command.contains("#getmodules")) {
            return moduleService.getAllModulesToSQL();
        }
        
        /*if (command.contains("#savemodules")) {
            
        }
        
        if (command.contains("#addmodules")) {
            
        }*/

        return "No valid command given";
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socialgreenhouse.server;

import com.socialgreenhouse.State;
import com.socialgreenhouse.server.command.CommandHandler;
import com.socialgreenhouse.server.command.LoginService;
import com.socialgreenhouse.server.command.ModuleService;
import com.socialgreenhouse.server.command.SaveValueService;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.net.NetServer;
import org.vertx.java.core.net.NetSocket;
import org.vertx.java.deploy.Verticle;
import twitter4j.TwitterFactory;
import twitter4j.api.TweetsResources;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author INF1C
 */
public class Server extends Verticle {

    private static final String URL = "jdbc:mysql://it-server.nl:3306/socialgreenhouse";
    private static final String USER = "loginserver";
    private static final String PASSWORD = "social";
    private boolean debugMode = false;
    private Map<String, List<Observer>> triggers;
    private TweetsResources twitter;
    private String twitterCheckCurrent;
    private String twitterCheckOld = "";
    private Server serverInstance;
    private Modules modules;
    private TwitterFactory tf;
    private ConfigurationBuilder cb;
    private DatabaseConnection connection;
    

    public void start() {

        serverInstance = this;
        //MySql Connection
        triggers = new HashMap<>();

        connection = null;
        try {
            connection = new DatabaseConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }

       
        /*
         * Create the Twitter Instance
         */

        //;

        twitter = updateTwitterData(connection);
        twitterCheckOld = twitterCheckCurrent;

        /*
         * Load the modules form the server
         */
        modules = new Modules(connection, twitter);
        triggers = modules.loadTriggers();

        /*
         * Start command handlers for the server applicatie
         */
        if (connection != null) {
            LoginService loginService = new LoginService(connection);
            SaveValueService saveValueService = new SaveValueService(connection);
            ModuleService moduleService = new ModuleService(connection);
            final CommandHandler commandHandler = new CommandHandler(loginService, saveValueService, moduleService, debugMode);


            /*
             * Start the vertx tcp server on port 9999
             */
            final StringBuffer command = new StringBuffer();
            NetServer server = vertx.createNetServer();
            server.connectHandler(new Handler<NetSocket>() {
                @Override
                public void handle(final NetSocket socket) {
                    socket.dataHandler(new Handler<Buffer>() {
                        @Override
                        public void handle(Buffer buffer) {
                            //socket.write(buffer);
                            String message = buffer.toString("UTF-8");
                            if (message.contains("$")) {
                                command.append(message);
                                
                                /*
                                 * Check for updates
                                 */
                                
                                triggers = modules.refreshTriggers(triggers);
                                System.out.println("Modules loaded : " + triggers.size());
                                
                                twitter = updateTwitterData(connection);
                                if(!twitterCheckCurrent.equals(twitterCheckOld))
                                {
                                    System.out.println("New twitter data loaded");
                                                
                                    modules = new Modules(connection, twitter);
                                    triggers = modules.loadTriggers();
                                    
                                    twitterCheckOld = twitterCheckCurrent;
                                }
                                
                                
                                String rawCommand = command.toString();
                                String line = rawCommand.substring(rawCommand.indexOf('#'), rawCommand.lastIndexOf('$'));
                                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                                Calendar cal = Calendar.getInstance();
                                System.out.print("[" + dateFormat.format(cal.getTime()) + "] " + line);
                                String left = rawCommand.substring(rawCommand.lastIndexOf('$') + 1);

                                String sendBack = commandHandler.handle(line, serverInstance);
                                System.out.println(" - " + sendBack);
                                
                                Buffer buff = new Buffer(sendBack);
                                socket.write(buff);
                                
                                command.delete(0, command.length());
                                command.append(left);
                            } else {
                                command.append(message);
                            }
                        }
                    });
                }
            }).listen(9999);
        }
    }

    /*
     * This method updates the latest values into the triggers
     */
    public void updateTriggers(State state) {
        if(triggers.get(state.getModuleId()) != null)
        {
            for (Observer observer : triggers.get(state.getModuleId())) {
                observer.update(state);
            }
        }
    }
    
    /*
     * This method makes it possible to update to a new twitter instance, if key and secret have changed
     */
    public TweetsResources updateTwitterData(DatabaseConnection connection)
    {
        //Load Twitter Settings From Database.
        TwitterData data = new TwitterData(connection); 
        if(data.getAccessToken().length() > 2 && data.getAccessTokenSecret().length() > 2 && data.getConsumerKey().length() > 2 && data.getConsumerSecret().length() > 2)
        {
            cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(data.getConsumerKey())
                    .setOAuthConsumerSecret(data.getConsumerSecret())
                    .setOAuthAccessToken(data.getAccessToken())
                    .setOAuthAccessTokenSecret(data.getAccessTokenSecret());

            tf = new TwitterFactory(cb.build());
            twitterCheckCurrent = data.getAccessToken();
            return tf.getInstance();
        }
        else
        {
            twitterCheckCurrent = "";
            return new MockTweetsResources();
        }
    }
}

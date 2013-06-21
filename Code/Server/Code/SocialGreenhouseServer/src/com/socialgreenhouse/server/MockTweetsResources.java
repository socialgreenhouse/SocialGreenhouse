/**
 * Deze klasse wordt gebruikt indien er geen twitteraccount gevonden is.
 * @author INF2C
 */
package com.socialgreenhouse.server;

import twitter4j.OEmbed;
import twitter4j.OEmbedRequest;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.api.TweetsResources;

public class MockTweetsResources implements TweetsResources {

    @Override
    public ResponseList<Status> getRetweets(long l) throws TwitterException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Status showStatus(long l) throws TwitterException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Status destroyStatus(long l) throws TwitterException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Status updateStatus(String string) throws TwitterException {
        return null;
    }

    @Override
    public Status updateStatus(StatusUpdate su) throws TwitterException {
        return null;
    }

    @Override
    public Status retweetStatus(long l) throws TwitterException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public OEmbed getOEmbed(OEmbedRequest oer) throws TwitterException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

package com.example.parstagram;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("FollowerTable")
public class FollowerTableObject extends ParseObject {
    public static final String KEY_FOLLOWER = "follower";
    public static final String KEY_FOLLOWED = "followed";

    public ParseUser getFollower() {
        return getParseUser(KEY_FOLLOWER);
    }
    public ParseUser getFollowed() {
        return getParseUser(KEY_FOLLOWED);
    }

    public void setFollower(ParseUser user) {put(KEY_FOLLOWER, user);}
    public void setFollowed(ParseUser user) {put(KEY_FOLLOWED, user);}
}

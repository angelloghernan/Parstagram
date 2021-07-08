package com.example.parstagram.wrappers;

import org.parceler.Parcel;

// Basic wrapper class for user info so that it can be parceled and passed around through intents.
@Parcel
public class UserWrapper {
    public String objectId;
    public String username;
    public String name;
    public String profilePictureUrl;
    public String bio;


    // empty constructor for parceler
    public UserWrapper() { }

    public UserWrapper(String objectId, String username, String profilePictureUrl) {
        this.objectId = objectId;
        this.username = username;
        this.name = username;
        this.profilePictureUrl = profilePictureUrl;
        this.bio = "";
    }
}

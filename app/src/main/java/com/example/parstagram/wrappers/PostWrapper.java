package com.example.parstagram.wrappers;

import org.parceler.Parcel;

import java.util.Date;

// Basic wrapper class for Post object info so it can be passed around through intents
@Parcel
public class PostWrapper {
    public String description;
    public String objectId;
    public String imageUrl;
    public String userId;
    public String createdAt;

    // empty constructor for parceler
    public PostWrapper() { }

    public PostWrapper(String description, String objectId, String imageUrl, String userId, String createdAt) {
        this.description = description;
        this.objectId = objectId;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.createdAt = createdAt;
    }
}

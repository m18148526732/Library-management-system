package main.java.com.library.server.entity.impl;

import main.java.com.library.server.entity.Entity;

public class Favorite implements Entity {
    private final String favoriteID;
    private final String userID;
    private final String bookID;


    public Favorite(String favoriteID, String userID, String bookID) {
        this.favoriteID = favoriteID;
        this.userID = userID;
        this.bookID = bookID;
    }

    public String getUserID() {
        return userID;
    }

    public String getBookID() {
        return bookID;
    }

    @Override
    public String getId() {
        return favoriteID;
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "favoriteID='" + favoriteID + '\'' +
                ", userID='" + userID + '\'' +
                ", bookID='" + bookID + '\'' +
                '}';
    }
}
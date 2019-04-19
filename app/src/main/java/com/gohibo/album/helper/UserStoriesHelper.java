package com.gohibo.album.helper;

import java.net.URI;

/**
 * Created by yuvi on 22-01-2018.
 */

public class UserStoriesHelper {
    String cardName;
    URI imageResourceId;
    int isturned;

    public int getIsturned() {
        return isturned;
    }
    public void setIsturned(int isturned) {
        this.isturned = isturned;
    }
    public String getCardName() {
        return cardName;
    }
    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
    public URI getImageResourceId() {
        return imageResourceId;
    }
    public void setImageResourceId(URI imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}

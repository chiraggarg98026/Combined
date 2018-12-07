package com.gui.guiprogramming.cbc.cbc_model;

import java.io.Serializable;

/**
 * <p>CBCNewsStory is a model class implements a Serializable interface
 * to be passed with intent as serializableExtra.
 * It holds all the info for each news article.
 * used to fetch and display titles to listview and article to detail screen
 * It also works as communicator between CBCDBManager and rest of classes</p>
 * */
public class CBCNewsStory implements Serializable {
    private String title, link, pubDate, author, category, description;

    public CBCNewsStory() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package com.example.littlerockplanet.spacesystem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ArticleObject {
    public String headline, description, link, datePublished;

    public ArticleObject (String headline, String description, String link, String datePublished){
        this.headline = headline;
        this.description = description;
        this.link = link;
        this.datePublished = datePublished;

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date properDate = format.parse(this.datePublished);
            DateFormat simpleDateFormat = new SimpleDateFormat("E, MMM dd yyyy");
            this.datePublished = simpleDateFormat.format(properDate);
        }catch (Exception e){}
    }
}
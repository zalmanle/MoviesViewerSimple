package com.example.user.moviesviewersimple.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.UUID;

/**
 * Created by User on 09/02/2016.
 */
public class Movie implements Parcelable {
    //region Constants
    static final int MIN_MOVIE_RATE = 1;

    static final int MAX_MOVIE_RATE = 10;

    public static final int DEFAULT_MOVIE_RATE = 5;
    //endregion

    //region Instance variables
    private int id;

    private String subject;

    private String body;

    private String imageUrl;

    private boolean isWatched;

    private int rate;

    private String year;
    //endregion

    //region Constructors
    /**
     * Constructor
     *
     * @param subject title of movie (String)
     * @param body description of movie (String
     * @param url - url to movie poster in internet
     * @param isWatched
     * @param rate  1-10 in another case set DEFAULT_MOVIE_RATE = 5
     * @param year  - movie creation year
     */
    public Movie(String subject,String body,String url,boolean isWatched,int rate,String year){


        this.id = generateUniqueID();
        setSubject(subject);
        setBody(body);
        setImageUrl(url);
        setWatched(isWatched);
        setRate(rate);
        setYear(year);
    }

    /**
     * Constructor
     *
     * @param id  identifier of movie
     * @param subject title of movie (String)
     * @param body description of movie (String
     * @param url - url to movie poster in internet or path to local storage image
     * @param isWatched
     * @param rate  1-10 in another case set DEFAULT_MOVIE_RATE = 5
     * @param year - movie creation year
     */
    public Movie(int id,String subject,String body,String url,boolean isWatched,int rate,String year){


        setSubject(subject);
        setBody(body);
        setImageUrl(url);
        setWatched(isWatched);
        setRate(rate);
        setId(id);
        setYear(year);

    }

    //endregion

    //region Getters&Setters

    //region id getter&setter
    /**
     * This function return id of movie object
     * @return integer
     */
    public int getId(){
        return this.id;
    }

    /**
     * This function set movie object id
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * This function get title of movie
     * @return
     */
    //endregion

    //region subject getter&setter
    /**
     * This function get subject of movie
     * @return
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * This function set movie subject
     * @param subject
     */
    public void setSubject(String subject){


        if(!TextUtils.isEmpty(subject)) {

            this.subject = subject;
        }

    }
    //endregion

    //region body getter&setter
    /**
     * This function return description of movie
     * @return
     */
    public String getBody() {
        return this.body;
    }

    /**
     * This function set movie description
     * @param body
     */
    public void setBody(String body){
        if(body != null){
            this.body = body;
        }
    }
    //endregion

    //region imageUrl getter&setter
    /**
     * This function return url of movie
     * @return
     */
    public String getImageUrl() {
        return this.imageUrl;
    }

    /**
     * Set poster url to movie object
     * @param url
     */
    public void setImageUrl(String url){
        this.imageUrl = url;
    }
    //endregion

    //region isWatched getter&setter
    /**
     * This function return if movie is watched
     * @return
     */
    public boolean isWatched() {
        return this.isWatched;
    }

    /**
     * This function set if movie is watched
     * @param isWatched
     */
    public void setWatched(boolean isWatched){
        this.isWatched = isWatched;
    }
    //endregion

    //region rate getter&setter
    /**
     * This function return movies rate
     * @return
     */
    public int getRate() {
        return this.rate;
    }

    /**
     * This function set movies rate
     * from one to ten
     * @param rate
     */
    public void setRate(int rate) {

        if(rate < MIN_MOVIE_RATE || rate > MAX_MOVIE_RATE) {
            this.rate = DEFAULT_MOVIE_RATE;
            return;
        }
        this.rate = rate;
    }
    //endregion
    //region year getters&setters
    /**
     * This function get year of movie
     * @return String
     */
    public String getYear(){
        return year;
    }

    /**
     * This function set year of movie
     * @param year
     */
    public void setYear(String year){
        this.year = year;
    }
    //endregion

    //region OVERRIDE FUNCTION
    @Override
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("{ subject : ");
        buffer.append(this.getSubject());
        buffer.append(",\n");
        buffer.append("     body : ");
        buffer.append(this.getBody());
        buffer.append(",\n");
        buffer.append("     year : ");
        buffer.append(this.getYear());
        buffer.append(" }");
        buffer.append(",\n");
        return buffer.toString();
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Movie)){
            return false;
        }
        Movie movie = (Movie)o;
        return this.toString().equals(movie.toString());
    }
    //endregion
    //endregion
    //region Service Function
    /**
     * This function generate unique identifier
     */
    private static int generateUniqueID() {

        String idStr = UUID.randomUUID().toString();
        int id = idStr.hashCode();
        id = Math.abs(id);
        return id;
    }
    //endregion
    public static class Constants{
        //region Storage fields
        public static final String ID = "id";

        public static final String SUBJECT = "subject";

        public static final String BODY = "body";

        public static final String IMAGE_URL = "image_url";

        public static final String WATCHED = "watched";

        public static final String RATE = "rate";

        public static final String YEAR = "year";
        //endregion

        //region Error messages
        public static final String EMPTY_SUBJECT_MESSAGE = "empty subject string";
        //endregion
    }

    //endregion

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.subject);
        dest.writeString(this.body);
        dest.writeString(this.imageUrl);
        dest.writeByte(isWatched ? (byte) 1 : (byte) 0);
        dest.writeInt(this.rate);
        dest.writeString(this.year);
    }

    private Movie(Parcel in) {
        this.id = in.readInt();
        this.subject = in.readString();
        this.body = in.readString();
        this.imageUrl = in.readString();
        this.isWatched = in.readByte() != 0;
        this.rate = in.readInt();
        this.year = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}

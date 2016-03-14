package com.example.user.moviesviewersimple.db;

/**
 * Created by User on 09/02/2016.
 */
public class MoviesDBConstants {
    //region TABLE NAME
    static final String TABLE_NAME = "movies";
    //endregion
    //region TABLE COLUMNS NAMES
    static final String ID = "id";

    static final String SUBJECT = "subject";

    static final String BODY = "body";

    static final String IMAGE_URL = "image_url";

    static final String WATCHED = "watched";

    static final String RATE = "rate";

    static final String DATE = "date";
    //endregion
    //region DATA BASE CODES
    static final long INSERT_FAILED = -1;

    static final int UPDATE_SUCCESS_CODE = 1;

    static final int DELETE_SUCCESS_CODE = 1;
    //endregion

}

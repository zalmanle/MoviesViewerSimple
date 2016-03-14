package com.example.user.moviesviewersimple.tasks;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.user.moviesviewersimple.data.Movie;
import com.example.user.moviesviewersimple.db.MoviesTableHandler;
import com.example.user.moviesviewersimple.utilities.Constants;

import java.util.List;

/**
 * Created by User on 22/02/2016.
 */
public class MoviesLoader extends AsyncTaskLoader<List<Movie>>{

    //region Constants
    private static final int ALL_MOVIES_CODE = 101;

    public static final int MOVIES_BY_YEAR_CODE = 102;

    public static final int MOVIES_BY_SUBJECT_CODE = 103;

    public static final int MOVIES_BY_SUBJECT_FRAGMENT_CODE = 104;
    //endregion
    //region Instance Variables
    private MoviesTableHandler handler;

    private int flag;

    private static String argument;
    //endregion

    /**
     * This constructor to load all movies
     * @param context
     */
    public MoviesLoader(Context context) {
        super(context);
        handler = new MoviesTableHandler(context);
        this.flag = ALL_MOVIES_CODE;
        this.argument = Constants.EMPTY_STRING;
    }
    public MoviesLoader(Context context,int flag) {
        super(context);
        handler = new MoviesTableHandler(context);
        this.flag = flag;
        this.argument = Constants.EMPTY_STRING;
    }

    @Override
    public List<Movie> loadInBackground() {
        return getMovies();
    }

    private List<Movie>getMovies(){
        List<Movie>list = null;
        switch (flag){
            case ALL_MOVIES_CODE:
                list = handler.getAllMovies();
                break;
            case MOVIES_BY_SUBJECT_CODE:
                if(isValidArgument(argument)){
                    list = handler.getMovieBySubject(argument);
                }
                break;
            case MOVIES_BY_YEAR_CODE:
                if(isValidArgument(argument)){
                    list = handler.getMovieByYear(argument);
                }
                break;
            case MOVIES_BY_SUBJECT_FRAGMENT_CODE:
                if(isValidArgument(argument)){
                    list = handler.getMovieBySubjectFragment(argument);
                }
                break;
            default:
                list = handler.getAllMovies();
                break;
        }
        return list;
    }

    public static void setArgument(String arg){
        argument = arg;
    }

    private boolean isValidArgument(String argument){
        if(argument == null){
            return false;
        }
        else if(argument.equals(Constants.EMPTY_STRING)){
            return false;
        }
        else {
            return true;
        }
    }
}

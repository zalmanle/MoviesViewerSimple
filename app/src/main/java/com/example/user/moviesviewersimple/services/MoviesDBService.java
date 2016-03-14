package com.example.user.moviesviewersimple.services;

import android.app.IntentService;
import android.content.Intent;

import com.example.user.moviesviewersimple.data.Movie;
import com.example.user.moviesviewersimple.db.MoviesTableHandler;

import java.util.List;

/**
 * Created by User on 02/03/2016.
 */
public class MoviesDBService extends IntentService {

    //region Instance variables
    private MoviesTableHandler handler;

    //endregion
    public MoviesDBService() {
        super(MoviesDBService.class.getName());
        handler = new MoviesTableHandler(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if (action.equals(Action.EDIT_ACTION)) {
            executeEditAction(intent);
        } else if (action.equals(Action.ADD_ACTION)) {
            executeAddAction(intent);
        } else if (action.equals(Action.DELETE_ACTION)) {
            executeDeleteAction(intent);
        }
    }

    private void executeDeleteAction(Intent intent) {

        int mode = intent.getIntExtra(Extras.Keys.EXTRA_DELETE_KEY, Extras.Values.NO_VALUE);

        if (mode == Extras.Values.EXTRA_ALL_DELETE_VALUE) {
            handler.deleteAllMovies();
        } else if (mode == Extras.Values.EXTRA_ITEM_DELETE_VALUE) {
            deleteMovie(intent);
        } else if (mode == Extras.Values.EXTRA_LIST_DELETE_VALUE) {
            deleteMoviesList(intent);
        }

    }

    private void deleteMoviesList(Intent intent) {
        List<Movie> movies = intent.getParcelableArrayListExtra(Extras.Keys.EXTRA_MOVIE_LIST_KEY);
        if (movies != null) {
            handler.deleteMoviesList(movies);
        }
    }

    private void deleteMovie(Intent intent) {
        Movie movie = intent.getParcelableExtra(Extras.Keys.EXTRA_MOVIE_KEY);
        if (movie != null) {
            handler.deleteMovie(movie);
        }
    }

    //region ADD ACTION
    private void executeAddAction(Intent intent) {
        int mode = intent.getIntExtra(Extras.Keys.EXTRA_ADD_KEY,Extras.Values.NO_VALUE);

        if (mode == Extras.Values.EXTRA_ITEM_ADD_VALUE) {
            addMovie(intent);
        } else if (mode == Extras.Values.EXTRA_LIST_ADD_VALUE){
            addMovieList(intent);
        }


    }

    private void addMovieList(Intent intent) {

        List<Movie> movies = intent.getParcelableArrayListExtra(Extras.Keys.EXTRA_MOVIE_LIST_KEY);
        if (movies != null) {
            handler.addMoviesList(movies);
        }
    }

    private void addMovie(Intent intent) {
        Movie movie = intent.getParcelableExtra(Extras.Keys.EXTRA_MOVIE_KEY);
        if (movie != null) {
            handler.addMovie(movie);
        }
    }
    //endregion

    //region EDIT ACTION
    private void executeEditAction(Intent intent) {

        Movie movie = intent.getParcelableExtra(Extras.Keys.EXTRA_MOVIE_KEY);
        if (movie != null) {
            handler.editMovie(movie);
        }
    }
    //endregion

    //region ACTIONS
    public class Action {

        public static final String ADD_ACTION = "com.example.user.moviesviewersimple.services.action.add";

        public static final String EDIT_ACTION = "com.example.user.moviesviewersimple.services.action.edit";

        public static final String DELETE_ACTION = "com.example.user.moviesviewersimple.services.action.delete";

    }
    //endregion

    //region Extras
    public class Extras {

        public class Keys {
            public static final String EXTRA_DELETE_KEY = "extra delete";

            public static final String EXTRA_ADD_KEY = "extra add";

            public static final String EXTRA_MOVIE_KEY = "extra movie";

            public static final String EXTRA_MOVIE_LIST_KEY = "extra movie list";
        }

        public class Values {
            public static final int EXTRA_ITEM_DELETE_VALUE = 101;

            public static final int EXTRA_ALL_DELETE_VALUE = 102;

            public static final int EXTRA_LIST_DELETE_VALUE = 103;

            public static final int EXTRA_ITEM_ADD_VALUE = 107;

            public static final int EXTRA_LIST_ADD_VALUE = 108;

            private static final int NO_VALUE = 0;

        }


    }
    //endregion
}

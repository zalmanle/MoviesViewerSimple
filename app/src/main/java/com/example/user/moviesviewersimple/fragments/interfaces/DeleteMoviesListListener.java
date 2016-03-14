package com.example.user.moviesviewersimple.fragments.interfaces;

import com.example.user.moviesviewersimple.data.Movie;

import java.util.List;

/**
 * Created by User on 02/03/2016.
 */
public interface DeleteMoviesListListener extends DeleteMovieListener{

    public void deleteMoviesList(List<Movie> list);
}

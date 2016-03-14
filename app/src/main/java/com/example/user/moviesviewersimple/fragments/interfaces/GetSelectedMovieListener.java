package com.example.user.moviesviewersimple.fragments.interfaces;

import com.example.user.moviesviewersimple.data.Movie;

/**
 * Created by User on 17/02/2016.
 */
public interface GetSelectedMovieListener {
    public void getSelectedMovie(Movie movie,String sourceFragmentName);
}

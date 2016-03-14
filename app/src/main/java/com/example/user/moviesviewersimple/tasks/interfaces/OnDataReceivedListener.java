package com.example.user.moviesviewersimple.tasks.interfaces;

import com.example.user.moviesviewersimple.data.Movie;

import java.util.List;

/**
 * Created by User on 09/02/2016.
 */
public interface OnDataReceivedListener {

    public void onDataReceived(List<Movie> movies);
}


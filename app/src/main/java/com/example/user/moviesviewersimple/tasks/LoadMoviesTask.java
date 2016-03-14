package com.example.user.moviesviewersimple.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.user.moviesviewersimple.data.Movie;
import com.example.user.moviesviewersimple.networking.MoviesLoader;
import com.example.user.moviesviewersimple.tasks.interfaces.OnDataReceivedListener;
import com.example.user.moviesviewersimple.utilities.Constants;

import java.util.List;

/**
 * Created by User on 09/02/2016.
 */
public class LoadMoviesTask extends AsyncTask<String,Integer,List<Movie>> {


    //region CONSTANTS
    private static final String TAG = "LoadMovies";
    //endregion

    //region INSTANCE VARIABLES
    private ProgressBar progressBar;

    private int counter;

    private MoviesLoader loader;

    private List<Movie>list;

    private OnDataReceivedListener listener;
    //endregion

    //region CONSTRUCTOR
    public LoadMoviesTask(ProgressBar progressBar,OnDataReceivedListener listener){
        this.progressBar = progressBar;
        this.listener = listener;
    }
    //endregion


    @Override
    protected List<Movie> doInBackground(String... params) {

        //check if parameter exist
        if(params == null) {
            return null;
        }
        String searchStr = params[0];
        try {
            loader = new MoviesLoader(searchStr);
            publishProgress(counter++);
            list = loader.getMoviesList();
        }
        catch (Exception e){
            Log.d(TAG, Constants.LOADING_ERROR_MESSAGE, e);
        }

        return list;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {

        //hide progress bar
        if(progressBar!= null){
            progressBar.setVisibility(View.GONE);
        }
        //set data
        if(listener != null){
            listener.onDataReceived(movies);
        }

    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        //set progress
        if(progressBar != null){
            progressBar.setProgress(values[0]);
        }
    }

    @Override
    protected void onPreExecute() {

        //check if progress bar exist
        if(progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(25);
        }
        //initialize counter
        counter = 0;
    }
}


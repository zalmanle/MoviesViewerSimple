package com.example.user.moviesviewersimple.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.moviesviewersimple.R;
import com.example.user.moviesviewersimple.data.Movie;
import com.example.user.moviesviewersimple.data.MoviesAdapter;
import com.example.user.moviesviewersimple.fragments.interfaces.GetSelectedMovieListener;
import com.example.user.moviesviewersimple.fragments.interfaces.GoToFragmentListener;
import com.example.user.moviesviewersimple.tasks.LoadMoviesTask;
import com.example.user.moviesviewersimple.tasks.interfaces.OnDataReceivedListener;
import com.example.user.moviesviewersimple.utilities.Constants;
import com.example.user.moviesviewersimple.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 16/02/2016.
 */
public class WebSearchFragment extends Fragment implements OnDataReceivedListener{

    //region Constants
    public static final String FRAGMENT_NAME = WebSearchFragment.class.getSimpleName();

    private static final String TAG = "TEST";
    private static final int ID_BACK = 1;
    //endregion
    //region INSTANCE VARIABLES
    private EditText searchEditText;

    private ListView resultsListView;

    private ProgressBar loadMoviesBar;

    private MoviesAdapter adapter;

    private List<Movie> resultsList;

    private String searchString;

    private Movie selectedMovie;

    private InputMethodManager imm;

    private GetSelectedMovieListener getSelectedMovieListener;

    private GoToFragmentListener goToFragmentListener;

    //endregion

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setGoToFragmentListener(context);
        setGetSelectedMovieListener(context);


    }

    private void setGoToFragmentListener(Context context) {
        try {
            if(context instanceof GoToFragmentListener){
                goToFragmentListener = (GoToFragmentListener)context;
            }
            else {
                throw new ClassCastException();
            }
        }
        catch (ClassCastException e){
            Log.d(TAG, context.getString(R.string.implement_go_to_fragment_listener_interface_message));
        }
    }

    private void setGetSelectedMovieListener(Context context) {
        try {
            if(context instanceof GetSelectedMovieListener){
                getSelectedMovieListener = (GetSelectedMovieListener)context;
            }
            else {
                throw new ClassCastException();
            }
        }
        catch (ClassCastException e){
            Log.d(TAG,context.getString(R.string.implement_get_selected_movie_listener));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.web_search_fragment,container,false);
        setHasOptionsMenu(true);
        setUIElements(view);
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        MenuItem exitItem =
                menu.add(Menu.NONE, ID_BACK, Menu.NONE, getString(R.string.home_menu_item_text));
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case ID_BACK:
                goToFragmentListener.goToFragment(MoviesListFragment.FRAGMENT_NAME);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //region SET UI ELEMENTS
    private void setUIElements(View view){

        //set load movies progress bar
        loadMoviesBar = (ProgressBar) view.findViewById(R.id.load_movies_progress_bar);
        //set search text edit text
        setSearchEditText(view);
        //set search results list
        setResultsListView(view);

    }

    //region SET RESULTS LIST VIEW
    private void setResultsListView(View view){
        resultsListView = (ListView)view.findViewById(R.id.search_results_list);
        resultsList = new ArrayList<Movie>();
        adapter = new MoviesAdapter(getActivity(),R.layout.movies_list_item,resultsList);
        resultsListView.setAdapter(adapter);
        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMovie = resultsList.get(position);
                getSelectedMovieListener.getSelectedMovie(selectedMovie,FRAGMENT_NAME);
            }
        });
    }

    //endregion
    //region SET SEARCH STRING EDIT TEXT
    private void setSearchEditText(View view){
        searchEditText = (EditText) view.findViewById(R.id.search_movies_edit_text);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
                } else {

                    imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                }
            }
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchString = searchEditText.getText().toString();
                    if (searchString.equals(Constants.EMPTY_STRING)) {
                        //Toast.makeText(SearchMoviesActivity.this, R.string.empty_search_string_message, Toast.LENGTH_LONG).show();
                        Utilities.makeImageToast(getActivity(), R.drawable.ic_error_24dp, R.string.empty_search_string_message, Toast.LENGTH_LONG).show();
                        return true;
                    }
                    Utilities.hide_keyboard(getActivity());
                    new LoadMoviesTask(loadMoviesBar, WebSearchFragment.this).execute(searchString);


                    return true;
                }
                return false;
            }
        });
    }
    //endregion

    @Override
    public void onDataReceived(List<Movie> movies) {

        if((resultsList != null)&&(movies != null)){
            if(movies.size() > Constants.MOVIES_EMPTY_NUMBER){

                updateResultsList(movies);

            }
            else {
                //Toast.makeText(this,R.string.not_movie_found_message,Toast.LENGTH_LONG).show();
                Utilities.makeImageToast(getActivity(),R.drawable.ic_error_24dp,R.string.movie_not_found_message,Toast.LENGTH_LONG).show();
            }
        }
        else {
            // Toast.makeText(this,R.string.not_movie_found_message,Toast.LENGTH_LONG).show();
            Utilities.makeImageToast(getActivity(),R.drawable.ic_error_24dp,R.string.movie_not_found_message,Toast.LENGTH_LONG).show();
        }
    }

    private void updateResultsList(List<Movie> movies) {
        resultsList.clear();
        resultsList.addAll(movies);
        Utilities.setMoviesOrder(resultsList,getActivity());
        adapter.notifyDataSetChanged();
    }

    //endregion
}

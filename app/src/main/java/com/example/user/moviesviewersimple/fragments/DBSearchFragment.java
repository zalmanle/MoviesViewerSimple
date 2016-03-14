package com.example.user.moviesviewersimple.fragments;

import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.moviesviewersimple.R;
import com.example.user.moviesviewersimple.data.Movie;
import com.example.user.moviesviewersimple.data.MoviesAdapter;
import com.example.user.moviesviewersimple.db.MoviesTableHandler;
import com.example.user.moviesviewersimple.fragments.interfaces.AddMoviesListListener;
import com.example.user.moviesviewersimple.fragments.interfaces.DeleteMoviesListListener;
import com.example.user.moviesviewersimple.fragments.interfaces.GetSelectedMovieListener;
import com.example.user.moviesviewersimple.fragments.interfaces.GoToFragmentListener;
import com.example.user.moviesviewersimple.fragments.interfaces.ShareImageListener;
import com.example.user.moviesviewersimple.tasks.MoviesLoader;
import com.example.user.moviesviewersimple.utilities.Constants;
import com.example.user.moviesviewersimple.utilities.Utilities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by User on 17/02/2016.
 */
public class DBSearchFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Movie>> {

    //region Constants
    public static final String FRAGMENT_NAME = DBSearchFragment.class.getSimpleName();

    private static final int SUBJECT_SEARCH_OPTION = 0;

    private static final int YEAR_SEARCH_OPTION = 1;

    private static final int SUBJECT_FRAGMENT_SEARCH_OPTION = 2;

    private static final int SUBJECT_LOADER_ID = 101;

    private static final int YEAR_LOADER_ID = 102;

    private static final int SUBJECT_FRAGMENT_LOADER_ID = 103;

    private static final String DB_SEARCH_STRING_KEY = "db_search_string_key";

    private static final int ID_MENU_REMOVE_ALL = 1;

    private static final int ID_BACK = 2;

    private static final int ID_MENU_RETURN = 3;

    private static final String TAG = "TEST";
    //endregion

    //region Instance Variables
    private Spinner dbOptionsSpinner;

    private ListView dbResultList;

    private EditText dbSearchEditText;

    private InputMethodManager imm;

    private String searchString;

    private int currentSearchOption;

    private List<Movie> results;

    private List<Movie>garbageMoviesList;

    private MoviesAdapter adapter;

    private Movie selectedMovie;

    private MoviesTableHandler handler;

    private GoToFragmentListener goToFragmentListener;

    private ShareImageListener shareImageListener;

    private GetSelectedMovieListener getSelectedMovieListener;

    private AddMoviesListListener addMoviesListListener;

    private DeleteMoviesListListener deleteMoviesListListener;

    //endregion

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setGoToFragmentListener(context);
        setGetSelectedMovieListener(context);
        setShareImageListener(context);
        setAddMoviesListListener(context);
        setDeleteMoviesListListener(context);

    }

    private void setDeleteMoviesListListener(Context context) {
        try {
            if(context instanceof DeleteMoviesListListener){
                deleteMoviesListListener = (DeleteMoviesListListener)context;
            }
        }
        catch (ClassCastException e){
            Log.d(TAG, context.getString(R.string.implement_delete_movies_list_listener_message));
        }
    }

    private void setAddMoviesListListener(Context context) {
        try {
            if(context instanceof AddMoviesListListener){
                addMoviesListListener = (AddMoviesListListener)context;
            }
            else {
                throw new ClassCastException();
            }
        }
        catch (ClassCastException e){
            Log.d(TAG,getString(R.string.implement_add_movies_list_listener_message));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getLoaderManager().initLoader(SUBJECT_LOADER_ID, null, this);
        getActivity().getLoaderManager().initLoader(YEAR_LOADER_ID, null, this);
        getActivity().getLoaderManager().initLoader(SUBJECT_FRAGMENT_LOADER_ID, null, this);
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

    private void setShareImageListener(Context context) {
        try {
            if(context instanceof ShareImageListener) {

                shareImageListener = (ShareImageListener)context;
            }
            else{
                throw new ClassCastException();
            }
        }
        catch (ClassCastException e){
            Log.d(TAG, context.getString(R.string.implement_share_image_listener_message));
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
        View view = inflater.inflate(R.layout.db_search_fragment, container, false);
        handler = new MoviesTableHandler(getActivity());
        setHasOptionsMenu(true);
        //initialize garb age movie list
        garbageMoviesList = new ArrayList<Movie>();
        initUIElements(view);
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem removeItem =
                menu.add(Menu.NONE, ID_MENU_REMOVE_ALL, Menu.NONE, getString(R.string.remove_all_item_text));
        MenuItem returnItem =
                menu.add(Menu.NONE, ID_MENU_RETURN, Menu.NONE, getString(R.string.return_movies_menu_item_text));
        MenuItem exitItem =
                menu.add(Menu.NONE, ID_BACK, Menu.NONE,getString(R.string.back_menu_item_text1));
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case ID_MENU_REMOVE_ALL:
                startRemoveMovies();
                break;
            case ID_BACK:
                goToFragmentListener.goToFragment(MoviesListFragment.FRAGMENT_NAME);
                break;
            case ID_MENU_RETURN:
                refreshMovies();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void refreshMovies() {
        if((garbageMoviesList != null)&&(garbageMoviesList.size() > 0)){
            if(results == null)
                results = new ArrayList<Movie>();
            results.addAll(garbageMoviesList);
            //handler.addMoviesList(garbageMoviesList);
            addMoviesListListener.addMovieList(garbageMoviesList);
            adapter.notifyDataSetChanged();

        }
        else {
            Utilities.makeImageToast(getActivity(), R.drawable.ic_report_24dp,R.string.empty_garbage_list_message, Toast.LENGTH_SHORT).show();
        }

    }

    private void startRemoveMovies() {

        if (results != null) {
            if (results.size() > Constants.MOVIES_EMPTY_NUMBER) {
                showDeleteAllItemsWarning();
            } else {
                Utilities.makeImageToast(getActivity(), R.drawable.ic_error_24dp, R.string.empty_movies_list_message, Toast.LENGTH_SHORT).show();
            }

        }
    }
    //region SHOW ALERT DIALOG TO REMOVE ALL ITEMS
    private void showDeleteAllItemsWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.warning_title_text);
        builder.setIcon(R.drawable.ic_report_24dp);
        builder.setMessage(R.string.delete_all_movies_warning_message);
        builder.setPositiveButton(R.string.positive_button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMovies();

            }
        });
        builder.setNegativeButton(R.string.negative_button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void deleteMovies() {
        garbageMoviesList.addAll(results);
        //handler.deleteMoviesList(results);
        deleteMoviesListListener.deleteMoviesList(results);
        results.clear();
        adapter.notifyDataSetChanged();
    }
    //endregion

    private void initUIElements(View view) {
        initDBOptionsSpinner(view);
        initDBSearchEditText(view);
        initDBResultList(view);

    }
    private void initDBResultList(View view) {
        dbResultList = (ListView)view.findViewById(R.id.db_search_results_list);
        results = new LinkedList<Movie>();
        adapter = new MoviesAdapter(getActivity(),R.layout.movies_list_item,results);
        dbResultList.setAdapter(adapter);
        dbResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getSelectedMovieListener != null) {
                    selectedMovie = results.get(position);
                    getSelectedMovieListener.getSelectedMovie(selectedMovie, FRAGMENT_NAME);
                }

            }
        });

        dbResultList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                ImageView imageView = null;
                Bitmap bitmap = null;
                if (view instanceof LinearLayout) {

                    imageView = (ImageView) view.findViewById(R.id.movies_list_item_image);
                    if (imageView != null) {
                        bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    }

                }
                showMovieOptionDialog(position, bitmap);
                return true;
            }
        });
    }

    //region SHOW ALERT DIALOG ON LIST ITEM CLICKED
    private void showMovieOptionDialog(final int position, final Bitmap bitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.movie_options_dialog_title_text));
        builder.setItems(R.array.movie_options_items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Movie movie;
                switch (which) {
                    case Constants.SHARE_IMAGE_CODE:
                        shareImage(bitmap);
                        break;
                    case Constants.EDIT_MOVIE_CODE:
                        movie = results.get(position);
                        getSelectedMovieListener.getSelectedMovie(movie, FRAGMENT_NAME);
                        break;
                    case Constants.DELETE_MOVIE_CODE:
                        showDeleteItemWarning(position);
                        break;
                }
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void shareImage(Bitmap bitmap) {
        if (bitmap != null) {
            shareImageListener.onSharedBitmapReceived(bitmap);
        } else {
            Utilities.makeImageToast(getActivity(), R.drawable.ic_report_24dp, R.string.empty_share_image_message, Toast.LENGTH_SHORT).show();
        }
    }

    //endregion
    //region SHOW ALERT DIALOG TO REMOVE ITEM
    private void showDeleteItemWarning(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.warning_title_text);
        builder.setIcon(R.drawable.ic_report_24dp);
        builder.setMessage(R.string.delete_one_movie_message);
        builder.setPositiveButton(R.string.positive_button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMovie(position);
            }
        });
        builder.setNegativeButton(R.string.negative_button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void deleteMovie(int position) {
        Movie movie = results.get(position);
        //handler.deleteMovie(movie);
        deleteMoviesListListener.deleteMovie(movie);
        results.remove(position);
        garbageMoviesList.add(movie);
        adapter.notifyDataSetChanged();
    }


    private void initDBOptionsSpinner(View view) {

        dbOptionsSpinner = (Spinner)view.findViewById(R.id.db_search_options_spinner);
        // Creating adapter for spinner
        final String[]options = getResources().getStringArray(R.array.db_search_options);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,options);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        dbOptionsSpinner.setAdapter(dataAdapter);
        dbOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case SUBJECT_SEARCH_OPTION:
                        currentSearchOption = SUBJECT_SEARCH_OPTION;
                        break;
                    case YEAR_SEARCH_OPTION:
                        currentSearchOption = YEAR_SEARCH_OPTION;
                        break;
                    case SUBJECT_FRAGMENT_SEARCH_OPTION:
                        currentSearchOption = SUBJECT_FRAGMENT_SEARCH_OPTION;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    //region GET DATA BASE DATA FUNCTION
    private boolean getDBData() {

        searchString = dbSearchEditText.getText().toString();
        searchString = searchString.trim();
        if (searchString.equals(Constants.EMPTY_STRING)) {
            //Toast.makeText(SearchMoviesActivity.this, R.string.empty_search_string_message, Toast.LENGTH_LONG).show();

            Utilities.makeImageToast(getActivity(), R.drawable.ic_error_24dp, R.string.empty_search_string_message, Toast.LENGTH_LONG).show();
            return true;
        }
        Utilities.hide_keyboard(getActivity());
        if (currentSearchOption == SUBJECT_SEARCH_OPTION) {
            //getSubjectMovies();
            loadSubjectMovies();
            return true;

        } else if (currentSearchOption == YEAR_SEARCH_OPTION) {
            //getYearMovies();
            loadYearMovies();

            return true;


        } else if(currentSearchOption == SUBJECT_FRAGMENT_SEARCH_OPTION){
            //getSubjectFragmentMovies();
            loadSubjectFragmentsMovies();
            return true;

        }
        return true;
    }

    private void loadSubjectFragmentsMovies() {
        MoviesLoader.setArgument(searchString);
        getActivity().getLoaderManager().getLoader(SUBJECT_FRAGMENT_LOADER_ID).forceLoad();
    }

    private void loadYearMovies() {
        if (Utilities.isValidYear(searchString)){
            MoviesLoader.setArgument(searchString);
            getActivity().getLoaderManager().getLoader(YEAR_LOADER_ID).forceLoad();
        }
        else {
            Utilities.makeImageToast(getActivity(), R.drawable.ic_error_24dp, R.string.invalid_year_message, Toast.LENGTH_LONG).show();
        }

    }

    private void loadSubjectMovies() {
        MoviesLoader.setArgument(searchString);
        getActivity().getLoaderManager().getLoader(SUBJECT_LOADER_ID).forceLoad();
    }

    private void getSubjectFragmentMovies() {
        List<Movie>movies;
        movies = handler.getMovieBySubjectFragment(searchString);
        if ((movies == null) ||(movies.size() == Constants.MOVIES_EMPTY_NUMBER)) {
            Utilities.makeImageToast(getActivity(), R.drawable.ic_error_24dp, R.string.movie_not_found_message, Toast.LENGTH_LONG).show();

        }
        updateResultsList(movies);
    }

    private void getYearMovies() {
        List<Movie>movies;
        if (Utilities.isValidYear(searchString)) {
            movies = handler.getMovieByYear(searchString);
            if ((movies == null) ||(movies.size() == Constants.MOVIES_EMPTY_NUMBER)) {
                Utilities.makeImageToast(getActivity(), R.drawable.ic_error_24dp, R.string.movie_not_found_message, Toast.LENGTH_LONG).show();
            }

            updateResultsList(movies);

        } else {
            Utilities.makeImageToast(getActivity(), R.drawable.ic_error_24dp, R.string.invalid_year_message, Toast.LENGTH_LONG).show();
        }
    }

    private void getSubjectMovies() {
        List<Movie>movies;
        movies = handler.getMovieBySubject(searchString.trim());
        if ((movies == null) ||(movies.size() == Constants.MOVIES_EMPTY_NUMBER)) {
            Utilities.makeImageToast(getActivity(), R.drawable.ic_error_24dp, R.string.movie_not_found_message, Toast.LENGTH_LONG).show();
        }
        updateResultsList(movies);
    }

    /*
     * This function update results list
     */
    private void updateResultsList(List<Movie>movies) {
        results.clear();
        results.addAll(movies);
        Utilities.setMoviesOrder(results,getActivity());
        adapter.notifyDataSetChanged();
    }
    //endregion

    private void initDBSearchEditText(View view) {
        dbSearchEditText = (EditText)view.findViewById(R.id.db_search_edit_text);
        currentSearchOption = SUBJECT_SEARCH_OPTION;
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        dbSearchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    imm.showSoftInput(dbSearchEditText, InputMethodManager.SHOW_IMPLICIT);
                } else {

                    imm.hideSoftInputFromWindow(dbSearchEditText.getWindowToken(), 0);
                }
            }
        });

        dbSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    return getDBData();

                }
                return false;
            }
        });
    }
    //endregion


    private void displayMessage(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return getMoviesLoader(id);
    }

    private Loader<List<Movie>> getMoviesLoader(int id) {
        MoviesLoader loader = null;
        switch(id){
            case SUBJECT_LOADER_ID:
                loader = new MoviesLoader(getActivity(),MoviesLoader.MOVIES_BY_SUBJECT_CODE);
                break;
            case SUBJECT_FRAGMENT_LOADER_ID:
                loader = new MoviesLoader(getActivity(),MoviesLoader.MOVIES_BY_SUBJECT_FRAGMENT_CODE);
                break;
            case YEAR_LOADER_ID:
                loader = new MoviesLoader(getActivity(),MoviesLoader.MOVIES_BY_YEAR_CODE);
                break;
            default:
                loader = new MoviesLoader(getActivity());
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        initResultsList(data);
    }

    private void initResultsList(List<Movie> data) {

        if(results == null){
            results = new LinkedList<Movie>();
        }

        if ((data == null)||(data.size() == Constants.MOVIES_EMPTY_NUMBER)) {
            Utilities.makeImageToast(getActivity(), R.drawable.ic_error_24dp, R.string.movie_not_found_message, Toast.LENGTH_LONG).show();
            return;
        }

        results.clear();
        results.addAll(data);
        Utilities.setMoviesOrder(results, getActivity());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }
}

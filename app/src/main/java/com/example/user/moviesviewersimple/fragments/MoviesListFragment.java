package com.example.user.moviesviewersimple.fragments;


import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.moviesviewersimple.ContainerScreenActivity;
import com.example.user.moviesviewersimple.R;
import com.example.user.moviesviewersimple.data.Movie;
import com.example.user.moviesviewersimple.data.MoviesAdapter;
import com.example.user.moviesviewersimple.db.MoviesTableHandler;
import com.example.user.moviesviewersimple.fragments.interfaces.AddMoviesListListener;
import com.example.user.moviesviewersimple.fragments.interfaces.DeleteAllMoviesListener;
import com.example.user.moviesviewersimple.fragments.interfaces.GetSelectedMovieListener;
import com.example.user.moviesviewersimple.fragments.interfaces.GoToFragmentListener;
import com.example.user.moviesviewersimple.fragments.interfaces.ShareImageListener;
import com.example.user.moviesviewersimple.tasks.MoviesLoader;
import com.example.user.moviesviewersimple.utilities.Constants;
import com.example.user.moviesviewersimple.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 16/02/2016.
 */
public class MoviesListFragment extends Fragment
        implements View.OnClickListener,
                   LoaderManager.LoaderCallbacks<List<Movie>>{


    //region Constants
    public static final String FRAGMENT_NAME = MoviesListFragment.class.getSimpleName();
    //endregion
    private static final String TAG = "TEST";

    private static final int ID_MENU_EXIT = 2;

    private static final int ID_MENU_RETURN = 3;

    private static final int ID_MENU_REMOVE_ALL = 1;
    //region INSTANCE VARIABLES
    private FloatingActionButton showOptionsBtn;

    private FloatingActionButton manuallyAddBtn;

    private FloatingActionButton onlineAddBtn;

    private ListView moviesListView;

    private List<Movie> moviesList;

    private List<Movie> garbageMoviesList;

    private MoviesTableHandler handler;

    private MoviesAdapter adapter;

    private GoToFragmentListener goToFragmentListener;


    private GetSelectedMovieListener getSelectedMovieListener;

    private ShareImageListener shareImageListener;

    private AddMoviesListListener addMoviesListListener;

    private DeleteAllMoviesListener deleteAllMoviesListener;
    //endregion


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setGoToFragmentListener(context);
        setGetSelectedMovieListener(context);
        setShareImageListener(context);
        setAddMoviesListListener(context);
        setDeleteAllMoviesListener(context);

    }

    private void setDeleteAllMoviesListener(Context context) {
        try {
            if(context instanceof DeleteAllMoviesListener){
                deleteAllMoviesListener = (DeleteAllMoviesListener)context;
            }
            else {
                throw new ClassCastException();
            }
        }
        catch (ClassCastException e){
            Log.d(TAG, context.getString(R.string.implement_delete_all_movies_listener_message));
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
            Log.d(TAG, context.getString(R.string.implement_add_movies_list_listener_message));
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
            else{
                throw new ClassCastException();
            }
        }
        catch (ClassCastException e){
            Log.d(TAG, context.getString(R.string.implement_set_selected_movie_listener_interface_message));
        }
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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movies_list_fragment,container,false);
        setUIElements(view);
        setHasOptionsMenu(true);
        garbageMoviesList = new ArrayList<>();
        //initialize db handler
        handler = new MoviesTableHandler(getActivity());
        //initMoviesList();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem removeItem =
                menu.add(Menu.NONE, ID_MENU_REMOVE_ALL, Menu.NONE, getString(R.string.remove_all_item_text));
        MenuItem returnItem =
                menu.add(Menu.NONE, ID_MENU_RETURN, Menu.NONE, getString(R.string.return_movies_menu_item_text));
        MenuItem exitItem =
                menu.add(Menu.NONE, ID_MENU_EXIT, Menu.NONE, getString(R.string.exit_item_text));
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ID_MENU_REMOVE_ALL:
                startRemoveMovies();
                break;
            case ID_MENU_EXIT:
                sendExitMessage();
                break;
            case ID_MENU_RETURN:
                refreshMovies();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendExitMessage() {
        Intent intent = new Intent(ContainerScreenActivity.BROADCAST_RECEIVER_IDENTIFIER);
        intent.putExtra(ContainerScreenActivity.EXTRA_EXIT_APP_KEY, ContainerScreenActivity.EXTRA_EXIT_APP_KEY);
        LocalBroadcastManager.getInstance(getActivity())
                .sendBroadcast(intent);
    }


    private void refreshMovies() {
        if((garbageMoviesList != null)&&(garbageMoviesList.size() > 0)){
            moviesList.addAll(garbageMoviesList);
            addMoviesListListener.addMovieList(garbageMoviesList);
            //handler.addMoviesList(garbageMoviesList);
            adapter.notifyDataSetChanged();

        }
        else {
            Utilities.makeImageToast(getActivity(), R.drawable.ic_report_24dp,R.string.empty_garbage_list_message, Toast.LENGTH_SHORT).show();
        }

    }


    private void startRemoveMovies() {

        if (moviesList != null) {
            if (moviesList.size() > Constants.MOVIES_EMPTY_NUMBER) {
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
        garbageMoviesList.addAll(moviesList);
        moviesList.clear();
        //handler.deleteAllMovies();
        deleteAllMoviesListener.deleteAllMovies();
        adapter.notifyDataSetChanged();
    }
    //endregion


    private void displayMessage(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    //region SET VIEW ELEMENTS
    private void setUIElements(View view) {
        //set floating buttons
        setShowFloatingActionButtons(view);

        //set movies list view
        setMoviesListView(view);

    }

    //region SET FLOATING BUTTONS
    private void setShowFloatingActionButtons(View view) {

        //set show options button
        showOptionsBtn = (FloatingActionButton) view.findViewById(R.id.show_options_button);
        showOptionsBtn.setOnClickListener(this);

        //set manually addition button
        manuallyAddBtn = (FloatingActionButton) view.findViewById(R.id.manually_add_button);
        manuallyAddBtn.setOnClickListener(this);
        manuallyAddBtn.setVisibility(View.GONE);

        //set manually addition button
        onlineAddBtn = (FloatingActionButton) view.findViewById(R.id.online_add_button);
        onlineAddBtn.setOnClickListener(this);
        onlineAddBtn.setVisibility(View.GONE);

    }
    //endregion

    //region SET MOVIES LIST VIEW
    private void setMoviesListView(View view) {
        moviesListView = (ListView) view.findViewById(R.id.movies_list_view);
        moviesList = new ArrayList<>();
        adapter = new MoviesAdapter(getActivity(),R.layout.movies_list_item,moviesList);
        moviesListView.setAdapter(adapter);
        moviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getSelectedMovieListener != null) {
                    Movie movie = moviesList.get(position);
                    getSelectedMovieListener.getSelectedMovie(movie, FRAGMENT_NAME);
                }
            }
        });

        moviesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                ImageView imageView = null;
                Bitmap bitmap = null;
                if (view instanceof LinearLayout) {

                    imageView = (ImageView) view.findViewById(R.id.movies_list_item_image);
                    if(imageView != null){
                        bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                    }

                }
                showMovieOptionDialog(position,bitmap);
                return true;

            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.show_options_button:
                swapButtons();
                break;
            case R.id.manually_add_button:
                goToFragmentListener.goToFragment(EditMovieFragment.FRAGMENT_NAME);
                break;
            case R.id.online_add_button:
                goToFragmentListener.goToFragment(WebSearchFragment.FRAGMENT_NAME);
                break;

        }
    }

    //region INIT MOVIES LIST
    private void initMoviesList() {


        moviesList = handler.getAllMovies();

        if ((moviesList != null)) {
            Utilities.setMoviesOrder(moviesList,getActivity());
            adapter = new MoviesAdapter(getActivity(), R.layout.movies_list_item, moviesList);
            moviesListView.setAdapter(adapter);
        }
    }
    //endregion
    //region SWAP BUTTONS FUNCTION
    private void swapButtons() {

        int onlineBtnVisibility = onlineAddBtn.getVisibility();
        int manuallyBtnVisibility = manuallyAddBtn.getVisibility();
        //swap online button visibility
        if (onlineBtnVisibility == View.VISIBLE) {
            onlineAddBtn.setVisibility(View.GONE);
        } else {
            onlineAddBtn.setVisibility(View.VISIBLE);
        }
        //swap manually add button
        if (manuallyBtnVisibility == View.VISIBLE) {
            manuallyAddBtn.setVisibility(View.GONE);
        } else {
            manuallyAddBtn.setVisibility(View.VISIBLE);
        }

    }
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {

        return new MoviesLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {

        initMoviesList(data);

    }

    private void initMoviesList(List<Movie> data) {
        moviesList = new ArrayList<>();
        moviesList.addAll(data);
        if ((moviesList != null)) {
            Utilities.setMoviesOrder(moviesList,getActivity());
            adapter = new MoviesAdapter(getActivity(), R.layout.movies_list_item, moviesList);
            moviesListView.setAdapter(adapter);
        }
    }


    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

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
                        movie = moviesList.get(position);
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
        Movie movie = moviesList.get(position);
        //handler.deleteMovie(movie);
        deleteAllMoviesListener.deleteMovie(movie);
        moviesList.remove(position);
        garbageMoviesList.add(movie);
        adapter.notifyDataSetChanged();
    }


}

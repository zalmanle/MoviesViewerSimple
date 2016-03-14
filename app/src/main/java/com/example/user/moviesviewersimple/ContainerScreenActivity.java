package com.example.user.moviesviewersimple;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.user.moviesviewersimple.data.Movie;
import com.example.user.moviesviewersimple.fragments.DBSearchFragment;
import com.example.user.moviesviewersimple.fragments.EditMovieFragment;
import com.example.user.moviesviewersimple.fragments.MoviesListFragment;
import com.example.user.moviesviewersimple.fragments.WebSearchFragment;
import com.example.user.moviesviewersimple.fragments.interfaces.AddMovieListener;
import com.example.user.moviesviewersimple.fragments.interfaces.AddMoviesListListener;
import com.example.user.moviesviewersimple.fragments.interfaces.DeleteAllMoviesListener;
import com.example.user.moviesviewersimple.fragments.interfaces.DeleteMoviesListListener;
import com.example.user.moviesviewersimple.fragments.interfaces.EditMovieListener;
import com.example.user.moviesviewersimple.fragments.interfaces.GetSelectedMovieListener;
import com.example.user.moviesviewersimple.fragments.interfaces.GoToFragmentListener;
import com.example.user.moviesviewersimple.fragments.interfaces.ShareImageListener;
import com.example.user.moviesviewersimple.services.MoviesDBService;
import com.example.user.moviesviewersimple.utilities.Utilities;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

public class ContainerScreenActivity extends AppCompatActivity
                           implements GoToFragmentListener,
                                      GetSelectedMovieListener,
                                      ShareImageListener,
                                      AddMoviesListListener,
                                      AddMovieListener,
                                      EditMovieListener,
                                      DeleteMoviesListListener,
                                      DeleteAllMoviesListener {


    //region Constants
    private static final int HOME_CODE = 0;

    private static final int ADD_MOVIE_CODE = 2;

    private static final int WEB_SEARCH_CODE = 4;

    private static final int DB_SEARCH_CODE= 5;

    private static final int SETTINGS_CODE = 7;

    public static final String BROADCAST_RECEIVER_IDENTIFIER
            = "com.example.user.moviesviewersimple.BROADCAST_RECEIVER_IDENTIFIER";


    private static final int SETTINGS_RESULT = 1;

    public static final String EXTRA_EXIT_APP_KEY = "exit app key";

    public static final String EXTRA_UPLOAD_IMAGE_KEY = "exit upload image key";

    private static int RESULT_LOAD_IMAGE = 251;
    //endregion
    //region Instance Variables
    private Drawer drawer;

    private FragmentsLoader loader;

    private MovieSavedReceiver receiver;

    private Fragment lastLoadedFragment;

    private Movie movieToPass;

    private String fragmentNameToPass;

    private String statusToPass;

    private boolean isMovieToPassExist;
    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_screen);
        createDrawer();
        initListFragment();
        initBroadcast();

    }

    private void initBroadcast() {

        IntentFilter filter = new IntentFilter(BROADCAST_RECEIVER_IDENTIFIER);
        receiver = new MovieSavedReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    private void initListFragment(){
        loader = new FragmentsLoader();
        loader.initFragmentManager();
        loader.loadMoviesListFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.open_drawer_menu_item);
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.open_drawer_menu_item:
                swapDrawer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void swapDrawer() {

        if(drawer != null){
            if(drawer.isDrawerOpen()){
                drawer.closeDrawer();
            }
            else {
                drawer.openDrawer();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if((drawer != null)&&(drawer.isDrawerOpen()))
        {
            drawer.closeDrawer();
        }
        /*
        else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0 ) {
                getSupportFragmentManager().popBackStack();
            }
            else {
                super.onBackPressed();
            }
        }*/


    }


    //endregion
    //region Service Functions
    //region Drawer Functions
    private void createDrawer() {
        PrimaryDrawerItem homeItem = new PrimaryDrawerItem()
                // .withIdentifier(HOME_CODE)
                .withName(R.string.home_menu_item_text)
                .withIcon(R.drawable.home_icon);
        SecondaryDrawerItem addMovieItem = new SecondaryDrawerItem()
                // .withIdentifier(ADD_MOVIE_CODE)
                .withName(R.string.add_movie_item_text)
                .withIcon(R.drawable.add_movie_icon);
        SecondaryDrawerItem webSearchItem = new SecondaryDrawerItem()
                // .withIdentifier(WEB_SEARCH_CODE)
                .withName(R.string.web_search_item_text)
                .withIcon(R.drawable.web_search_icon);
        SecondaryDrawerItem dbSearchItem = new SecondaryDrawerItem()
                // .withIdentifier(DB_SEARCH_CODE)
                .withName(R.string.db_search_item_text)
                .withIcon(R.drawable.db_search_icon);
        SecondaryDrawerItem settingsItem = new SecondaryDrawerItem()
                // .withIdentifier(SETTINGS_CODE)
                .withName(R.string.settings_item_text)
                .withIcon(R.drawable.settings_icon);

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(false)
                .addDrawerItems(
                        homeItem,
                        new DividerDrawerItem(),
                        addMovieItem,
                        new DividerDrawerItem(),
                        webSearchItem,
                        dbSearchItem,
                        new DividerDrawerItem(),
                        settingsItem
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        executeDrawerAction(position);
                        return false;
                    }
                })
                .build();
    }

    private void executeDrawerAction(int position) {
        switch (position){
            case HOME_CODE:
                loader.loadMoviesListFragment();
                break;
            case ADD_MOVIE_CODE:
                loader.loadManuallyAddMovieFragment();
                break;
            case WEB_SEARCH_CODE:
                loader.loadWebSearchFragment();
                break;
            case DB_SEARCH_CODE:
                loader.loadDBSearchFragment();
                break;
            case SETTINGS_CODE:
                goToSettingsScreen();
                break;
        }
    }

    private void goToSettingsScreen() {
        Intent intent = new Intent(this,PreferencesActivity.class);
        lastLoadedFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        startActivityForResult(intent, SETTINGS_RESULT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==SETTINGS_RESULT) {

            if(resultCode == RESULT_OK){
                 new LastFragmentLoadTask(lastLoadedFragment).execute();
            }

        }
        if ((requestCode == RESULT_LOAD_IMAGE) &&(resultCode == RESULT_OK) && (null != data)) {
            getData(data);

        }
    }

    private void getData(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        final String picturePath = cursor.getString(columnIndex);
        loadData(picturePath);
        cursor.close();

    }

    private void loadData(String picturePath) {
        if(isMovieToPassExist){
            loader.loadEditMovieFragmentWithImageAndMovie(movieToPass,statusToPass,fragmentNameToPass,picturePath);
        }
        else{
            loader.loadEditMovieFragmentWithImage(picturePath);
        }
    }


    //endregion
    private void displayMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void goToFragment(String fragmentName) {
        loader.loadFragmentByName(fragmentName);
    }



    @Override
    public void getSelectedMovie(Movie movie, String sourceFragmentName) {

        loader.loadSelectedMovie(movie, sourceFragmentName);
    }

    @Override
    public void onSharedBitmapReceived(Bitmap bitmap) {
       try {
           Utilities.shareImageFromBitmap(this,bitmap);
       }
       catch (Exception e){
           Utilities.makeImageToast(this,R.drawable.ic_report_24dp,R.string.fail_share_image_message,Toast.LENGTH_SHORT).show();
       }
    }
    //endregion
    //region SERVICE FUNCTIONS
    @Override
    public void deleteMovie(Movie movie) {
         sendDeleteMovieMessage(movie);
    }

    private void sendDeleteMovieMessage(Movie movie) {
        Intent intent = new Intent(this, MoviesDBService.class);
        intent.setAction(MoviesDBService.Action.DELETE_ACTION);
        intent.putExtra(MoviesDBService.Extras.Keys.EXTRA_DELETE_KEY,MoviesDBService.Extras.Values.EXTRA_ITEM_DELETE_VALUE);
        intent.putExtra(MoviesDBService.Extras.Keys.EXTRA_MOVIE_KEY, movie);
        startService(intent);
    }

    @Override
    public void deleteMoviesList(List<Movie> list) {

        sendDeleteMoviesListMessage(list);
    }

    private void sendDeleteMoviesListMessage(List<Movie> list) {
        Intent intent = new Intent(this, MoviesDBService.class);
        intent.setAction(MoviesDBService.Action.DELETE_ACTION);
        intent.putExtra(MoviesDBService.Extras.Keys.EXTRA_DELETE_KEY, MoviesDBService.Extras.Values.EXTRA_LIST_DELETE_VALUE);
        ArrayList<Movie>items = new ArrayList<Movie>();
        items.addAll(list);
        intent.putParcelableArrayListExtra(MoviesDBService.Extras.Keys.EXTRA_MOVIE_LIST_KEY,items);
        startService(intent);
    }

    @Override
    public void deleteAllMovies() {

        sendDeleteAllMoviesMessage();

    }

    private void sendDeleteAllMoviesMessage() {
        Intent intent = new Intent(this, MoviesDBService.class);
        intent.setAction(MoviesDBService.Action.DELETE_ACTION);
        intent.putExtra(MoviesDBService.Extras.Keys.EXTRA_DELETE_KEY,MoviesDBService.Extras.Values.EXTRA_ALL_DELETE_VALUE);
        startService(intent);
    }

    @Override
    public void addMovie(Movie movie) {
         sendAddMovieMessage(movie);
    }

    private void sendAddMovieMessage(Movie movie) {
        Intent intent = new Intent(this, MoviesDBService.class);
        intent.setAction(MoviesDBService.Action.ADD_ACTION);
        intent.putExtra(MoviesDBService.Extras.Keys.EXTRA_ADD_KEY,MoviesDBService.Extras.Values.EXTRA_ITEM_ADD_VALUE);
        intent.putExtra(MoviesDBService.Extras.Keys.EXTRA_MOVIE_KEY, movie);
        startService(intent);
    }

    @Override
    public void addMovieList(List<Movie> list) {
        sendAddMovieListMessage(list);
    }

    private void sendAddMovieListMessage(List<Movie> list) {
        Intent intent = new Intent(this, MoviesDBService.class);
        intent.setAction(MoviesDBService.Action.ADD_ACTION);
        intent.putExtra(MoviesDBService.Extras.Keys.EXTRA_ADD_KEY, MoviesDBService.Extras.Values.EXTRA_LIST_ADD_VALUE);
        ArrayList<Movie>items = new ArrayList<Movie>();
        items.addAll(list);
        intent.putParcelableArrayListExtra(MoviesDBService.Extras.Keys.EXTRA_MOVIE_LIST_KEY,items);
        startService(intent);
    }

    @Override
    public void editMovie(Movie movie) {
        sendEditMovieMessage(movie);
    }

    private void sendEditMovieMessage(Movie movie) {
        Intent intent = new Intent(this, MoviesDBService.class);
        intent.setAction(MoviesDBService.Action.EDIT_ACTION);
        intent.putExtra(MoviesDBService.Extras.Keys.EXTRA_MOVIE_KEY, movie);
        startService(intent);
    }
    //endregion

    //region FRAGMENT LOADER CLASS
        private class FragmentsLoader {

            //region Instance Variables
            private FragmentManager manager;

            private FragmentTransaction transaction;

            private MoviesListFragment moviesListFragment;

            private WebSearchFragment webSearchFragment;

            private DBSearchFragment dbSearchFragment;
            //endregion
            //region Constants
            private static final String RESTART_CODE = "restart";
            //endregion



        private void loadMoviesListFragment(String...params){
            if(manager == null){
                initFragmentManager();
            }
            if((moviesListFragment == null)||((params.length != 0)&&(params[0].equals(RESTART_CODE)))){
                moviesListFragment = new MoviesListFragment();
            }
            transaction = manager.beginTransaction();
            transaction.replace(R.id.container,moviesListFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        private void loadFragmentByInstance(Fragment instance){
            if(instance instanceof MoviesListFragment){
                loadMoviesListFragment(RESTART_CODE);
            }
            else if(instance instanceof WebSearchFragment){
                loadWebSearchFragment(RESTART_CODE);
            }
            else if(instance instanceof DBSearchFragment){
                loadDBSearchFragment(RESTART_CODE);
            }
            else {
                loadMoviesListFragment(RESTART_CODE);
            }
        }

        private void loadWebSearchFragment(String...params){
            if(manager == null){
                initFragmentManager();
            }
            if((webSearchFragment == null)||((params.length != 0)&&(params[0].equals(RESTART_CODE)))){
                webSearchFragment = new WebSearchFragment();
            }
            transaction = manager.beginTransaction();
            transaction.replace(R.id.container,webSearchFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        private void loadManuallyAddMovieFragment(){
            if(manager == null){
                initFragmentManager();
            }
            transaction = manager.beginTransaction();
            transaction.replace(R.id.container,new EditMovieFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        }

        private void loadDBSearchFragment(String...params){
            if(manager == null){
                initFragmentManager();
            }
            if((dbSearchFragment == null)||((params.length != 0)&&(params[0].equals(RESTART_CODE)))){
                dbSearchFragment = new DBSearchFragment();
            }
            transaction = manager.beginTransaction();
            transaction.replace(R.id.container,dbSearchFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }


        private void loadWebAddMovieFragment(Movie movie){
            if(manager == null){
                initFragmentManager();
            }
            transaction = manager.beginTransaction();
            EditMovieFragment fragment = EditMovieFragment.getInstance(movie,EditMovieFragment.EXTRA_WEB_MOVIE_TO_ADD,WebSearchFragment.FRAGMENT_NAME);
            transaction.replace(R.id.container,fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }
        private void loadEditMovieFragmentWithImage(String path){
            if(manager == null){
                initFragmentManager();
            }
            transaction = manager.beginTransaction();
            EditMovieFragment fragment = EditMovieFragment.getInstance(path);
            transaction.replace(R.id.container,fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }

        private void loadEditMovieFragmentWithImageAndMovie(Movie movie,String target,String sourceFragmentName,String imagePath){
            if(manager == null){
                initFragmentManager();
            }
            transaction = manager.beginTransaction();
            EditMovieFragment fragment = EditMovieFragment.getInstance(movie,target,sourceFragmentName,imagePath);
            transaction.replace(R.id.container,fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }

        private void loadEditExistMovieFragment(Movie movie,String fragmentName){
            if(manager == null){
                initFragmentManager();
            }
            transaction = manager.beginTransaction();
            EditMovieFragment fragment = EditMovieFragment.getInstance(movie,EditMovieFragment.EXTRA_EXIST_MOVIE_TO_EDIT,fragmentName);
            transaction.replace(R.id.container,fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        private void loadFragmentByName(String fragmentName) {

            if(fragmentName.equals(WebSearchFragment.FRAGMENT_NAME)){
                loadWebSearchFragment();
            }
            if(fragmentName.equals(EditMovieFragment.FRAGMENT_NAME)){
                loadManuallyAddMovieFragment();
            }
            if(fragmentName.equals(MoviesListFragment.FRAGMENT_NAME)){
                loadMoviesListFragment();
            }
            if(fragmentName.equals(DBSearchFragment.FRAGMENT_NAME)){
                loadDBSearchFragment();
            }
        }

        public void loadSelectedMovie(Movie movie, String sourceFragmentName) {
            if(sourceFragmentName.equals(WebSearchFragment.FRAGMENT_NAME)){
                loader.loadWebAddMovieFragment(movie);
            }
            if(sourceFragmentName.equals(DBSearchFragment.FRAGMENT_NAME)){
                loader.loadEditExistMovieFragment(movie, sourceFragmentName);
            }
            if(sourceFragmentName.equals(MoviesListFragment.FRAGMENT_NAME)){
                loader.loadEditExistMovieFragment(movie, sourceFragmentName);
            }
        }

        private void initFragmentManager(){
            manager = getSupportFragmentManager();
        }


    }
    //endregion
    //region SAVE DATA RECEIVER
    private class MovieSavedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String status = intent.getStringExtra(EXTRA_EXIT_APP_KEY);
            if ((status != null)&&(status.equals(EXTRA_EXIT_APP_KEY))) {

                finish();
            }
            status = intent.getStringExtra(EXTRA_UPLOAD_IMAGE_KEY);
            int movieStatus = intent.getIntExtra(EditMovieFragment.IS_MOVIE_EXIST_KEY,EditMovieFragment.IS_MOVIE_EXIST_DEFAULT_VALUE);
            if ((status != null)&&(status.equals(EXTRA_UPLOAD_IMAGE_KEY))) {

                if(movieStatus == EditMovieFragment.IS_MOVIE_EXIST_POSITIVE_VALUE){
                    movieToPass = intent.getParcelableExtra(EditMovieFragment.MOVIE_TO_PASS_KEY);
                    statusToPass = intent.getStringExtra(EditMovieFragment.EXTRA_MOVIE_TARGET_KEY);
                    fragmentNameToPass = intent.getStringExtra(EditMovieFragment.SOURCE_NAME_CODE);
                    isMovieToPassExist = true;
                }
                else {
                    isMovieToPassExist = false;
                }
                goToGalleryApp();
            }

        }
    }

    private void goToGalleryApp() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }



    class MoviesListFragmentLoadTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... p) {
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            loader.loadMoviesListFragment();
        }
    }

    class LastFragmentLoadTask extends AsyncTask<Void, Void, Void> {

        //region Instance Variables
        Fragment fragment;
        //endregion

        public LastFragmentLoadTask(Fragment fragment){
            this.fragment = fragment;
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            loader.loadFragmentByInstance(fragment);
        }
    }

    ;
    //endregion
}


package com.example.user.moviesviewersimple.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.user.moviesviewersimple.ContainerScreenActivity;
import com.example.user.moviesviewersimple.R;
import com.example.user.moviesviewersimple.data.Movie;
import com.example.user.moviesviewersimple.db.MoviesTableHandler;
import com.example.user.moviesviewersimple.fragments.interfaces.AddMovieListener;
import com.example.user.moviesviewersimple.fragments.interfaces.EditMovieListener;
import com.example.user.moviesviewersimple.fragments.interfaces.GoToFragmentListener;
import com.example.user.moviesviewersimple.fragments.interfaces.ShareImageListener;
import com.example.user.moviesviewersimple.tasks.LoadMovieBodyTask;
import com.example.user.moviesviewersimple.tasks.interfaces.OnDataReceivedListener;
import com.example.user.moviesviewersimple.utilities.Constants;
import com.example.user.moviesviewersimple.utilities.Utilities;

import java.util.List;

/**
 * Created by User on 16/02/2016.
 */
public class EditMovieFragment extends Fragment
           implements OnDataReceivedListener,View.OnClickListener{

    //region Constants
    public static final String IS_MOVIE_EXIST_KEY = "is movie exist";

    public static final int IS_MOVIE_EXIST_POSITIVE_VALUE = 15;

    public static final int IS_MOVIE_EXIST_NEGATIVE_VALUE = 23;

    public static final int IS_MOVIE_EXIST_DEFAULT_VALUE = 36;

    public static final String MOVIE_TO_PASS_KEY = "movie to pass";

    public static final String SOURCE_NAME_CODE = "source";

    private static final int WATCHED_POSITION = 0;

    private static final int ADD_RATE_POSITION = 1;

    private static final int STORE_IMAGE_OPTION_ID = 201;

    private static final int STORE_IMAGE_TO_GALLERY_OPTION_ID = 203;

    private static final int UPLOAD_FROM_GALLERY_OPTION_ID = 206;

    private static final int SHARE_IMAGE_OPTION_ID = 209;

    private static final int MOVIE_POSITION = 0;

    private static final String EXTRA_EDIT_MOVIE_KEY = "extra edit movie key";

    private static final String EXTRA_IMAGE_PATH_KEY = "extra image path key";

    private static final String EXTRA_SOURCE_FRAGMENT_KEY = "extra source fragment key";

    public static final String EXTRA_MOVIE_TARGET_KEY = "extra movie target key";

    public static final String EXTRA_WEB_MOVIE_TO_ADD = "extra web movie to add";

    public static final String EXTRA_EXIST_MOVIE_TO_EDIT = "extra exist movie to edit";

    private static final String EXTRA_MANUALLY_MOVIE_TO_ADD = "extra manually movie to add";

    private static final String TAG = "TEST";

    public static final String FRAGMENT_NAME = EditMovieFragment.class.getSimpleName();


    //endregion
    //region INSTANCE VARIABLES
    private FloatingActionButton saveBtn;

    private FloatingActionButton advancedOptionsBtn;

    private EditText subjectEditText;

    private EditText bodyEditText;

    private EditText urlEditText;

    private ImageView posterImageView;

    private EditText yearEditText;

    private Button showImageBtn;

    private Movie movie;

    private ActionBar bar;

    private ProgressBar loadBodyPb;

    private String sender;

    private MoviesTableHandler handler;

    private boolean isNewAddition = true;

    private String subject;

    private String body;

    private String url;

    private String year;

    private boolean watched = false;

    private int movieRate = Constants.DEFAULT_MOVIE_RATE;

    private String status;

    private View view;

    private GoToFragmentListener goToFragmentListener;

    private ShareImageListener shareImageListener;

    private String sourceFragmentName;

    private AddMovieListener addMovieListener;

    private EditMovieListener editMovieListener;
    //endregion


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setGoToFragmentListener(context);
        setShareImageListener(context);
        setAddMovieListener(context);
        setEditMovieListener(context);

    }

    private void setAddMovieListener(Context context) {
        try {
            if(context instanceof AddMovieListener){
                addMovieListener = (AddMovieListener)context;
            }
            else {
                throw new ClassCastException();
            }
        }
        catch(ClassCastException e){
            Log.d(TAG, context.getString(R.string.implement_add_movie_listener_message));
        }
    }

    private void setEditMovieListener(Context context) {
        try {
            if(context instanceof EditMovieListener){
                editMovieListener = (EditMovieListener)context;
            }
            else {
                throw new ClassCastException();
            }
        }
        catch (ClassCastException e){
            Log.d(TAG, context.getString(R.string.implement_edit_movie_listener_message));
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

    private void setStatus() {
        Bundle bundle = getArguments();
        status = EXTRA_MANUALLY_MOVIE_TO_ADD;
        sourceFragmentName = MoviesListFragment.FRAGMENT_NAME;
        if(bundle != null) {
            String target = bundle.getString(EXTRA_MOVIE_TARGET_KEY);
            movie = bundle.getParcelable(EXTRA_EDIT_MOVIE_KEY);
            if(target != null){
                if(target.equals(EXTRA_EXIST_MOVIE_TO_EDIT)){
                    status = EXTRA_EXIST_MOVIE_TO_EDIT;
                    String sourceFragmentName = bundle.getString(EXTRA_SOURCE_FRAGMENT_KEY);
                    this.sourceFragmentName = sourceFragmentName;
                }
                else if(target.equals(EXTRA_WEB_MOVIE_TO_ADD)){
                    status = EXTRA_WEB_MOVIE_TO_ADD;
                    sourceFragmentName = WebSearchFragment.FRAGMENT_NAME;
                }
            }
        }
    }

    private  void setImageToImageView(){
        Bundle bundle = getArguments();
        if(bundle != null) {
            String picturePath = bundle.getString(EXTRA_IMAGE_PATH_KEY);
            if (picturePath != null) {
                posterImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                urlEditText.setText(picturePath);
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_movie_fragment, container, false);
        handler = new MoviesTableHandler(getActivity());
        //initialize ui elements
        this.view = view;
        setStatus();
        initUIElements();
        fillUIElements();
        setImageToImageView();
        return view;
    }

    private void fillUIElements() {
        if(status.equals(EXTRA_WEB_MOVIE_TO_ADD)){
            new LoadMovieBodyTask(loadBodyPb,this).execute(movie);
        }
        if(status.equals(EXTRA_WEB_MOVIE_TO_ADD)||(status.equals(EXTRA_EXIST_MOVIE_TO_EDIT))){
            initEditTextFields();
            Utilities.setImageToImageView(movie, posterImageView, getActivity());
        }
    }

    //region INIT EDIT TEXT FIELDS
    private void initEditTextFields() {
        if(movie != null) {
            subjectEditText.setText(movie.getSubject());
            bodyEditText.setText(movie.getBody());
            urlEditText.setText(movie.getImageUrl());
            yearEditText.setText(movie.getYear());
        }
    }
    //endregion


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.save_button:
                storeMovie();
                break;
            case R.id.advanced_options_button:
                showAdvancedOptionsDialog();
                break;
            case R.id.show_image_button:
                showImage();
                break;
        }

    }

    private void showImage() {
        String url = urlEditText.getText().toString();

        if(Utilities.isValidURL(url)){

            String fileName = Utilities.getFileNameFromURL(url);

            if((fileName != null)&&(Utilities.isValidImageName(fileName))){
                //set image to image view
                Utilities.setImageToImageView(url,posterImageView,getActivity());
                return;
            }
            else {
                //Toast.makeText(this,R.string.invalid_file_name_message,Toast.LENGTH_SHORT).show();
                Utilities.makeImageToast(getActivity(),R.drawable.ic_report_24dp,R.string.invalid_file_name_message,Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else  if(Utilities.isValidImageName(url)) {

            Utilities.setImageToImageView(url,posterImageView,getActivity());
            return;

        }
        else {
            //Toast.makeText(this,R.string.invalid_url_message,Toast.LENGTH_SHORT).show();
            Utilities.makeImageToast(getActivity(),R.drawable.ic_report_24dp,R.string.invalid_image_url_message,Toast.LENGTH_SHORT).show();
            return;
        }

    }


    //region SHOW ADVANCED OPTIONS DIALOG
    private void showAdvancedOptionsDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.advanced_options_items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case WATCHED_POSITION:
                        watched = true;
                        break;
                    case ADD_RATE_POSITION:
                        showRatesDialog();
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //endregion

    //region SHOW RATES DIALOG
    private void showRatesDialog(){
        final String[]rates = getResources().getStringArray(R.array.rates);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.rates, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                movieRate = Integer.parseInt(rates[which]);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //endregion
    //region STORE MOVIE
    private void storeMovie() {
        if (prepareMovieFromFields()) {

            if ((status.equals(EXTRA_WEB_MOVIE_TO_ADD))||(status.equals(EXTRA_MANUALLY_MOVIE_TO_ADD))){
                if (!handler.isMovieExist(movie)) {
                    //handler.addMovie(movie);
                    addMovieListener.addMovie(movie);

                } else {
                    Utilities.makeImageToast(getActivity(), R.drawable.ic_report_24dp,R.string.exist_movie_message, Toast.LENGTH_SHORT).show();
                }

            } else if(status.equals(EXTRA_EXIST_MOVIE_TO_EDIT)){

                //handler.editMovie(movie);
                editMovieListener.editMovie(movie);
            }
        }
        goToFragmentListener.goToFragment(sourceFragmentName);
    }

    //region PREPARE MOVIE TO STORE
    private boolean prepareMovieFromFields(){


        //get data from edit texts
        subject = subjectEditText.getText().toString();
        body = bodyEditText.getText().toString();
        url = urlEditText.getText().toString();
        year = yearEditText.getText().toString();

        if ((status.equals(EXTRA_WEB_MOVIE_TO_ADD))||(status.equals(EXTRA_MANUALLY_MOVIE_TO_ADD))){

            return prepareNewItem();

        }else{

            return prepareExistingItem();

        }
    }
    //endregion

    //region PREPARE NEW ITEM
    private boolean prepareNewItem(){

        //check if subject is valid
        if(subject.equals(Constants.EMPTY_STRING)){
            //Toast.makeText(this,R.string.empty_subject_message,Toast.LENGTH_LONG).show();
            Utilities.makeImageToast(getActivity(),R.drawable.ic_error_24dp,R.string.empty_subject_message,Toast.LENGTH_LONG).show();
            return false;
        }

        //check if url is valid
        if(!(Utilities.isValidURL(url)||Utilities.isValidImageName(url))){
            //Toast.makeText(this,R.string.invalid_image_data_message,Toast.LENGTH_SHORT).show();
            Utilities.makeImageToast(getActivity(),R.drawable.ic_error_24dp,R.string.empty_image_url_message,Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!Utilities.isValidYear(year.trim())){
            Utilities.makeImageToast(getActivity(),R.drawable.ic_error_24dp,R.string.invalid_year_message,Toast.LENGTH_SHORT).show();
            return false;
        }


        movie = new Movie(subject,body,url,watched,movieRate,year);
        return true;

    }
    //endregion

    //region PREPARE EXISTING ITEM
    private boolean prepareExistingItem(){

        //check if subject is valid

        if(!subject.equals(Constants.EMPTY_STRING)){
            movie.setSubject(subject);
        }

        //check if url is valid
        if((Utilities.isValidURL(url)||Utilities.isValidImageName(url))){
            movie.setImageUrl(url);
        }

        if(Utilities.isValidYear(year)){
            movie.setYear(year);
        }
        movie.setBody(body);

        if((watched == true)&&(!movie.isWatched())){
            movie.setWatched(watched);
        }

        movie.setRate(movieRate);

        return true;
    }
    //endregion

    //region BACK FUNCTION
    //endregion

    public static EditMovieFragment getInstance(Movie movie,String target,String sourceFragmentName){
        EditMovieFragment fragment = new EditMovieFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_EDIT_MOVIE_KEY, movie);
        bundle.putString(EXTRA_MOVIE_TARGET_KEY,target);
        bundle.putString(EXTRA_SOURCE_FRAGMENT_KEY,sourceFragmentName);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static EditMovieFragment getInstance(Movie movie,String target,String sourceFragmentName,String imagePath){
        EditMovieFragment fragment = new EditMovieFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_EDIT_MOVIE_KEY, movie);
        bundle.putString(EXTRA_MOVIE_TARGET_KEY,target);
        bundle.putString(EXTRA_SOURCE_FRAGMENT_KEY,sourceFragmentName);
        bundle.putString(EXTRA_IMAGE_PATH_KEY, imagePath);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static EditMovieFragment getInstance(String imagePath){
        EditMovieFragment fragment = new EditMovieFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_IMAGE_PATH_KEY, imagePath);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDataReceived(List<Movie> movies) {
        if(movies != null){
            movie = movies.get(MOVIE_POSITION);
            bodyEditText.setText(movie.getBody());
        }
    }

    //region INIT UI ELEMENTS
    private void initUIElements(){

        //initialize subject edit text
        subjectEditText = (EditText) view.findViewById(R.id.subject_edit_text);
        //initialize body edit text
        bodyEditText = (EditText) view.findViewById(R.id.body_edit_text);
        //initialize url edit text
        urlEditText = (EditText) view.findViewById(R.id.url_edit_text);
        //initialize year edit text
        yearEditText = (EditText)view.findViewById(R.id.year_edit_text);
        //initialize image view
        posterImageView = (ImageView)view.findViewById(R.id.movie_poster_image_view);
        registerForContextMenu(posterImageView);
        //initialize load body progress bar
        loadBodyPb = (ProgressBar)view.findViewById(R.id.load_body_pb);

        showImageBtn =(Button)view.findViewById(R.id.show_image_button);
        showImageBtn.setOnClickListener(this);

        saveBtn = (FloatingActionButton)view.findViewById(R.id.save_button);
        saveBtn.setOnClickListener(this);

        advancedOptionsBtn = (FloatingActionButton)view.findViewById(R.id.advanced_options_button);
        advancedOptionsBtn.setOnClickListener(this);

    }
    //endregion

    private void displayMessage(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, STORE_IMAGE_OPTION_ID, Menu.NONE, R.string.store_image_context_menu_item_text);
        menu.add(Menu.NONE, STORE_IMAGE_TO_GALLERY_OPTION_ID, Menu.NONE, R.string.store_image_to_gallery_context_menu_option_text);
        menu.add(Menu.NONE, UPLOAD_FROM_GALLERY_OPTION_ID, Menu.NONE, R.string.upload_image_from_gallery_context_item_text);
        menu.add(Menu.NONE, SHARE_IMAGE_OPTION_ID, Menu.NONE, R.string.share_image_option_text);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case STORE_IMAGE_OPTION_ID:
                storeImage();
                return true;
            case STORE_IMAGE_TO_GALLERY_OPTION_ID:
                storeImageToGallery();
                return true;
            case UPLOAD_FROM_GALLERY_OPTION_ID:
                sendUploadMessage();
                return true;
            case SHARE_IMAGE_OPTION_ID:
                shareImage();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void sendUploadMessage() {
        Intent intent = new Intent(ContainerScreenActivity.BROADCAST_RECEIVER_IDENTIFIER);
        intent.putExtra(ContainerScreenActivity.EXTRA_UPLOAD_IMAGE_KEY, ContainerScreenActivity.EXTRA_UPLOAD_IMAGE_KEY);
        if((status.equals(EXTRA_EXIST_MOVIE_TO_EDIT))||(status.equals(EXTRA_WEB_MOVIE_TO_ADD))){
            intent.putExtra(IS_MOVIE_EXIST_KEY,IS_MOVIE_EXIST_POSITIVE_VALUE);
            intent.putExtra(MOVIE_TO_PASS_KEY,movie);
            intent.putExtra(SOURCE_NAME_CODE,sourceFragmentName);
            intent.putExtra(EXTRA_MOVIE_TARGET_KEY,status);
        }
        else {
            intent.putExtra(IS_MOVIE_EXIST_KEY,IS_MOVIE_EXIST_NEGATIVE_VALUE);
        }
        LocalBroadcastManager.getInstance(getActivity())
                .sendBroadcast(intent);
    }

    private void storeImageToGallery() {
        if((movie!= null)&&(posterImageView.getDrawable() != null)){
            Utilities.storeImageToGallery(getActivity(), posterImageView, movie);
        }
    }

    //region STORE IMAGE FUNCTION
    private void storeImage(){


        String data = urlEditText.getText().toString();
        if(!data.equals(Constants.EMPTY_STRING)){

            if(Utilities.isValidURL(data)){

                String fileName = Utilities.getFileNameFromURL(data);
                storeImageByFileName(fileName);

            }
            else if(Utilities.isValidImageName(data)){
                storeImageByFileName(data);
            }
            else {
                //Toast.makeText(this,R.string.invalid_url_message,Toast.LENGTH_SHORT).show();
                Utilities.makeImageToast(getActivity(),R.drawable.ic_report_24dp,R.string.invalid_image_url_message,Toast.LENGTH_SHORT).show();

            }
        }
        else {
            //Toast.makeText(this,R.string.empty_url_image_message,Toast.LENGTH_SHORT).show();
            Utilities.makeImageToast(getActivity(),R.drawable.ic_report_24dp,R.string.empty_url_image_message,Toast.LENGTH_SHORT).show();
        }

    }
    //endregion
    //region STORE IMAGE BY FILE NAME
    private void storeImageByFileName(String fileName){

        //check if image not store already
        if((fileName != null)&&(posterImageView.getDrawable()!= null)) {
            Bitmap bitmap = ((BitmapDrawable) posterImageView.getDrawable()).getBitmap();
            //check if image exist in image view
            if (bitmap == null) {
                // Toast.makeText(this, R.string.empty_image_message, Toast.LENGTH_SHORT).show();
                Utilities.makeImageToast(getActivity(),R.drawable.ic_report_24dp,R.string.empty_image_message,Toast.LENGTH_SHORT).show();
                return;
            }
            //store image and his name in preferences
            if(Utilities.isFilePresent(fileName,getActivity())){

                Utilities.makeImageToast(getActivity(),R.drawable.ic_report_24dp,R.string.already_stored_image_message,Toast.LENGTH_SHORT).show();
                return;
            }
            Utilities.storeImage(bitmap, fileName,getActivity());
            urlEditText.setText(fileName);
            Utilities.makeImageToast(getActivity(),R.drawable.ic_report_24dp,R.string.warning_to_store_data_text,Toast.LENGTH_SHORT).show();
        }
        else if(posterImageView.getDrawable() == null){
            //Toast.makeText(this,R.string.empty_image_message,Toast.LENGTH_SHORT).show();
            Utilities.makeImageToast(getActivity(),R.drawable.ic_report_24dp,R.string.empty_image_message,Toast.LENGTH_SHORT).show();
        }
        else if(fileName == null){
            //Toast.makeText(this,R.string.empty_file_name_message,Toast.LENGTH_SHORT).show();
            Utilities.makeImageToast(getActivity(),R.drawable.ic_report_24dp,R.string.empty_file_name_message,Toast.LENGTH_SHORT).show();
        }

    }
    //endregion

    private void shareImage() {

        Bitmap bitmap;
        if(posterImageView.getDrawable() != null){
            bitmap = ((BitmapDrawable)posterImageView.getDrawable()).getBitmap();
            if(bitmap != null){
                shareImageListener.onSharedBitmapReceived(bitmap);
            }
            else {
                Utilities.makeImageToast(getActivity(),R.drawable.ic_report_24dp,R.string.empty_share_image_message,Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Utilities.makeImageToast(getActivity(),R.drawable.ic_report_24dp,R.string.empty_share_image_message,Toast.LENGTH_SHORT).show();
        }

    }


}

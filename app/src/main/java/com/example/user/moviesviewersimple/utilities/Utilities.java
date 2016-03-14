package com.example.user.moviesviewersimple.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.moviesviewersimple.ContainerScreenActivity;
import com.example.user.moviesviewersimple.R;
import com.example.user.moviesviewersimple.data.Movie;
import com.example.user.moviesviewersimple.fragments.DBSearchFragment;
import com.example.user.moviesviewersimple.fragments.MoviesListFragment;
import com.example.user.moviesviewersimple.fragments.WebSearchFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by User on 09/02/2016.
 */
public class Utilities {

    //region Constants
    private static final String IMAGE_PATTERN =
            "([^\\s]+(\\.(?i)(jpg|png|gif|bmp|jpeg))$)";

    private static final String TAG = "Utilities";

    private static final String CAMERA_LOCATION_STRING = "DCIM/Camera";

    private static final int RADIUS = 70;

    private static final int MARGIN = 10;
    //endregion

    //region service functions

    //region image name validator

    /**
     * This function validate image name
     */
    public static boolean isValidImageName(String imageName) {

        Pattern pattern = Pattern.compile(IMAGE_PATTERN);
        Matcher matcher = pattern.matcher(imageName);
        return matcher.matches();
    }
    //endregion

    //region image url validator

    /**
     * This function check if image url is valid
     */
    public static boolean isValidURL(CharSequence input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        Pattern URL_PATTERN = Patterns.WEB_URL;
        boolean isURL = URL_PATTERN.matcher(input).matches();
        if (!isURL) {
            String urlString = input + "";
            if (URLUtil.isNetworkUrl(urlString)) {
                try {
                    new URL(urlString);
                    isURL = true;
                } catch (Exception e) {
                }
            }
        }
        return isURL;
    }
    //endregion

    //region unique int id generator

    /**
     * This function generate unique identifier
     */
    public static int generateUniqueID() {

        String idStr = UUID.randomUUID().toString();
        int id = idStr.hashCode();
        id = Math.abs(id);
        return id;
    }
    //endregion

    //region GET FILE NAME FROM URL
    public static String getFileNameFromURL(String url) {
        String fileNameWithExtension = null;
        if (URLUtil.isValidUrl(url)) {
            fileNameWithExtension = URLUtil.guessFileName(url, null, null);
        }
        return fileNameWithExtension;
    }
    //endregion

    //region SET IMAGE TO IMAGE VIEW WITH MOVIE OBJECT
    public static void setImageToImageView(Movie movie, ImageView imageView, Context context) {


        //check if movie not equals null
        if (movie != null) {

            String data = movie.getImageUrl();

            //check if image url field is url
            if (isValidURL(data)) {

                //load image from url from web
                Picasso.with(context)
                        .load(data)
                                //.transform(new RoundedTransformation(RADIUS,MARGIN))//radius,margin
                        .into(imageView);

            } //image loaded and load it from application
            else if (isValidImageName(data)) {

                File imgFile = new File(movie.getImageUrl().trim());
                if (imgFile.exists()) {

                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    imageView.setImageBitmap(myBitmap);

                } else {
                    //internal path to load
                    String filePath = Environment.getExternalStorageDirectory()
                            + "/Android/data/"
                            + context.getApplicationContext().getPackageName()
                            + "/Images/" + data;
                    Bitmap bmp = BitmapFactory.decodeFile(filePath);
                    //set image to image view
                    imageView.setImageBitmap(bmp);
                }


            }
        }
    }
    //endregion

    //region SET IMAGE TO IMAGE VIEW WITH URL
    public static boolean setImageToImageView(String url, ImageView imageView, Context context) {


        //check if movie not equals null
        if (url != null) {

            //check if image url field is url
            if (isValidURL(url)) {

                //load image from url from web
                Picasso.with(context)
                        .load(url)
                                //.transform(new RoundedTransformation(RADIUS, MARGIN))//radius,margin
                        .into(imageView);
                return true;
            }
            else {
                return false;
            }

        } //image loaded and load it from application
        else {
            return false;
        }


    }

    //endregion

    //region STORE IMAGE FUNCTIONS

    /**
     * This function store image in internal storage
     * @param image - file to store
     * @param fileName - name of file
     * @param context - context of activity
     */
    public static void storeImage(Bitmap image,String fileName,Context context) {
        File pictureFile = getOutputMediaFile(fileName,context);
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }catch (OutOfMemoryError e) {
            Log.d(TAG, "Image is too large. choose other " + e.getMessage());

        }
    }

    private static File getOutputMediaFile(String fileName,Context context){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/Images");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return mediaFile;
    }
    //endregion

    //region CHECK IF IMAGE FILE ALREADY EXIST
    public static boolean isFilePresent(String fileName,Context context) {
        String filePath = Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/Images/" + fileName;
        File file = new File(filePath);
        return file.exists();
    }
    //endregion

    //region HIDE VIRTUAL KEYBOARD METHOD
    public static void hide_keyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if(view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    //endregion

    /*
    //region RETURN INTENT TO EDIT
    public static Intent getIntentFroMovie(Movie movie,Context context){
        //create intent
        Intent intent = new Intent(context,StoreMovieActivity.class);

        //set data
        intent.putExtra(DataConstants.ID,movie.getId());
        intent.putExtra(DataConstants.SUBJECT,movie.getSubject());
        intent.putExtra(DataConstants.BODY,movie.getBody());
        intent.putExtra(DataConstants.IMAGE_URL,movie.getImageUrl());
        intent.putExtra(DataConstants.RATE,movie.getRate());
        intent.putExtra(DataConstants.WATCHED, movie.isWatched());
        intent.putExtra(DataConstants.YEAR,movie.getYear());

        //return intent
        return intent;
    }
    //endregion */

    //region CREATE TOAST WITH IMAGE
    public static Toast makeImageToast(Context context, int imageResId,int textResId, int length) {
        Toast toast = Toast.makeText(context,textResId, length);

        View rootView = toast.getView();
        LinearLayout linearLayout = null;
        View messageTextView = null;

        // check (expected) toast layout
        if (rootView instanceof LinearLayout) {
            linearLayout = (LinearLayout) rootView;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                linearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

            if (linearLayout.getChildCount() == 1) {
                View child = linearLayout.getChildAt(0);

                if (child instanceof TextView) {
                    messageTextView = (TextView) child;
                }
            }
        }

        // cancel modification because toast layout is not what we expected
        if (linearLayout == null || messageTextView == null) {
            return toast;
        }

        ViewGroup.LayoutParams textParams = messageTextView.getLayoutParams();
        ((LinearLayout.LayoutParams) textParams).gravity = Gravity.CENTER_VERTICAL;

        // convert dip dimension
        float density = context.getResources().getDisplayMetrics().density;
        int imageSize = (int) (density * 25 + 0.5f);
        int imageMargin = (int) (density * 15 + 0.5f);

        // setup image view layout parameters
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);
        imageParams.setMargins(0, 0, imageMargin, 0);
        imageParams.gravity = Gravity.CENTER_VERTICAL;

        // setup image view
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(imageResId);
        imageView.setLayoutParams(imageParams);

        // modify root layout
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(imageView, 0);

        return toast;
    }
    //endregion

    //region SHARE BITMAP IMAGE
    public static void shareImageFromBitmap(Activity activity,Bitmap bitmap)
            throws Exception {


        if(bitmap != null){
            String pathofBmp = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap,"title", null);
            Uri bmpUri = Uri.parse(pathofBmp);
            final Intent emailIntent1 = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri);
            emailIntent1.setType("image/png");
            activity.startActivity(emailIntent1);
            } else {

                throw new Exception();

            }
    }

    //region VALIDATE YEAR
    public static boolean isValidYear(String year){

        boolean isValidYear = true;
        String date = year + "-03-10";
        isValidYear = isValidDate(date);
        return isValidYear;
    }
    private static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }
    //endregion

    //region SORTING FUNCTIONS
    public static void setMoviesOrder(List<Movie> list,Context context){
        int orderValue = getOrderValue(context);
        switch(orderValue){
            case Constants.EMPTY_ORDER_CODE:
            case Constants.BY_INSERT_ORDER_CODE:
                break;
            case Constants.SUBJECT_ORDER_CODE:
                sortMoviesListBySubject(list);
                break;
            case Constants.ASCENDING_YEAR_CODE:
                sortMoviesListByYear(list,Constants.ASC_YEAR_ORDER);
                break;
            case Constants.DESCENDING_YEAR_CODE:
                sortMoviesListByYear(list,Constants.DESC_YEAR_ORDER);
                break;
        }

    }
    private static int getOrderValue(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String key = null;
        String valueStr;
        int value;
        if(context instanceof ContainerScreenActivity){

            AppCompatActivity activity = (AppCompatActivity)context;
            Fragment fragment = activity.getSupportFragmentManager().findFragmentById(R.id.container);
            //get preferences key
            if(fragment instanceof MoviesListFragment){
                key = context.getString(R.string.movies_list_order_list_key);
            }
            else if(fragment instanceof WebSearchFragment){
                key = context.getString(R.string.web_search_order_list_key);
            }
            else if(fragment instanceof DBSearchFragment){
                key = context.getString(R.string.db_search_order_list_key);
            }
            //return value of order preferences list
            if(key.equals(Constants.EMPTY_STRING)){
                return Constants.EMPTY_ORDER_CODE;
            }
            else {
                valueStr = prefs.getString(key, Constants.BY_INSERT_ORDER_CODE_STRING);
                value = Integer.parseInt(valueStr);
                return value;
            }
        }
        else {
            return Constants.EMPTY_ORDER_CODE;
        }


    }
    private static void sortMoviesListBySubject(List<Movie>list){

        Comparator<Movie> comparator = new Comparator<Movie>() {
            @Override
            public int compare(Movie lhs, Movie rhs) {
                return lhs.getSubject().compareToIgnoreCase(rhs.getSubject());
            }
        };
        Collections.sort(list, comparator);
    }

    private static void sortMoviesListByYear(List<Movie>list, final String yearOrder){
        Comparator<Movie>comparator = new Comparator<Movie>() {
            @Override
            public int compare(Movie lhs, Movie rhs) {
                int lYear = Integer.parseInt(lhs.getYear());
                int rYear = Integer.parseInt(rhs.getYear());
                return yearOrder.equals(Constants.DESC_YEAR_ORDER)? rYear - lYear:lYear - rYear;
            }
        };
        Collections.sort(list,comparator);
    }
    //endregion

    //region Gallery Storage Function
    public static void storeImageToGallery(Context context,ImageView imageView,Movie movie) {

        fixMediaDir();
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap,movie.getSubject(),movie.getBody());
    }

    public static void fixMediaDir() {
        File sdcard = Environment.getExternalStorageDirectory();
        if (sdcard != null) {
            File mediaDir = new File(sdcard, CAMERA_LOCATION_STRING);
            if (!mediaDir.exists()) {
                mediaDir.mkdirs();
            }
        }
    }

    //endregion
    //endregion

}

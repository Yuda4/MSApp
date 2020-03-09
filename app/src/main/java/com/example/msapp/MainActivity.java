package com.example.msapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    CoordinatorLayout mainLayout;
    FloatingActionButton fab;
    MovieAdapter movieAdapter;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 122;
    private int CAMERA_REQUEST = 100;
    String url = "";
    String link;
    SQLiteDatabase db;
    DatabaseHelper dbHelper;

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab_btn);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Camerapermission();
                try {
                    scanBarcode(v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showFloatingActionButton() {
        fab.show();
    };

    public void hideFloatingActionButton() {
        fab.hide();
    };

    // go to QR scanner
    public void scanBarcode(View view){
        Intent intent = new Intent(this, ScanBarcodeActivity.class);
        startActivityForResult(intent,CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mainLayout = findViewById(R.id.mainLayout);
        if(requestCode == CAMERA_REQUEST){
            if(resultCode == CommonStatusCodes.SUCCESS){
                if(data != null){

                    // getting scanned QR code data from scan barcode activity
                    Barcode barcode = data.getParcelableExtra("barcode");

                    Gson g = new Gson();
                    Movie movie = g.fromJson(barcode.displayValue, Movie.class);

                    dbHelper = new DatabaseHelper(this);
                    db = dbHelper.getWritableDatabase();

                    // checking in database if the movie exists
                    boolean isExists = dbHelper.Exists(db, movie.getTitle());

                    // movie is not exists in database
                    if(!isExists){
                        String genre = movie.getGenre().get(0);
                        if(movie.getGenre().size() > 1){
                            for(int i = 1; i < movie.getGenre().size(); i++)
                                genre = genre + ", " + movie.getGenre().get(i);
                        }

                        boolean extensionIsImage = checkExtension(url);
                        url = movie.getImage();
                        String year = Integer.toString(movie.getReleaseYear());
                        String rating = Double.toString(movie.getRating());

                        // if url's extension is not an image
                        if(!extensionIsImage) {
                            FetchMetadataFromURL fetch = new FetchMetadataFromURL();
                            fetch.execute(movie.getTitle(), url, rating, year, genre);

                        }else{
                            setMovie( movie.getTitle(),  movie.getImage(),year);
                            dbHelper.insertData(movie.getTitle(), url, movie.getRating() , movie.getReleaseYear(), genre);
                        }
                        movieAddedSnackbar(movie.getTitle());
                    }
                    // movie is exists in database
                    else{
                        movieExistsSnackbar();
                    }

                }else{
                    Toast.makeText(MainActivity.this, "No barcode Found", Toast.LENGTH_SHORT).show();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // camera permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
            return ;
        }
    }

    private void Camerapermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
            return ;
        }

    }

    public void setMovieAdapter(MovieAdapter movieAdap){
        movieAdapter = movieAdap;
    }

    public void movieExistsSnackbar(){
        Snackbar.make(mainLayout, "Current movie already exist in the Database", Snackbar.LENGTH_LONG)
                .setAction("close", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setActionTextColor(getResources().getColor(R.color.colorAccent))
                .show();
    }

    public void movieAddedSnackbar(String movie_title){
        Snackbar.make(mainLayout, movie_title + " is inserted to the Database", Snackbar.LENGTH_LONG)
                .setAction("close", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setActionTextColor(getResources().getColor(R.color.colorAccent))
                .show();
    }

    private void onBackgroundTaskDataObtained(String results) {
        link = results;
    }

    private void setMovie(final String title, final String image, final String year){
        // Runs a specified action on the UI thread, If the current thread is the UI thread - then the action is executed immediately.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setMovieAdapter(movieAdapter);
                movieAdapter.addMovie(title, image, year);
            }
        });
    }

    // Fetch image from a url - if the given image in json is without an extension
    private class FetchMetadataFromURL extends AsyncTask<String, Integer, String> {
        String websiteTitle, websiteDescription, imgurl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                // Connect to website
                Document document = Jsoup.connect(url).get();
                // Get the html document title
                websiteTitle = document.title();

                Elements metaElems = document.select("meta");
                // Locate the content attribute
                websiteDescription = metaElems.attr("content");
                Elements metaOgImage = document.select("meta[property=og:image]");

                if (metaOgImage != null) {
                    imgurl = metaOgImage.first().attr("content");

                    Double rate = Double.parseDouble(params[2]);
                    int year = Integer.parseInt(params[3]);

                    dbHelper.insertData(params[0], imgurl, rate , year, params[3]);
                    setMovie(params[0],imgurl, params[3]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            MainActivity.this.onBackgroundTaskDataObtained(result);
        }



    }

    public boolean checkExtension(String pathname) {
        String extension = "";
        if(pathname.contains(".")) {
            extension = pathname.substring(url.lastIndexOf(".") +1);
        }
        if (extension == null)
            return false;
        else if (!extension.equals("jpg") && !extension.equals("jpeg") && !extension.equals("png") && !extension.equals("gif"))
            return false;
        else
            return true;
    }

}



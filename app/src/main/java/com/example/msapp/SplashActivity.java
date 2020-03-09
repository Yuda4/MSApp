package com.example.msapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {

    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        myDb = new DatabaseHelper(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // Convert Java Objects into JSON representation
                .build();

        Api api = retrofit.create(Api.class);

        // Get all movies from Link
        Call<List<Movie>> call = api.getMovies();

        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                // Storing all movies in a list of movies
                List<Movie> movies_from_url = response.body();
                String genre = "";
                for (Movie movie : movies_from_url){
                    genre = movie.getGenre().get(0);
                    if(movie.getGenre().size() > 1){
                        for(int i = 1; i < movie.getGenre().size(); i++)
                            genre = genre + ", " + movie.getGenre().get(i);
                    }
                    myDb.insertData(movie.getTitle(), movie.getImage(), movie.getRating(), movie.getReleaseYear(), genre);
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 2500);
    }
}

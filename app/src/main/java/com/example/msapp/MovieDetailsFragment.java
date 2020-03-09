package com.example.msapp;

import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class MovieDetailsFragment extends Fragment {

    DatabaseHelper myDb;
    TextView title, releasedYear, genre, rating;
    ImageView image;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View itemView = inflater.inflate(R.layout.fragment_movie_details,container, false);

        ((MainActivity) getActivity()).hideFloatingActionButton(); // hide Floating Action Button

        title = (TextView)itemView.findViewById(R.id.title_detail_txt);
        releasedYear = (TextView)itemView.findViewById(R.id.released_year_detail_txt);
        image = (ImageView)itemView.findViewById(R.id.image_detail_view);
        genre = (TextView)itemView.findViewById(R.id.genre_detail_txt);
        rating = (TextView)itemView.findViewById(R.id.rating_detail_txt);

        myDb = new DatabaseHelper(inflater.getContext());

        // Getting movie title from bundle in MovieAdapter
        String title_movie = getArguments().getString("message");

        // SQL query for fetching movie details by his title
        Cursor res = myDb.getMovieDetails(title_movie);

        // Inserting data to views
        title.setText(res.getString(0));
        Picasso.get().load(res.getString(1)).into(image);
        image.setClipToOutline(true); //making rounded edges for image
        rating.setText(res.getString(2));
        releasedYear.setText(res.getString(3));
        genre.setText(res.getString(4));

        return itemView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // show Floating Action Button after closing movie details fragment
        ((MainActivity) getActivity()).showFloatingActionButton();
    }

}
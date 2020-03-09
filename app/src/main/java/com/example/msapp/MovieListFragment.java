package com.example.msapp;

import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class MovieListFragment extends Fragment {
    DatabaseHelper myDb;
    RecyclerView recyclerView;
    MovieAdapter movieAdapter;

    List<String> titles = new ArrayList<>();
    List<String> images = new ArrayList<>();
    List<String> releasedYears = new ArrayList<>();;

    public MovieListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        myDb = new DatabaseHelper(inflater.getContext());

        recyclerView = view.findViewById(R.id.recyclerView);
        Cursor res = myDb.getAllDataNewToOld();

        while(res.moveToNext()){
            titles.add(res.getString(0));
            images.add(res.getString(1));
            releasedYears.add(res.getString(3));
        }

        movieAdapter = new MovieAdapter(inflater.getContext(),titles, images, releasedYears);
        recyclerView.setAdapter(movieAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager( inflater.getContext()));
        MainActivity main = (MainActivity) getActivity();
        main.setMovieAdapter(movieAdapter);
        return view;
    }

}
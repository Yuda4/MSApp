package com.example.msapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    List<String> titles = new ArrayList<>();;
    List<String> images = new ArrayList<>();;
    List<String> releasedYears = new ArrayList<>();;
    Context context;

    public MovieAdapter(Context cont, List<String> title, List<String> img, List<String> releasedYear ){
        context = cont;
        titles = title;
        releasedYears = releasedYear;
        images = img;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_row, parent, false);
        return new MovieViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, final int position) {
        holder.title.setText(titles.get(position));
        holder.releasedYear.setText(releasedYears.get(position));
        Picasso.get().load(images.get(position)).into(holder.image);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title_movie = titles.get(position).toString();
                Bundle bundle = new Bundle();
                bundle.putString("message", title_movie); // sending title movie to movie details fragment

                MovieDetailsFragment fragment = new MovieDetailsFragment(); // Movie details fragment
                FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager(); // instantiate my view context
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.movie_list_fragment, fragment);// my container and fragment

                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                fragment.setArguments(bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public void addMovie(String title, String image, String year ){
        titles.add(title);
        images.add(image);
        releasedYears.add(year);
        notifyDataSetChanged();

    }


    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title, releasedYear;
        ImageView image;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.title_txt);
            releasedYear = itemView.findViewById(R.id.released_year_txt);
            image = itemView.findViewById(R.id.image_view);

            image.setClipToOutline(true);

        }

        @Override
        public void onClick(View view) {

        }
    }

}

package com.jdbdemo.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jdbdemo.R;
import com.jdbdemo.fragments.HomeFragment;
import com.jdbdemo.fragments.MovieDetail;
import com.jdbdemo.pojo.MoviesContainer;

public class MoviesAdapter extends BaseAdapter {

    Context context;
    private List<MoviesContainer> data;
    private static LayoutInflater inflater = null;
    private String TAG = getClass().getName();
    HomeFragment homeFragment1;

    public MoviesAdapter(Context a, List<MoviesContainer> d, HomeFragment homeFragment) {
        context = a;
        data = d;
        homeFragment1 = homeFragment;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.item_row, null);

        TextView txttitle = (TextView) vi.findViewById(R.id.txttitle); // title
        TextView txtreleaseYear = (TextView) vi.findViewById(R.id.txtreleaseYear); // artist name
        TextView txtrating = (TextView) vi.findViewById(R.id.txtrating); // duration
        ImageView imgmovie = (ImageView) vi.findViewById(R.id.imgmovie); // thumb image
        ImageView img_edit = (ImageView) vi.findViewById(R.id.img_edit);


        final MoviesContainer movie = data.get(position);

        try {
            if (movie.getImage() != null) {
                Glide.with(context).load(movie.image.toString())
                        .error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher)
                        .into(imgmovie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Setting all values in listview
        txttitle.setText("Movie Name : " + movie.title);
        txtreleaseYear.setText("Release year : " + String.valueOf(movie.releaseYear));
        txtrating.setText("Ratting : " + movie.getRating());
        img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "selected item-->" + movie.id);
                FragmentTransaction transaction = homeFragment1.getFragmentManager().beginTransaction();
                MovieDetail movieDetail = new MovieDetail();
                Bundle bundle = new Bundle();
                bundle.putBoolean("is_forEdit", true);
                bundle.putString("movieID", movie.id);
                movieDetail.setArguments(bundle);
                transaction.add(R.id.maincointener, movieDetail, "MovieDetail");
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        return vi;
    }
}
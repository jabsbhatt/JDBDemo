package com.jdbdemo.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jdbdemo.R;
import com.jdbdemo.adapter.MoviesAdapter;
import com.jdbdemo.pojo.MoviesContainer;
import com.jdbdemo.sqlite.MoviesDBHelper;
import com.jdbdemo.utils.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private ProgressDialog pDialog;
    ListView list_movies;
    private static String url = "https://api.androidhive.info/json/movies.json";
    private String TAG = getClass().getName();
    List<MoviesContainer> moviesContainerList;
    List<String> stringList;
    MoviesDBHelper moviesDBHelper;
    List<MoviesContainer> movies;
    MoviesAdapter moviesAdapter;
    public static boolean isFromSQLite = false;
    MoviesContainer moviesContainer = new MoviesContainer();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);
        initView(view);


        movies = moviesDBHelper.getAllMovieList();
        if (movies.size() == 0) {
            new GetMovieList().execute();
        } else {
            showMovieList(movies);
//            isFromSQLite = true;
        }


        return view;


    }

    private void initView(View view) {
        list_movies = (ListView) view.findViewById(R.id.list_movies);
        moviesDBHelper = new MoviesDBHelper(getActivity());
        moviesContainerList = new ArrayList<>();
        movies = new ArrayList<>();
        stringList = new ArrayList<>();

        list_movies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (movies.size() != 0) {
                    Log.e(TAG, "selected item-->" + movies.get(position).id);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    MovieDetail movieDetail = new MovieDetail();
                    Bundle bundle = new Bundle();
                    bundle.putString("movieID", movies.get(position).id);
                    movieDetail.setArguments(bundle);
                    transaction.add(R.id.maincointener, movieDetail, "MovieDetail");
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
//                else {
//                    Log.e(TAG, "selected item-->" + moviesContainerList.get(position + 1).id);
//                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                    MovieDetail movieDetail = new MovieDetail();
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("movieID", moviesContainerList.get(position + 1).id);
//                    movieDetail.setArguments(bundle);
//                    transaction.add(R.id.maincointener, movieDetail, "MovieDetail");
//                    transaction.addToBackStack(null);
//                    transaction.commit();
//                }

            }
        });
        list_movies.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                deleteDialog(position);
                return false;
            }
        });
    }

    private void deleteDialog(final int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Remove Movie");
        if (movies.size() != 0) {
            moviesContainer = movies.get(position);
            alertDialog.setMessage("" + movies.get(position).getTitle());
            alertDialog.setButton2("Remove", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    movies.remove(position);//or equalalent of remove method
                    moviesAdapter.notifyDataSetChanged();
                    moviesDBHelper.deleteMovie(moviesContainer);

                }
            });
        }
//        else {
//            moviesContainer = moviesContainerList.get(position + 1);
//            alertDialog.setMessage("" + moviesContainerList.get(position + 1).getTitle());
//            alertDialog.setButton2("Remove", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    moviesContainerList.remove(position + 1);//or equalalent of remove method
//                    moviesAdapter.notifyDataSetChanged();
//                    moviesDBHelper.deleteMovie(moviesContainer);
//                    Toast.makeText(getActivity(), R.string.record_removed, Toast.LENGTH_SHORT).show();
//
//                }
//            });
//        }

        alertDialog.show();
    }

    public class GetMovieList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Getting movie list...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {


                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        MoviesContainer moviesContainer = new MoviesContainer();
                        moviesContainer.setId(String.valueOf(i));
                        moviesContainer.setTitle(jsonObject.getString("title"));
                        moviesContainer.setImage(jsonObject.getString("image"));
                        moviesContainer.setRating(jsonObject.getDouble("rating"));
                        moviesContainer.setReleaseYear(jsonObject.getInt("releaseYear"));

                        JSONArray jsonArray1 = jsonObject.getJSONArray("genre");


                        for (int j = 0; j < jsonArray1.length(); j++) {
                            String value = jsonArray1.getString(j);
                            stringList.add(value);
                        }

                        moviesContainer.setGenre(stringList);
                        moviesContainerList.add(moviesContainer);
                    }

                    storetoMoviesDatabase(moviesContainerList);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            showMovieList(moviesContainerList);
        }

    }

    private void showMovieList(List<MoviesContainer> movielist) {
        if (movielist.size() != 0) {
            moviesAdapter = new MoviesAdapter(getActivity(), movielist, HomeFragment.this);
            list_movies.setAdapter(moviesAdapter);
        }

    }

    private void storetoMoviesDatabase(List<MoviesContainer> moviesContainerList) {

        for (int i = 0; i < moviesContainerList.size(); i++) {
            MoviesContainer moviesContainer = new MoviesContainer();
            moviesContainer.setId("" + i);
            moviesContainer.setTitle(moviesContainerList.get(i).getTitle());
            moviesContainer.setImage(moviesContainerList.get(i).getImage());
            moviesContainer.setRating(moviesContainerList.get(i).getRating());
            moviesContainer.setReleaseYear(moviesContainerList.get(i).getReleaseYear());
            moviesContainer.setGenre(moviesContainerList.get(i).getGenre());
            moviesDBHelper.addMovie(moviesContainer);
        }

    }
}


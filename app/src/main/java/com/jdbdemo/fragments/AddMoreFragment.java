package com.jdbdemo.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jdbdemo.R;
import com.jdbdemo.pojo.MoviesContainer;
import com.jdbdemo.sqlite.MoviesDBHelper;
import com.jdbdemo.utils.RealPathUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddMoreFragment extends Fragment {

    ImageView profilepic, profilepicdiff;
    EditText edit_title, edit_category, edit_description;
    RatingBar rtbHighScore;
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int PICK_FROM_FILE = 3;
    private Uri mImageCaptureUri;
    private Bitmap profile_imag = null;
    TextView txtAddPhoto, txtDone;
    String ratedValue;
    private String TAG = getClass().getName();
    MoviesDBHelper moviesDBHelper;
    List<String> stringList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_more, null);
        initView(view);


        return view;

    }

    private void initView(View view) {
        stringList = new ArrayList<>();
        moviesDBHelper = new MoviesDBHelper(getActivity());
        profilepic = (ImageView) view.findViewById(R.id.profilepic);
        profilepicdiff = (ImageView) view.findViewById(R.id.profilepicdiff);
        edit_title = (EditText) view.findViewById(R.id.edit_title);
        edit_category = (EditText) view.findViewById(R.id.edit_category);
        edit_description = (EditText) view.findViewById(R.id.edit_description);
        rtbHighScore = (RatingBar) view.findViewById(R.id.rtbHighScore);
        txtAddPhoto = (TextView) view.findViewById(R.id.txtAddPhoto);
        txtDone = (TextView) view.findViewById(R.id.txtDone);

        txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_title.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Title Empty", Toast.LENGTH_SHORT).show();
                } else if (edit_category.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Category Empty", Toast.LENGTH_SHORT).show();
                } else if (edit_description.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Description Empty", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "image Url->" + mImageCaptureUri.toString());
                    storeMovieInDatabase(mImageCaptureUri.toString(), edit_title.getText().toString().trim(), edit_category.getText().toString(), edit_description.getText().toString());
                }
            }
        });
        rtbHighScore.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratedValue = String.valueOf(ratingBar.getRating());
                Log.e(TAG, "rating value-->" + ratedValue);
            }
        });
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
//        if (ContextCompat.checkSelfPermission(getActivity(),
//                Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.CAMERA}, 2);
//        }
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(
                            Intent.createChooser(intent, "Select Picture"),
                            PICK_IMAGE_REQUEST);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                }
            }
        });
    }

    private void storeMovieInDatabase(String path, String title, String category, String description) {
        String value = "Action";
        stringList.add(value);
        MoviesContainer moviesContainer = new MoviesContainer();
        moviesContainer.setTitle(title);
        moviesContainer.setImage(path);
        moviesContainer.setRating(Double.valueOf(ratedValue));
        moviesContainer.setGenre(stringList);
        moviesContainer.setReleaseYear(2017);


        moviesDBHelper.addMovie(moviesContainer);

    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri originalUri = null;
            if (Build.VERSION.SDK_INT < 19) {
                originalUri = data.getData();
            } else {
                originalUri = data.getData();
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                // Check for the freshest data.
                getActivity().getContentResolver().takePersistableUriPermission(originalUri,
                        takeFlags);
            }
            mImageCaptureUri = Uri
                    .fromFile(new File(uriToFilename(originalUri)));
            doCropCircle(mImageCaptureUri);

        } else if (requestCode == PICK_FROM_FILE && resultCode == Activity.RESULT_OK) {
            final Bundle extras_pickfile = data.getExtras();
            profile_imag = extras_pickfile.getParcelable("data");
            if (profile_imag != null) {
                profilepicdiff.setImageBitmap(profile_imag);
                profilepicdiff.setVisibility(View.VISIBLE);
                txtAddPhoto.setVisibility(View.GONE);
                profilepic.setVisibility(View.GONE);
            }
        }

    }

    private String uriToFilename(Uri uri) {
        String path = null;
        if (Build.VERSION.SDK_INT < 11) {
            path = RealPathUtil.getRealPathFromURI_BelowAPI11(
                    getActivity(), uri);
        } else if (Build.VERSION.SDK_INT < 19) {
            path = RealPathUtil.getRealPathFromURI_API11to18(
                    getActivity(), uri);
        } else {
            path = RealPathUtil.getRealPathFromURI_API19(getActivity(),
                    uri);
        }

        return path;
    }

    private void doCropCircle(Uri uri) {

        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("outputX", 256);
            intent.putExtra("outputY", 256);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // intent.putExtra("scale", true);
            intent.putExtra("crop", true);
            intent.putExtra("circleCrop", "true");
            intent.putExtra("return-data", true);
            startActivityForResult(intent, PICK_FROM_FILE);
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                return;
            }

            default:
                break;
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}

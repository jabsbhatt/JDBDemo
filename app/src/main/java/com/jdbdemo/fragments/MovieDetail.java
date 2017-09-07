package com.jdbdemo.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
public class MovieDetail extends Fragment {

    ImageView img;
    EditText edit_title, edit_description;
    private String TAG = getClass().getName();
    MoviesDBHelper moviesDBHelper;
    Button btn_update;
    String movieId, title, description, image;
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int PICK_FROM_FILE = 3;
    private Uri mImageCaptureUri, defaultImageCaptureUri;
    private Bitmap profile_imag = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_moviedetail, null);

        initView(view);
        performTask();


        return view;

    }

    private void performTask() {
        Bundle b = getArguments();
        Log.e(TAG, "movieID-->" + b.getString("movieID"));


        if (b.getBoolean("is_forEdit")) {
            movieId = b.getString("movieID");
            getDataAndSetItems(movieId);
            edit_title.setEnabled(true);
            edit_description.setEnabled(true);
            img.setEnabled(true);
            btn_update.setVisibility(View.VISIBLE);
            btn_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (mImageCaptureUri == null) {
                            if (moviesDBHelper.updateContact(Integer.parseInt(movieId), defaultImageCaptureUri.toString(), edit_title.getText().toString().trim(), edit_description.getText().toString().trim())) {
                                Toast.makeText(getActivity(), R.string.record_updated, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), R.string.record_not_updated, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (moviesDBHelper.updateContact(Integer.parseInt(movieId), mImageCaptureUri.toString(), edit_title.getText().toString().trim(), edit_description.getText().toString().trim())) {
                                Toast.makeText(getActivity(), R.string.record_updated, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), R.string.record_not_updated, Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (NullPointerException e) {
                        Log.e(TAG, "NullpointerException-->" + e.getMessage());
                    }


                }
            });
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
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
        } else {
            movieId = b.getString("movieID");
            getDataAndSetItems(movieId);
            edit_title.setEnabled(false);
            edit_description.setEnabled(false);
            img.setEnabled(false);
            btn_update.setVisibility(View.GONE);
        }
    }


    private void initView(View view) {
        moviesDBHelper = new MoviesDBHelper(getActivity());
        img = (ImageView) view.findViewById(R.id.img);
        edit_title = (EditText) view.findViewById(R.id.edit_title);
        edit_description = (EditText) view.findViewById(R.id.edit_description);
        btn_update = (Button) view.findViewById(R.id.btn_update);

    }

    public void getDataAndSetItems(String id) {
        Cursor rs = moviesDBHelper.getData(Integer.parseInt(id));
        rs.moveToFirst();
        title = rs.getString(rs.getColumnIndex(MoviesDBHelper.KEY_TITLE));
        image = rs.getString(rs.getColumnIndex(MoviesDBHelper.KEY_IMAGE));
        defaultImageCaptureUri = Uri.parse(image);
        description = rs.getString(rs.getColumnIndex(MoviesDBHelper.KEY_RELEASEYEAR));
//        Log.e(TAG, "GenreTitle->" + rs.getString(rs.getColumnIndex(MoviesDBHelper.KEY_GENRE_TITLE)));
        if (!rs.isClosed()) {
            rs.close();
        }
        edit_title.setText(title);
        edit_description.setText(description);
        try {
            if (image != null) {
                Glide.with(getActivity()).load(image)
                        .error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher)
                        .into(img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
            if (img != null) {
                img.setImageBitmap(profile_imag);
                img.setVisibility(View.VISIBLE);
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

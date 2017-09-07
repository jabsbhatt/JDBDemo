package com.jdbdemo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jdbdemo.R;
import com.jdbdemo.fragments.AddMoreFragment;
import com.jdbdemo.fragments.HomeFragment;
import com.jdbdemo.pojo.DrawerDataContainer;
import com.jdbdemo.utils.SecurePreferences;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static TextView maintitle;
    RecyclerView mRecyclerView;
    ArrayList<DrawerDataContainer> drawerdatalist = new ArrayList<DrawerDataContainer>();
    DrawerAdapter drawerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView txt_username = (TextView) findViewById(R.id.txt_username);
        txt_username.setText(SecurePreferences.getStringPreference(MainActivity.this, "userName"));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.menu_icon, getTheme());

        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });


        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        maintitle = (TextView) findViewById(R.id.maintitle);
        maintitle.setText(R.string.home_string);
        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        DrawerDataContainer dataContainer = new DrawerDataContainer(0, getResources().getString(R.string.home_string), R.drawable.home_icon);
        DrawerDataContainer dataContainer1 = new DrawerDataContainer(1, getResources().getString(R.string.addmore_string), R.drawable.home_icon);
        DrawerDataContainer dataContainer2 = new DrawerDataContainer(2, getResources().getString(R.string.logout_string), R.drawable.logout_icon);

        drawerdatalist.add(dataContainer);
        drawerdatalist.add(dataContainer1);
        drawerdatalist.add(dataContainer2);

        drawerAdapter = new DrawerAdapter(drawerdatalist);
        mRecyclerView.setAdapter(drawerAdapter);
        displayView(0);
    }

    public class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<DrawerDataContainer> drawerdatalist;
        String notification = "";

        public DrawerAdapter(ArrayList<DrawerDataContainer> drawerdatalist) {
            this.drawerdatalist = drawerdatalist;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_nav_drawer, parent, false);
            RecyclerView.ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder dataholder, final int position) {

            final ViewHolder holder = (ViewHolder) dataholder;

            final DrawerDataContainer data = drawerdatalist.get(position);

            holder.tvTitle.setText(data.getDrawertitle());
            holder.ivImage.setImageResource(data.getDrawerimg());

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    maintitle.setText(data.getDrawertitle());
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawers();
                    displayView(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return drawerdatalist.size();
        }

        public void setNotification(String notification) {
            this.notification = notification;
            notifyItemChanged(1);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView ivImage;
        protected TextView tvTitle;
        protected View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
            ivImage = (ImageView) v.findViewById(R.id.ivdraweritem);
            tvTitle = (TextView) v.findViewById(R.id.tvdrawertitle);
        }
    }

    public void displayView(final int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new HomeFragment();
                maintitle.setText(R.string.home_string);
                break;
            case 1:
                fragment = new AddMoreFragment();
                maintitle.setText(R.string.add_new_movie);
                break;
            case 2:
                SecurePreferences.clearUserData(MainActivity.this);

                Intent intent = new Intent(MainActivity.this,
                        LoginActivity.class);
                // Closing all the Activities
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Add new Flag to start new Activity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
//                overridePendingTransition(R.anim.back_slide_in,
//                        R.anim.back_slide_out);
//                finish();


            default:
                break;


        }
        if (fragment != null) {
            ChangeFragment(fragment);

        }
    }

    public void ChangeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.maincointener, fragment, fragment.getClass().getName());
        fragmentTransaction.commit();
    }

    public void centerToolbarTitle(@NonNull final Toolbar toolbar) {
        final CharSequence title = toolbar.getTitle();
        final ArrayList<View> outViews = new ArrayList<>(1);
        toolbar.findViewsWithText(outViews, title, View.FIND_VIEWS_WITH_TEXT);
        if (!outViews.isEmpty()) {
            final TextView titleView = (TextView) outViews.get(0);
            titleView.setGravity(Gravity.CENTER);
            final Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) titleView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            toolbar.requestLayout();
            //also you can use titleView for changing font: titleView.setTypeface(Typeface);
        }
    }
}

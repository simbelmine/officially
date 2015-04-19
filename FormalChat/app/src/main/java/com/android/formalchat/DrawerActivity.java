package com.android.formalchat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Sve on 3/26/15.
 */
public class DrawerActivity extends FragmentActivity {
    public static final int NONE = 101;
    private RoundedImageView profilePic;
    private TextView profileName;
    private DrawerLayout drawerLayout;
    private RelativeLayout leftDrawerLayout;
    private ListView leftDrawerList;
    private String[] listElements;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence title;
    private CharSequence drawerTitle;
    private ParseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);

        user = ParseUser.getCurrentUser();
        title = drawerTitle = getTitle();
        listElements = getResources().getStringArray(R.array.menu_list);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawerLayout = (RelativeLayout) findViewById(R.id.left_drawer);
        leftDrawerList = (ListView) findViewById(R.id.left_list_drawer);
        profilePic = (RoundedImageView) findViewById(R.id.profile_img);
        profileName = (TextView) findViewById(R.id.profile_name);
        initDrawableToggle();
        initActionBar();
        setPic();
        setPicOnClickListenre();
        setProfileName();

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
        drawerLayout.setDrawerListener(drawerToggle);
        leftDrawerList.setAdapter(new DrawerListAdapter(this));
        setListOnClickItemListener();
    }

    private void setPic() {
        String profilePicUri = getUserProfilePicUri();
        if(profilePicUri != null) {
            Picasso.with(getApplicationContext()).load(profilePicUri).into(profilePic);
        }
        else {
            Picasso.with(getApplicationContext()).load(R.drawable.profile_pic_).into(profilePic);
        }
    }

    private String getUserProfilePicUri() {
        ParseFile pic = user.getParseFile("profileImg");
        if(pic != null) {
            return pic.getUrl();
        }
        return null;
    }

    private void setPicOnClickListenre() {
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(ProfileActivity.class, NONE);
            }
        });
    }

    private void setProfileName() {
        final ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserInfo");
        query.whereEqualTo("loginName", user.getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                ParseObject userInfo = parseObjects.get(0);
                profileName.setText(userInfo.get("name").toString());
            }
        });
    }

    private void initActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        // Hide Action Bar icon and text
        getActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = drawerLayout.isDrawerOpen(leftDrawerLayout);
        //menu.findItem(R.id.search_button).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void initDrawableToggle() {
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //updateTitle(title);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //updateTitle(drawerTitle);
                setPic();
            }
        };
    }

    private void updateTitle(CharSequence title) {
        getActionBar().setTitle(title);
        invalidateOptionsMenu();
    }

    private void setListOnClickItemListener() {
        leftDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
    }

    private void selectItem(int position) {
        switch (position) {
            case 0:
                onItemSelected(position);
                launchActivity(MainActivity.class, position);
                break;
            case 1:
                onItemSelected(position);
                launchActivity(ProfileActivity.class, position);
                break;
            case 2:
                logOut();
                break;
        }
    }

    private void launchActivity(Class classToCall, int title_position) {
        Intent intent = new Intent(DrawerActivity.this, classToCall);
        intent.putExtra("title_position", title_position);
        startActivity(intent);
    }

    private void onItemSelected(int position) {
        leftDrawerList.setItemChecked(position, true);
        setTitle(listElements[position]);
        drawerLayout.closeDrawer(leftDrawerLayout);
    }

    public void logOut() {
        ParseUser.logOut();
        launchActivity(LoginActivity.class, NONE);
    }
}

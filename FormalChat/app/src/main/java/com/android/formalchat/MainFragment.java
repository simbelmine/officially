package com.android.formalchat;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

/**
 * Created by Sve on 4/16/15.
 */
public class MainFragment extends Fragment {
    public static final String PREFS_NAME = "FormalChatPrefs";
    public static final int NONE = 101;
    private SharedPreferences sharedPreferences;
    private TextView userName;
    private ParseUser currentUser;
    private Boolean exit;
    private DrawerLayout drawerLayout;
    private GridView people_GridView;
    private ImageButton grid_list_btn;
    private boolean isGrid;
    private ListView people_ListView;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.main_activity, container, false);

        Log.v("formalchat", "Main Fragment onCreateView ..... ");

        exit = false;
        isGrid = true;

        setTitle();
        initSharedPreferences();

        userName = (TextView) rootView.findViewById(R.id.user_name);
        currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            userName.setText(currentUser.getUsername());
        } else {
            // show the signup or login screen
            launchLoginActivity();
        }

        people_GridView = (GridView) rootView.findViewById(R.id.people_gridview);
        people_ListView = (ListView) rootView.findViewById(R.id.people_listview);

        grid_list_btn = (ImageButton) rootView.findViewById(R.id.grid_list_btn);
        initGridListBtn();
        setOnClickListeners();

        return rootView;
    }




    private void initSharedPreferences() {
        sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, 0);
    }

    private void initGridListBtn() {
        if(sharedPreferences.getBoolean("isGrid", false)) {
            grid_list_btn.setImageResource(R.drawable.grid);
            setPplGridView();
        }
        else {
            grid_list_btn.setImageResource(R.drawable.list);
            setPplListView();
        }
    }

    private void setOnClickListeners() {
        grid_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isGrid) {
                    isGrid = false;
                    grid_list_btn.setImageResource(R.drawable.list);
                    setGridListStatus();
                    setPplListView();
                }
                else {
                    isGrid = true;
                    grid_list_btn.setImageResource(R.drawable.grid);
                    setGridListStatus();
                    setPplGridView();
                }
            }
        });
    }

    private void setPplListView() {
        people_ListView.setAdapter(new PeopleListViewAdapter(getActivity().getApplicationContext()));
        people_GridView.setVisibility(View.GONE);
        people_ListView.setVisibility(View.VISIBLE);
    }

    private void setPplGridView() {
        people_GridView.setAdapter(new PeopleGridViewAdapter(getActivity().getApplicationContext()));
        people_ListView.setVisibility(View.GONE);
        people_GridView.setVisibility(View.VISIBLE );
    }

    private void setGridListStatus() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isGrid", isGrid);
        editor.commit();
    }

    private void setTitle() {
        int title_position = getActivity().getIntent().getIntExtra("title_position", NONE);
        if(title_position != NONE) {
            getActivity().getActionBar().setTitle(getResources().getStringArray(R.array.menu_list)[title_position]);
        }
        else {
            getActivity().getActionBar().setTitle(getResources().getStringArray(R.array.menu_list)[0]);
        }
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

//    @Override
//    public void onBackPressed() {
//        if(isLoggedIn()){
//            if(exit) {
//                getActivity().finish();
//            }
//            else {
//                //The Handler here handles accidental back presses,
//                // it simply shows a Toast, and if there is another back press within 3 seconds,
//                // it closes the application.
//                Toast.makeText(getActivity(), getString(R.string.back_to_exit), Toast.LENGTH_SHORT).show();
//                exit = true;
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        exit = false;
//                    }
//                }, 3 * 1000 );
//            }
//        }
//    }
//
//    private boolean isLoggedIn() {
//        if(sharedPreferences.getBoolean("loggedIn", false)) {
//            return true;
//        }
//        return false;
//    }
}
package com.android.formalchat.profile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.formalchat.CropActivity;
import com.android.formalchat.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Sve on 3/10/15.
 */
public class FullImageActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "FormalChatPrefs";
    private static final int CROP_FROM_IMG = 123;
    public static final String ACTION_DELETED = "PICTURE_DELETE_COMPLETE";
    public static final String ACTION_UPLOADED_PROFILE_PIC = "PROFILE_PICTURE_UPLOAD_COMPLETE";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageView fullScreenView;
    private RelativeLayout topBtnsLayout;
    private ImageView backBtn;
    private ImageView menuBtn;
    private boolean isVisible = true;
    private String url;
    private ParseUser user;
    private Menu menu;
    private Toolbar toolbar;
    private ProgressBar fullImgProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initWindow();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_full_screen_img);

        initToolbar();
        initActionBar();

        sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        editor = sharedPreferences.edit();
        Intent i = getIntent();
        url = i.getExtras().getString("url");
        user = ParseUser.getCurrentUser();

        fullScreenView = (ImageView) findViewById(R.id.full_screen_img);
        fullImgProgressBar = (ProgressBar) findViewById(R.id.full_image_progress_bar);

        Picasso.with(this)
                .load(url)
                .into(fullScreenView, new Callback() {
                    @Override
                    public void onSuccess() {
                        fullImgProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {

                    }
                });

        topBtnsLayout = (RelativeLayout) findViewById(R.id.top_btns_layout);
        backBtn = (ImageView) findViewById(R.id.back_btn);
        menuBtn = (ImageView) findViewById(R.id.menu_btn);

        fullScreenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isVisible) {
//                    topBtnsLayout.setVisibility(View.VISIBLE);
//                    backBtn.setVisibility(View.VISIBLE);
//                    menuBtn.setVisibility(View.VISIBLE);

                    exitFullScreenMode();
                    getSupportActionBar().show();
                    isVisible = true;
                }
                else {
//                    topBtnsLayout.setVisibility(View.GONE);
//                    backBtn.setVisibility(View.GONE);
//                    menuBtn.setVisibility(View.GONE);

                    goToFullScreenMode();
                    getSupportActionBar().hide();
                    isVisible = false;
                }
            }
        });

//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

//        menuBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu popupMenu = new PopupMenu(FullImageActivity.this, menuBtn);
//                popupMenu.getMenuInflater().inflate(R.menu.pop_up, popupMenu.getMenu());
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.delete:
//                                deleteImage();
//                                return true;
//                            case R.id.set_as:
//                                startCropActivity();
//                                return true;
//                            default:
//                                return false;
//                        }
//                    }
//                });
//                popupMenu.show();
//            }
//        });
    }

    private void initWindow() {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar_purple));
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
    }

    private void initActionBar() {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transp_black_16)));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            if(resultCode == RESULT_OK) {
                byte[] profileImg = data.getByteArrayExtra("profileImg");
                byte[] profileImgOriginal = data.getByteArrayExtra("profileImgOriginal");
                saveBlurProfileImgToParse(profileImgOriginal);
                saveProfileImgToParse(profileImg);
                sendBroadcastMessage(ACTION_UPLOADED_PROFILE_PIC);
                setProfPicNameToSharPrefs();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.full_img_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                deleteImage();
                return true;
            case R.id.set_as:
                startCropActivity();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    private void saveProfileImgToLocal(byte[] profileImg) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(profileImg, 0, profileImg.length);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.formal_chat";
        Bitmap blurredBitmap = getBlurredBitmap(bitmap);

        File dir = new File(path);
        File profilePic = new File(dir, "blurred_profile.jpg");
        try {
            FileOutputStream out = new FileOutputStream(profilePic);
            blurredBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        }
        catch (IOException ex) {
            Log.e("formalchat", ex.getMessage());
        }
    }

    private Bitmap getBlurredBitmap(Bitmap bitmap) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {

            //Let's create an empty bitmap with the same size of the bitmap we want to blur
            Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

            //Instantiate a new Renderscript
            RenderScript rs = RenderScript.create(getApplicationContext());

            //Create an Intrinsic Blur Script using the Renderscript
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            //Create the in/out Allocations with the Renderscript and the in/out bitmaps
            Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
            Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

            //Set the radius of the blur
            blurScript.setRadius(25.f);

            //Perform the Renderscript
            blurScript.setInput(allIn);
            blurScript.forEach(allOut);

            //Copy the final bitmap created by the out Allocation to the outBitmap
            allOut.copyTo(outBitmap);

            //recycle the original bitmap
            bitmap.recycle();

            //After finishing everything, we destroy the Renderscript.
            rs.destroy();

            return outBitmap;
        }

        return null;
    }

    private void setProfPicNameToSharPrefs() {
        editor.putString("profPic", url);
        editor.putString("profPicName", getShortImageNameFromUri());
        editor.commit();
    }

    private void startCropActivity() {
        Intent intent = new Intent(FullImageActivity.this, CropActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // NB: if it starts with FLAG_ACTIVITY_NEW_TASK it cannot start it for result

        intent.putExtra("url", url);
        startActivityForResult(intent, CROP_FROM_IMG);
    }

    private void saveProfileImgToParse(byte[] profileImg) {
        ParseFile parseProfImg = new ParseFile(profileImg);
        user.put("profileImg", parseProfImg);
        user.put("profileImgName", getShortImageNameFromUri());
        user.saveInBackground();
    }

    private void saveBlurProfileImgToParse(byte[] profileImg) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(profileImg, 0, profileImg.length);
        Bitmap blurredBitmap = getBlurredBitmap(bitmap);

        ParseFile parseProfImg = new ParseFile(bitmapToByteArray(blurredBitmap));
        user.put("profileBlurImg", parseProfImg);
        user.saveInBackground();
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    private void deleteImage() {
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("UserImages");
        parseQuery.whereEqualTo("photo", getParseImgNameFromUri());

        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> imagesList, ParseException e) {
                if (e == null) {
                    if (imagesList.size() > 0) {
                        ParseObject currentParseObject = imagesList.get(0);
                        ProfileGalleryUtils profileGalleryUtils = new ProfileGalleryUtils(getApplicationContext(), user, currentParseObject);

                        if (profileGalleryUtils.isProfilePic()) {
                            profileGalleryUtils.deleteProfileImgFromParse();
                            profileGalleryUtils.deleteBlurrredImageFromLocal();
                        }
                        profileGalleryUtils.deleteImgFromParse();

                        finish();
                    }
                } else {
                    Log.e("formalchat", "Delete command: " + e.getMessage());
                }
            }
        });
    }



    private void sendBroadcastMessage(String action) {
        Intent sender = new Intent(action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(sender);
    }

    public String getParseImgNameFromUri() {
        return url.substring(url.lastIndexOf("/")+1);
    }

    public String getShortImageNameFromUri() {
        return url.substring(url.lastIndexOf("-")+1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void goToFullScreenMode() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            fullScreenView
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void exitFullScreenMode() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            fullScreenView
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}

package com.adamwilson.golf;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adamwilson.golf.DataModel.GolfDB;
import com.adamwilson.golf.DataModel.ProfilesDB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by adam on 7/21/15.
 */
public class ProfileBar {
    PreferenceManager.OnActivityResultListener activityResultListener;
    private static ProfileBar profileBar;
    private String[] currentProfile;
    public String currentProfileName;
    public Activity context;
    private TextView profileNameTextView;
    private TextView profileHandicapTextView;
    private ImageView profilePicImageView;
    private GolfDB profilesDB;
    private Dialog dialog;
    public Uri newProfilePicURI = null;
    public boolean shouldShowBackButton = true;
    public boolean areSettingsAvailable = true;

    private int IMAGE_WIDTH = 160;
    private int IMAGE_HEIGHT = 140;
    private int rightMargin = 20;
    private int topMargin = 5;
    private int bottomMargin = 40;
    private int settingsMargin = 65;
    private int textSize = 20;

    public static ProfileBar getProfileBar(Context c){
        if (profileBar == null){
            profileBar = new ProfileBar(c);
        }
        return  profileBar;
    }

    private ProfileBar(Context c){
        if (profilesDB == null) {
            profilesDB = GolfDB.getGolfDatabase(c);
        }

        profiles = profilesDB.getAllProfiles();
        if (profiles.size() > 0){
            currentProfile = profiles.get(0);
        }else{
            currentProfile = new String[]{"Golfer", "Default", ""};
        }
    }

    private void getSceenSize(){
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenDensity = metrics.densityDpi;

        if (screenDensity == DisplayMetrics.DENSITY_MEDIUM) {
            if ((context.getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK) ==
                    Configuration.SCREENLAYOUT_SIZE_NORMAL) {
                textSize = 16;
                settingsMargin = 30;
            }else{
                textSize = 18;
                settingsMargin = 35;
            }
            rightMargin = 20;
            topMargin = 5;
            bottomMargin = 20;
            settingsMargin = 35;
            IMAGE_WIDTH = 80;
            IMAGE_HEIGHT = 60;
        } else if (screenDensity == DisplayMetrics.DENSITY_HIGH) {
            rightMargin = 20;
            topMargin = 5;
            bottomMargin = 20;
            settingsMargin = 45;
            IMAGE_WIDTH = 120;
            IMAGE_HEIGHT = 100;
            textSize = 18;
        } else if (screenDensity == DisplayMetrics.DENSITY_XXHIGH) {
            topMargin = 15;
            bottomMargin = 40;
            settingsMargin = 65;
            IMAGE_WIDTH = 200;
            IMAGE_HEIGHT = 180;
        }
    }

    public void setupProfileBar(ActionBar actionBar, Activity c){
        //this.bar = actionBar;
        this.context = c;
        getSceenSize();

        int picMargin;
        if (!areSettingsAvailable){
            picMargin = settingsMargin;
        }else{
            picMargin = 0;
        }

        //layout for profilebar
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //actionBar.getDisplayOptions
        RelativeLayout layout = new RelativeLayout(actionBar.getThemedContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutParams);

        //layout for profile pic CENTER_CROP
        profilePicImageView = new ImageView(actionBar.getThemedContext());
        profilePicImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        RelativeLayout.LayoutParams profileLayoutParams = new RelativeLayout.LayoutParams(
                IMAGE_WIDTH, IMAGE_HEIGHT);
        profileLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        profileLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        profileLayoutParams.rightMargin = picMargin;
        profilePicImageView.setLayoutParams(profileLayoutParams);
        layout.addView(profilePicImageView);

        //layout for profile name
        profileNameTextView = new TextView(actionBar.getThemedContext());
        profileNameTextView.setTextColor(Color.BLACK);
        profileNameTextView.setTextSize(textSize);
        profileNameTextView.setTypeface(Typeface.DEFAULT_BOLD);

        RelativeLayout.LayoutParams nameLayoutParams = new RelativeLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        nameLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        nameLayoutParams.rightMargin = IMAGE_WIDTH + picMargin + rightMargin;
        nameLayoutParams.topMargin = topMargin;
        profileNameTextView.setLayoutParams(nameLayoutParams);
        layout.addView(profileNameTextView);

        //layout for profile handicap
        profileHandicapTextView = new TextView(actionBar.getThemedContext());
        profileHandicapTextView.setTextColor(Color.BLACK);
        profileHandicapTextView.setTextSize(textSize);
        profileHandicapTextView.setTypeface(Typeface.DEFAULT_BOLD);

        RelativeLayout.LayoutParams handicapLayoutParams = new RelativeLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        handicapLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        handicapLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        handicapLayoutParams.rightMargin = IMAGE_WIDTH + picMargin + rightMargin;
        handicapLayoutParams.bottomMargin = bottomMargin;
        profileHandicapTextView.setLayoutParams(handicapLayoutParams);
        layout.addView(profileHandicapTextView);

        actionBar.setCustomView(layout);

        if (shouldShowBackButton){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (currentProfile != null){
            loadProfile(currentProfile);
        }
    }

    public void deleteProfile(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete this profile?");

        // Add the buttons
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                profilesDB.deleteProfile(currentProfileName);
            }
        });
        builder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void changeProfile(){

        final ArrayList<String[]> profiles = profilesDB.getAllProfiles();

        final ArrayList<String> profileNames = new ArrayList<String>();
        for (String[] profile : profiles){
            profileNames.add(profile[0]);
        }


        dialog = new Dialog(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        ListView profileList = new ListView(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, profileNames);
        profileList.setAdapter(adapter);
        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedProfileName = ((TextView) view).getText().toString();
                currentProfile = profiles.get(profileNames.indexOf(selectedProfileName));
                loadProfile(currentProfile);
                dialog.dismiss();
            }
        });


        builder.setTitle("Select Profile");
        builder.setView(profileList);
        dialog = builder.create();
        dialog.show();

    }

    public void createNewProfile(){
        LinearLayout dialogLayout = new LinearLayout(context);
        LinearLayout.LayoutParams dialogParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setLayoutParams(dialogParams);

        //new profile pic
        final ImageView newProfilePic = new ImageView(context);
        if (newProfilePicURI != null && !newProfilePicURI.toString().equalsIgnoreCase("default")){
            try {
                Bitmap largeBitmap = MediaStore.Images.Media.getBitmap(
                        context.getContentResolver(), newProfilePicURI);
                newProfilePic.setImageBitmap(Bitmap.createScaledBitmap(largeBitmap, largeBitmap.getWidth()/5, largeBitmap.getHeight()/5, true));
                newProfilePic.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            newProfilePicURI = Uri.parse("default");
            newProfilePic.setImageResource(R.mipmap.golficon180);
        }
        dialogLayout.addView(newProfilePic, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        //new profile name
        final EditText profileNameEditText = new EditText(context);
        profileNameEditText.setHint("profile name");
        profileNameEditText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        profileNameEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        profileNameEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        dialogLayout.addView(profileNameEditText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        String[] options = new String[]{"Camera", "Album"};
        AlertDialog.Builder newProfileDialog = new AlertDialog.Builder(context);

        TextView title = new TextView(context);
        title.setText("Create New Profile");
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(32);
        newProfileDialog.setCustomTitle(title);

        newProfileDialog.setView(dialogLayout);
        newProfileDialog.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.isCreatingProfile = true;
                    Intent camIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }

                    camIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    mainActivity.startActivityForResult(camIntent, 3);
                }
                if (which == 1) {
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.isCreatingProfile = true;
                    Intent albumIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    mainActivity.startActivityForResult(albumIntent, 4);
                }
            }
        });

        newProfileDialog.setPositiveButton("add", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!profileNameEditText.getText().toString().equalsIgnoreCase("")) {
                    boolean isNew = true;
                    for (String[] profile : profiles) {
                        if (profileNameEditText.getText().toString().equalsIgnoreCase(profile[0])) {
                            isNew = false;
                            break;
                        }
                    }
                    if (isNew) {
                        currentProfile[0] = profileNameEditText.getText().toString();
                        currentProfile[1] = newProfilePicURI.toString();
                        currentProfile[2] = getGolferHandicap(profileNameEditText.getText().toString());

                        profilesDB.insertProfile(profileNameEditText.getText().toString(), newProfilePicURI.toString(), getGolferHandicap(profileNameEditText.getText().toString()));
                        loadProfile(currentProfile);
                        dialog.dismiss();
                    }
                }
            }
        });

        newProfileDialog.setNeutralButton("cancel", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        newProfileDialog.show();
    }
    private ArrayList<String[]> profiles;
    private void loadProfile(String[] profile){

        currentProfileName = profile[0];
        profileNameTextView.setText(profile[0]);
        profileHandicapTextView.setText("Handicap " + currentProfile[2]);

        if  (!profile[1].equalsIgnoreCase("default")) {
            try {
                //Bitmap largeBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(profile[1]));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 6;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inDither = true;

                AssetFileDescriptor fileDescriptor = context.getContentResolver().openAssetFileDescriptor(  Uri.parse(profile[1]), "r");
                Bitmap largeBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                profilePicImageView.setImageBitmap(Bitmap.createScaledBitmap(largeBitmap, largeBitmap.getWidth()/4, largeBitmap.getHeight()/4, true));
                largeBitmap.recycle();

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }// catch (IOException e) {
            //    // TODO Auto-generated catch block
            //    e.printStackTrace();
            //}
        }else{
            profilePicImageView.setImageResource(R.mipmap.golficon180);
        }
    }

    String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HH").format(new Date());
        String imageFileName = "golf_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath(), imageFileName);
        image.createNewFile();

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public void updateGolferHandicap(){
        currentProfile[2] = getGolferHandicap(currentProfileName);
        profilesDB.updateProfile(currentProfile);
    }

    public String getGolferHandicap(String golferName){

        ArrayList<ArrayList<String[]>> allRounds = new ArrayList<ArrayList<String[]>>();

        allRounds = profilesDB.getAllRounds();

        int numRounds = 0;
        int positiveScore = 0;
        int numHolesPlayed = 0;

        for (ArrayList<String[]> round : allRounds){
            //numRounds++;
            int parTotal = 0;
            int scoreTotal = 0;
            for (String[] entry : round){
                if (entry[1].equalsIgnoreCase(golferName)){
                    parTotal += Integer.parseInt(entry[3]);
                    scoreTotal += Integer.parseInt(entry[5]);
                    numHolesPlayed++;
                }
            }

            positiveScore += (scoreTotal - parTotal);
        }

        //System.out.println(positiveScore);
        //System.out.println(numRounds);

         numRounds = (int) Math.round(((numHolesPlayed / 18) * 2.0) / 2.0);

        if (numRounds < 3){

            //for (int i = 0; i < 3; i++){
                //golfDB.insertTestRound(currentProfileName);
            //}
            return "TBD";
        }

        if (positiveScore < 0){
            return "0";
        }

        return Integer.toString(positiveScore/numRounds);

    }

}

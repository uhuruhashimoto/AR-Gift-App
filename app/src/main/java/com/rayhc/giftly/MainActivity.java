package com.rayhc.giftly;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rayhc.giftly.frag.FriendsFragment;
import com.rayhc.giftly.frag.HomeFragment;
import com.rayhc.giftly.frag.SettingsFragment;
import com.rayhc.giftly.util.Gift;
import com.rayhc.giftly.util.Globals;
import com.rayhc.giftly.util.NotifService;
import com.rayhc.giftly.util.UserManager;
import com.rayhc.giftly.util.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.rayhc.giftly.util.Globals.GOT_GIFTS_KEY;
import static com.rayhc.giftly.util.Globals.REC_MAP_KEY;
import static com.rayhc.giftly.util.Globals.SENT_MAP_KEY;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    public static final String NAV_ITEM_ID = "NAV_ITEM_ID";
    public static final int RC_SIGN_IN = 123;

    private User activityUser;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private FriendsFragment friendsFragment;
    private HomeFragment homeFragment;
    private SettingsFragment settingsFragment;

    private int navId;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private boolean firstRun = true;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Startup startup;


    private Gift mGift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // get id to restore state if needed
        if (savedInstanceState != null) {
            navId = savedInstanceState.getInt(NAV_ITEM_ID);
        } else {
            navId = R.id.nav_home;
        }

        Log.d(Globals.TAG, "Created!");

        // get first run info
        startup = (Startup) getApplication();
        firstRun = startup.getFirstRun();
        Log.d("LPC", "is first run? " + firstRun);

        // define fragments
        friendsFragment = new FriendsFragment();
        homeFragment = new HomeFragment();
        settingsFragment = new SettingsFragment();

        // get navigation
        BottomNavigationView navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setOnNavigationItemSelectedListener(this);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        //check permissions
        checkPermissions();

        //determine if we've gotten gifts
        Intent startIntent = getIntent();
        Log.d("LPC", "GOT GITS? " + startIntent.getBooleanExtra("GOT GIFTS", false));
        if (mFirebaseUser == null) {
            loadFirebase();
        }
        if (startIntent.getBooleanExtra("SENT GIFT", false)) {
            mGift = new Gift();
            Log.d("LPC", "onCreate: made a new gift");
        }
        navId = R.id.nav_home;

        if(mFirebaseUser != null || (prefs != null && prefs.getString(Globals.USER_ID_KEY, null) != null)) {
            Intent notifIntent = new Intent(getApplicationContext(), NotifService.class);
            startService(notifIntent);
        }

        navigateToFragment(navId);
    }

    // launches authentication intent with preferences.
    private void loadFirebase() {
        //starts login page
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.Auth)
                        .setLogo(R.drawable.joy_logo)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onDestroy() {
        startup.setFistRun(true);
        Log.d("LPC", "first run is now: " + startup.getFirstRun());
        super.onDestroy();
    }

    // creates fragment if chosen
    public void navigateToFragment(int navId) {
        if (navId == R.id.nav_friends_list) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, friendsFragment, "FriendsFragment").commit();
        } else if (navId == R.id.nav_home) {
            homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, homeFragment, "HomeFragment").commit();
        } else if (navId == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, settingsFragment, "SettingsFragment").commit();
        }
    }

    //navigates to and from fragment
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        navId = item.getItemId();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToFragment((item.getItemId()));
            }
        }, 250);


        return true;
    }

    // To handle state changes
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, navId);
    }

    private void onAuthSuccess(FirebaseUser currentUser) {
        String userId = currentUser.getUid();

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();
        editor.putString(Globals.USER_ID_KEY, currentUser.getUid());
        editor.apply();

        //start notif intent
        Intent notifIntent = new Intent(getApplicationContext(), NotifService.class);
        startService(notifIntent);

        //go to download splash
        if (firstRun) {
            startup.setFistRun(false);
            Log.d("LPC", "run in first run in auth success");
            navigateToFragment(R.id.nav_home);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("LPC", "onActivityResult: called");
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            if (resultCode == RESULT_OK) {
                Log.d("LPC", "result ok");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Query query = db.child("users").orderByChild("userId").equalTo(user.getUid());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("LPC", "does data snap exist?: " + snapshot.exists());
                        if (snapshot.exists()) {
                            Log.d("LPC", "snapshot exists");
                            activityUser = UserManager.snapshotToUser(snapshot, user.getUid());
                            Log.d("LPC", "user exists");
                        } else activityUser = UserManager.snapshotToEmptyUser(snapshot, user);
                        onAuthSuccess(user);

                        Intent notifIntent = new Intent(getApplicationContext(), NotifService.class);
                        startService(notifIntent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            } else {
                Log.d("iandebug", "User Login Failed");
                Toast.makeText(this, "You need to login to continue", Toast.LENGTH_SHORT).show();
                ExitLogoutActivity.exitApplication(this);
            }

        }
    }

    /**
     * Request user permission to write to external storage
     */
    public void checkPermissions() {
        if (Build.VERSION.SDK_INT < 23)
            return;
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    /**
     * Deal with denial of storage permissions (adapted from class code)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important for the app.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                            }

                        }
                    });
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    //Never ask again and handle your app without permission.
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExitLogoutActivity.exitApplication(this);
    }

    public void onLogoutClicked(View view) {
        Log.d("iandebug", "logout clicked");
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mSharedPref.edit();
        FirebaseAuth.getInstance().signOut();
        editor.remove(Globals.USER_ID_KEY);
        editor.remove("USER ID");
        editor.commit();
        ExitLogoutActivity.exitApplication(this);
    }

    public void updateSent() {
        if (navId == R.id.nav_home) {
            homeFragment.updateSent();
            findViewById(R.id.downloading).setVisibility(View.INVISIBLE);
            Log.d("rhc", "on home : " + navId);
        } else {
            Log.d("rhc", "Not on home : " + navId);
        }
    }
}


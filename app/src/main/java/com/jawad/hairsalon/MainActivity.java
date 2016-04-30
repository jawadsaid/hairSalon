package com.jawad.hairsalon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class MainActivity extends AppCompatActivity {
    private ProfileTracker mProfileTracker;
    private CallbackManager mCallbackManager;
    SharedPreferences prefs;
    LinearLayout layout;
    CalendarView calendarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);
        layout = (LinearLayout) findViewById(R.id.liLayout);
        prefs  = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        if (loginButton != null) {
            loginButton.setReadPermissions("public_profile");
        }
        mCallbackManager = CallbackManager.Factory.create();
        if (loginButton != null) {
            loginButton.registerCallback(mCallbackManager,mCallback);
        }



        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken2) {
                if (accessToken2 == null) {
                    // Log out logic
                    Toast.makeText(MainActivity.this, "Logout ", Toast.LENGTH_SHORT).show();
                    layout.setVisibility(View.INVISIBLE);
                }
            }
        };

        if(Profile.getCurrentProfile() != null){
            layout.setVisibility(View.VISIBLE);
            Profile profile = Profile.getCurrentProfile();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("name", profile.getFirstName() + " " + profile.getLastName());
            editor.apply();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }


    private FacebookCallback<LoginResult> mCallback= new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            layout.setVisibility(View.VISIBLE);
            if(Profile.getCurrentProfile() == null) {
                mProfileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                        // profile2 is the new profile
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("name", profile2.getFirstName() + " " + profile2.getLastName());
                        editor.commit();

                        mProfileTracker.stopTracking();
                    }
                };
                mProfileTracker.startTracking();
            }
            else {
                Profile profile = Profile.getCurrentProfile();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("name", profile.getFirstName() + " " + profile.getLastName());
                editor.commit();

            }
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

}

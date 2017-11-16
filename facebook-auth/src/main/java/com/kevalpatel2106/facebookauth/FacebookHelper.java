package com.kevalpatel2106.facebookauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import timber.log.Timber;

/**
 * Created by multidots on 6/16/2016.
 * This class will initialize facebook login and handle other sdk functions.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */
public class FacebookHelper {
    @SuppressWarnings("unused")
    private static final String TAG = "FacebookHelper";

    private FacebookResponse mListener;
    private String mFieldString;
    private CallbackManager mCallBackManager;

    /**
     * Public constructor.
     *
     * @param responseListener {@link FacebookResponse} listener to get call back response.
     * @param fieldString      name of the fields required. (e.g. latlong,name,email,gender,birthday,picture,cover)
     *                         See {@link 'https://developers.facebook.com/docs/graph-api/reference/user'} for more info on user node.
     * @param context          instance of the caller activity
     */
    public FacebookHelper(@NonNull FacebookResponse responseListener,
                          @SuppressWarnings("SameParameterValue") @NonNull String fieldString,
                          @NonNull Activity context) {
        FacebookSdk.sdkInitialize(context.getApplicationContext());

        //noinspection ConstantConditions
        if (responseListener == null)
            throw new IllegalArgumentException("FacebookResponse listener cannot be null.");

        //noinspection ConstantConditions
        if (fieldString == null) throw new IllegalArgumentException("field string cannot be null.");

        mListener = responseListener;
        mFieldString = fieldString;
        mCallBackManager = CallbackManager.Factory.create();

        //get access token
        FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //get the user profile
                getUserProfile(loginResult);
            }

            @Override
            public void onCancel() {
                mListener.onFbSignInFail();
            }

            @Override
            public void onError(FacebookException e) {
                mListener.onFbSignInFail();
            }
        };
        LoginManager.getInstance().registerCallback(mCallBackManager, mCallBack);
    }

    /**
     * Get user facebook profile.
     *
     * @param loginResult login result with user credentials.
     */
    private void getUserProfile(LoginResult loginResult) {
        // App code
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), (object, response) -> {

                    Log.d(TAG, "response: " + response + "");
                    try {
                        mListener.onFbProfileReceived(parseResponse(object));
                    } catch (Exception e) {
                        Timber.e(e);
                        mListener.onFbSignInFail();
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", mFieldString);
        request.setParameters(parameters);
        request.executeAsync();
    }

    /**
     * Get the {@link CallbackManager} for managing callbacks.
     *
     * @return {@link CallbackManager}
     */
    @NonNull
    @CheckResult
    public CallbackManager getCallbackManager() {
        return mCallBackManager;
    }

    /**
     * Parse the response received into {@link FacebookUser} object.
     *
     * @param object response received.
     *
     * @return {@link FacebookUser} with required fields.
     *
     * @throws JSONException - If json parsing is failed.
     */
    private FacebookUser parseResponse(JSONObject object) throws JSONException {
        FacebookUser user = new FacebookUser(object);

        if (object.has("id")) user.setFacebookID(object.getString("id"));
        if (object.has("email")) user.setEmail(object.getString("email"));
        if (object.has("name")) user.setName(object.getString("name"));
        if (object.has("gender"))
            user.setGender(object.getString("gender"));
        if (object.has("about")) user.setAbout(object.getString("about"));
        if (object.has("bio")) user.setBio(object.getString("bio"));
        if (object.has("cover"))
            user.setCoverPicUrl(object.getJSONObject("cover").getString("source"));
        if (object.has("picture"))
            user.setProfilePic(object.getJSONObject("picture").getJSONObject("data").getString("url"));
        return user;
    }

    /**
     * Perform facebook sign in.<p>
     * NOTE: If you are signing from the fragment than you should call {@link #performSignIn(Fragment)}.<p>
     * This method should generally call when user clicks on "Sign in with Facebook" button.
     *
     * @param activity instance of the caller activity.
     */
    @SuppressWarnings("WeakerAccess")
    public void performSignIn(Activity activity) {
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "user_friends", "email"));
    }

    /**
     * Perform facebook login. This method should be called when you are signing in from
     * fragment.<p>
     * This method should generally call when user clicks on "Sign in with Facebook" button.
     *
     * @param fragment caller fragment.
     */
    @SuppressWarnings("WeakerAccess")
    public void performSignIn(Fragment fragment) {
        LoginManager.getInstance().logInWithReadPermissions(fragment, Arrays.asList("public_profile", "user_friends", "email"));
    }

    /**
     * This method handles onActivityResult callbacks from fragment or activity.
     *
     * @param requestCode request code received.
     * @param resultCode  result code received.
     * @param data        Data intent.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallBackManager.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("WeakerAccess")
    public void performSignOut() {
        LoginManager.getInstance().logOut();
        mListener.onFBSignOut();
    }
}

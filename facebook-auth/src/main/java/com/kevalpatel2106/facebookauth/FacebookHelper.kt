package com.kevalpatel2106.facebookauth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

/**
 * Created by multidots on 6/16/2016.
 * This class will initialize facebook login and handle other sdk functions.
 *
 * @property listener [FacebookResponse] listener to get call back response.
 * @property fieldString      name of the fields required. (e.g. latlong,name,email,gender,birthday,picture,cover)
 * See [&#39;https://developers.facebook.com/docs/graph-api/reference/user&#39;]['https://developers.facebook.com/docs/graph-api/reference/user'] for more info on user node.
 * @author 'https://github.com/kevalpatel2106'
 */
class FacebookHelper(private val listener: FacebookResponse,
                     private val fieldString: String) {

    /**
     * Get the [CallbackManager] for managing callbacks.
     *
     * @return [CallbackManager]
     */
    private val callbackManager = CallbackManager.Factory.create()

    init {
        //Prepare the login manager
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                //get the user profile
                getUserProfile(loginResult)
            }

            override fun onCancel() {
                listener.onFbSignInFail()
            }

            override fun onError(e: FacebookException) {
                Timber.e(e)
                listener.onFbSignInFail()
            }
        })
    }

    /**
     * Get user facebook profile.
     *
     * @param loginResult login result with user credentials.
     */
    private fun getUserProfile(loginResult: LoginResult) {
        val parameters = Bundle()
        parameters.putString("fields", fieldString)

        val request = GraphRequest.newMeRequest(loginResult.accessToken) { `object`, _ ->

            try {
                listener.onFbProfileReceived(parseResponse(`object`))
            } catch (e: Exception) {
                Timber.e(e)
                listener.onFbSignInFail()
            }
        }
        request.parameters = parameters
        request.executeAsync()
    }

    /**
     * Parse the response received into [FacebookUser] object.
     *
     * @param object response received.
     *
     * @return [FacebookUser] with required fields.
     *
     * @throws JSONException - If json parsing is failed.
     */
    @Throws(JSONException::class)
    internal fun parseResponse(`object`: JSONObject): FacebookUser {
        val user = FacebookUser(facebookID = `object`.getString("id"))
        if (`object`.has("email")) user.email = `object`.getString("email")
        if (`object`.has("name")) user.name = `object`.getString("name")
        if (`object`.has("gender"))
            user.gender = `object`.getString("gender")
        if (`object`.has("about")) user.about = `object`.getString("about")
        if (`object`.has("bio")) user.bio = `object`.getString("bio")
        if (`object`.has("cover"))
            user.coverPicUrl = `object`.getJSONObject("cover").getString("source")
        if (`object`.has("picture"))
            user.profilePic = "http://graph.facebook.com/" + user.facebookID + "/picture?type=normal"
        return user
    }

    /**
     * Perform facebook sign in.
     *
     *
     * NOTE: If you are signing from the fragment than you should call [.performSignIn].
     *
     *
     * This method should generally call when user clicks on "Sign in with Facebook" button.
     *
     * @param activity instance of the caller activity.
     */
    fun performSignIn(activity: Activity) {
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "user_friends", "email"))
    }

    /**
     * Perform facebook login. This method should be called when you are signing in from
     * fragment.
     *
     *
     * This method should generally call when user clicks on "Sign in with Facebook" button.
     *
     * @param fragment caller fragment.
     */
    fun performSignIn(fragment: Fragment) {
        LoginManager.getInstance().logInWithReadPermissions(fragment, Arrays.asList("public_profile", "user_friends", "email"))
    }

    /**
     * This method handles onActivityResult callbacks from fragment or activity.
     *
     * @param requestCode request code received.
     * @param resultCode  result code received.
     * @param data        Data intent.
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    fun performSignOut() {
        LoginManager.getInstance().logOut()
        listener.onFBSignOut()
    }
}

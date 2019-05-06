package com.glac.ecommerce.Mpesa1;

import android.content.SharedPreferences;

import com.glac.ecommerce.PostItemDesc;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by miles on 20/11/2017.
 */

public class MPESAInstanceIDService extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences sharedPreferences = getSharedPreferences(PostItemDesc.SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("InstanceID", refreshedToken);
        editor.commit();

    }

}
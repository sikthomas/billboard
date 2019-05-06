package com.glac.ecommerce.Mpesa1.interfaces;


import com.glac.ecommerce.Mpesa1.utils.Pair;

/**
 * Created by miles on 18/11/2017.
 */

public interface AuthListener {
    public void onAuthError(Pair<Integer, String> result);
    public void onAuthSuccess();
}

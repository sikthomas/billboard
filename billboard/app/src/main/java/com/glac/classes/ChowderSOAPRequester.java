package com.glac.classes;

import android.content.Context;

import com.alexgilleran.icesoap.request.impl.ApacheSOAPRequester;

/**
 * Created by ian on 04/09/2017.
 */

public class ChowderSOAPRequester extends ApacheSOAPRequester {

    private final Context context;

    public ChowderSOAPRequester(Context context) {
        this.context = context;
    }

    /*@Override
    protected SchemeRegistry getSchemeRegistry() {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme(HTTP_NAME, PlainSocketFactory.getSocketFactory(), DEFAULT_HTTP_PORT));
        schemeRegistry.register(new Scheme(HTTPS_NAME, Utils.hostnameVerification(context), DEFAULT_HTTPS_PORT));
        return schemeRegistry;
    }*/
}

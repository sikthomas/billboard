package com.glac.mpesab2c;

import android.support.annotation.NonNull;

public interface MpesaLib<Result> {
    void onResult(@NonNull Result result);

    void onError(String error);
}

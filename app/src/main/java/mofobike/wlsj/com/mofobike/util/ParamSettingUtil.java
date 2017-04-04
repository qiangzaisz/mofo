package mofobike.wlsj.com.mofobike.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xiangyang550 on 2017/4/2.
 */

public class ParamSettingUtil {


    public static boolean isTimerOpen(Context context) {
        SharedPreferences sp = context.getSharedPreferences("ParamSet", Context.MODE_PRIVATE);
        return sp.getBoolean("timerOpen", false);
    }

    public static void setTimerIsOpen(Context context, boolean isTimerOpen) {
        SharedPreferences sp = context.getSharedPreferences("ParamSet", Context.MODE_PRIVATE);
        sp.edit().putBoolean("timerOpen", isTimerOpen).apply();
    }
}

package mofobike.wlsj.com.mofobike.manager;

import android.os.Handler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BluetoothLogManager {


    private static StringBuffer sb = new StringBuffer();


    private static Handler MAIN_HANDLER = new Handler();

    private static final ArrayList<ILogListener> listeners = new ArrayList<>();


    public static void register(ILogListener listener) {
        listeners.add(listener);
    }

    public static void unregister(ILogListener listener) {
        listeners.remove(listener);
    }

    public static void log(String tag, String msg) {
        sb.append(new SimpleDateFormat("HH:mm:ss.sss  ").format(new Date())).append(msg).append("\n");
        for (final ILogListener listener : listeners) {
            MAIN_HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    listener.onLogRec(sb.toString());
                }
            });
        }

    }

    public interface ILogListener {
        void onLogRec(String s);
    }
}


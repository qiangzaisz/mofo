package mofobike.wlsj.com.mofobike.model;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;

import static com.android.volley.VolleyLog.TAG;

public class DataStreamBean {

    /**
     * errno : 0
     * data : {"create_time":"2017-03-25 01:25:43","update_at":"2017-03-25 01:41:38","id":"OFO","current_value":"IFO#A#N#1000.00000#E#01000.02000#10.0000#11.00#170322#123010.00#10#0200#0300#0400#O#090#01#02#11#@"}
     * error : succ
     */

    private int errno;
    private DataBean data;
    private String error;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public static class DataBean {
        /**
         * create_time : 2017-03-25 01:25:43
         * update_at : 2017-03-25 01:41:38
         * id : OFO
         * current_value : IFO#A#N#1000.00000#E#01000.02000#10.0000#11.00#170322#123010.00#10#0200#0300#0400#O#090#01#02#11#@
         */

        private String create_time;
        private String update_at;
        private String id;
        private String current_value;

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getUpdate_at() {
            return update_at;
        }

        public void setUpdate_at(String update_at) {
            this.update_at = update_at;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCurrent_value() {
            return current_value;
        }

        public void setCurrent_value(String current_value) {
            this.current_value = current_value;
        }


        public boolean isUnlocked() {
            String[] content = getCurrent_value().split("#");
            String lockStatus = content[4] + "(";
            if (content[4].equals("O")) {
                lockStatus += "开锁";
                return true;
            } else if (content[4].equals("C")) {
                lockStatus += "闭锁";
            } else {
                lockStatus += "未知";
            }
            return false;
        }


        public LatLng getLocation() {
            Log.i(TAG, "onDataRec");
            String[] content = getCurrent_value().split("#");

            try {
                double latitude = Double.valueOf(content[11]) / 100;
                if (latitude > 100) {
                    return new LatLng(22.574823, 114.122285);
                }
                double longitude = Double.valueOf(content[13]) / 100;
                LatLng latLng = new LatLng(latitude, longitude);
                return latLng;
            } catch (Exception e) {

            }
            return new LatLng(22.574823, 114.122285);

        }
    }
}

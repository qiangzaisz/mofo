package mofobike.wlsj.com.mofobike.model;

import java.util.List;

public class DeviceListBean {
    /**
     * errno : 0
     * data : {"per_page":30,"devices":[{"private":false,"protocol":"EDP","create_time":"2017-03-25 02:01:07","online":false,"location":{"lon":0,"lat":0},"id":"5095226","auth_info":"861853030676373","title":"device_for_kong","tags":[]},{"private":true,"protocol":"EDP","create_time":"2017-03-25 01:23:00","online":false,"location":{"lon":0,"lat":0},"auth_info":"861853030676662","id":"5095205","title":"szlt","tags":[]},{"private":true,"protocol":"EDP","create_time":"2017-03-23 20:38:04","online":false,"location":{"lon":0,"lat":0},"auth_info":"2016CP0514","id":"5066875","title":"lttest","tags":[]},{"private":true,"protocol":"EDP","create_time":"2017-03-23 00:00:08","online":false,"location":{"lon":0,"lat":0},"auth_info":"1","id":"5041913","title":"test2","tags":[]},{"private":true,"protocol":"EDP","create_time":"2017-03-20 22:13:36","online":false,"location":{"lon":0,"lat":0},"auth_info":"2162751728725","id":"5022165","title":"test","tags":[]},{"private":false,"protocol":"EDP","create_time":"2017-03-20 19:06:33","online":false,"location":{"lon":0,"lat":0},"id":"5020736","auth_info":"Testsam01","title":"ZLJT0001T","tags":[]}],"total_count":6,"page":1}
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
         * per_page : 30
         * devices : [{"private":false,"protocol":"EDP","create_time":"2017-03-25 02:01:07","online":false,"location":{"lon":0,"lat":0},"id":"5095226","auth_info":"861853030676373","title":"device_for_kong","tags":[]},{"private":true,"protocol":"EDP","create_time":"2017-03-25 01:23:00","online":false,"location":{"lon":0,"lat":0},"auth_info":"861853030676662","id":"5095205","title":"szlt","tags":[]},{"private":true,"protocol":"EDP","create_time":"2017-03-23 20:38:04","online":false,"location":{"lon":0,"lat":0},"auth_info":"2016CP0514","id":"5066875","title":"lttest","tags":[]},{"private":true,"protocol":"EDP","create_time":"2017-03-23 00:00:08","online":false,"location":{"lon":0,"lat":0},"auth_info":"1","id":"5041913","title":"test2","tags":[]},{"private":true,"protocol":"EDP","create_time":"2017-03-20 22:13:36","online":false,"location":{"lon":0,"lat":0},"auth_info":"2162751728725","id":"5022165","title":"test","tags":[]},{"private":false,"protocol":"EDP","create_time":"2017-03-20 19:06:33","online":false,"location":{"lon":0,"lat":0},"id":"5020736","auth_info":"Testsam01","title":"ZLJT0001T","tags":[]}]
         * total_count : 6
         * page : 1
         */

        private int per_page;
        private int total_count;
        private int page;
        private List<DevicesBean> devices;

        public int getPer_page() {
            return per_page;
        }

        public void setPer_page(int per_page) {
            this.per_page = per_page;
        }

        public int getTotal_count() {
            return total_count;
        }

        public void setTotal_count(int total_count) {
            this.total_count = total_count;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public List<DevicesBean> getDevices() {
            return devices;
        }

        public void setDevices(List<DevicesBean> devices) {
            this.devices = devices;
        }

        public static class DevicesBean {
            /**
             * private : false
             * protocol : EDP
             * create_time : 2017-03-25 02:01:07
             * online : false
             * location : {"lon":0,"lat":0}
             * id : 5095226
             * auth_info : 861853030676373
             * title : device_for_kong
             * tags : []
             */

            private boolean privateX;
            private String protocol;
            private String create_time;
            private boolean online;
            private LocationBean location;
            private String id;
            private String auth_info;
            private String title;
            private List<?> tags;

            public boolean isPrivateX() {
                return privateX;
            }

            public void setPrivateX(boolean privateX) {
                this.privateX = privateX;
            }

            public String getProtocol() {
                return protocol;
            }

            public void setProtocol(String protocol) {
                this.protocol = protocol;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public boolean isOnline() {
                return online;
            }

            public void setOnline(boolean online) {
                this.online = online;
            }

            public LocationBean getLocation() {
                return location;
            }

            public void setLocation(LocationBean location) {
                this.location = location;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getAuth_info() {
                return auth_info;
            }

            public void setAuth_info(String auth_info) {
                this.auth_info = auth_info;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public List<?> getTags() {
                return tags;
            }

            public void setTags(List<?> tags) {
                this.tags = tags;
            }

            public static class LocationBean {
                /**
                 * lon : 0
                 * lat : 0
                 */

                private int lon;
                private int lat;

                public int getLon() {
                    return lon;
                }

                public void setLon(int lon) {
                    this.lon = lon;
                }

                public int getLat() {
                    return lat;
                }

                public void setLat(int lat) {
                    this.lat = lat;
                }
            }
        }
    }
}

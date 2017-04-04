package mofobike.wlsj.com.mofobike.model;

public class BikeStatus {


    public byte[] data;
    /**
     * 0x01-Lock，0x00-UNLOCKED
     *
     * @return
     */
    public byte lockStatus;

    /**
     * 0-100,show by percent
     *
     * @return
     */
    public byte batterStatus;

    /**
     * length must be 3,XYZ degree.
     *
     * @return
     */
    public short[] degree;

    /**
     * 是否震动
     *
     * @return
     */
    public byte shakeStatus;
}

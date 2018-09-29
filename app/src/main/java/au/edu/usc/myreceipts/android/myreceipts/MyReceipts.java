package au.edu.usc.myreceipts.android.myreceipts;



import android.location.Location;

import java.util.Date;
import java.util.UUID;

//model layer
public class MyReceipts {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private String mShopName;
    private boolean mReceiptSent;
    private String mReceipt;
    private String mComments;
    private String mLatitude;
    private String mLongitude;
    private Location mLocation;


    public MyReceipts() {
        // Generate unique identifier
        this(UUID.randomUUID());
    }

    //return a MyReceipts with an appropriate UUID (constructor)
    public MyReceipts(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {

        return mId;
    }

    public String getTitle() {

        return mTitle;
    }

    public void setTitle(String title) {

        mTitle = title;
    }

    public String getShopName() {

        return mShopName;
    }

    public void setShopName(String shopName) {

        mShopName = shopName;
    }

    public Date getDate() {

        return mDate;
    }

    public void setDate(Date date) {

        mDate = date;
    }

    public boolean isReceiptSent() {

        return mReceiptSent;
    }

    public void setReceiptSent(boolean receiptsent) {

        mReceiptSent = receiptsent;
    }


    public String getRecepit() {

        return mReceipt;
    }

    public void setReceipt(String receipt) {

    mReceipt = receipt;
    }

    public String getComments() {

        return mComments;
    }

    public void setComments(String comments) {

        mComments = comments;
    }

    public String getPhotoFilename() {

        return "IMG_" + getId().toString() + ".jpg";
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String Longitude) {
        mLongitude = Longitude;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String Latitude) {
        mLatitude = Latitude;
    }

}
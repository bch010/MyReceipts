package au.edu.usc.myreceipts.android.myreceipts;



import java.util.Date;
import java.util.UUID;

//model layer
public class MyReceipts {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private String mShopName;
    private boolean mSolved;
    private String mReceipt;


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

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {

        mSolved = solved;
    }


}
package au.edu.usc.myreceipts.android.myreceipts;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;
import java.util.UUID;


public class MyReceiptsPagerActivity extends AppCompatActivity
        implements MyReceiptsFragment.Callbacks {

    private static final String EXTRA_MYRECEIPT_ID = "au.edu.usc.myreceipts.android.myreceipts.myreceipt_id";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private ViewPager mViewPager;
    private List <MyReceipts> mMyReceipts;
    private static Context context;
    private LocationManager locationManager;
    private boolean gpsStatus = false;
    private boolean gpsServiceAvailable = false;

    public boolean mLocationPermissionGranted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myreceipts_pager);
        gpsServiceAvailable = isServicesOk();
        gpsStatus = checkGpsStatus();
        getLocationPermission();


        if (mLocationPermissionGranted == false) {
            finish();
        } else if (!gpsStatus) {
            Toast.makeText(getApplicationContext(), "Turn on your GPS first", Toast.LENGTH_LONG).show();
            finish();
        }

        UUID receiptId = (UUID) getIntent().getSerializableExtra(EXTRA_MYRECEIPT_ID);

        mViewPager = findViewById(R.id.activity_myreceipts_pager_view_pager);
        mMyReceipts = MyReceiptsObjects.get(this).getMyReceipts();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                MyReceipts myReceipts = mMyReceipts.get(position);
                return MyReceiptsFragment.newInstance(myReceipts.getId());
            }

            @Override
            public int getCount() {

                return mMyReceipts.size();
            }
        });

        for (int i = 0; i < mMyReceipts.size(); i++) {
            if (mMyReceipts.get(i).getId().equals(receiptId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    public static Intent newIntent(Context packageContext, UUID myReceiptsId) {
        Intent intent = new Intent(packageContext, MyReceiptsPagerActivity.class);
        intent.putExtra(EXTRA_MYRECEIPT_ID, myReceiptsId);
        return intent;
    }

    //google map connectivity methods
    public boolean isServicesOk() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MyReceiptsPagerActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MyReceiptsPagerActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Cant make map request", Toast.LENGTH_LONG).show();
        }
        return false;

    }

    private boolean checkGpsStatus() {
        context = getApplicationContext();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    //get map permissions
    private void getLocationPermission() {
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                //initMap();
            } else {
                ActivityCompat.requestPermissions((Activity) context, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;

                }

            }
        }
    }

    @Override
    public void onMyReceiptsUpdated(MyReceipts myReceipts) {

    }
}
package au.edu.usc.myreceipts.android.myreceipts;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class MyReceiptsFragment extends Fragment {

    private static final String ARG_MYRECEIPTS_ID = "myReceipts_id";

    // Dialog tags
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_IMAGE = "DialogImage";
    private static final int ERROR_DIALOG_REQUEST = 1001;

    // request codes
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 2;

    // Widgets
    private MyReceipts mMyReceipts;
    private EditText mTitleField;
    private Button mDateButton;
    private EditText mShopNameField;
    private EditText mCommentsField;
    private CheckBox mReceiptSentCheckBox;
    private Callbacks mCallbacks;
    private Button mReportButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private ViewTreeObserver mPhotoTreeObserver;
    private Point mPhotoViewSize;
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private Button mShowMapButton;
    private Context context = getActivity();
    private LocationManager locationManager;
    private boolean gpsServiceAvailable = false;
    private boolean gpsStatus = false;
    public String currentLocation;

    public interface Callbacks {
        void onMyReceiptsUpdated(MyReceipts myReceipts);
    }

    public static MyReceiptsFragment newInstance(UUID myReceiptsId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MYRECEIPTS_ID, myReceiptsId);

        MyReceiptsFragment fragment = new MyReceiptsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID myReceiptsId = (UUID) getArguments().getSerializable(ARG_MYRECEIPTS_ID);

        mMyReceipts = MyReceiptsObjects.get(getActivity()).getMyReceipt(myReceiptsId);
        mPhotoFile = MyReceiptsObjects.get(getActivity()).getPhotoFile(mMyReceipts);

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        gpsServiceAvailable = isServicesOk();
        gpsStatus = checkGpsStatus();

        if (gpsServiceAvailable) {
            if (gpsStatus) {
                //mShowMapButton.setEnabled(true);
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener <Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
//                            Toast.makeText(getContext(), location.toString(), Toast.LENGTH_LONG).show();

                            if (mMyReceipts.getLocation() == null) {
                                currentLocation = location.getLatitude() + "," + location.getLongitude();
                                mMyReceipts.setLocation(currentLocation);
                                MyReceiptsObjects.get(getActivity()).updateMyReceipts(mMyReceipts);
                                updateLocationView();
//                                Toast.makeText(getContext(),"onSuccess "+currentLocation,Toast.LENGTH_SHORT).show();
                            } else {

                                updateLocationView();
                            }
                        } else
                            Toast.makeText(getContext(), "Location is null", Toast.LENGTH_LONG).show();
                    }

                });
            } else {
//                    mShowMapButton.setEnabled(false);
                Toast.makeText(getActivity(), "Enable Location on your device", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_myreceipts, container, false);
        setHasOptionsMenu(true);

        // Setup button's
        mTitleField = v.findViewById(R.id.myReceipts_title); //list_item_receipt.xml
        mShopNameField = v.findViewById(R.id.myReceipts_shopname);
        mCommentsField = v.findViewById(R.id.myReceipts_comments);
        mDateButton = v.findViewById(R.id.myReceipts_date);
        mReceiptSentCheckBox = v.findViewById(R.id.myReceipts_sent);
        mPhotoButton = v.findViewById(R.id.myReceipts_camera); //
        mReportButton = v.findViewById(R.id.myReceipts_report);
        mPhotoView = v.findViewById(R.id.myReceipts_photo);
        mShowMapButton = v.findViewById(R.id.myReceipts_location);
        mLatitudeTextView = v.findViewById(R.id.myReceipts_latitude);
        mLongitudeTextView = v.findViewById(R.id.myReceipts_longitude);



        // Photos
        PackageManager packageManager = getActivity().getPackageManager();
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        // / If a camera is not available, disable the camera functionality
//        if (canTakePhoto) {
//            Uri uri = Uri.fromFile(mPhotoFile);
//            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(), "au.edu.usc.myreceipts.android.myreceipts.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List <ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager()
                        .queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        // On image click, open zoomed image dialog
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                ImageDisplayFragment ImageFragment = ImageDisplayFragment.newInstance(mPhotoFile);

                ImageFragment.show(fm, DIALOG_IMAGE);
            }
        });

        mTitleField.setText(mMyReceipts.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mMyReceipts.setTitle(charSequence.toString());
                updateMyReceipts();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Also intentionally left blank
            }
        });

        mShopNameField.setText(mMyReceipts.getShopName());
        mShopNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mMyReceipts.setShopName(charSequence.toString());
                updateMyReceipts();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Also intentionally left blank
            }
        });

        if (!gpsStatus) mShowMapButton.setEnabled(false);
        mShowMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] coords = getLocationCords();
                if (mMyReceipts.getLocation() == null) {
                    Toast.makeText(getContext(), "The receipt doesn't have specified coordinates",
                            Toast.LENGTH_SHORT).show();
                    mShowMapButton.setEnabled(false);

                } else if (coords.length == 2) {
                    LatLng latLng = new LatLng(coords[0], coords[1]);
                    Intent intent = MyReceiptsMapActivity.newIntent(getActivity(), latLng);
                    startActivity(intent);
                }
            }
        });

        mCommentsField.setText(mMyReceipts.getComments());
        mCommentsField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mMyReceipts.setComments(charSequence.toString());
                updateMyReceipts();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Also intentionally left blank
            }
        });

        String formatDate = DateFormat.format("EEEE, MMM dd, yyyy", mMyReceipts.getDate()).toString();
        mDateButton.setText(formatDate);

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mMyReceipts.getDate());
                dialog.setTargetFragment(MyReceiptsFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        // Setup checkbox

        mReceiptSentCheckBox.setChecked(mMyReceipts.isReceiptSent());
        mReceiptSentCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mMyReceipts.setReceiptSent(isChecked);
                updateMyReceipts();
            }
        });

        mDateButton.setEnabled(true);

        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getReceiptReport())
                        .setSubject(getString(R.string.myReceipts_report_subject))
                        .setChooserTitle(getString(R.string.send_report))
                        .startChooser();
            }
        });

        mPhotoTreeObserver = mPhotoView.getViewTreeObserver();
        mPhotoTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mPhotoViewSize = new Point();
                mPhotoViewSize.set(mPhotoView.getWidth(), mPhotoView.getHeight());

                updatePhotoView();
            }
        });

        return v;
    }

    // For sending a formatted email/sms report
    private String getReceiptReport() {

        String dateFormat = "EEE, MMM dd, yyyy";
        String dateString = DateFormat.format(dateFormat, mMyReceipts.getDate()).toString();
        String shopNameString = getString(R.string.myReceipts_report_shopname) + mMyReceipts.getShopName();
        String commentsString = getString(R.string.myReceipts_report_comments) + mMyReceipts.getComments();

        /// Return the report
        return getString(R.string.receipt_report,
                mMyReceipts.getTitle(), dateString, shopNameString, commentsString, mMyReceipts.getPhotoFilename());
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
            mPhotoView.setClickable(false);
            mPhotoView.setContentDescription(getString(R.string.myReceipts_photo_no_image_description));
        } else {
            mPhotoView.setClickable(true);
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), mPhotoViewSize.x, mPhotoViewSize.y);
            mPhotoView.setImageBitmap(bitmap);
            mPhotoView.setContentDescription(getString(R.string.myReceipts_photo_image_description));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mMyReceipts.setDate(date);
            mDateButton.setText(mMyReceipts.getDate().toString());

            updateMyReceipts();

        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "au.edu.usc.myreceipts.android.myreceipts.fileprovider", mPhotoFile);
            // Remove temporary write access to file from camera
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhotoView();
            updateMyReceipts();
            updateLocationView();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        MyReceiptsObjects.get(getActivity()).updateMyReceipts(mMyReceipts);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private void updateLocationView() {
        if (mMyReceipts.getLocation() != null) {
            String[] shopLocation = mMyReceipts.getLocation().split(",");
            float longitude = Float.parseFloat(shopLocation[0].trim());
            float latitude = Float.parseFloat(shopLocation[1].trim());

            mLongitudeTextView.setText("     Long: " + String.format("%.3f", longitude));
            mLatitudeTextView.setText("    Lat: " + String.format("%.3f", latitude));
        }
    }

    private float[] getLocationCords() {
        String[] shopLocation = mMyReceipts.getLocation().split(",");
        float longitude = Float.parseFloat(shopLocation[0].trim());
        float latitude = Float.parseFloat(shopLocation[1].trim());

        return new float[]{longitude, latitude};
    }

    public void finishFragment() {

        if (NavUtils.getParentActivityName(getActivity()) != null) {
            NavUtils.navigateUpFromSameTask(getActivity());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putSerializable(ARG_MYRECEIPTS_ID, mMyReceipts.getId());

        super.onSaveInstanceState(outState);
    }

    private void deleteMyReceipts() {
        MyReceiptsObjects myReceiptsObjects = MyReceiptsObjects.get(getActivity());
        myReceiptsObjects.deleteMyReceipts(mMyReceipts);
        mCallbacks.onMyReceiptsUpdated(mMyReceipts);

        Toast.makeText(getActivity(), R.string.toast_delete_myReceipts, Toast.LENGTH_SHORT).show();
        finishFragment();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_myreceipts, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_single_myReceipts:
                UUID myReceiptId = (UUID) getArguments().getSerializable(ARG_MYRECEIPTS_ID);
                MyReceiptsObjects myReceiptsObjects = MyReceiptsObjects.get(getActivity());
                mMyReceipts = myReceiptsObjects.getMyReceipt(myReceiptId);
                myReceiptsObjects.deleteMyReceipts(mMyReceipts);
                getActivity().finish();
                deleteMyReceipts();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateMyReceipts() {
        MyReceiptsObjects.get(getActivity()).updateMyReceipts(mMyReceipts);
        mCallbacks.onMyReceiptsUpdated(mMyReceipts);
    }

    //google map connectivity methods
    public boolean isServicesOk() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(context, "Cant make map request", Toast.LENGTH_LONG).show();
        }
        return false;

    }

    private boolean checkGpsStatus() {

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
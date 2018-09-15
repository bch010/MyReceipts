package au.edu.usc.myreceipts.android.myreceipts;




import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;



public class MyReceiptsFragment extends Fragment {

    private static final String ARG_MYRECEIPTS_ID = "myReceipts_id";

    // Dialog tags
    private static final String DIALOG_DATE = "DialogDate";


    // request codes
    private static final int REQUEST_DATE = 0;
    public static final int REQUEST_CONTACT = 5;
    private static final int REQUEST_PHOTO= 2;
    private static final String DIALOG_IMAGE = "DialogImage";

    private static final String TAG = "MyReceiptsFragment";

    // Widgets
    private MyReceipts mMyReceipts;
    private EditText mTitleField;
    private Button mDateButton;
    private EditText mShopNameField;
    private EditText mCommentsField;
    private CheckBox mSolvedCheckBox;
    private Callbacks mCallbacks;
    private Button mReportButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Button myReceiptButton;
    private File mPhotoFile;
    private ViewTreeObserver mPhotoTreeObserver;
    private Point mPhotoViewSize;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_myreceipts, container, false);
        setHasOptionsMenu(true);

        mTitleField = v.findViewById(R.id.myReceipts_title); //list_item_receipt.xml
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

        mShopNameField = v.findViewById(R.id.myReceipts_shopname);
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

        mCommentsField = v.findViewById(R.id.myReceipts_comments);
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

        // Setup date button
        mDateButton = v.findViewById(R.id.myReceipts_date);

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
        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mMyReceipts.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mMyReceipts.setSolved(isChecked);
                updateMyReceipts();
            }
        });


        mReportButton = v.findViewById(R.id.myReceipts_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getReceiptReport());
                i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.myReceipts_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        myReceiptButton = v.findViewById(R.id.myReceipts_receipt);
        myReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mMyReceipts.getRecepit() != null) {
            myReceiptButton.setText(mMyReceipts.getRecepit());
        }

        // Disable the choose receipt button to prevent crash when no contacts in app are available
        PackageManager packageManager = Objects.requireNonNull(getActivity()).getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            myReceiptButton.setEnabled(false);
        }



        // Setup photo taking functions
        mPhotoView =  v.findViewById(R.id.myReceipts_photo);
        mPhotoButton = v.findViewById(R.id.myReceipts_camera); //

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled (canTakePhoto);

        // If a camera is not available, disable the camera functionality
        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } 

        mPhotoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "au.edu.usc.myreceipts.android.myreceipts.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });



        // On image click, open zoomed image dialog
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                ImageDisplayFragment fragment = ImageDisplayFragment.newInstance(mPhotoFile);
                assert fragmentManager != null;
                fragment.show(fragmentManager, DIALOG_IMAGE);
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
        String solvedString = null;
        if (mMyReceipts.isSolved()) {
            solvedString = getString(R.string.myReceipts_shopname);
        } else {
            solvedString = getString(R.string.myReceipts_shopname);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,
                mMyReceipts.getDate()).toString();
        String receipt = mMyReceipts.getRecepit();
        if (receipt == null) {
            receipt = getString(R.string.myReceipts_report_no_receipt);
        } else {
            receipt = getString(R.string.myReceipts_report_receipt, receipt);
        }

        // return the receipt report

        String report = getString(R.string.receipt_report,
                mMyReceipts.getTitle(), dateString, solvedString, receipt);
        return report;
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
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mMyReceipts.setDate(date);
            mDateButton.setText(mMyReceipts.getDate().toString());

            updateMyReceipts();

        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Specify which fields you want your query to return
            // values for.
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts._ID
            };


            // Perform your query - the contactUri is like a "where"
            // clause here
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

            try {
                // Double-check that results were actually received
                if (c != null && c.getCount() == 0) {
                    return;
                }

                updateMyReceipts();
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", mPhotoFile);
            // Remove temporary write access to file from camera
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            mPhotoView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Announce to screenreader
                    mPhotoView.announceForAccessibility(getString(R.string.photo_announcement));
                }
            }, 500);

            updateMyReceipts();
            updatePhotoView();
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_myreceipts, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
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

}
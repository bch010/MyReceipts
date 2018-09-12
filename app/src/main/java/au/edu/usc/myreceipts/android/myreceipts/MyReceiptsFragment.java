package au.edu.usc.myreceipts.android.myreceipts;




import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Date;
import java.util.UUID;

public class MyReceiptsFragment extends Fragment {

    private static final String ARG_MYRECEIPTS_ID = "myReceipts_id";

    // Dialog tags
    private static final String DIALOG_DATE = "DialogDate";


    // request codes
    private static final int REQUEST_DATE = 0;


    public static final int REQUEST_CONTACT = 5;

    private static final String TAG = "MyReceiptsFragment";

    // Widgets
    private MyReceipts mMyReceipts;

    private EditText mTitleField;
    private Button mDateButton;
    private EditText mShopNameField;
    private CheckBox mSolvedCheckBox;
    private Callbacks mCallbacks;

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
                mMyReceipts.setTitle(charSequence.toString());
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

        return v;
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
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            try {
                // Double-check that results were actually received
                if (c != null && c.getCount() == 0) {
                    return;
                }

                updateMyReceipts();
            } finally {
                c.close();
            }
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
//                myReceiptsObjects.deleteMyReceipts(mMyReceipts);
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
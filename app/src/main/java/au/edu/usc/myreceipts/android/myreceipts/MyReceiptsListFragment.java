package au.edu.usc.myreceipts.android.myreceipts;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import java.util.Objects;

public class MyReceiptsListFragment extends Fragment {

    private static final String TAG = "MyReceiptsListFragment";
    private static final String SAVE_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mReceiptsRecyclerView;
    private MyReceiptsAdapter mAdapter;
    private boolean mSubtitleVisible;

    private Callbacks mCallBacks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); //  state that the fragment has an “options menu.”
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_myreceipts_list, menu);

        MenuItem subtitle = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitle.setTitle(R.string.hide_subtitle);
        } else {
            subtitle.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // when a tool bar item is selected
        switch (item.getItemId()) {
            case R.id.new_myReceipts:
                addAReceipt();
                return true;
//
            case R.id.help_myReceipts:
                Intent i = new Intent(getActivity(), MyReceiptsWebView.class);
                startActivity(i);
                return false;

            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:

                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * add single receipt method
     */
    private void addAReceipt() {
        MyReceipts receipt = new MyReceipts();
        MyReceiptsObjects.get(getActivity()).addMyReceipts(receipt);
        updateUI();
        mCallBacks.onMyReceiptsSelected(receipt);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_myreceipts_list, container, false);

        mReceiptsRecyclerView = v.findViewById(R.id.myReceipts_recycler_view);
        mReceiptsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVE_SUBTITLE_VISIBLE);
        }

        updateUI();
        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVE_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallBacks = (Callbacks) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getClass().getSimpleName() +
                    " must implement MyReceiptsFragment.Callbacks");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBacks = null;
    }

    public void updateUI() {
        MyReceiptsObjects myReceiptsObjects = MyReceiptsObjects.get(getContext());
        List <MyReceipts> myReceipts = myReceiptsObjects.getMyReceipts();

        if (mAdapter == null) {
            mAdapter = new MyReceiptsAdapter(myReceipts);
            mReceiptsRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setMyReceipts(myReceipts);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private class MyReceiptsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mShopNameView;
        private MyReceipts mMyReceipts;
        private ImageView mSolvedImageView;

        public MyReceiptsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_receipt, parent, false));

            mTitleTextView = itemView.findViewById(R.id.myReceipts_title);
            mDateTextView = itemView.findViewById(R.id.myReceipts_date);
            mShopNameView = itemView.findViewById(R.id.myReceipts_shopname);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);

            itemView.setOnClickListener(this);
        }

        // bind myReceipts details to textviews
        public void bind(MyReceipts myReceipts) {
            mMyReceipts = myReceipts;
            mTitleTextView.setText(myReceipts.getTitle());
            mDateTextView.setText(myReceipts.getDate().toString());
            String formatDate = DateFormat.format("EEEE, MMM dd, yyyy", myReceipts.getDate()).toString();
            mDateTextView.setText(formatDate);
            mShopNameView.setText(myReceipts.getShopName());
            mSolvedImageView.setVisibility(myReceipts.isSolved() ? View.VISIBLE : View.GONE);

        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, " Date button clicked: ");

            mCallBacks.onMyReceiptsSelected(mMyReceipts);
        }
    }

    private class MyReceiptsAdapter extends RecyclerView.Adapter <MyReceiptsHolder> {
        private List <MyReceipts> mMyReceipts;

        public MyReceiptsAdapter(List <MyReceipts> myReceipts) {

            mMyReceipts = myReceipts;
        }

        @Override
        public MyReceiptsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            return new MyReceiptsHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(MyReceiptsHolder holder, int position) {

            MyReceipts myReceipts = mMyReceipts.get(position);
            holder.bind(myReceipts);
        }

        @Override
        public int getItemCount() {
            return mMyReceipts.size();
        }

        public void setMyReceipts(List <MyReceipts> myReceipts) {


            mMyReceipts = myReceipts;
        }
    }

    private void updateSubtitle() {
        MyReceiptsObjects myReceiptsObjects = MyReceiptsObjects.get(getContext());
        int myReceiptsCount = myReceiptsObjects.getMyReceipts().size();
        String subtitle = getString(R.string.subtitle_format, myReceiptsCount);
        if (!mSubtitleVisible) {
            subtitle = null;
        }
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setSubtitle(subtitle);
    }

    public interface Callbacks {
        void onMyReceiptsSelected(MyReceipts myReceipts);
    }

}
package au.edu.usc.myreceipts.android.myreceipts;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;


public class MyReceiptsPagerActivity extends AppCompatActivity
        implements MyReceiptsFragment.Callbacks {

    private static final String EXTRA_CRIME_ID = "usc.ICT311.android.MyReceipts.crime_id";

    private ViewPager mViewPager;
    private List<MyReceipts> mMyReceipts;

    public static Intent newIntent(Context packageContext, UUID myReceiptsId) {
        Intent intent = new Intent(packageContext, MyReceiptsPagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, myReceiptsId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myreceipts_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

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
            if (mMyReceipts.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public void onMyReceiptsUpdated(MyReceipts myReceipts) {

    }
}
package au.edu.usc.myreceipts.android.myreceipts;

import android.content.Intent;
import android.support.v4.app.Fragment;


public class MyReceiptsListActivity extends SingleFragmentActivity
        implements MyReceiptsListFragment.Callbacks,
        MyReceiptsFragment.Callbacks {

    @Override
    protected Fragment createFragment() {

        return new MyReceiptsListFragment();
    }

    @Override
    protected int getLayoutResId() {

        return R.layout.activity_masterdetail;
    }

    @Override
    public void onMyReceiptsSelected(MyReceipts myReceipts) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = MyReceiptsPagerActivity.newIntent(this, myReceipts.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = MyReceiptsFragment.newInstance(myReceipts.getId());

            getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container, newDetail).commit();
        }
    }

    @Override
    public void onMyReceiptsUpdated(MyReceipts myReceipts) {
        MyReceiptsListFragment listFragment = (MyReceiptsListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        assert listFragment != null;
        listFragment.updateUI();
    }
}
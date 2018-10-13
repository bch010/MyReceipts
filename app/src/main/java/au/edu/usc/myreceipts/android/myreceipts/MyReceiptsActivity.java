package au.edu.usc.myreceipts.android.myreceipts;

import   android.support.v4.app.Fragment;

import java.util.UUID;

public class MyReceiptsActivity extends SingleFragmentActivity {


    public static final String EXTRA_RECEIPT_ID ="au.edu.usc.myreceipts.android.myreceipts_myReceiptsId" ;
    protected Fragment createFragment() {
        UUID id = (UUID)getIntent().getSerializableExtra(EXTRA_RECEIPT_ID);
        return  MyReceiptsFragment.newInstance(id);
    }


}

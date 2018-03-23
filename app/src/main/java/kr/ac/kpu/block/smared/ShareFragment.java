package kr.ac.kpu.block.smared;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class ShareFragment extends Fragment{


        Fragment fragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.lednavi_input:
                    fragment = new ShareLedgerRegFragment();
                    switchFragment(fragment);
                    return true;
                case R.id.lednavi_output:
                    fragment = new ShareLedgerViewFragment();
                    switchFragment(fragment);
                    return true;
                case R.id.lednavi_statistic:
                    fragment = new LedgerStatFragment();
                    switchFragment(fragment);
                    return true;
            }
            return false;
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_share, container, false);



        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ShareLedgerRegFragment fragment = new ShareLedgerRegFragment();
        fragmentTransaction.add(R.id.shareledger,fragment);
        fragmentTransaction.commit();


        BottomNavigationView navigation = (BottomNavigationView) v.findViewById(R.id.sharenavi);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        return v;
    }


    public void switchFragment(Fragment fragment) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.shareledger, fragment);
// Commit the transaction
        transaction.commit();

    }

    }
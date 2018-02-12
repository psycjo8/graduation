package kr.ac.kpu.block.smared;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment {


        Fragment fragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.lednavi_input:
                    fragment = new LedgerRegFragment();
                    switchFragment(fragment);
                    return true;
                case R.id.lednavi_output:
                    fragment = new LedgerViewFragment();
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

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LedgerRegFragment fragment = new LedgerRegFragment();
        fragmentTransaction.add(R.id.ledger,fragment);
        fragmentTransaction.commit();


        BottomNavigationView navigation = (BottomNavigationView) v.findViewById(R.id.lednavi);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        return v;
    }


    public void switchFragment(Fragment fragment) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.ledger, fragment);
// Commit the transaction
        transaction.commit();

    }

    }
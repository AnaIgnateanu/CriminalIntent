package com.example.ana.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by ana on 1/25/2017.
 */

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}

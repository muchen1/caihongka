package com.rainbowcard.client.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.View;


/**
 * Created by kleist on 14-7-26.
 */
public abstract class SimpleFragmentSwitcher {
    private FragmentManager mFragmentManager;
    private SparseArray<Fragment> mFragmentSparseArray;

    private int mCurrentPos = -1;


    public SimpleFragmentSwitcher(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
        mFragmentSparseArray = new SparseArray<Fragment>();
    }

    public abstract Fragment getItem(int pos);

    public void showFragment(int viewID, int pos) {
        if (mCurrentPos == pos) {
            return;
        }
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        hideAllFragment(transaction);

        Fragment fragment = mFragmentSparseArray.get(pos);
        if (fragment == null) {
            fragment = getItem(pos);
            mFragmentSparseArray.put(pos, fragment);
            transaction.add(viewID, fragment);
        } else {
            transaction.show(fragment);
        }

        mCurrentPos = pos;
        transaction.commit();
    }

    public void showFragment(View view, int pos) {
        showFragment(view.getId(), pos);
    }


    private String makeFragmentName(int viewId, int pos) {
        return String.format("%1$d%2$d", viewId, pos);
    }

    public int getCurrentPos() {
        return mCurrentPos;
    }

    public Fragment getCurrentItem() {
        return mFragmentSparseArray.get(mCurrentPos);
    }

    private void hideAllFragment(FragmentTransaction transaction) {
        for (int i = 0, len = mFragmentSparseArray.size(); i < len; i++) {
            transaction.hide(mFragmentSparseArray.valueAt(i));
        }
    }

}

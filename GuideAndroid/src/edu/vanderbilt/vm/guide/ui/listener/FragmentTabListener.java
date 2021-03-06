
package edu.vanderbilt.vm.guide.ui.listener;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * FragmentTabListener switches between fragments when a tab is clicked. Code
 * borrowed from
 * http://developer.android.com/guide/topics/ui/actionbar.html#Tabs.
 * 
 * @author nicholasking
 * @param <T> The fragment's class
 */
public class FragmentTabListener<T extends Fragment> implements ActionBar.TabListener {
    private Fragment mFragment;

    private final SherlockFragmentActivity mActivity;

    private final String mTag;

    private final Class<T> mClass;

    /**
     * Constructor used each time a new tab is created.
     * 
     * @param activity The host Activity, used to instantiate the fragment
     * @param tag The identifier tag for the fragment
     * @param clz The fragment's Class, used to instantiate the fragment
     */
    public FragmentTabListener(SherlockFragmentActivity activity, String tag, Class<T> clz) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;
    }

    /* The following are each of the ActionBar.TabListener callbacks */

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // Check if the fragment is already initialized
        if (mFragment == null) {
            // If not, instantiate and add it to the activity
            mFragment = Fragment.instantiate(mActivity, mClass.getName());
            ft.add(android.R.id.content, mFragment, mTag);
        } else {
            // If it exists, simply attach it in order to show it
            ft.attach(mFragment);
        }
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        if (mFragment != null) {
            // Detach the fragment, because another one is being attached
            ft.detach(mFragment);
        }
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // User selected the already selected tab. Usually do nothing.
    }
}

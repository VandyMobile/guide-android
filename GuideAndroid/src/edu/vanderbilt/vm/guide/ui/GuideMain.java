package edu.vanderbilt.vm.guide.ui;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.ui.adapter.SwipingTabsAdapter;
import edu.vanderbilt.vm.guide.ui.listener.ActivityTabListener;
import edu.vanderbilt.vm.guide.util.Geomancer;
import edu.vanderbilt.vm.guide.util.GlobalState;

/**
 * The main Activity of the Guide app.  Contains the 4 main tabs:
 * Map, Tours, Places, and Agenda.  Currently the launch activity.
 * @author nicholasking
 *
 */
@TargetApi(16)
public class GuideMain extends Activity {

	private ActionBar mAction;
	private ViewPager mViewPager;
    private SwipingTabsAdapter mTabsAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Geomancer.activateGeolocation(this);
		setContentView(R.layout.activity_guide_main);

        setupActionBar();
        
        if (savedInstanceState != null) {
            mAction.setSelectedNavigationItem(
            		savedInstanceState.getInt("tab", 0));
        }
	}

	/**
	 * Configure the action bar with the appropriate tabs and options
	 */
	private void setupActionBar() {
		mAction = getActionBar();
		mAction.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mAction.setDisplayShowTitleEnabled(true);
		mAction.setBackgroundDrawable(new ColorDrawable(
				Color.rgb(189, 187, 14)));
		mAction.setSplitBackgroundDrawable(new ColorDrawable(
				Color.rgb(189, 187, 14)));
		
		mViewPager = (ViewPager) findViewById(R.id.swiper_1);
        mTabsAdapter = new SwipingTabsAdapter(this, mViewPager);
        mTabsAdapter.addTab(mAction.newTab().setText("Places"),
        		PlaceTabFragment.class, null);
        mTabsAdapter.addTab(mAction.newTab().setText("Agenda"),
        		AgendaFragment.class, null);
        mTabsAdapter.addTab(mAction.newTab().setText("Tours"),
        		TourFragment.class, null);
        mTabsAdapter.addTab(mAction.newTab().setText("Stats"),
        		StatsFragment.class, null);
	}
	
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_guide_main, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		
		switch (item.getItemId()){
		case R.id.menu_map:
			MapViewer.openAgenda(this);
			return true;
		case R.id.menu_refresh:
			Place temp = Geomancer.findClosestPlace(Geomancer
					.getDeviceLocation(),GlobalState.getPlaceList(this));
			try {
				/*
				 * This is a hack to get the reference to ViewPager's child
				 * Fragments. It is not in the official documentation,
				 * so there is a possibility that they change it in the future.
				 * http://stackoverflow.com/questions/7379165
				 * /update-data-in-listfragment-as-part-of-viewpager
				 */
				PlaceTabFragment frag = (PlaceTabFragment) getFragmentManager()
						.findFragmentByTag("android:switcher:"
								+R.id.swiper_1+":0");
				frag.setCurrentPlace(temp);
				Toast.makeText(this, "Current Location Updated", 
						Toast.LENGTH_SHORT).show();
				
			} catch (NullPointerException e){
				Toast.makeText(this, "Update failed =(", Toast.LENGTH_SHORT)
					.show();
			}
			
			
			return true;
		case R.id.menu_about:
			About.open(this);
			return true;
		case R.id.menu_sort_alphabetic:
			// this is a bit spagetti, so bear with me.
			switch (mAction.getSelectedTab().getPosition()) {
			case 0:
				// Place tab is selected
				// sort the place list alphabetically
				
				Toast.makeText(this, "PlacesList is arranged alphabetically",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				// Agenda tab is selected
				// sort the list in agenda alphabetically
				GlobalState.getUserAgenda().sortAlphabetically();
				
				// Refresh view
				((AgendaFragment)getFragmentManager().findFragmentByTag(
						"android:switcher:"+R.id.swiper_1+":1"))
						.getListView().invalidateViews();
				
				Toast.makeText(this, "Agenda is arranged alphabetically",
						Toast.LENGTH_SHORT).show();
				break;
			default: return false;
			}
			return true;
		case R.id.menu_sort_distance:
			
			switch (mAction.getSelectedTab().getPosition()){
			case 0:
				// Place tab is selected
				// sort the place list by distance away
				
				Toast.makeText(this, "PlacesList is arranged by distance",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				// Agenda tab is selected
				// sort the list in agenda by distance away
				
				Toast.makeText(this, "Agenda is arranged by distance",
						Toast.LENGTH_SHORT).show();
				break;
			default: return false;
			}
			return true;	
		default: return false;
		}
	}
	
	public void onSaveInstanceState(Bundle state){
		state.putInt("tab", mAction.getSelectedTab().getPosition());
	}
	// ---------- END setup and lifecycle related methods ---------- //
	

	/**
	 * Determine whether a tab is selected
	 * @param n The number of the tab to test for (ex: 
	 * 			if Tours tab is #3, then n=3)
	 * @param selection The Integer that has the selected tab
	 * @return True if tab number n is selected, false otherwise
	 */
	private boolean isSelected(int n, Integer selection) {
		return selection != null && n == selection;
	}

	/**
	 * Adds a little hack to "forward" the user on to the map activity when the
	 * map tab is clicked. This effectively removes this activity from the back
	 * stack.
	 * 
	 * @author nicholasking
	 * 
	 */
	private class MyActivityTabListener extends ActivityTabListener {

		public MyActivityTabListener(Context packageCtx, Class<?> target) {
			super(packageCtx, target);
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			super.onTabSelected(tab, ft);
			finish();
		}

	}
	
	/**
	 * Use this method to return to the Main. This will clear all
	 * in the stack
	 * 
	 * @param ctx
	 */
	public static void open(Context ctx){
		Intent i = new Intent(ctx, GuideMain.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ctx.startActivity(i);
	}
	
}

package us.wmwm.citibike.activities;

import us.wmwm.citibike.fragments.StationsFragment;
import us.wmwm.citibike.fragments.StationsMapFragment;
import us.wmwm.citibike2.R;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {

	DrawerLayout drawer;
	
	ActionBarDrawerToggle toggle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		drawer = (DrawerLayout) findViewById(R.id.drawer);
 		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.content_fragment, new StationsMapFragment());
		t.commit();
		toggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_navigation_drawer, R.string.open, R.string.close);
		drawer.setDrawerListener(toggle);
		toggle.setDrawerIndicatorEnabled(true);		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		toggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		toggle.syncState();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(toggle.onOptionsItemSelected(item)) {
			return true;
		}
		if(item.getItemId()==R.id.remove_fragment) {
			
			Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_fragment);
			if(fragment!=null) {
				FragmentTransaction t = getSupportFragmentManager().beginTransaction();
				t.remove(fragment);
				t.commit();
			}
		}
		if(item.getItemId()==R.id.add_fragment) {
			FragmentTransaction t = getSupportFragmentManager().beginTransaction();
			t.replace(R.id.content_fragment, StationsFragment.newInstance());
			t.commit();
		}
		return super.onOptionsItemSelected(item);
		
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

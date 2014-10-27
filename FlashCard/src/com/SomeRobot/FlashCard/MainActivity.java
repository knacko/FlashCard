package com.SomeRobot.FlashCard;


import java.io.File;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	//   https://docs.google.com/spreadsheets/d/1fRMPIrJ1scd20bpgD9aDK-zkM-GPzMh2iNcNcbldMuo/edit#gid=0

	Context mContext;
	DatabaseHandler db;
	CardManager cm;
	DelayedViewPager vp;
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		mContext = this;
		
		boolean doesDatabaseExist = checkDatabase(this, "flashcard.db");		

		db = new DatabaseHandler(mContext);		

		if (doesDatabaseExist) {
			Log.d("onCreate()", "Database does exist.");
			cm = new CardManager(db);
			setupEverything();
		} else {
			Log.d("onCreate()", "Database does not exist.");
			getDataThenSetup();				
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_activity_actions, menu);  

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		if (null != searchView) {
			searchView.setSearchableInfo(searchManager
					.getSearchableInfo(getComponentName()));
			searchView.setIconifiedByDefault(false);
		}

		SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

			public boolean onQueryTextSubmit(String query) {

				setupEverything(query);
				return true;

			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// do nothing
				return true;
			}
		};
		searchView.setOnQueryTextListener(queryTextListener);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_search:
			// do nothing
			return true;
		case R.id.action_sync:
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					mContext);

			alertDialogBuilder
			.setMessage("Sync to online database?")
			.setCancelable(false)
			.setPositiveButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();					
				}
			})
			.setNegativeButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					getDataThenSetup();
				}
			});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();



			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private static boolean checkDatabase(ContextWrapper context, String dbName) {
		File dbFile = context.getDatabasePath(dbName);
		return dbFile.exists();
	}

	public void getDataThenSetup() {

		ViewPager pager = (ViewPager)findViewById(R.id.viewPager);
		pager.setVisibility(View.GONE);

		final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
				"Loading Database...", true);		

		new AsyncTask<Void, Void, Void> () {

			SheetsAdapter sa;

			@Override
			protected Void doInBackground(Void... params) {
				try {
					sa = new SheetsAdapter();
					db.createDatabase(sa.getCardList());
					cm = new CardManager(db);
				} catch (Exception e) {
					//Do nothing, sa will be null now
				}

				return null;
			}			

			@Override
			protected void onPostExecute(Void params) {

				dialog.dismiss();

				if (sa == null) {
					displayError();
				} 
				else {
					setupEverything();
				}
			}			

		}.execute();
	}	

	public void displayError() {
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				mContext);

		alertDialogBuilder
		.setMessage("Database unable to be loaded. Please check internet connection and re-open program.")
		.setCancelable(false)
		.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				MainActivity.this.finish();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public void setupEverything() {

		cm.getCardStack();
		loadAdapters();

	}

	public void setupEverything(String s) {
		
		int num = cm.getCardStack(s).size();
		
		if (num == 0) {

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					mContext);

			alertDialogBuilder
			.setMessage("No cards found with that term. Please try again.")
			.setCancelable(false)
			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					return;
				}  
			});

			AlertDialog alertDialog = alertDialogBuilder.create();

			alertDialog.show();		
		}	else {
			
			String toast = "Found " + num + " cards.";
			
			Toast.makeText(getApplicationContext(), toast, 
					   Toast.LENGTH_LONG).show();			
			
			loadAdapters();
		}
	}

	private void loadAdapters() {
		
		DelayedViewPager pager = (DelayedViewPager)findViewById(R.id.viewPager);
		pager.setVisibility(View.GONE);
				
		CardFragmentPagerAdapter pageAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(),cm);

		pager.setPageTransformer(true, new ZoomOutPageTransformer());
		pager.setAdapter(pageAdapter);
		//pager.setOffscreenPageLimit(3); //TODO: figure out why this doesn't work, only the first two fragments showup
		pager.setVisibility(View.VISIBLE);
	}
}

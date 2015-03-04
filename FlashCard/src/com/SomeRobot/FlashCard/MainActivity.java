package com.SomeRobot.FlashCard;


import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	//   https://docs.google.com/spreadsheets/d/1fRMPIrJ1scd20bpgD9aDK-zkM-GPzMh2iNcNcbldMuo/edit#gid=0

	Context mContext;
	DatabaseHandler db;
	CardManager cm;

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
			loadFullCardStack();
		} else {
			Log.d("onCreate()", "Database does not exist.");
			getDataThenLoadFullCardStack();				
		}
	}


	private static boolean checkDatabase(ContextWrapper context, String dbName) {
		File dbFile = context.getDatabasePath(dbName);
		return dbFile.exists();
	}

	public void getDataThenLoadFullCardStack() {

		SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
	    final Editor editor = pref.edit();
		
		final int numOfCards = pref.getInt("numOfCards", 0);
		
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
					
					int cardsInStack = sa.getCardList().size();
					editor.putInt("numOfCards", cardsInStack);
					editor.clear();
				    editor.commit();
					
					String toast = "Found " + Math.max(cardsInStack - numOfCards,0) + " new cards.";

					Toast.makeText(getApplicationContext(), toast, 
							Toast.LENGTH_LONG).show();										
					
					loadFullCardStack();
				}
			}			

		}.execute();
	}	

	public void loadFullCardStack() {

		cm.getCardStack();
		loadAdapters();

	}

	public void loadPartialCardStack(String s) {

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
		//pager.setOffscreenPageLimit(2); //TODO: figure out why this doesn't work, only the first two fragments showup

		CardFragmentPagerAdapter pageAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(),cm);

		pager.setPageTransformer(true, new ZoomOutPageTransformer());
		pager.setAdapter(pageAdapter);
		pager.setCurrentItem(5);
		pager.setVisibility(View.VISIBLE);
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

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			public boolean onQueryTextSubmit(String query) {
				loadPartialCardStack(query);
				return true;

			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// do nothing
				return true;
			}
		});

		MenuItem search = menu.findItem(R.id.action_search);

		search.setOnActionExpandListener(new OnActionExpandListener()
		{

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item)
			{  
				loadFullCardStack();
				return true;
			}

			@Override
			public boolean onMenuItemActionExpand(MenuItem item)
			{
				// do nothing
				return true;
			}
		});

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
			displaySync();
			return true;
		
		case R.id.action_help:
			displayHelp();
			return true;
			
		case R.id.action_database:
			displayVisitDatabase();
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
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
	
	public void displaySync() {
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
				getDataThenLoadFullCardStack();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	
	public void displayHelp() {
		
		String s = "<br><big><u>Navigation</u></big><p>" +
				"Tap the card to show the answer.<br>" +
				"Swipe left and right to change cards.<p>" +
				"If you run into an invisible card, swiping left or right twice and then back will redraw it. Still figuring out why it happens." +
				"<p><big><u>Search</u></big><p>" +
				"Type your term into the box, the search will return all cards that include that term. " +
				"To retreive all of the cards again, just close the search box (click the flash card icon in the top left when the search bar is open)." +
				"<p><big><u>Sync</u></big><p>" +
				"Entire database is redownloaded when you sync, so be careful if using when using mobile data." +
				"<p><big><u>Database</u></big><p>" +
				"Some mobile browsers do not allow you to interact with Google Spreadsheets. A desktop is best for adding questions.";

		ScrollView sv = new ScrollView(mContext);
		
		TextView msg = new TextView(mContext);
		msg.setText(Html.fromHtml(s));			
						
		msg.setPadding(20,0,20,0);				
		sv.addView(msg);
		
		AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
		
		ab.setTitle("How to use");
		ab.setView(sv);
		ab.setIcon(R.drawable.ic_action_help);
		ab.setNeutralButton("Close", null);
		ab.show();
	}
	
	private void displayVisitDatabase() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				mContext);

		alertDialogBuilder
		.setMessage("Visit online database?\n\nNote: Not all mobile browser are compatible with Google Spreadsheets. You may not be able to edit anything.")
		.setCancelable(false)
		.setPositiveButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();					
			}
		})
		.setNegativeButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/spreadsheets/d/1fRMPIrJ1scd20bpgD9aDK-zkM-GPzMh2iNcNcbldMuo/edit#gid=0"));
				startActivity(browserIntent);
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	
	
}

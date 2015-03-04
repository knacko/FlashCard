package com.SomeRobot.FlashCard;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper{

	private final Context mContext;

	private static final String DB_NAME = "flashcard.db";	
	private static final int DATABASE_VERSION = 1;

	private static final String TABLE_BIOL218 = "Biol218";

	private static final String KEY_ID = "id";
	private static final String KEY_QUESTION = "question";
	private static final String KEY_ANSWER = "answer";
	private static final String KEY_TAGS = "tags";
   
	public DatabaseHandler(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
		mContext = context;

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//Do nothing
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Do nothing
	}
	
	public void createDatabase(List<CardData> cardData) {

		SQLiteDatabase db = this.getWritableDatabase();
		String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_BIOL218;
		db.execSQL(DROP_TABLE);		

		String CREATE_TABLE = "CREATE TABLE " + TABLE_BIOL218 + "("
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + KEY_QUESTION + " TEXT,"
				+ KEY_ANSWER + " TEXT," + KEY_TAGS + " TEXT"+ ")";
		db.execSQL(CREATE_TABLE);
		
		for(CardData card : cardData) addCard(db, card);
		
		db.close();	
		
	}

	private void addCard(SQLiteDatabase db, CardData card) {


		ContentValues values = new ContentValues();
		values.put(KEY_QUESTION, card.getQuestion());
		values.put(KEY_ANSWER, card.getAnswer());
		values.put(KEY_TAGS, card.getTags());

		// Inserting Row
		db.insert(TABLE_BIOL218, null, values);
	}	
	
	public List<CardData> getCardData() {

		SQLiteDatabase db = this.getReadableDatabase();

		String s = "SELECT * FROM " + TABLE_BIOL218;
		
		Cursor cursor = db.rawQuery(s, null);
				
		List<CardData> cardData= parseCardDataCursor(cursor);
		
		db.close();

		return cardData;
	}

	public List<CardData> getCardData(String[] tags) {

		if (tags.length == 0) return getCardData();
		
		SQLiteDatabase db = this.getReadableDatabase();

		String s = "SELECT * FROM " + TABLE_BIOL218 + " WHERE ";

		Log.d("getCardData(s[])",s);
		
		for(int i = 0; i < tags.length; i++) {	//TODO: Make it search the questions as well
			tags[i] = "%" + tags[i].trim() + "%";
			s += KEY_TAGS + " LIKE ?";
			if (i != tags.length - 1) s+= " AND ";
		}		
		
		Cursor cursor = db.rawQuery(s, tags);
		
		List<CardData> cardData= parseCardDataCursor(cursor);
		
		db.close();

		return cardData;

	}

	private List<CardData> parseCardDataCursor(Cursor cursor) {

		List<CardData> cardData = new ArrayList<CardData>();

		while(cursor.moveToNext()){

			String question = cursor.getString(cursor.getColumnIndex(KEY_QUESTION));
			String answer = cursor.getString(cursor.getColumnIndex(KEY_ANSWER));
			String tags = cursor.getString(cursor.getColumnIndex(KEY_TAGS));

			CardData cd = new CardData(question, answer, tags);
									
			cardData.add(cd);	

		}

		return cardData;
	}
}

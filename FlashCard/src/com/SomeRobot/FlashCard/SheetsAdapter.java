package com.SomeRobot.FlashCard;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.ServiceException;

public class SheetsAdapter {

	List<CardData> cardList = new ArrayList<CardData>();
	
	public SheetsAdapter() throws Exception { //TODO: implement the actual exceptions

		String applicationName = "FlashCard";
		String key = "1fRMPIrJ1scd20bpgD9aDK-zkM-GPzMh2iNcNcbldMuo";

		SpreadsheetService service = new SpreadsheetService(applicationName);

		URL url = FeedURLFactory.getDefault().getWorksheetFeedUrl(key, "public", "basic");

		WorksheetFeed feed = service.getFeed(url, WorksheetFeed.class);
		List<WorksheetEntry> worksheetList = feed.getEntries();
		WorksheetEntry worksheetEntry = worksheetList.get(0);

		ListQuery listQuery = new ListQuery(worksheetEntry.getListFeedUrl());
		ListFeed listFeed = service.query( listQuery, ListFeed.class );  

		List<ListEntry> list = listFeed.getEntries();

		for (ListEntry row : list) {   

			try {
				cardList.add(parseData(row.getPlainTextContent()));
			} catch (ArrayIndexOutOfBoundsException e) { //throws when row not completely filled
				continue;
			}
		}        
		
		Log.d("SheetsAdapter()","Sheet parsed with " + cardList.size() + " entries");
	}

	private CardData parseData(String data) throws ArrayIndexOutOfBoundsException {

		Log.d("SheetsAdapter.parseData()","Parsing: " + data);

		String workingData[], question = "", answer = "", tags = ",,";

		//question: How many what?, answer: All of them., tags: No, endstring: 5

		workingData = data.split("\\, endstring\\: ");
		
		workingData = workingData[0].split("question\\: ");
		workingData = workingData[1].split("\\, answer\\: ");
		question = workingData[0];		
		workingData = workingData[1].split("\\, tags\\: ");
		answer = workingData[0];
		tags = workingData.length > 1 ? tags = "," + workingData[1] + "," : ",,"; //so first and last tags are comma delimited

		CardData cd = new CardData(question,answer,tags);
		
		Log.d("SheetsAdapter().parseData()","Parsed as " + cd.toString());
		
		return cd;
	}

	public List<CardData> getCardList() {
		return cardList;
	}	
}

package com.SomeRobot.FlashCard;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class CardFragmentPagerAdapter extends FragmentStatePagerAdapter {

	private List<Fragment> fragments = new ArrayList<Fragment>();
	CardManager cm;
	int maxCards = 500;

	public CardFragmentPagerAdapter(FragmentManager fm, CardManager cm) {

		super(fm);

		this.cm = cm;

		for (int i = 0; i < maxCards; i++) addCard();

	}

	@Override
	public Fragment getItem(int position) {
		
		
		
		CardFragment cardFragment = (CardFragment) fragments.get(position);
		
		Log.d("getItem()","Pos: " + position + ", Question: " + cardFragment.data.getQuestion());
		
		return cardFragment;
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	public void addCard() { //TODO: Dynamically add cards, for some reason keep getting nullPointerException:119 whenever something is added during runtime
		
		if (fragments.size() > maxCards) fragments.remove(0);

		fragments.add(cm.getCardFragment());

		notifyDataSetChanged();
	}
}

package com.SomeRobot.FlashCard;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CardFragment extends Fragment {

	private static String QUESTION = "question";
	private static String ANSWER = "answer";
	boolean isBackVisible = false;

	public static final CardFragment newInstance(String question, String answer) {

		CardFragment cf = new CardFragment();
		Bundle bundle = new Bundle(2);
		bundle.putString(QUESTION,question);
		bundle.putString(ANSWER,answer);
		cf.setArguments(bundle);

		return cf;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_card, container, false);
		final TextView q = (TextView) view.findViewById(R.id.question);
		final TextView a = (TextView) view.findViewById(R.id.answer);

		String question = getArguments().getString(QUESTION);
		String answer = getArguments().getString(ANSWER);

		q.setText(question);
		a.setText(answer);

		final AnimatorSet setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(),
				R.animator.flight_right_out);

		final AnimatorSet setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(),
				R.animator.flight_left_in);


		view.setOnTouchListener(new View.OnTouchListener() {

			float x = 0, y = 0;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				case(MotionEvent.ACTION_DOWN):
					x = event.getX();
					y = event.getY();
				break;
				case(MotionEvent.ACTION_UP): {
					x = Math.abs(event.getX()-x);
					y = Math.abs(event.getY()-y);

					if (x + y < 30 && !setRightOut.isRunning() && !setLeftIn.isRunning()) {

						if(!isBackVisible){
							setRightOut.setTarget(q);
							setLeftIn.setTarget(a);  
							setRightOut.start();
							setLeftIn.start();
							isBackVisible = true;
						}
						else{
							setRightOut.setTarget(a);
							setLeftIn.setTarget(q);
							setRightOut.start();
							setLeftIn.start();
							isBackVisible = false;
						}
						break;
					}
				}
				}
				return true;
			}	

		});
		
		return view;
	}		
}

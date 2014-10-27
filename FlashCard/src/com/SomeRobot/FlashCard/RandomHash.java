package com.SomeRobot.FlashCard;

import java.util.Random;

public class RandomHash {

	int x = 0;
	
	public RandomHash() {
		x = new Random().nextInt(31);
	}

    private int hash(int a) {         
          a ^= (a << 13);
          a ^= (a >>> 17);        
          a ^= (a << 5);
          return a;   
    }
    
    public int nextInt() {return hash (++x);}
    
    public int previousInt() {return hash (--x);}
    
}

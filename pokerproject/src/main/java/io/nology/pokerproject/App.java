package io.nology.pokerproject;

import java.io.File;
import java.io.FileNotFoundException;
/**
 * Hello world!
 *
 */
import java.util.*;

import java.util.stream.IntStream;

import javax.sound.midi.Soundbank;

public class App {
	
    public static void main( String[] args ) throws FileNotFoundException {
    	
    	File file = new File("/Users/frede/OneDrive/Desktop/poker-hands.txt");
    	
    	Scanner sc = new Scanner(file);
    	
    	
    	HashMap<Character, Integer> cardValues = new HashMap<>();
    	
    	cardValues.put('2',2);
    	cardValues.put('3',3);
    	cardValues.put('4',4);
    	cardValues.put('5',5);
    	cardValues.put('6',6);
    	cardValues.put('7',7);
    	cardValues.put('8',8);
    	cardValues.put('9',9);
    	cardValues.put('T',10);
    	cardValues.put('J',11);
    	cardValues.put('Q',12);
    	cardValues.put('K',13);
    	cardValues.put('A',14);
    	
    	HashMap<Integer, String> pokerRank = new HashMap<>();
    	
    	pokerRank.put(0, "Royal Flush");
    	pokerRank.put(1, "Straight Flush");
    	pokerRank.put(2, "Four of a Kind");
    	pokerRank.put(3, "Full House");
    	pokerRank.put(4, "Flush");
    	pokerRank.put(5, "Straight");
    	pokerRank.put(6, "Three of a Kind");
    	pokerRank.put(7, "Two Pair");
    	pokerRank.put(8, "Pair");
    	pokerRank.put(9, "High Card");
    	
    	HashMap<String, Integer> winsNLosses = new HashMap<>();
    	
    	winsNLosses.put("Player1", 0);
    	winsNLosses.put("Player2", 0);
    	
//    	while(sc.hasNextLine()) {
    		
        	String[] total = {"2H", "AS", "TD", "2D", "6C", "2C", "2S", "TC", "9H", "5D"};
    		
//        	String[] total = sc.nextLine().split(" ",0);
//        	System.out.println(Arrays.toString(total));    	
        	String test = findError(total, cardValues);
        	
        	if(test == "GO") {
        		
        		String[] p1 = new String[5];
        		for(int i = 0; i < 5; i++) {
        			p1[i] = total[i];
        		}
        		
        		String[] p2 = new String[5];
        		for(int i = 5; i < 10; i++) {
        			p2[i - 5] = total[i];
        		}
        		
        		ArrayList<Object[]> p1ReadyDataSet = setUp(p1, cardValues);
        		ArrayList<Object[]> p2ReadyDataSet = setUp(p2, cardValues);
        		
        		String winner = determineWinner(p1ReadyDataSet, p2ReadyDataSet, cardValues, pokerRank, winsNLosses, p1, p2);
        		
        		System.out.println(winner);
        		System.out.println(Arrays.toString(p1) + " P1 " + winsNLosses.get("Player1"));
        		System.out.println(Arrays.toString(p2) + " P2 " + winsNLosses.get("Player2"));
        		
        	} else System.out.println(test);
        }
    	
//    }
    
    public static String findError(String[] total, HashMap<Character, Integer> cardValues) {
    	
    	if (total.length != 10) return "There must be 10 entries when there are currently " + total.length + " entries";
    	
    	int i = 0;
    	for (String card : total) {
    		if (cardValues.get(card.charAt(0)) == null) {
    			return card.charAt(0) + " is an invalid value";
    		}
    		
    		if (card.charAt(1) != 'S' &&
    				card.charAt(1) != 'H' &&
    				card.charAt(1) != 'C' &&
    				card.charAt(1) != 'D' ) {
    			return card.charAt(1) + " is an invalid suit";
    		}
    		
    		for (int x = i + 1 ; x < total.length; x++) {
    			
    			if (card == total[x]) {
    				return card + " is a duplicate card";
    			}
    		}
    		i++;
    	}
    	return "GO";
    }
    
    public static ArrayList<Object[]> setUp(String[] lineUp, HashMap<Character, Integer> cardValues) {
    	

    	ArrayList<Object[]> lineUpList = new ArrayList<>();
   
    	Character value;
    	char suit;
    	Integer realValue; 	
    	
    	for(int i = 0; i < lineUp.length; i++) {
    		value = lineUp[i].charAt(0);
    		suit = lineUp[i].charAt(1);
    		realValue = cardValues.get(value);
    		Object[] card = {value, suit, realValue};
    		lineUpList.add(i,card);
    	}	
    	
    	Collections.sort(lineUpList, new Comparator<Object[]>() {

			public int compare(Object[] a, Object[] b) {
				return ((Integer) b[2]).compareTo((Integer) a[2]);
			}
    		
    	});

    	return lineUpList;
    }
    
    
    public static ArrayList<ArrayList<Object[]>> findCombo (ArrayList<Object[]> lineUp, HashMap<Character, Integer> cardValues) {
    	

    	ArrayList<Object[]> dupeList =  findDupes(lineUp, cardValues);
    	
    	ArrayList<Object[]> singleList = findKinds(dupeList, 1);
    	ArrayList<Object[]> pairList = findKinds(dupeList, 2);
    	ArrayList<Object[]> tripList = findKinds(dupeList, 3);
    	ArrayList<Object[]> quadList = findKinds(dupeList, 4);
    	
    	ArrayList<Object[]> highCard = findTopDupe(singleList);
    	ArrayList<Object[]> pair = findTopDupe(pairList);
    	ArrayList<Object[]> trip = findTopDupe(tripList);
    	ArrayList<Object[]> quad = findTopDupe(quadList);
    	
    	ArrayList<Object[]> twoPair = findTwoPair(pairList);
    	ArrayList<Object[]> straight = findStraight(lineUp);
    	ArrayList<Object[]> flush = findFlush(lineUp);
    	ArrayList<Object[]> fullHouse = findFullHouse(trip, pair);
    	ArrayList<Object[]> straightFlush = findStraight(flush);
    	ArrayList<Object[]> royalFlush = findRoyalFlush(straightFlush);
    	
    	ArrayList<ArrayList<Object[]>> allHands = new ArrayList<>();
    	
    	allHands.add(royalFlush);
    	allHands.add(straightFlush);
    	allHands.add(quad);
    	allHands.add(fullHouse);
    	allHands.add(flush);
    	allHands.add(straight);
    	allHands.add(trip);
    	allHands.add(twoPair);
    	allHands.add(pair);
    	allHands.add(highCard);
    	
    	return allHands;
    }
    
    public static ArrayList<Object[]> findDupes(ArrayList<Object[]> lineUp, HashMap<Character, Integer> cardValues) {
    	
    	HashMap<Object, Integer> dupeCounter = new HashMap<>();
    	ArrayList<Object[]> dupeArr = new ArrayList<>();
    	
    	Integer count;
    	
    	for (Object[] card : lineUp) {
    		if(dupeCounter.get(card[0]) == null) {
    			dupeCounter.put(card[0], 0);
    		}
    		if(dupeCounter.get(card[0]) != null) {
    			count = dupeCounter.get(card[0]) + 1;
    			dupeCounter.put(card[0],count);
    		}
		}
    	
    	for (int i = 0; i < dupeCounter.size(); i++) {
    		Character value = (Character) dupeCounter.keySet().toArray()[i];
    		Integer dupeCount = (Integer) dupeCounter.values().toArray()[i];
    		Integer realValue = cardValues.get(value);
    		
    		Object[] dupeDataSet = {value, dupeCount, realValue};
    		dupeArr.add(dupeDataSet);
    	}
    	
    	Collections.sort(dupeArr, new Comparator<Object[]>() {

			public int compare(Object[] a, Object[] b) {
				return ((Integer) b[2]).compareTo((Integer) a[2]);
			}
    	});
    	
    	return dupeArr;
    }
    
    public static ArrayList<Object[]> findKinds(ArrayList<Object[]> dupeList, Integer numberOfKind) {
    	
    	ArrayList<Object[]> kindList = new ArrayList<Object[]>();
    	
    	for (Object[] card : dupeList) {
			if(card[1] == numberOfKind) {
				kindList.add(card);
			}
		}
    	return kindList;
    }
    
    public static ArrayList<Object[]> findTopDupe (ArrayList<Object[]> dupeList) {
    	ArrayList<Object[]> topDupe = new ArrayList<>();
    	
    	if (dupeList.size() == 0) return null;
    	

    	if ((Integer)dupeList.get(0)[1] == 1) return dupeList;
    	
    	topDupe.add(dupeList.get(0));
    	
    	return topDupe;
    };
    
    public static ArrayList<Object[]> findTwoPair (ArrayList<Object[]> pairList) {
    	ArrayList<Object[]> twoPair = new ArrayList<>();
    	
    	if (pairList.size() < 2) return null;
    			
    	twoPair.add(pairList.get(0));
    	twoPair.add(pairList.get(1));
    	
    	return twoPair;
    }
    
    public static ArrayList<Object[]> findStraight (ArrayList<Object[]> lineUp) {
    	
    	if(lineUp == null) return null;
    	
    	Integer count = 1;
    	
    	for (int i = 0; i < lineUp.size() - 1; i++) {
    		
    		Integer current = (Integer) lineUp.get(i)[2];
    		Integer nextPlusOne = (Integer) lineUp.get(i + 1)[2] + 1;
    		
			if(current == nextPlusOne) {
				count += 1;
			}
			
		}
    	
    	if (count != 5) return null; 
    	
    	return lineUp;
    }

    public static ArrayList<Object[]> findFlush(ArrayList<Object[]> lineUp) {
    	
    	Integer count = 1;
    	
    	for (int i = 0; i < lineUp.size() - 1; i++) {
    		
    		
			if(lineUp.get(i)[1] == lineUp.get(i+1)[1]) {
				count += 1;
			}
			
		}
    	
    	if (count != 5) return null; 
    	
    	return lineUp;
    }
    
    public static ArrayList<Object[]> findFullHouse(ArrayList<Object[]> topTrip, ArrayList<Object[]> topPair) {
    	ArrayList<Object[]> fullHouse = new ArrayList<>();
    	if (topTrip == null || topPair == null) return null;
    	
    	fullHouse.add(topTrip.get(0));
    	fullHouse.add(topPair.get(0));
    	
    	return fullHouse;
    }
    
    public static ArrayList<Object[]> findRoyalFlush(ArrayList<Object[]> straightFlush) {
    	
    	if(straightFlush == null) return null;
    	
    	if ((Integer) straightFlush.get(0)[2] == 14) return straightFlush;
    	
    	return null;
    }
    
    public static ArrayList<ArrayList<Object[]>> findTopHand (ArrayList<ArrayList<Object[]>> allHands){

    	ArrayList<ArrayList<Object[]>> topHand = new ArrayList<>();
    	
    	for (ArrayList<Object[]> hand : allHands) {
			if(hand != null) {
				topHand.add(hand);
				break;
			}
		}
    	
    	return topHand;
    }
    
    public static Integer findRankIndex (ArrayList<ArrayList<Object[]>> allHands){
    	
    	Integer rankIndex = 0;
    	
    	for (ArrayList<Object[]> hand : allHands) {
			if(hand != null) {
				rankIndex = allHands.indexOf(hand);
				break;
			}
		}
    	
    	return rankIndex;
    }
    
    
    public static String determineWinner 
    (ArrayList<Object[]> p1ReadyDataSet, ArrayList<Object[]> p2ReadyDataSet, HashMap<Character, Integer> cardValues,
    HashMap<Integer, String> pokerRank, HashMap<String, Integer> winsNLosses, String[] p1Hand, String[] p2Hand) {
    	
    	
    	ArrayList<ArrayList<Object[]>> p1AllHands = findCombo(p1ReadyDataSet, cardValues);
    	ArrayList<ArrayList<Object[]>> p2AllHands = findCombo(p2ReadyDataSet, cardValues);

    	ArrayList<ArrayList<Object[]>> p1TopHand = findTopHand(p1AllHands);
    	ArrayList<ArrayList<Object[]>> p2TopHand = findTopHand(p2AllHands);

		Integer p1Rank = findRankIndex(p1AllHands);
		Integer p2Rank = findRankIndex(p2AllHands);

		Integer p1currentScore = winsNLosses.get("Player1");
		Integer p2currentScore = winsNLosses.get("Player2");
		
		
		
		if (p1Rank < p2Rank) {
			winsNLosses.put("Player1", p1currentScore + 1);
			return announcement("Player 1", p1Rank, p1Hand, pokerRank, winsNLosses);
		}
		if (p1Rank > p2Rank) {
			winsNLosses.put("Player2", p2currentScore + 1);
			return announcement("Player 2", p2Rank, p2Hand, pokerRank, winsNLosses);
		}

		if (p1Rank == p2Rank) {
			for (int i = 0; i < p1TopHand.get(0).size(); i++) {
				
				Character p1TopFirstChar = (Character) p1TopHand.get(0).get(i)[0];
				Character p2TopFirstChar = (Character) p2TopHand.get(0).get(i)[0];
				
				if (cardValues.get(p1TopFirstChar) > cardValues.get(p2TopFirstChar)) {
					winsNLosses.put("Player1", p1currentScore + 1);
					return announcement("Player 1", p1Rank, p1Hand, pokerRank, winsNLosses);
				}
				
				if (cardValues.get(p1TopFirstChar) < cardValues.get(p2TopFirstChar)) {
					winsNLosses.put("Player2", p2currentScore + 1);
					return announcement("Player 2", p2Rank, p2Hand, pokerRank, winsNLosses);
				}
			}
		}
    	return "Tie";
    }
    

	public static String announcement 
    (String winningPlayerName, Integer playerRank, String[] hand, HashMap<Integer, String> pokerRank, HashMap<String, Integer> winsNLosses ) {
    	return winningPlayerName + " wins with " + pokerRank.get(playerRank) + ". Their hand is " + Arrays.toString(hand) ;
    }
    

    
//  public class Card {
//	private String value;
//	private String suit;
//	private int realValue;
//	
//	public Card(String value, String suit, int realValue) {
//        this.value = value;
//        this.suit = suit;
//        this.realValue = realValue;
//    }
//	
//    public String getValue() { return this.value; }
//    public String getSuit() { return this.suit; }
//    public int getRealValue() { return this.realValue; }
//}
}

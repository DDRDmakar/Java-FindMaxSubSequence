 
package main;

import java.util.ArrayList;
import java.util.List;

class Searcher {
	
	private List<Integer> storage;
	
	Searcher() {
		System.out.println("LOL KEK constructor");
	}
	
	public void start() {
		storage = new ArrayList<Integer>();
		
		// Five random lists
		for (int j = 0; j < 5; ++j) {
			// Random values ( .. 100)
			for (int i = 0; i < 30; ++i) storage.add((int)(Math.random() * 101));
			System.out.println("Random list: ");
			test();
			storage.clear();
		}
		
		// Ascending values from 0 to 29
		for (int i = 0; i < 30; ++i) storage.add(i);
		System.out.println("Ascending values: ");
		test();
		storage.clear();
		
		// Descending values from 29 to 0
		for (int i = 29; i >= 0; --i) storage.add(i);
		System.out.println("Descending values: ");
		test();
		storage.clear();
	}
	
	// Test one list
	public void test() {
		System.out.println(storage.toString());
		// Simple and slow algorithm
		List<Integer> alg1 = easySearch();
		// Effective search algorithm
		List<Integer> alg2 = effectiveSearch();
		
		// Check
		if (
			alg1.get(alg1.size() - 1) - alg1.get(0) ==
			alg2.get(alg2.size() - 1) - alg2.get(0)
		) System.out.println("OK");
		else System.out.println("FAIL");
	}
	
	// Debugging function - finding maximum sub-sequence checking all combinations
	// Very simple, but uneffective algorithm.
	private List<Integer> easySearch() {
		
		if (storage.size() <= 2) return storage;
		
		int maxSeqBegin = 0;
		int maxSeqEnd   = 1;
		int maxPair = storage.get(1) - storage.get(0);
		
		for (int i = 0; i < storage.size(); ++i) {
			for (int j = i + 1; j < storage.size(); ++j) {
				if (storage.get(j) - storage.get(i) > maxPair) {
					maxPair = storage.get(j) - storage.get(i);
					maxSeqBegin = i;
					maxSeqEnd = j;
				}
			}
		}
		
		//System.out.println(storage.subList(maxSeqBegin, maxSeqEnd + 1).toString());
		//System.out.println("Begin = " + maxSeqBegin + " End = " + maxSeqEnd + " Sum = " + maxPair);
		
		return storage.subList(maxSeqBegin, maxSeqEnd + 1);
	}
	
	private List<Integer> effectiveSearch() {
		// Finding maximum delta
		// (long) = (32 bit sum) (16 bit end index) (16 bit begin index)
		long res = findMaxPair((short)0, (short)(storage.size() - 1));
		if (res == -1) return new ArrayList<Integer>();
		short maxBegin = (short)(res & 0xFFFF);       // Start index
		short maxEnd = (short)(res >> 16 & 0xFFFF); // End index
		int maxSum = (int)(res >> 32);              // Max sum
		
		//System.out.println(storage.subList(maxBegin, maxEnd + 1).toString());
		//System.out.println("Begin = " + maxBegin + " End = " + maxEnd + " Sum = " + maxSum);
		
		// Result
		return storage.subList(maxBegin, maxEnd + 1);
	}
	
	private long findMaxPair(short seqBegin, short seqEnd) {
		if (seqEnd - seqBegin < 1) return -1;
		
		if(seqEnd - seqBegin == 1) {
			//                                                          SUM    |    END_index        | BEGIN_index
			return ((long)(storage.get(seqEnd) - storage.get(seqBegin)) << 32) | ((int)seqEnd << 16) | seqBegin;
		} else {
			short separator = (short)((seqEnd - seqBegin) / 2 + seqBegin);
			long leftSeq = findMaxPair(seqBegin, separator);
			long rightSeq = findMaxPair(separator, seqEnd);
			long middleSeq = findMiddlePair(seqBegin, seqEnd, separator);
			
			if      (leftSeq != -1 &&  (int)(leftSeq >> 32) >= (int)(rightSeq >> 32) && (int)(leftSeq >> 32) >= (int)(middleSeq >> 32)) return leftSeq;
			else if (rightSeq != -1 && (int)(rightSeq >> 32) >= (int)(leftSeq >> 32) && (int)(rightSeq >> 32) >= (int)(middleSeq >> 32)) return rightSeq;
			else return middleSeq;
		}
	}
	
	private long findMiddlePair(short seqBegin, short seqEnd, short separator) {
		if (seqEnd - seqBegin < 3) return -1;
		
		short currentIndex;
		
		short minLeftElement = (short)((int)separator - 1);
		currentIndex = (short)((int)separator - 1);
		while (currentIndex >= seqBegin) {
			if (storage.get(currentIndex) < storage.get(minLeftElement)) minLeftElement = currentIndex;
			--currentIndex;
		}
		
		short maxRightElement = (short)((int)separator + 1);
		currentIndex = (short)((int)separator + 1);
		while (currentIndex <= seqEnd) {
			if (storage.get(currentIndex) > storage.get(maxRightElement)) maxRightElement = currentIndex;
			++currentIndex;
		}
		
		return ((long)(storage.get(maxRightElement) - storage.get(minLeftElement)) << 32) | ((int)maxRightElement << 16) | minLeftElement;
	}
}

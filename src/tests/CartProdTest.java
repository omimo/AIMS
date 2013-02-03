package tests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.lowagie.text.List;

public class CartProdTest {
	public static void main(String args[]) {

	    ArrayList<int[]> input = new ArrayList<int[]>();
	    input.add(new int[] { 1, 2, 3 });
	    input.add(new int[] { 4, 5 });
	    input.add(new int[] { 6, 7 });

	    ArrayList<int[]> input2 = new ArrayList<int[]>();
	    for (int i=0;i<input.size();i++)
	    {
	    	input2.add(input.get(i));
	    	int[] cu = new int[input2.size()];
	    	ArrayList<Set<Integer>> res  = combine(input2, cu, 0);
	    	System.out.println("==="+res.size());
	    	for(Set<Integer> s: res) {
	    		for (int r: s)
	    			System.out.print(r + " ");
	    		System.out.println();
	    	}
	    }
	    
	}

	
	
	private static ArrayList<Set<Integer>> combine(ArrayList<int[]> input, int[] current, int k) {

		ArrayList<Set<Integer>> comb = null;
		
		if (comb==null)
    		comb = new ArrayList<Set<Integer>>();
    	
	    if(k == input.size()) {
	    	Set<Integer> res = new HashSet<Integer>();
	        for(int i = 0; i < k; i++) {
	          // System.out.print(current[i] + " ");
	            res.add(current[i]);
	        }
        	comb.add(res);
	        return comb; 	       
	    } else {          
	    	
	        for(int j = 0; j < input.get(k).length; j++) {
	            current[k] = input.get(k)[j];
	            comb.addAll(combine(input, current, k + 1));	            
	        }    
	        return comb;
	    }
	}
}

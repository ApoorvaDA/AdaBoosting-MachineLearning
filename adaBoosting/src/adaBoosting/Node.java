package adaBoosting;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class Node {

	public static String infile = "adaboost-1.txt";
	public static int no_of_iterations=0;
	public static int no_of_examples = 0;
	public static double epsilon=0;
	
	public static ArrayList<String> examples = new ArrayList<String>();
	public static ArrayList<String> classification = new ArrayList<String>();
	public static ArrayList<String> new_classification = new ArrayList<String>();
	public static ArrayList<String> probability = new ArrayList<String>();
	public static ArrayList<String> probability_ada = new ArrayList<String>();
	public static ArrayList<String> probability_realada = new ArrayList<String>();
	public static ArrayList<Double> pre_normalized_probability_ada = new ArrayList<Double>();
	public static ArrayList<Double> pre_normalized_probability_realada = new ArrayList<Double>();
	public static ArrayList<Float> thresholds = new ArrayList<Float>();
	public static ArrayList<Double> upper_bound_error  = new ArrayList<Double>();
	public static ArrayList<String> h_of_x = new ArrayList<String>();
	public static ArrayList<Double> gt_of_x = new ArrayList<Double>();
	
	//variables for adaboosting
	public static float left_pos_error=0;
	public static float right_pos_error=0;
	public static float min_error = 0;
	public static float min_error_threshold = 0;
	public static boolean min_error_left_pos = true;
	public static boolean min_error_tmp_flag = true;
	public static int iteration_count = 1;
	public static String f_of_x = null;
	public static int wrongly_classified = 0;
	public static double boosted_classifier_error = 0;
	public static double upper_bound_error_ada = 1;
	
	//variables for real_adaboosting
	public static double g_j_min = 0;
	public static double g_j_min_threshold = 0;
	public static boolean g_j_min_left_pos = true;
	public static boolean g_j_tmp_flag = true;
	public static int iteration_count_realada = 1;
	public static double upper_bound_error_realada = 1;
	
	// Function to read input file
	public static void read_infile(){
		String line;
		
		try {
			FileReader filereader = new FileReader(infile);
			BufferedReader bufferReader = new BufferedReader(filereader);
			
			if(( line = bufferReader.readLine()) != null){
				String[] vals = line.split(" ");
				no_of_iterations = Integer.parseInt(vals[0]);
				no_of_examples = Integer.parseInt(vals[1]);
				epsilon = Double.parseDouble(vals[2]);
			}	
			if(( line = bufferReader.readLine()) != null){
				String[] vals = line.split(" ");
				for(int i=0; i<no_of_examples; i++){
					examples.add(vals[i]);
				}
			}
			if(( line = bufferReader.readLine()) != null){
				String[] vals = line.split(" ");
				for(int i=0; i<no_of_examples; i++){
					classification.add(vals[i]);
				}
			}
			if(( line = bufferReader.readLine()) != null){
				String[] vals = line.split(" ");
				for(int i=0; i<no_of_examples; i++){
					probability.add(vals[i]);
				}
				probability_ada.addAll(probability);
				probability_realada.addAll(probability);
			}
			bufferReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException a) {
			a.printStackTrace();
		}
	}
	
	//Function to calculate threshold
	public static void adaboosting_thresholdcalculation(){
		// Finding thresholds
		for(int i=1; i<no_of_examples; i++){
			String s0 = classification.get(i-1);
			String s1 = classification.get(i);
			if(! s0.equals(s1)){
				float temp1 = Float.parseFloat(examples.get(i-1));
				float temp2 = Float.parseFloat(examples.get(i));
				float threshold = (temp1 + temp2)/2;
				thresholds.add(threshold);
			}	
		}
	}
	
	//Function to calculate minimum error and corresponding threshold
	public static void adaboosting_errorcalculation(ArrayList<String> probability_ada){	
		// Compute the error for each threshold
		int count=0;
		float thresh1;
		ArrayList<String> prob = probability_ada;
		float min_error_temp;
		
		System.out.println("Iteration:"+iteration_count);
		System.out.println("---------------------------");
		
		System.out.println("");
		for (int j=0; j<thresholds.size(); j++){
			thresh1 = thresholds.get(j);
			
			for(int k=0; k<no_of_examples; k++){
				if(Float.parseFloat(examples.get(k))<thresh1){
					count++;
				}
			}
			int no_elements_lessthan_threshold=count;
			
			count=0;
			
			for (int m=0; m<no_elements_lessthan_threshold; m++){
				if(Integer.parseInt(classification.get(m))!=1){
					left_pos_error = left_pos_error + Float.parseFloat(prob.get(m));
				} 
				if(Integer.parseInt(classification.get(m))!=-1){
					right_pos_error = right_pos_error + Float.parseFloat(prob.get(m));
				}
			}
			for (int n=no_elements_lessthan_threshold; n<no_of_examples; n++  ){
				if(Integer.parseInt(classification.get(n))!= -1){
					left_pos_error = left_pos_error + Float.parseFloat(prob.get(n));
				}
				if(Integer.parseInt(classification.get(n))!= 1){
					right_pos_error = right_pos_error + Float.parseFloat(prob.get(n));
				}
			}
			
			if(j==0){
				if(left_pos_error < right_pos_error){
					min_error = left_pos_error;	
				} else {
					min_error = right_pos_error;
					min_error_tmp_flag = false;
				}
				
				min_error_threshold = thresh1;
				min_error_left_pos = min_error_tmp_flag;
			} else {
				if(left_pos_error < right_pos_error){
					min_error_temp = left_pos_error;
					min_error_tmp_flag = true;
				} else {
					min_error_temp = right_pos_error;
					min_error_tmp_flag = false;
				}
				
				if(min_error_temp < min_error){
					min_error = min_error_temp;
					min_error_threshold = thresh1;
					min_error_left_pos = min_error_tmp_flag;
				}
			}
			min_error_temp = 0;
			left_pos_error = 0;
			right_pos_error = 0;
		}
		if(min_error_left_pos == true){
			System.out.println("The selected weak classifier: x < "+min_error_threshold);
		}else{
			System.out.println("The selected weak classifier: x > "+min_error_threshold);
		}
		System.out.println("The error of Ht:"+min_error);
		adaboosting_calculatenewprobs();
	}
	
	//Function to calculate new probabilities for each iteration
	public static void adaboosting_calculatenewprobs(){
		int count = 0;
		
		float adaboosting_epsilon = min_error;
		double alpha = (Math.log((1-adaboosting_epsilon)/adaboosting_epsilon))/2;
		double alpha_rounded = rounded_value(alpha);
		System.out.println("The weight of Ht:"+alpha_rounded);
		double q_value_right = Math.exp(-alpha_rounded); 
		double q_value_wrong = Math.exp(alpha_rounded);
		double q_value_right_rounded = rounded_value(q_value_right);
		double q_value_wrong_rounded = rounded_value(q_value_wrong);
		
		if(min_error_left_pos == true){
			f_of_x = new StringBuilder().append(alpha_rounded).append("*").append("I(x<").append(min_error_threshold).append(")+").append(f_of_x).toString();
		} else {
			f_of_x = new StringBuilder().append(alpha_rounded).append("*").append("I(x>").append(min_error_threshold).append(")+").append(f_of_x).toString();
		}
		
		String[] f_of_x_parts = f_of_x.split("\\+null");
		f_of_x = f_of_x_parts[0];
		
		for(int i=0; i<no_of_examples; i++){
			if(Float.parseFloat(examples.get(i))<min_error_threshold){
				count++;
			}
		}
		int no_elements_lessthan_threshold=count;
		
		if(min_error_left_pos == true){
			for (int m=0; m<no_elements_lessthan_threshold; m++){
				if(Integer.parseInt(classification.get(m))!=1){
					double pq = Double.parseDouble(probability_ada.get(m)) *  q_value_wrong_rounded;
					double pq_rounded = rounded_value(pq); 
					pre_normalized_probability_ada.add(m,pq_rounded);
					new_classification.add(m,String.valueOf(-1));
					wrongly_classified++;
				} else {
					double pq = Double.parseDouble(probability_ada.get(m)) *  q_value_right_rounded;
					double pq_rounded = rounded_value(pq); 
					pre_normalized_probability_ada.add(m,pq_rounded);
					new_classification.add(m,String.valueOf(1));
				}
			}
			for (int n=no_elements_lessthan_threshold; n<no_of_examples; n++  ){
				if(Integer.parseInt(classification.get(n))!= -1){
					double pq = Double.parseDouble(probability_ada.get(n)) *  q_value_wrong_rounded;
					double pq_rounded = rounded_value(pq); 
					pre_normalized_probability_ada.add(n,pq_rounded);
					new_classification.add(n,String.valueOf(-1));
					wrongly_classified++;
				} else {
					double pq = Double.parseDouble(probability_ada.get(n)) *  q_value_right_rounded;
					double pq_rounded = rounded_value(pq); 
					pre_normalized_probability_ada.add(n,pq_rounded);
					new_classification.add(n,String.valueOf(1));
				}
			}
		} else {
			for (int m=0; m<no_elements_lessthan_threshold; m++){
				if(Integer.parseInt(classification.get(m))!=-1){
					double pq = Double.parseDouble(probability_ada.get(m)) *  q_value_wrong_rounded;
					double pq_rounded = rounded_value(pq); 
					pre_normalized_probability_ada.add(m,pq_rounded);
					new_classification.add(m,String.valueOf(-1));
					wrongly_classified++;
				} else {
					double pq = Double.parseDouble(probability_ada.get(m)) *  q_value_right_rounded;
					double pq_rounded = rounded_value(pq); 
					pre_normalized_probability_ada.add(m,pq_rounded);
					new_classification.add(m,String.valueOf(1));
				}
			}
			for (int n=no_elements_lessthan_threshold; n<no_of_examples; n++  ){
				if(Integer.parseInt(classification.get(n))!= 1){
					double pq = Double.parseDouble(probability_ada.get(n)) *  q_value_wrong_rounded;
					double pq_rounded = rounded_value(pq); 
					pre_normalized_probability_ada.add(n,pq_rounded);
					new_classification.add(n,String.valueOf(-1));
					wrongly_classified++;
				} else {
					double pq = Double.parseDouble(probability_ada.get(n)) *  q_value_right_rounded;
					double pq_rounded = rounded_value(pq); 
					pre_normalized_probability_ada.add(n,pq_rounded);
					new_classification.add(n,String.valueOf(1));
				}
			}
		}
		
		boosted_classifier_error = (double)wrongly_classified /(double) no_of_examples;
		
		double Z = rounded_value(2 * Math.sqrt(adaboosting_epsilon * (1 - adaboosting_epsilon)));
		System.out.println("The probabilities normalization factor Zt:"+Z);
				
		for(int i=0; i<no_of_examples; i++){
			double new_pi = pre_normalized_probability_ada.get(i) / Z;
			double new_pi_rounded = rounded_value(new_pi);
			probability_ada.add(i,String.valueOf(new_pi_rounded));
		}
		
		System.out.print("The probabilities after normalization:");
		for(int i=0; i<no_of_examples; i++){
			System.out.print("\t"+probability_ada.get(i));
		}
		
		System.out.println("");
		System.out.println("The boosted classifier: "+f_of_x);
		
		System.out.println("The error of the boosted classifier Et:"+boosted_classifier_error);
		
		if(iteration_count < no_of_iterations){

			upper_bound_error_ada = upper_bound_error_ada * Z;
			
			System.out.println("The bound on Et:"+upper_bound_error_ada+"\n");
			System.out.println("");
			iteration_count++;
			boosted_classifier_error = 0;
			wrongly_classified = 0;
			adaboosting_errorcalculation(probability_ada);
		}	
	}
	
	public static void real_adaboosting_select_hypothesis(ArrayList<String> probability_realada){
		
		int count=0;
		float thresh2;
		ArrayList<String> prob1 = probability_realada;
		//List<Double> pvalues = new ArrayList<Double>();
		//Map<Float,List<Double>> map_pvalue = new HashMap<Float,List<Double>>();
		
		System.out.println("Iteration:"+iteration_count_realada);
		System.out.println("---------------------------");
		
		double g_j_value=0;
		for (int j=0; j<thresholds.size(); j++){
			double p_r_plus = 0, p_r_minus = 0, p_w_plus = 0, p_w_minus = 0;
			//pvalues.clear();
			thresh2 = thresholds.get(j);
			
			for(int k=0; k<no_of_examples; k++){
				if(Float.parseFloat(examples.get(k))<thresh2){
					count++;
				}
			}
			
			int no_elements_lessthan_threshold=count;

			count=0;
			
			//System.out.println("Threshold:"+thresh2);
			
			for (int m=0; m<no_elements_lessthan_threshold; m++){
				h_of_x.add(m,String.valueOf(1));
			}
			for (int n=no_elements_lessthan_threshold; n<no_of_examples; n++  ){
				h_of_x.add(n,String.valueOf(-1));
			}
			
			for (int k=0; k<no_of_examples; k++){
				if(h_of_x.get(k).equals("1") && classification.get(k).equals("1")){
					p_r_plus = rounded_value(p_r_plus + Double.parseDouble(prob1.get(k)));
				}else if(h_of_x.get(k).equals("-1") && classification.get(k).equals("-1")){
					p_r_minus = rounded_value(p_r_minus + Double.parseDouble(prob1.get(k)));
				}else if(h_of_x.get(k).equals("-1") && classification.get(k).equals("1")){
					p_w_plus = rounded_value(p_w_plus + Double.parseDouble(prob1.get(k)));
				}else if(h_of_x.get(k).equals("1") && classification.get(k).equals("-1")){
					p_w_minus = rounded_value(p_w_minus + Double.parseDouble(prob1.get(k)));
				}
			}
			/*
			 * Tried this part of the code to store all the pvalues in a hashmap and pass it on 
			 * to newprobs function for cvalue calculation. Dint get it working unfortunately and
			 * had to implement redundant code.
			 * Use for future development
			pvalues.add(p_r_plus);
			pvalues.add(p_r_minus);
			pvalues.add(p_w_plus);
			pvalues.add(p_w_minus);
	
			//printList(pvalues);
			map_pvalue.put(thresh2, pvalues);
			*/
			g_j_value = rounded_value(Math.sqrt(p_r_plus * p_w_minus) + Math.sqrt(p_r_minus * p_w_plus ));
			
			if(j==0){
				g_j_min = g_j_value;	
				g_j_min_threshold = thresh2;
			} else {
				
				if(g_j_value < g_j_min){
					g_j_min = g_j_value;
					g_j_min_threshold = thresh2;
				}
			}
			g_j_value = 0;
			h_of_x.clear();
			
		}
		System.out.println("The selected weak classifier: x < "+g_j_min_threshold);
		
		System.out.println("The G error value of Ht:"+g_j_min);
		real_adaboosting_calculatenewprobs(prob1);
	}
	
	public static void real_adaboosting_calculatenewprobs(ArrayList<String> prob1){

		int count = 0;
		double p_r_plus = 0, p_r_minus = 0, p_w_plus = 0, p_w_minus = 0;
		h_of_x.clear();
		
		for(int i=0; i<no_of_examples; i++){
			if(Float.parseFloat(examples.get(i))<g_j_min_threshold){
				count++;
			}
		}
		int no_elements_lessthan_threshold=count;
		for(int j=0; j<no_elements_lessthan_threshold; j++){
			h_of_x.add(String.valueOf(1));
		} 
		for(int j=no_elements_lessthan_threshold; j<no_of_examples; j++){
			h_of_x.add(String.valueOf(-1));
		}
		
		for (int k=0; k<no_of_examples; k++){
			if(h_of_x.get(k).equals("1") && classification.get(k).equals("1")){
				p_r_plus = rounded_value(p_r_plus + Double.parseDouble(prob1.get(k)));
			}else if(h_of_x.get(k).equals("-1") && classification.get(k).equals("-1")){
				p_r_minus = rounded_value(p_r_minus + Double.parseDouble(prob1.get(k)));
			}else if(h_of_x.get(k).equals("-1") && classification.get(k).equals("1")){
				p_w_plus = rounded_value(p_w_plus + Double.parseDouble(prob1.get(k)));
			}else if(h_of_x.get(k).equals("1") && classification.get(k).equals("-1")){
				p_w_minus = rounded_value(p_w_minus + Double.parseDouble(prob1.get(k)));
			}
		}
		double c_plus = rounded_value((Math.log((p_r_plus + epsilon)/(p_w_minus + epsilon)))/2); 
 		double c_minus = rounded_value((Math.log((p_w_plus + epsilon)/(p_r_minus + epsilon)))/2);
 		System.out.println("The weights Ct+, Ct-:"+c_plus+","+c_minus);
 		
 		for(int i=0; i<no_of_examples;i++){
 			if(h_of_x.get(i).equals("1")){
 				gt_of_x.add(i,c_plus);
 			} else {
 				gt_of_x.add(i,c_minus);
 			}
 		}
 		//printList(gt_of_x);
 		
 		for(int i=0; i<no_of_examples; i++){
 			double tmp = Math.exp(-(Double.parseDouble(classification.get(i)) * gt_of_x.get(i)));
 			double pre_norm_pi = rounded_value(Double.parseDouble(prob1.get(i)) * tmp);
 			pre_normalized_probability_realada.add(i,pre_norm_pi);
 		}
 		
 		//printList(pre_normalized_probability_realada);
 		double Z = rounded_value( 2*(Math.sqrt(p_r_plus * p_w_minus) + Math.sqrt(p_w_plus * p_r_minus)));
 		System.out.println("The probabilities normalization factor Zt:"+Z);
 		
 		for(int i=0; i<no_of_examples; i++){
 			double new_pi = rounded_value(pre_normalized_probability_realada.get(i) / Z);
 			probability_realada.add(i,String.valueOf(new_pi));
 		}
 		
 		System.out.print("The probabilities after normalization:");
		for(int i=0; i<no_of_examples; i++){
			System.out.print("\t"+probability_realada.get(i));
		}
		System.out.println("");
		System.out.print("The values ft(xi) for each one of the examples:");
		for(int i=0; i<no_of_examples; i++){
			System.out.print("\t"+gt_of_x.get(i));
		}
		System.out.println("");
		if(iteration_count_realada < no_of_iterations){
			
			upper_bound_error_realada = upper_bound_error_realada * Z;
			
			System.out.println("The bound on Et:"+upper_bound_error_realada+"\n");
			System.out.println("");
			iteration_count_realada++;
			real_adaboosting_select_hypothesis(probability_realada);
		}
	}
	/*
	private static void printList(List<Double> myList) {
        for (Double val : myList) {
            System.out.println(val);
        }
    }
	*/
	public static double rounded_value(double x){
		double y = (double) Math.round(x * 10000) / 10000;
		return y;
	}
	
	// Main function
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		FileOutputStream f = new FileOutputStream("binary-1.txt");
		System.setOut(new PrintStream(f));
		read_infile();
		adaboosting_thresholdcalculation();
		adaboosting_errorcalculation(probability_ada);
		
		FileOutputStream f1 = new FileOutputStream("real-1.txt");
		System.setOut(new PrintStream(f1));
		read_infile();
		real_adaboosting_select_hypothesis(probability_realada);
	}
}

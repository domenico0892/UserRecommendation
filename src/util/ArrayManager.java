package util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ArrayManager {
	
	public static String arrayToText (double[] a) {
		String s = "";
		for (double n : a) {
			s = s + n + ";";
		}
//		System.out.println(s);
		return s;
	}
	
	public static double[] textToArray (String s) {
		List<Double> l = new ArrayList<Double>();
		Double[] a;
		String[] ss = s.split(";");
		for (String t : ss) 
			l.add(Double.parseDouble(t));
		a = l.toArray(new Double[0]);
		double[] res = new double[a.length];
		for (int i=0;i<a.length;i++)
			res[i] = a[i];
		return res;
	}
	
	public static void main (String[] args) {
//		double[] a = new double[] {1.0,4.3,6.1};
//		System.out.println(arrayToText(a));
		String s = "1.0;4.3;6.1;";
		double[] a = textToArray(s);
		for (double n : a) 
			System.out.println("*" + n);
	}
}
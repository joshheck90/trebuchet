package trebuchet.DynamicModel;

import Jama.Matrix;

public class PrintMatrix {
	
	public static void print(Matrix a) {
		
		System.out.println("BEGIN MATRIX PRINT");
		
		System.out.println("Printing Non-Zero Elements:");
		
		for (int i=0; i<a.getRowDimension(); i++) {
			for (int j=0; j<a.getColumnDimension(); j++) {
				if (a.get(i,j) != 0) {
				System.out.printf("Matrix Element %2d , %2d  holds value %9.4f \n",i,j,a.get(i,j));
				
				}
			}
		}
		System.out.println("END MATRIX PRINT");
		
	}
	
}

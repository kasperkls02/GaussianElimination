import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

public class GaussianElimination {
	public static void main(String[] args) throws IOException {
		Object[][] matEntries;
		int nrows,ncols,dash,t,kasper;
		Matrix matrix;
		Scanner input = new Scanner(System.in);
		System.out.println("Enter number of rows: ");
		nrows=input.nextInt();
		System.out.println("Enter number of columns: ");
		ncols=input.nextInt();
		System.out.println("Enter position of vertical dash (0 for none): ");
		kasper=input.nextInt();
		dash=ncols;
		
		//System.out.println("Enter type of matrix (0 for Z_p, 1 for rational numbers): ");
		//t=input.nextInt();
		t=1;
		if(t==0) {
			System.out.println("Enter p: ");
			int p=input.nextInt();
			IntModP.setP(p);
			matEntries=new IntModP[nrows][ncols];
			for(int i=0;i<nrows;++i) {
				for(int j=0;j<ncols;++j) {
					matEntries[i][j]=new IntModP(0);
				}
			}
			matrix=new Matrix<IntModP>((IntModP[][])matEntries,dash,kasper);
		}
		else {
			matEntries=new Rational[nrows][ncols];
			for(int i=0;i<nrows;++i) {
				for(int j=0;j<ncols;++j) {
					matEntries[i][j]=new Rational(0);
				}
			}
			matrix=new Matrix<Rational>((Rational[][])matEntries,dash,kasper);
		}
		input.nextLine();
		System.out.println("Enter matrix rows:");
		String line;
		for(int i=0;i<nrows;++i) {
			line=input.nextLine();
			matrix.parseFill(line,i);
		}
		input.close();
		FileWriter f=new FileWriter("out.tex");
		String result=matrix.gaussElim();
		//System.out.println(result);
		f.write(result);
		f.close();
	}
}

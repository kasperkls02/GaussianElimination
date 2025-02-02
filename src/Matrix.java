//import java.util.Vector;

public class Matrix<T extends Arithmetic<T> & Parse & Latex & HasDelim> {
	T[][] mat;
	int vdash;
	int vkasper;
	StringBuffer latexLog;
	String preamble=  "\\documentclass[preview]{standalone}\n"
					 +"\\newcommand{\\BAR}{%\n	\\hspace{-\\arraycolsep}%\n	\\strut\\vrule % the `\\vrule` is as high and deep as a strut\n	\\hspace{-\\arraycolsep}%\n}\n"
					 +"\\usepackage{gauss}\n"
					 +"\\newmatrix{[}{]}{q}\n"
					 +"\\begin{document}\n"
					 +"\\begin{align*}";
	String post="\\end{align*}\n"
	+"\\end{document}";
	String ldelim="\\left(";
	String rdelim="\\right)";
	public Matrix(T[][] matrix,int dash, int kasper) {
		mat=matrix;
		vdash=dash;
		vkasper=kasper;
	}
	public Matrix(T[][] matrix) {
		mat=matrix;
		vdash=-1;
	}
	public T getEntry(int row, int col) {
		return mat[row][col];
	}
	public void setEntry(int row, int col, T entry) {
		mat[row][col]=entry;
	}
	public String latexMult(int row, T factor) {
		if(!factor.isOne()) {
			return "\\mult{"+row+"}"+"{\\cdot "+(factor.hasDelim()?ldelim:"")+factor.latex()+(factor.hasDelim()?rdelim:"")+"}\n";
		}
		else {
			return "";
		}
	}
	public void gaussMult(int row, T factor) {
		for(int i=0;i<mat[0].length;++i) {
			mat[row][i]=mat[row][i].mul(factor);
		}
	}
	public String latexAdd(int row1, int row2, T factor) {
		if(!factor.isOne()) {
			return "\\add[\\cdot"+(factor.hasDelim()?ldelim:"")+factor.latex()+(factor.hasDelim()?rdelim:"")+"]"+"{"+row1+"}"+"{"+row2+"}\n";
		}
		else {
			return "\\add{"+row1+"}"+"{"+row2+"}\n";
		}
	}
	public void gaussAdd(int row1, int row2, T factor) {
		for(int i=0;i<mat[0].length;++i) {
			mat[row2][i]=mat[row2][i].add(mat[row1][i].mul(factor));
		}
	}
	public String latexSwap(int row1, int row2) {
		return "\\swap{"+row1+"}"+"{"+row2+"}\n";
	}
	public void gaussSwap(int row1, int row2) {
		T tmp;
		for(int i=0;i<mat[0].length;++i) {
			tmp = mat[row2][i];
			mat[row2][i]=mat[row1][i];
			mat[row1][i]=tmp;
		}
	}
	public String latex(String ops) {
		//System.out.println();
		int mid=(vdash==-1)?mat[0].length:vdash;
		
		StringBuffer result=new StringBuffer("&\\linespread{1.2}\\selectfont");
		result.append((vdash==-1)?"\\begin{gmatrix}[p]\n":"\\begin{gmatrix}[q]\n");
		
		for(int i=0;i<mat.length;++i) {
			for(int j=0;j<mid;++j) {
				if(j==vkasper && j!=0) {
					result.append(" \\BAR & ");
				}
				result.append(mat[i][j].latex());
				
				if(j==(mid-1) && i<mat.length-1) {
					result.append(" \\\\\n");
				}
				else if(j<mid-1) {
					result.append(" & ");
				}
			}
		}
		//result.append("\\end{gmatrix}\n");
		
		/*if(vdash!=-1) {
			result.append("\\end{matrix}\n"
					+ "\\begin{gmatrix}\n");
			for (int i = 0; i < mat.length; ++i) {
				for (int j = mid; j < mat[0].length; ++j) {
					result.append(mat[i][j].latex());
					if (j == (mat[0].length - 1) && i < mat.length - 1) {
						result.append(" \\\\\n");
					} else if (j < mat[0].length - 1) {
						result.append(" & ");
					}
				}
			}
		}
		*/
		//result.append("\\begin{gmatrix}");
		if(ops!="") {
			result.append("\\rowops\n");
			result.append(ops);
		}
		
		result.append("\\end{gmatrix}\\\\\n");
		return result.toString();
	}
	private int firstColEntryNeq0(int col,int start){
		for(int i=start;i<mat.length;++i) {
			if(!mat[i][col].isZero()) {
				return i;
			}
		}
		return -1;
	}
	public String gaussElim() {
		int i=0;
		int j=0;
		int k;
		T factor;
		String op;
		latexLog=new StringBuffer(preamble);
		while(i<mat.length && j<mat.length && (vdash==-1 || j<vdash)) {
			k=firstColEntryNeq0(j,i);
			if(k==-1) {
				++j;
				continue;
			}
			if(k!=i) {
				op=latexSwap(i,k);
				latexLog.append(latex(op));
				gaussSwap(i,k);
			}
			if(!mat[i][j].isOne()) {
				factor=mat[i][j].inv();
				op=latexMult(i,factor);
				latexLog.append(latex(op));
				gaussMult(i,factor);
			}
			op="";
			for(int l=0;l<mat.length;++l) {
				if(!mat[l][j].isZero() && l!=i) {
					op+=latexAdd(i,l,mat[l][j].neg());
				}
			}
			if(op!="") {
				latexLog.append(latex(op));
			}
			for(int l=0;l<mat.length;++l) {
				if(!mat[l][j].isZero() && l!=i) {
					gaussAdd(i,l,mat[l][j].neg());
				}
			}
			++i;
			++j;
		}
		latexLog.append(latex(""));
		latexLog.append(post);
		return latexLog.toString();
	}
	public void parseFill(String line,int row) {
		String[] entries=line.split(" ");
		for(int j=0;j<mat[0].length;++j) {
			mat[row][j].parse(entries[j]);
		}
	}
}

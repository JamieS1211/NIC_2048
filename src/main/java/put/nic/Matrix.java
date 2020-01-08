package put.nic;

public class Matrix {
	float rows;
	float cols;
	float[][] data;
	
	float max = 1;
	float min = -1;

	Matrix(float[][] matrix_){
		this.rows = matrix_.length;
		this.cols = matrix_[0].length;
		this.data= matrix_;
	}
	
	Matrix(float rows_, float cols_){
		this.rows=rows_;
		this.cols=cols_;
		this.data= new float[(int)rows][(int)cols];

		for (int i=0; i<(int)this.rows;i++) {
			for (int j=0; j<(int)this.cols;j++) {
				this.data[i][j] = 0;
			}

		}
	}
	
	static Matrix fromArray(float[] input_array) {
		Matrix m = new Matrix(input_array.length,1);
		for(int i=0;i<input_array.length;i++) {
			m.data[i][0]=input_array[i];
		}

		return m;
	}
	
	float[] toArray(){
		float[] output = new float[(int) (this.rows*this.cols)];
		int index = 0;
		for (int i=0; i<(int)this.rows;i++) {
			for (int j=0; j<(int)this.cols;j++) {
				output[index] = this.data[i][j];
				index +=1;
			}
		}

		return output;
	}
	
	void print() {
		for (int i=0; i<(int)this.rows;i++) {
			for (int j=0; j<(int)this.cols;j++) {
				System.out.print(this.data[i][j]+" ");
			}
			System.out.println();
		}
	}
	
	void multiply(float n){
		Matrix result = new Matrix(this.rows,this.cols);
		for (int i=0; i<this.rows;i++) {
			for(int j=0; j<this.cols;j++) {
				result.data[i][j] = this.data[i][j]*n;
			}
		}
	}
	
	void multiply(Matrix n){
		for (int i=0; i<this.rows;i++) {
			for(int j=0; j<this.cols;j++) {
				this.data[i][j] *= n.data[i][j];
			}
		}
	}
	
	static Matrix multiply(Matrix m, Matrix n) {
		if (m.cols != n.rows) {
			throw new IllegalArgumentException("cols must be equal to rows");
		}
		else {
			Matrix result = new Matrix(m.rows,n.cols);
			for (int i=0;i<result.rows;i++) {
				for (int j=0;j<result.cols;j++) {
					for (int k=0;k<m.cols;k++) {
						result.data[i][j] += m.data[i][k] * n.data[k][j];
					}
				}
			}
					
			return result;		
		}
	}

	
	static Matrix add(Matrix m, Matrix n){
		Matrix result = new Matrix(m.rows,m.cols);
		for (int i = 0; i < m.rows; i++) {
			for(int j = 0; j < m.cols; j++) {
				result.data[i][j] = m.data[i][j] + n.data[i][j];
			}
		}

		return result;
	}
	
	
	void add(float n){
		for (int i = 0; i < this.rows; i++) {
			for(int j = 0; j < this.cols; j++) {
				this.data[i][j] = this.data[i][j] + n;
			}
		}
	}
	
	static Matrix subtract(float n, Matrix m) {
		//Return n-m
		Matrix result = new Matrix(m.rows,m.cols);
		for (int i = 0; i < result.rows; i++) {
			for(int j = 0; j < result.cols; j++) {
				result.data[i][j] = n - m.data[i][j];
			}
		}

		return result;	
	}
		
	
	static Matrix subtract(Matrix m, Matrix n) {
		// Return a new Matrix a-b
		Matrix result = new Matrix(m.rows,m.cols);
		for (int i = 0; i < result.rows; i++) {
			for(int j = 0; j < result.cols; j++) {
				result.data[i][j] = m.data[i][j] - n.data[i][j];
			}
		}

		return result;	
	}
	
	void randomize() {
		for (int i = 0; i < this.rows; i++) {
			for(int j = 0; j < this.cols; j++) {
				this.data[i][j] = (float)(Math.random() * ((this.max - this.min)+1)) + this.min ;
			}
		}	
	}
	
	static Matrix transpose(Matrix t) {
		Matrix result = new Matrix(t.cols,t.rows);
		for (int i = 0; i < t.rows; i++) {
			for(int j = 0; j < t.cols; j++) {
				result.data[j][i] = t.data[i][j];
			}	
		}

		return result;
	}
}

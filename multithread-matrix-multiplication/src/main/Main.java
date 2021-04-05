package main;

import java.io.*;
import java.lang.Thread;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Matrix {
	int[][] data;
	int i, j;
	
	Matrix() {}
	
	public void createData(int i, int j, int max) {
		this.i = i;
		this.j = j;
		data = new int[this.i][this.j];
		Random rand = new Random();
		for(i = 0; i < this.i; i++) {
			for(j = 0; j < this.j; j++) {
				this.data[i][j] = rand.nextInt(max+1);
			}
		}
	}
	
	public void writeToFile(String path) {
		try {
			// Read file (create if not exist)
			File f = new File(path);
			f.createNewFile();
		
			// Write data
			FileWriter wf = new FileWriter(path);
			for(int i = 0; i < this.i; i++) {
				for(int j = 0; j < this.j; j++) {
					wf.write(this.data[i][j] + " ");
				}
				wf.write("\n");
			}
			wf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	
	public void readFromFile(String path) {
		try {
			// Read data
			File f = new File(path);
			Scanner myReader = new Scanner(f);
			List<String[]> list = new ArrayList<String[]>();
			while(myReader.hasNextLine()) {
				String inData = myReader.nextLine();
				String[] arrInData = inData.split(" ");
				list.add(arrInData);
			}
			
			// Get size
			this.i = list.size();
			if(this.i > 0) {
				this.j = list.get(0).length;
			} else {
				this.j = 0;
			}
			
			// Convert to 2D array data
			this.data = new int[this.i][this.j];
			for(int i = 0; i < this.i; i++) {
				for(int j = 0; j < this.j; j++) {
					this.data[i][j] = Integer.parseInt(list.get(i)[j]);
				}
			}
			
			myReader.close();
	    } catch (FileNotFoundException e) {
	    	System.out.println("An error occurred.");
	    	e.printStackTrace();
	    }
	}
	
	public void createEmptyData(int i, int j) {
		this.i = i;
		this.j = j;
		this.data = new int[i][j];
	}
}

// Threads
class CThreadCreateMatrixData extends Thread {
	Matrix A;
	int max;
	
	CThreadCreateMatrixData(Matrix A, int i, int j, int max){
		this.A = A;
		this.A.i = i;
		this.A.j = j;
	    this.max = max;
	}
    
	public void run() {
		this.A.createData(this.A.i, this.A.j, this.max);
	}
}
class CThreadReadMatrixData extends Thread {
	Matrix A;
	String path;
	
	CThreadReadMatrixData(Matrix A, String path){
		this.A = A;
	    this.path = path;
	}
    
	public void run() {
		this.A.readFromFile(this.path);
	}
}
class CThreadCalculateCMatrixData extends Thread {
	Matrix A;
	Matrix B;
	Matrix C;
	int startIndex, operationLength;
	
	CThreadCalculateCMatrixData(Matrix A, Matrix B, Matrix C, int operationLength, int startIndex){
		this.A = A;
		this.B = B;
		this.C = C;
		this.startIndex = startIndex;
		this.operationLength = operationLength;
	}
    
	public void run() {
		for(int i = this.startIndex; i < this.startIndex + this.operationLength; i++) {
			// A.i = m length
			// C.j = n length
			// B.j = p length
			int n = i%C.j,
				mp = i/C.j,
				m = mp/B.j,
				p = mp%B.j;
			
			// Check if end of array
			if(mp >= A.i * B.j) {
				return;
			}
			
			C.data[mp][n] = A.data[m][n] * B.data[n][p];
		}
	}
}
class CThreadCalculateDMatrixData extends Thread {
	Matrix A;
	Matrix B;
	Matrix C;
	Matrix D;
	int startIndex, operationLength;
	
	CThreadCalculateDMatrixData(Matrix A, Matrix B, Matrix C, Matrix D, int operationLength, int startIndex){
		this.A = A;
		this.B = B;
		this.C = C;
		this.D = D;
		this.startIndex = startIndex;
		this.operationLength = operationLength;
	}
    
	public void run() {
		for(int i = this.startIndex; i < this.startIndex + this.operationLength; i++) {
			// A.i = m length
			// C.j = n length
			// B.j = p length
			int mp = i,
				m = mp/B.j,
				p = mp%B.j;
			
			// Check if end of array
			if(mp >= A.i * B.j) {
				return;
			}
			
			D.data[m][p] = 0;
			for(int j = 0; j < C.j; j++) {
				D.data[m][p] += C.data[mp][j];
			}
		}
	}
}

// Run
public class Main {
	public static void createMatrixData(int m, int n, int p, int max) {
		Matrix A = new Matrix();
		Matrix B = new Matrix();
		
		// Create data
		CThreadCreateMatrixData AThread = new CThreadCreateMatrixData(A, m, n, max);
		CThreadCreateMatrixData BThread = new CThreadCreateMatrixData(B, n, p, max);
		AThread.start();
		BThread.start();
		try {
			AThread.join();
			BThread.join();
		} catch ( Exception e) {
			System.out.println("Interrupted");
		}
		
		// Write data to file
		A.writeToFile("matrices/a.txt");
		B.writeToFile("matrices/b.txt");
	}
	
	public static void readMatrixData(Matrix A, Matrix B) {
		// Read data
		CThreadReadMatrixData AThread = new CThreadReadMatrixData(A, "matrices/a.txt");
		CThreadReadMatrixData BThread = new CThreadReadMatrixData(B, "matrices/b.txt");
		AThread.start();
		BThread.start();
		try {
			AThread.join();
			BThread.join();
		} catch ( Exception e) {
			System.out.println("Interrupted");
		}
	}
	
	public static void printMatrix(Matrix A) {
		for(int i = 0; i < A.i; i++) {
			System.out.print("          ");
			for(int j = 0; j < A.j; j++) {
				System.out.print(A.data[i][j] + " ");
			}
			System.out.print("\n");
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		System.out.print("Create new matrix data? (y/N): ");
		Scanner sc = new Scanner(System.in);
		String opt = sc.nextLine();
		
		// Create matrix data
		if(opt.equals("y")) {
			int m, n, p, max;
			System.out.print("m axis length: ");
			m = sc.nextInt();
			
			System.out.print("n axis length: ");
			n = sc.nextInt();
			
			System.out.print("p axis length: ");
			p = sc.nextInt();
			
			System.out.print("Max value: ");
			max = sc.nextInt();
			createMatrixData(m, n, p, max);
			System.out.println("Created matrix data!\n");
		}
		
		// Read matrix data just be created from file
		Matrix A = new Matrix();
		Matrix B = new Matrix();
		readMatrixData(A, B);
		System.out.println("Read matrix data!\n");
		
		// Multiply matrices
		// A.i = m length
		// C.i = n length
		// B.j = p length
		// A[m x n] * B[n x p] = D[m x p]
		// C[n x mp] => sum all element of each row of C => C[1 x mp] => 2D => D
		int cores = Runtime.getRuntime().availableProcessors(),
			operationsPerCoreC, operationsPerCoreD,
			totalOperationsC = A.i * A.j * B.j, totalOperationsD = A.i * B.j;
		CThreadCalculateCMatrixData[] coreCThreads = new CThreadCalculateCMatrixData[cores];
		CThreadCalculateDMatrixData[] coreDThreads = new CThreadCalculateDMatrixData[cores];
		Matrix C = new Matrix();
		C.createEmptyData(A.i * B.j, A.j);
		Matrix D = new Matrix();
		D.createEmptyData(A.i, B.j);
		
		// Calculate operations per core
		if(totalOperationsC % cores > 0) {
			operationsPerCoreC = totalOperationsC/cores + 1;
		} else {
			operationsPerCoreC = totalOperationsC/cores;
		}
		if(totalOperationsD % cores > 0) {
			operationsPerCoreD = totalOperationsD/cores + 1;
		} else {
			operationsPerCoreD = totalOperationsD/cores;
		}
		
		// Setup for per core C thread
		for(int i = 0; i < cores; i++) {
			coreCThreads[i] = new CThreadCalculateCMatrixData(A, B, C, operationsPerCoreC, i * operationsPerCoreC);
			coreCThreads[i].start();
		}
		// Sync core C threads
		for(int i = 0; i < cores; i++) {
			coreCThreads[i].join();
		}

		// Setup for per core D thread
		for(int i = 0; i < cores; i++) {
			coreDThreads[i] = new CThreadCalculateDMatrixData(A, B, C, D, operationsPerCoreD, i * operationsPerCoreD);
			coreDThreads[i].start();
		}
		// Sync core D threads
		for(int i = 0; i < cores; i++) {
			coreDThreads[i].join();
		}
		
		// Write result
		System.out.println("Matrix A: ");
		printMatrix(A);
		System.out.println("Matrix B: ");
		printMatrix(B);
		System.out.println("Result: ");
		printMatrix(D);
		
		System.out.print("\n\nExited.");
	}
}

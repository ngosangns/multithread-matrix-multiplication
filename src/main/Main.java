package main;

import java.io.*;
import java.lang.Thread;
import java.util.Scanner;
import java.util.Random;

class Matrix {
	int[][] data;
	int i, j;
	
	Matrix(int i, int j) {
		this.i = i;
		this.j = j;
		this.data = new int[i][j];
	}
	
	public void createData(int max) {
		Random rand = new Random();
		for(int i = 0; i < this.i; i++) {
			for(int j = 0; j < this.j; j++) {
				this.data[i][j] = rand.nextInt(max+1);
			}
		}
	}
}

class CThreadCreateMatrixData extends Thread {
	Matrix A;
	int max;
	
	CThreadCreateMatrixData(Matrix A, int max){
		this.A = A;
	    this.max = max;
	}
    
	public void run() {
		this.A.createData(this.max);
	}
}

public class Main {
	public static void main(String[] args) {
		System.out.print("Create matrix data? (y/N): ");
		Scanner sc = new Scanner(System.in);
		String opt = sc.nextLine();
		if(opt.equals("y")) {
			int m, n, p, max;
			System.out.print("m axis length: ");
			m = sc.nextInt();
			
			System.out.print("n axis length: ");
			n = sc.nextInt();
			
			System.out.print("p axis length: ");
			p = sc.nextInt();
			
			System.out.print("max value: ");
			max = sc.nextInt();
			createMatrixData(m, n, p, max);
		}
		
//		Loader obj = new Loader(10);
//        Loader obj2 = new Loader(200);
//        Loader obj3 = new Loader(300);
//        obj.start();
//        obj2.start();
//        obj3.start();
		System.out.print("\n\nExited.");
	}
	
	public static void createMatrixData(int m, int n, int p, int max) {
		Matrix A = new Matrix(m, n);
		Matrix B = new Matrix(n, p);
		
		CThreadCreateMatrixData AThread = new CThreadCreateMatrixData(A, max);
		CThreadCreateMatrixData BThread = new CThreadCreateMatrixData(B, max);
		
		AThread.start();
		BThread.start();
	}
}

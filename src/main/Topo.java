package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Topo {
	private List<Point> listNodes;
	private int numNode;
	
	public Topo(){
		listNodes = new ArrayList<Point>();
	}
	
	public void construct(String input) {
		File f = new File(input);
		FileReader reader = null;
		BufferedReader in = null;
		try {
			reader = new FileReader(f);
			in = new BufferedReader(reader);
			
			String str = in.readLine();
			//str = in.readLine();
			
			this.numNode = Integer.valueOf(str.split(" ")[1]);
			for (int i = 0; i < numNode; ++i) {
				str = in.readLine();
				Point p = new Point(str.split(" ")[0], str.split(" ")[1]);
				this.listNodes.add(p);
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("Read file: File not found");
		} catch (IOException e) {
			System.out.println("Read file: Error in reading");
		} finally {
			if (null != reader){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	
	public Point getSensorByIndex(int i) {
		return this.listNodes.get(i);
	}

	public List<Point> getListNodes() {
		return listNodes;
	}

	public void setListNodes(List<Point> listNodes) {
		this.listNodes = listNodes;
	}

	public int getNumNode() {
		return numNode;
	}

	public void setNumNode(int numNode) {
		this.numNode = numNode;
	}
	
	public void printTopo() {
		for (int i = 0; i < this.getNumNode(); ++i) {
			System.out.println(this.getSensorByIndex(i).getX() + " - " + this.getSensorByIndex(i).getY());
		}
	}
	
}

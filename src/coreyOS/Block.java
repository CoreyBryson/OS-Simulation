package coreyOS;

import java.util.ArrayList;

public class Block {
	
	// Holds all of the PCB that are created when the HDD is filled
	private static Block instance = new Block();
	
	public ArrayList<PCB> blocks;
	
	Block(){
		blocks = new ArrayList<>();
	}
	
	public static Block getInstance(){
		if (instance == null){
			instance = new Block();
		}
		return instance;
	}
	
	public void clear(){
		instance = new Block();
	}
	
	// Adds a PCB from the 'job' line read during boot
	public void add(String line){
		
		MemoryManager.getInstance().jobsLeft++; //Increments job count to ensure all job completion
		MemoryManager.getInstance().jobsTotal++; //Increments total job count
		PCB tempPCB;
		String[] job;
		
		line = line.substring(3, line.length());
		job = line.split(",");
		
		tempPCB = new PCB(Integer.parseInt(job[0].trim()),Integer.parseInt(job[1].trim()),Integer.parseInt(job[2].trim()));
		blocks.add(tempPCB);
	}
	
	public void print(){
		for(int i = 0; i < blocks.size(); i++){
			System.out.println(blocks.get(i).ID);
		}
	}

}

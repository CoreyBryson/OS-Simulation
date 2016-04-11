package coreyOS;

import java.util.ArrayList;

public class HDD {
	
	private static HDD instance = new HDD();
	
	public ArrayList<Code> storage;
	
	private int size;

	private HDD(){
		storage = new ArrayList<>();
		size = 0;
	}
	
	public static HDD getInstance(){
		if (instance == null){
			instance = new HDD();
		}
		
		return instance;
	}
	
	// Clears the HDD and Block (PCBs)
	public static void clear(){
		Block.getInstance().clear();
		instance = new HDD();
	}
	
	public void setSize(int size){
		this.size = size;
	}
	

	// Stores lines of code in the HDD
	public void store(String line){

		if(line.charAt(0)== 'J'){
			Block.getInstance().add(line); // Creates the PCB for all jobs written to the HDD
			Block.getInstance().blocks.get(Block.getInstance().blocks.size()-1).hddAddress =  storage.size(); // Stores HDD memory address in the PCB
		} else{
			if(size == 0){
				storage.add(new Code(line));
			}else if(storage.size() < size){
				storage.add(new Code(line));
			}else{
				System.out.println("ERROR: STORAGE FULL");
			}
		}
	}
	
	// Prints every line of Code in HDD
	public void print(){
		for(int i = 0; i < storage.size(); i++){
			System.out.println(storage.get(i));
		}
	}
}

package coreyOS;

import java.util.ArrayList;

public class RAM {
	
	private static RAM instance = new RAM();
	
	public ArrayList<Code> storage;
	public ArrayList<PCB> pcbStorage;
	private int size;

	private RAM(){
		storage = new ArrayList<>();
		pcbStorage = new ArrayList<>();
		size = 0;
	}
	
	public static RAM getInstance(){
		if (instance == null){
			instance = new RAM();
		}
		
		return instance;
	}
	
	// Clears the RAM
	public static void clear(){
		instance = new RAM();
	}
	
	// Sets the RAM size
	public void setSize(int size){
		this.size = size;
	}
	
	// Returns the size of RAM
	public int getSize(){
		return size;
	}
	
	// Stores a line of code if there is room in RAM
	public void store(Code line){
		if(size == 0){
			storage.add(line); //'INFINITE' Storage Mode
		}else if(storage.size() < size){
			storage.add(line);
		}else{
			System.out.println("ERROR: MEMORY FULL");
		}
	}
	
	// Prints every line of code in RAM
	public void print(){
		if(storage.isEmpty())
			return;
		for(int i = 0; i < storage.size(); i++){
			System.out.println(storage.get(i));
		}
	}
	
	// Removes a job from RAM, and updates RAM addresses in other PCB
	public void removeJob(PCB p){
		try{
		int address = p.getAddress();
		//System.out.println("_____________________________________________________");
		//System.out.println("Removing : " + p.ID + " length : " + p.length);
		for(int i = 0; i < p.length; i++){
			//System.out.println("	"+address);
			//System.out.println("   "+storage.get(address));
			storage.remove(address);
		}
		
		int index = 0;
		while(p.ID != pcbStorage.get(index).ID){
			index++;
		}
		pcbStorage.get(index).setAddress(-1);
		pcbStorage.remove(index);
		while(index < pcbStorage.size()){
			//System.out.println("updating : " + pcbStorage.get(index).ID + " : " + pcbStorage.get(index).getAddress());
			pcbStorage.get(index).setAddress(pcbStorage.get(index).getAddress() - p.length);
			//System.out.println("	new : "+ pcbStorage.get(index).getAddress());
			index++;
		}
		
		storage.trimToSize();
		
		//print();
		//System.out.println("_____________________________________________________");
		
		} catch (Exception e){
			System.out.println("FAILURE TO REMOVE A JOB!!!!!!!!!!!!!!!!!!");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
}

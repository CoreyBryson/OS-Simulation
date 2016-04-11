package coreyOS;

import java.util.ArrayList;

public class Scheduler {
	
	private ArrayList<PCB> in;
	private ArrayList<PCB> out;	
	
	//LTS
	Scheduler(ArrayList<PCB> toSort, int mode){
		in = new ArrayList<>();
		out = new ArrayList<>();
		
		for(int i = 0; i < toSort.size(); i++){
			in.add(toSort.get(i));
		}
		
		// Determines the LTS or STS schedule mode
		switch (mode) {
			case 0: out = first();
					break;
			case 1: out = priority();
					break;
			case 2: out = shortest();
					break;
			default: System.out.println("Invalid Schedule Mode");
		}		

	}
	
	//STS
	Scheduler(){
		in = new ArrayList<>();
		out = new ArrayList<>();
	}
	
	// Meant to be used by the LTS, fills the RAM with as many jobs as possible as long as there is enough space
	public void fillRAM(){
		boolean full = false;
		
		while(!full && !out.isEmpty()){
				if(RAM.getInstance().getSize() >= (RAM.getInstance().storage.size() + out.get(0).length)){
					
					int address = out.get(0).hddAddress;
	
					out.get(0).setAddress(RAM.getInstance().storage.size());
					out.get(0).hddAddress = -1;
					
					for(int i = 0; i < out.get(0).length; i++){;
						RAM.getInstance().store(HDD.getInstance().storage.get(address));
						HDD.getInstance().storage.remove(address);
					}
					
					int index = 0;
					while(out.get(0).ID != Block.getInstance().blocks.get(index).ID){
						index++;
					}
					Block.getInstance().blocks.remove(index);
					while(index < Block.getInstance().blocks.size()){
						Block.getInstance().blocks.get(index).hddAddress = Block.getInstance().blocks.get(index).hddAddress - out.get(0).length;
						index++;
					}
	
					
					RAM.getInstance().pcbStorage.add(out.get(0));
					//MemoryManager.getInstance().add(out.get(0).ID, out.get(0).ramAddress);
					out.remove(0);
					
				} else {
					full = true;
				}
			
		}
		
		
	}
	
	// Creates an organize list of PCB to be used by the schedulers
	//FIFO
	public ArrayList<PCB> first(){
		ArrayList<PCB> first = new ArrayList<>();
		
		for(int i = 0; i < in.size(); i++){
			first.add(in.get(i));
		}
		
		return first;
	}
	
	//Priority
	public ArrayList<PCB> priority(){
		ArrayList<PCB> priority = new ArrayList<>();
		boolean done = false;
		int tempIndex = -1;
		PCB tempPCB;
		while(!done){
			tempPCB = in.get(0);
			tempIndex = 0;
			for(int i = 0; i < in.size(); i++){
				if( in.get(i).priority > tempPCB.priority){
					tempPCB = in.get(i);
					tempIndex = i;
				}
			}
			
			priority.add(tempPCB);
			in.remove(tempIndex);
			
			if(in.isEmpty()){
				done = true;
			}
		}
		
		return priority;
	}
	
	// SJF
	public ArrayList<PCB> shortest(){
		ArrayList<PCB> shortest = new ArrayList<>();
		boolean done = false;
		int tempIndex = -1;
		PCB tempPCB;
		while(!done){
			tempPCB = in.get(0);
			tempIndex = 0;
			for(int i = 0; i < in.size(); i++){
				if( in.get(i).length < tempPCB.length){
					tempPCB = in.get(i);
					tempIndex = i;
				}
			}
			
			shortest.add(tempPCB);
			in.remove(tempIndex);
			
			if(in.isEmpty()){
				done = true;
			}
		}
		
		return shortest;
	}
	
	// STS Method that moves jobs into RQ
	public void stsSchedule(){
		
		ArrayList<PCB> temp = new ArrayList<>();
		for(PCB ele : RAM.getInstance().pcbStorage){
			temp.add(ele);
		}
		 for(PCB ele : temp){
			 if(ele.state.equalsIgnoreCase("new")){
				 ele.state = "ready";
				 in.add(ele);
			 }
		 }
		
		out = first();
		
		ReadyQueue.getInstance().offerList(out);
	}
	

}

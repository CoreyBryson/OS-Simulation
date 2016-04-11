package coreyOS;

import java.util.ArrayList;

public class Queue {
	
	ArrayList<ArrayList<PCB>> q;
	
	ArrayList<PCB> timeBlock;
	
	Queue(){
		q = new ArrayList<>();
	}
	
	//Inserts PCB into proper wait block
	public void offer(PCB p, int time){
		if(q.isEmpty()){
			q.add(new ArrayList<PCB>());
		}
		
		if(time > q.size())
			for(int i = 0; i <= time; i++){
				q.add(new ArrayList<PCB>());
			}
		
		try{
		q.get(time-1).add(p);
		}catch(IndexOutOfBoundsException e){
			System.out.println("Desired block : " + (time-1));
			System.out.println("Job : " + p.toString());
			System.out.println("Queue size : " + q.size());
		}
	}
	
	//Gets First Block
	public ArrayList<PCB> peek(){
		if (!q.isEmpty())
			return q.get(0);
		else
			return null;
	}
	
	//Gets and Removes First Block
	public ArrayList<PCB> poll(){
		if (!q.isEmpty()){
			ArrayList<PCB> temp = q.remove(0);
 			return temp;
		}
		else
			return null;
	}
	
	//Checks if the queue is empty
	public boolean isEmpty(){
		for(ArrayList<PCB> ele : q){
			if(ele != null){
				return false;
			}
		}
		return true;
	}
	

}

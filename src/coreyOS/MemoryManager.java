package coreyOS;

import java.util.ArrayList;
import java.util.Collections;
//import java.util.Hashtable;
import java.util.concurrent.Semaphore;


public class MemoryManager {
	
	private static MemoryManager instance = new MemoryManager();
	
	Queue wait;
	Queue io;
	ArrayList<PCB> terminate;
	ArrayList<PCB> remove;
	
	int jobsLeft;
	int jobsTotal;
	int cycles;
	
	Semaphore lock = new Semaphore(1);
	Semaphore remLock = new Semaphore(1);
	
	MemoryManager(){
		wait = new Queue();
		io = new Queue();
		terminate = new ArrayList<>();
		remove = new ArrayList<>();
	}
	
	public static MemoryManager getInstance(){
		if (instance == null){
			instance = new MemoryManager();
		}
		
		return instance;
	}
	
	public void updateQueues(){
		ArrayList<PCB> temp;
		temp = wait.poll(); //Gets all the PCB done waiting
		if(temp != null){
			Collections.reverse(temp); //Reverses the list so that First added to waiting queue ends up at front of RQ
			for(PCB ele : temp){
				if(ele != null){
					if(ele.counter == ele.length){
						ele.state = "terminated";
						ele.turnaroundTime = cycles;
						exit(ele);
					}
					else{
						ReadyQueue.getInstance().offerFront(ele); //Adds each PCB to the front of RQ
					}
				}
			}
		}
		temp = io.poll(); // Same as above, except for IO queue
		if(temp != null){
			Collections.reverse(temp);
			for(PCB ele : temp){
				if(ele != null){
					if(ele.counter == ele.length){
						ele.state = "terminated";
						ele.turnaroundTime = cycles;
						exit(ele);
					}
					else{
						ReadyQueue.getInstance().offerFront(ele);
					}
				}
			}
		}
		
		
		
	}
	
	//Checks if both IO and Wait queues are empty
	public boolean queueEmpty(){		
		return (wait.isEmpty() && io.isEmpty());
	}
	
	//Gets Memory Address from RAM for CPU without risking race conditions
	public Code getMemory(int address){
		Code code = null;
		do{
			//System.out.println("GETTING");
			try {
				lock.acquire(); // Ensures RAM is not being changed while CPU is fetching
				code = RAM.getInstance().storage.get(address);
				lock.release();				
			} catch (InterruptedException e) {
				System.out.println("BLEH BLEH BLEH");
			} finally {
				lock.release();	
			}
		}while(code == null);
		//System.out.println("RETURNING -> " + code);
		return code;
	}
	
	//Helps with managing terminating jobs
	public void exit(PCB p){
		jobsLeft--; //Decrements total job count
		termAdd(p);
		
		try {
			remLock.acquire();
			remove.add(p);
			remLock.release();
		} catch (InterruptedException e) {
			System.out.println("ERROR");
			e.printStackTrace();
		}
	}
	
	//Removes terminated jobs from RAM without risking race conditions
	public void cleanMemeory(){
		try {
			remLock.acquire();
			lock.acquire();
			for(PCB ele : remove){
				if(ele.getAddress() != -1)
					RAM.getInstance().removeJob(ele);
			}
		
			remove = new ArrayList<>();
			lock.release();
			remLock.release();
		} catch (InterruptedException e) {
			System.out.println("ERROR");
			e.printStackTrace();
		}
	}
	
	public void printRAM(){
		try {
			lock.acquire();
			RAM.getInstance().print();
			lock.release();
		} catch (InterruptedException e) {
			System.out.println("ERROR");
			e.printStackTrace();
		}
	}
	
	Semaphore termLock = new Semaphore(1);
	
	public void termAdd(PCB p){
		
		try {
			termLock.acquire();
		if(terminate.isEmpty()){
			for(int i = 0; i < jobsTotal; i++){
				terminate.add(new PCB());
			}
		}
		
		terminate.set(p.ID,p);
		} catch (InterruptedException e) {
			//System.out.println("Term Queue add fail");
		} finally{
			termLock.release();
		}
	}
}

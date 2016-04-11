package coreyOS;

import java.util.concurrent.Semaphore;

public class PCB {
	
	int ID = -1; // Process/Job Number
	int counter = 0; // Tracks line of code to be worked on
	int length = 0; // Number of lines of code
	int priority; // Priority, 10 = HIGH, 1 = LOW
	int hddAddress; // Start of programs HDD memory
	int ramAddress = -1; // Start of programs RAM memory
	
	int rqWait; // Number of cycles job was in Ready Queue
	int turnaroundTime; // Number of cycles until job was completed
	int responseTime; // Number of cycles until jobs first dispatched
	
	
	// Registers and Accumulator
	int A = 1;
	int B = 3;
	int C = 5;
	int D = 7;
	int Acc = 9;
	
	Semaphore addressLock = new Semaphore(1);
	String state = "new"; // New,ready,running,waiting,halted,...
	
	PCB(){}
	
	PCB(int ID, int length, int priority){
		this.ID = ID;
		this.length = length;
		this.priority = priority;
	}
	
	public String toString(){
		return "ID: " + ID + ",	Counter: " + counter + ",	Length: " + length + ",	Priority: "+ priority + ",	HDD Address: " + hddAddress + ",	RAM Address: "+ getAddress();
	}
	
	public String registers(){
		return A+","+B+","+C+","+D+","+Acc;
	}
	
	public int getAddress(){
		int add = -1;
		try {
			addressLock.acquire();
			add = this.ramAddress;
			addressLock.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return add;
	}
	
	public void setAddress(int add){
		try {
			addressLock.acquire();
			this.ramAddress = add;
			addressLock.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

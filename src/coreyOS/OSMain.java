package coreyOS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OSMain {

	public static void main(String[] args) {
		int ramSize = 150;
		int cpuSize = 8;
		int sortingMethod = 0;
		
		boolean userMode = true;
		//
		// Start User Input
		//
		if(userMode){
			BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
			boolean ok = false;
			String temp;
			while(!ok){
				try {
					System.out.print("# of CPU -> ");
					temp = read.readLine();
					cpuSize = Integer.parseInt(temp);
					ok = true;
				} catch (Exception e) {
					ok = false;
				}
			}
			ok = false;
			while(!ok){
				try {
					System.out.print("Size of RAM -> ");
					temp = read.readLine();
					ramSize = Integer.parseInt(temp);
					ok = true;
				} catch (Exception e) {
					ok = false;
				}
			}
	
			ok = false;
			while(!ok){
				try {
					System.out.println("1. LTS First");
					System.out.println("2. LTS Priority");
					System.out.println("3. LTS Smallest");
					System.out.print("Soring Method -> ");
					temp = read.readLine();
					sortingMethod = Integer.parseInt(temp);
					sortingMethod--;
					if(sortingMethod >= 0 && sortingMethod <= 2)
						ok = true;
				} catch (Exception e) {
					ok = false;
				}
			}
			try {
				read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//
		// End User Input
		//

		
		//
		// Start Getting Things Prepared for 'Big Loop'
		//
		long start = System.currentTimeMillis(); // Start total OS run time
		
		Boot boot = new Boot("ugradPart2.txt");
		boot.go();
		
		HDD.getInstance();
		RAM.getInstance();
		Scheduler longTerm;
		Scheduler shortTerm;
		

		RAM.getInstance().setSize(ramSize);
		
		longTerm = new Scheduler(Block.getInstance().blocks, sortingMethod);
		shortTerm = new Scheduler();
		ReadyQueue.getInstance();
		MemoryManager.getInstance();
		
		// Creates the CPUs
		CPU[] CPUs = new CPU[cpuSize];
		Thread[] threads = new Thread[cpuSize];
		for(int i = 0; i < cpuSize; i++){
			CPUs[i] = new CPU(i);
			threads[i] = new Thread();
		}
		//
		// Done with Prep
		//
		
		//
		// Starting 'Big Loop'
		//
		int jobLeft = 0;
		System.out.println("STARTING");
		while(MemoryManager.getInstance().jobsLeft > 0){
			//System.out.println("Cycle : " + MemoryManager.getInstance().cycles + " -- Jobs Left : "+ MemoryManager.getInstance().jobsLeft);
			//Start Metrics
			MemoryManager.getInstance().cycles++;
			ReadyQueue.getInstance().waitTime();
			//End Metrics

			MemoryManager.getInstance().cleanMemeory();
			longTerm.fillRAM();
			shortTerm.stsSchedule();

			//System.out.print("RQ : ");
			//ReadyQueue.getInstance().print2();
			
			for(CPU ele : CPUs){
				if(!ele.running && ele.flag){
					if(!threads[ele.ID].isAlive()){
						ReadyQueue.getInstance().dispatch(ele);
						if(ele.job.ID != -1){
							threads[ele.ID] = new Thread(ele);
							threads[ele.ID].setName(""+ele.ID);
							//System.out.println("Starting -> Thread : " + threads[ele.ID].getName() + "| CPU : "+ ele.ID);
							threads[ele.ID].start();
						}
					}
				}
				System.out.println("CPU : " + ele.ID + ", job : " + ele.job.ID + ", state : " + ele.running);// + ", details : " + ele.job.toString());
			}
			MemoryManager.getInstance().updateQueues();

			if(MemoryManager.getInstance().cycles > 3999){
//				System.out.println("FORCE STOPING INFINITE LOOP");
				jobLeft = MemoryManager.getInstance().jobsLeft;
				MemoryManager.getInstance().jobsLeft = 0;
			}
		}
		long end = System.currentTimeMillis(); // End total OS run time
		//
		// Ending 'Big Loop'
		//

		//
		// Starting Metrics
		//
		long total = end - start;
		float rqWaits = 0;
		float cmdsExecuted = 0;
		float turnaroundTimes = 0;
		float responseTimes = 0;
		
		int cycles = MemoryManager.getInstance().cycles;
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("--TERMINATE QUEUE---------------");
		for(PCB ele : MemoryManager.getInstance().terminate){
			if(ele.ID != -1){
				System.out.println(ele.ID + " : " + ele.registers()); // Prints Terminate Queue
				
				rqWaits += ele.rqWait; // Totals RQ Wait Time
				turnaroundTimes += ele.turnaroundTime; // Totals Turnaround Time
				responseTimes += ele.responseTime; // Totals Response Time
			}else{
				jobLeft++;
				System.out.println("Uncompleted");
			}				
		}
		System.out.println("--------------------------------");
		 
		
		System.out.println("--METRICS-----------------------");
		System.out.println("Total Time : " + total + " ms"); 		
		System.out.println("Total Cycles : " + cycles);	
		System.out.println("Total Jobs : " + MemoryManager.getInstance().jobsTotal);
		System.out.println("Average Cycles Over Time : " + (float)cycles/(float)total + " cycles/ms \n");
		System.out.println("Throughput : " + (float)MemoryManager.getInstance().jobsTotal /(float)total + " job/ms");	
		System.out.println("Throughput : " + (float)MemoryManager.getInstance().jobsTotal/cycles + " job/cycle \n");	
		System.out.println("Average Waiting Time : " + rqWaits /(float)MemoryManager.getInstance().jobsTotal + " cycle/job");
		System.out.println("Average Response Time : " + responseTimes/(float)MemoryManager.getInstance().jobsTotal + " cycle/job");
		System.out.println("Average Turnaround Time : " + turnaroundTimes/(float)MemoryManager.getInstance().jobsTotal + " cycle/job \n");
		String cpuUtilization = "";
		for(CPU ele : CPUs){
			cmdsExecuted += ele.executed;

			cpuUtilization += ("	CPU " + ele.ID + " Utilization : " + (float)ele.executed/(float)cycles + " instruction/cycle\n");			
		}
		
		System.out.println("Average CPU Utilization : "  + cmdsExecuted/(float)cycles/(float)cpuSize + " instruction/cycle");
		System.out.println(cpuUtilization);
		System.out.println("--------------------------------");
		//
		//Ending Metrics
		//

		if(MemoryManager.getInstance().cycles > 3999){
			System.out.println("Failure - CPU Thread(s) Froze");

			System.out.println("Jobs Left Uncompelted : " +jobLeft);

		} else {
			System.out.println("Jobs Left Uncompelted : " + jobLeft);
		}
	}
	
	/*
		CPU utilization – keep the CPU as busy as possible
		Throughput – # of processes that complete their execution per time unit
		Turnaround time – amount of time to execute a particular process
		Waiting time – amount of time a process has been waiting in the ready queue
		Response time – amount of time it takes from when a request was submitted until the first response is produced, not output  (for time-sharing environment)
	 */

}

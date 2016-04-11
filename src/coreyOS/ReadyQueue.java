package coreyOS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class ReadyQueue {
	
	private static ReadyQueue instance = new ReadyQueue();
	
	ArrayList<PCB> rq;
	Set<Integer> check;

	Semaphore rqLock = new Semaphore(1);
	
	// Basics of the Ready Queue
	
	ReadyQueue(){
		rq = new ArrayList<PCB>();
		check = new HashSet<Integer>();
		//status = new ArrayList<>();
	}
	
	public static ReadyQueue getInstance(){
		if (instance == null){
			instance = new ReadyQueue();
		}
		
		return instance;
	}
	
	public void offer(PCB p){
		if(check.add(p.ID)){
			rq.add(p);
		}
	}
	
	public void offerFront(PCB p){

		try {
			rqLock.acquire();
			p.state = "ready";
			rq.add(0,p);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			rqLock.release();
		}
	}
	
	public PCB peek(){
		if (!rq.isEmpty())
			return rq.get(0);
		else
			return null;
	}
	
	public PCB poll(){
		PCB p = null;
		if (!rq.isEmpty()){
			p = rq.remove(0);
			check.remove(p);
			return p;
		}else
			return p;
	}
	
	public void offerList(ArrayList<PCB> pList){
		for(PCB ele : pList){
			offer(ele);
		}
	}
	
	public void waitTime(){
		
		try {
			rqLock.acquire();
			for(PCB ele : rq){
				ele.rqWait++;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			rqLock.release();
		}
	}
	
	//
	//Dispatcher Code
	//
	
	//Gives each available CPU a job from RQ
	public void dispatch(CPU[] proccessors){
		
		for(CPU ele : proccessors){
			if(ele.flag()){
				if(peek() != null && peek().ID != -1){
					ele.flag = false;
					//System.out.println("Dispatching : CPU " + ele.ID + ", Job " + peek().ID);
					ele.insert(poll());
				}
				else {
					//System.out.println("RQ Empty!");
				}
				
			}
		}
	}
	
	public void dispatch(CPU proccessor){
		
			if(peek() != null && peek().ID != -1){
					proccessor.flag = false;
					//System.out.println("Dispatching : CPU " + proccessor.ID + ", Job " + peek().ID);
					proccessor.insert(poll());
			}
			else {
					//System.out.println("RQ Empty!");
			}
				
			
		
	}
	
	public void print(){
		for(PCB ele : rq){
			System.out.println(ele);
		}
		
	}
	public void print2(){
		String s = "";
		
		if(rq.isEmpty()){
			System.out.println(s);
			return;
		}
		
		for(PCB ele : rq){
			s += "["+ele.ID+"]";
		}
		System.out.println(s);
		
	}

}

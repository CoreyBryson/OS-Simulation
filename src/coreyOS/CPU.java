package coreyOS;


public class CPU implements Runnable{


	private int A;
	private int B;
	private int C;
	private int D;
	private int Acc;
	
	public PCB job;
	
	public int ID; // CPU ID
	public int executed;
	public boolean flag = true; // True = Ready for Job, False = Busy
	public boolean running = false; // If a job is running in the CPU or not
	private boolean wait = false; //Job command sends to a ready/wait/io queue

	
	CPU (){
		ID = 0;
		flag = true;
		job = new PCB();
	}
	
	CPU (int id){
		ID = id;
		flag = true;
		job = new PCB();
	}
	
	//Sets up job for processing
	public void insert(PCB j){
		flag = false;
		wait = false;
		this.job = j;
		this.A = job.A;
		this.B = job.B;
		this.C = job.C;
		this.D = job.D;
		this.Acc = job.Acc;
		this.job.state = "running";
		
		if(this.job.counter == 0){
			this.job.responseTime = MemoryManager.getInstance().cycles;
		}
	}

	//Fetches, Decodes, and Executes until termination or queue
	public void run() {

		//System.out.println("CPU : " + ID + " - Running");
		running = true;
		Code current  = null;
		while(!wait && job.counter <= job.length-1 && job.ID != -1){
		//while(current == null){
			try{
				//System.out.println("CPU : " + ID + " ASKING");
				current = MemoryManager.getInstance().getMemory(job.getAddress()+job.counter);
			}catch(Exception e){
				//System.out.println("INSTRUCTION GET FAILURE !!!");
			}
		//}
		job.counter++;
		//System.out.println("execute " + job.ID + " : " + current);

		int var1 = 0;
		int var2 = 0;
		try{
		switch(current.var1){
			case 'A':	var1 = A;
						break;
			case 'B':	var1 = B;
						break;
			case 'C': 	var1 = C;
						break;
			case 'D': 	var1 = D;
						break;
			default:  	break;
		}

		switch(current.var2){
			case 'A':	var2 = A;
						break;
			case 'B':	var2 = B;
						break;
			case 'C': 	var2 = C;
						break;
			case 'D': 	var2 = D;
						break;
			default:  	break;
		}

		switch(current.cmd){
			case "add" :	this.Acc += (var1 + var2);
							break;
			case "sub" :	this.Acc += (var1 - var2);
							break;
			case "mul" :	this.Acc += (var1 * var2);
							break;
			case "div" :	this.Acc += (var2 / var1);
							break;
			case "_rd" :	prepJob();
							this.job.state = "waiting";
							MemoryManager.getInstance().io.offer(this.job, current.var3);
							break;
			case "_wr" :	prepJob();
							this.job.state = "waiting";
							MemoryManager.getInstance().io.offer(this.job, current.var3);
							break;
			case "_wt" :	prepJob();
							this.job.state = "waiting";
							MemoryManager.getInstance().wait.offer(this.job, current.var3);
							break;
			case "sto" :	this.Acc = current.var3;
							break;
			case "rcl" :	rcl(current.var1);
							break;
			case "nul" :	nul();
							break;
			case "stp" :	prepJob();
							this.job.state = "ready";
							ReadyQueue.getInstance().offerFront(this.job);
							break;
			case "err" :	//job.counter = job.length;	
							prepJob();
							this.job.state = "terminated";
							this.job.turnaroundTime = MemoryManager.getInstance().cycles;
							MemoryManager.getInstance().exit(this.job);
							break;
			default :		break;				
		}
		}catch(NullPointerException e){
			job.counter--;
		}
		}
		
		if(job.length == job.counter && job.ID != -1 && !wait){
			prepJob();
			this.job.state = "terminated";
			this.job.turnaroundTime = MemoryManager.getInstance().cycles;
			MemoryManager.getInstance().exit(this.job);
			
			this.job = new PCB();
		}
		running = false;
		executed++;
		
		this.job = new PCB();
	}
	
	public void rcl(char var){
		switch(var){
			case 'A':	A = this.Acc ;
			break;
			case 'B':	B = this.Acc ;
			break;
			case 'C': 	C = this.Acc ;
			break;
			case 'D': 	D = this.Acc ;
			break;
			default :	break;
		}
	}
	
	public void nul(){
		this.A = 1;
		this.B = 3;
		this.C = 5;
		this.D = 7;
		this.Acc = 9;
	}
	
	public void save(){
		job.A = this.A;
		job.B = this.B;
		job.C = this.C;
		job.D = this.D;
		job.Acc = this.Acc;
	}
	
	public boolean flag(){
		return true;
	}
	
	public void print(){
		//System.out.println("A-> " + A + " B-> " + B + " C-> " + C + " D-> " + D + " Acc-> " + Acc);
		System.out.println( "Job " + job.ID + " : " + A + "," + B + "," + C + "," + D + "," + Acc);
	}
	
	private void prepJob(){
		save();
		wait = true;
		flag = true;
	}

}

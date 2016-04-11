package coreyOS;

import java.io.*;

public class Boot {
	
	private String code;

	Boot(){}
	
	Boot(String fileName){
		code = fileName;
	}
	
	// Reads file into the HDD
	public void go(){
		String line;
		
		File file = new File(code);
		BufferedReader read = null;
		try {
			read = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {	System.out.println(e.toString()); }
		
		try{
			while((line = read.readLine()) != null)
			{
				HDD.getInstance().store(line);
			}

			read.close();
		}catch(Throwable e){ System.out.println(e.toString());}
		
	}
	

}

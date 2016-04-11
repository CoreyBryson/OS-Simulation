package coreyOS;

public class Code {
	
	int 	line;
	String 	cmd;
	char 	var1;
	char 	var2;
	int 	var3;
	
	Code(){}
	
	Code(String s){
		String[] parts = s.split(",");
		this.line = Integer.parseInt(parts[0]);
		this.cmd = parts[1].trim();
		this.var1 = parts[2].trim().charAt(0);
		this.var2 = parts[3].trim().charAt(0);
		this.var3 = Integer.parseInt(parts[4].trim());
		
	}
	
	public String toString(){
		return line + ", " + cmd + ", " + var1 + ", " + var2 + ", " + var3;
	}
	
}

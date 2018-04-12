/*
 * Matthew Vastarelli
 * employee.java
 */
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract  class employee {
		
		protected String hiringTime;							//Time of employee hiring
		protected double baseSalary;							//Base pay
		protected String employmentType;						//Hourly or Salaried
		protected String loginName;								//Employees actual name
		protected String employeeName;							//Employees username
		protected static int nextID = -1;						//The next employee ID
		protected final int employeeID;							//Employees ID number
		protected double payed = 0.0;							//Pay for the current period
		protected byte[] password;								//Emplyoee's password
		//----------------------------------------------------------------------------
		public employee (int ID, String login, String type, double salary, String time, String name, byte[] pass) {
			employeeName = name;						//Set employees actual name
			loginName = login; 							//Set employees username
			baseSalary = salary;						//Set employees pay
			employmentType = type;						//Set employee's pay type
			hiringTime = time; 							//time of entry
			employeeID = ID; 							//Employees ID number
			password = pass;							//Emplyoee's password
			nextID = ID;
		}
		//-----------------------------------------------------------------------------
		public employee (String login, double salary, String name, byte[] pass) {
			//nextID();
			employeeName = name;					//Set employees actual name
			loginName = login; 						//Set employees username
			baseSalary = salary;					//Set employees pay
			hiringTime = date(); 					//time of hire
			employeeID = nextID(); 					//Employees ID number 
			password = pass;							//Emplyoee's password
		}
		//-----------------------------------------------------------------------------
		// Set salary
		public void setSalary (Double salary) {
			 baseSalary = salary;
		}
		//-----------------------------------------------------------------------------
		//To string to aid in the output of the employee class
		public String toString() {
			String IDformatted = String.format("%05d", employeeID);
			
			String info = String.format("%-10.30s   %-10.30s  %-10.30s %-10.30s   %-10.30s  %-10.30s  %-10.30s%n", 
					IDformatted, loginName, employmentType, baseSalary, hiringTime, employeeName, password);
			
			return  info;
		}
		//------------------------------------------------------------------------------
		//Creation of the employee ID
		public int nextID() {
			nextID++;
			return nextID;
		}
		//------------------------------------------------------------------------------
		//Gets the employee's login name
		public String getLoginName() {
			return loginName;
		}
		//------------------------------------------------------------------------------
		//Gets the employee's ID
		public int getID() {
			return employeeID;
		}
		//------------------------------------------------------------------------------
		//Gets the employee's password
		public byte[] getPass() {
			return password;
		}
		//------------------------------------------------------------------------------
		//Get the date and set it as a String
		public static String date() { 
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");  
			Date time = new Date();
			
			return dateFormat.format(time);
		}
		//------------------------------------------------------------------------------
		//Facilitates the payment of the employee's salaries
		public abstract double getPay();
		//------------------------------------------------------------------------------
	}
	

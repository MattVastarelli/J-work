/* 
 * Matthew Vastarelli
 * Hourly.Java
 */
import java.util.*;

public class Hourly extends employee {

	//-----------------------------------------------------------------------------------------------------------------
	public Hourly(String login, double salary, String name, byte[] password) {
		super(login, salary, name, password);
	}
	//----------------------------------------------------------------------------------------------------------------
		public Hourly (int ID, String login, String type, double salary, String time, String name, byte[] password) {
			super(ID, login, type, salary, time, name, password);
		}
	//-----------------------------------------------------------------------------------------------------------------
	//Calculates the pay for a hourly employee
	@Override
	public double getPay() {
		Scanner sc = new Scanner(System.in);	//Console scanner
		
		double hoursWorked = 0.00;				//Number of hours work in the two week period 
		double pay = 0.0;						//Amount to be payed to the employee
		
		System.out.println("Enter The number of hours "+ employeeName + " worked in the last two weeks: ");
		hoursWorked = sc.nextDouble();
		sc.nextLine();
		
		pay = baseSalary * hoursWorked;
		
		return pay;
	}
	//-----------------------------------------------------------------------------------------------------------------
}

/* 
 * Matthew Vastarelli
 * Salaried.Java
 */
public class Salaried extends employee {
	
	//-----------------------------------------------------------------------------------------------------------------
	public Salaried(String login, double salary, String name, byte[] password) {
		super(login, salary, name, password);
	}
	//----------------------------------------------------------------------------------------------------------------
	public Salaried (int ID, String login, String type, double salary, String time, String name, byte[] password) {
		super(ID, login, type, salary, time, name, password);
	}
	//-----------------------------------------------------------------------------------------------------------------
	//Calculates the pay for a salaried employee
	@Override
	public double getPay() {
		
		double pay = 0.0;						//Amount to be payed to the employee
		
		pay = baseSalary / 24;
		
		return pay;
	}
	//-----------------------------------------------------------------------------------------------------------------
}

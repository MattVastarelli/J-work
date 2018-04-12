/* 
 * Matthew Vastarelli
 * Payroll.Java
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.security.*;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
//U: mvast
//P: pass
public class Payroll extends Application {
	
	static employee currentUser;			//The index of the current employee using the system
	public static int currentID = -1;		//Id of the current person using the system
	static boolean loggedIn = false;
	static boolean isBoss = false;
	static ArrayList <employee> employeeList = new ArrayList <employee> (); //ArrayList of employees
	static ArrayList <employee> quitOrFired = new ArrayList <employee> ();	//ArrayList of employees fired employees
	public static String date = employee.date(); 
	
	private static String menu = "Payroll Menu\n\t1. Log In "+			//Menu of the System
			"\n\t2. Enter employees\n\t3. List Employees"+
			"\n\t4. Terminate an employee" +
			"\n\t5. Change employee data" +
			"\n\t6. Pay employees  \n\t0. Exit system";
	
	//Header for the payroll output
	private static String header = String.format("%-30.30s  %-30.30s%n %-30s%n %-18.30s   %-18.30s  %-18.30s%n", 
			" Payroll Report", date,"---------------------------------------------------","Pay" , "ID", "Name");
	
	static Scanner scConsle = new Scanner(System.in);		//Console scanner
	static File dataFile = new File("dataFile.txt");		//DatabaseFile
	static File payrollFile = new File("payroll.txt");		//Payroll file	
	static FileInputStream Fin = null;
	static FileOutputStream Fout = null;
	
	//------------------------------------------------------------------------------------------------------
	//Handles the reading and creation of the database file if there is none
	public static void fileHandler() {
		
		try {
			Fin = new FileInputStream(dataFile); 			//Database file
			ObjectInputStream ObjInstream = new ObjectInputStream(Fin);	//Object input stream
			
			int id;				//ID
			String login;		//Employee login
			double salary; 		//Employee pay
			String date;		//Date of hire
			String name;		//Employee name
			String type;		//Hourly or Salaried
			byte[] password;	//Emplyoee's password
			
			while(true) {
				
				id = (Integer) ObjInstream.readObject();
				login = (String) ObjInstream.readObject();
				salary = (Double) ObjInstream.readObject();
				type = (String) ObjInstream.readObject();
				date = (String) ObjInstream.readObject();
				name = (String) ObjInstream.readObject();
				password = (byte[]) ObjInstream.readObject();
				
				if(type.equals("Salaried")) {
					Salaried newEmplyoee = new Salaried (id, login, type, salary, date, name, password);
			        employeeList.add(newEmplyoee);
				}
				else {
					Hourly newEmplyoee = new Hourly (id, login, type, salary, date, name, password);
					employeeList.add(newEmplyoee);
				}
			}
			// Fout.close(); says unreachable
		} //Try
		catch (FileNotFoundException ex) {
			System.out.println("Error: No Database file, Creating one now.");
			try {
				dataFile.createNewFile();
				newEmplyoee();
			} catch (IOException e) {
				System.out.println("Error: IOException");
			}
		}
		catch (IOException e) {
		} catch (ClassNotFoundException e) {
			System.out.println("Error: Class not found");
		}
	}
	//-------------------------------------------------------------------------------------------------------
	//Login to the system 
	private static void doLogin() {
		String login;
		String password;
		loggedIn = false;
		int i = 0;
		
		System.out.print("\nEnter Login name: \n");
		login = scConsle.next();
		scConsle.nextLine();
		System.out.println("Enter password: ");
		password = scConsle.next();

		//search the employee arrayList
		for (employee emplyoeeSearch: employeeList) 
		{
			//Match on user
			if (login.equals(emplyoeeSearch.loginName)) {
				//check pass
				if(passMatch(emplyoeeSearch.getPass(), password)) {
					System.out.println("Login Successful.");
					if(emplyoeeSearch.employeeID == 0) {
						isBoss = true;
						loggedIn = true;
						currentUser = employeeList.get(i);
						break;
						}
					else {
						isBoss = false;
						currentUser = employeeList.get(i);
						loggedIn = true;
					}
				}	
				else
					break;
			}
			i++;
		}
		if (!loggedIn) {
			System.out.println("Login unsuccessful");
		}
	}
	//-------------------------------------------------------------------------------------------------------
	//Checks to see if passwords match
	public static boolean passMatch(byte[] pass, String input) {
		byte[] bArray1;
		MessageDigest digest1 = null;
		
		try {
			digest1 = MessageDigest.getInstance("SHA-256");
			digest1.update(input.getBytes());
			
		} catch (NoSuchAlgorithmException e) {
		}
		
		//hash and store
		bArray1 = digest1.digest();
		
		//convert the byte Array to hex string to compare 
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < bArray1.length; i++) {
         s.append(Integer.toString((bArray1[i] & 0xff) + 0x100, 16).substring(1));
        }
        
        StringBuffer s2 = new StringBuffer();
        for (int i = 0; i < pass.length; i++) {
         s2.append(Integer.toString((pass[i] & 0xff) + 0x100, 16).substring(1));
        }
		
        //Compare
		if(s.toString().equals(s2.toString()))
			return true;
		else
			return false;
	}
	//-------------------------------------------------------------------------------------------------------
	//Add an employee
	public static void newEmplyoee() {
		if(employeeList.isEmpty())
			isBoss = true;
	
		 if (isBoss) {
			String login;		//Employee login
    		double salary; 		//Employee pay
    		String name;		//Employee name
			String garbage;		//Eats the new line char
			int payType = 0;	//Salaried or hourly
			byte[] pass;		//Password
			
    			System.out.print("\nEnter name: \n"); 									
    			name = scConsle.nextLine();			
    			
    			System.out.print("\nEnter login name: ");
    			login = scConsle.next();
    			garbage = scConsle.nextLine();
    			
    			//search for the existence of the login name
    			for (employee emplyoeeSearch: employeeList) {
    				if(login.equals(emplyoeeSearch.loginName)) {
    					System.out.println("Error: login name is alread in use.");
    					System.out.print("\nEnter login name: ");
    	    			login = scConsle.next();
    	    			garbage = scConsle.nextLine();
    				}
    			}
    			
    			//calls for the password
    			pass = getNewPassword();
    		        
    			System.out.println("\n1: Salaried");
    			System.out.println("\n2: Hourly");
    			System.out.print("\nEnter the corresponding number for the emplyoment type: ");
    			payType = scConsle.nextInt();
    			garbage = scConsle.nextLine();
    			
    			//Salaried
    			if(payType == 1) {
    				System.out.print("\nEnter salaried employee salary: ");
        			salary = scConsle.nextDouble();
        			garbage = scConsle.nextLine();
        			
        			//create the employee
        			employee newemplyoee = new Salaried(login, salary, name, pass);
        			newemplyoee.employmentType = "Salaried";
        			//Add the employee to the ArrayList
        			employeeList.add(newemplyoee);
    			}
    			//Hourly
    			else if(payType == 2) {
    				System.out.print("\nEnter salary: ");
        			salary = scConsle.nextDouble();
        			garbage = scConsle.nextLine();
        			
        			//create the employee
        			employee newemplyoee = new Hourly(login, salary, name, pass);
        			newemplyoee.employmentType = "Hourly";
        			//Add the employee to the ArrayList
        			employeeList.add(newemplyoee);
    			}
    			else {
    				System.out.println("Error: Please select one of the two options.");
    			}
		}
		//Not the boss
		else {
			System.out.println("Error: permission denied, user is not the boss or not logged in.");
		}
	}
	//-------------------------------------------------------------------------------------------------------
	//Prompts for a password twice and compares the hashes of the passwords if they match the byte array is
	//Returned else the function prompts until two matching passwords are entered.
	public static byte[] getNewPassword() {
		byte[] bArray1;
		byte[] bArray2;
		String input;
		MessageDigest digest1 = null;
		MessageDigest digest2 = null;
		
		while(true) {
			
			System.out.println("Enter Password: ");
			input = scConsle.next();
			//bArray1 = input.getBytes();
			//set up the first password
			try {
				digest1 = MessageDigest.getInstance("SHA-256");
				digest1.update(input.getBytes());
				
			} catch (NoSuchAlgorithmException e) {
			}
			
			System.out.println("Renter Password: ");
			input = scConsle.next();
			bArray2 = input.getBytes();
			//set up the second password
			try {
				digest2 = MessageDigest.getInstance("SHA-256");
				digest2.update(input.getBytes());
				
			} catch (NoSuchAlgorithmException e) {
			}
			//hash and store
			bArray1 = digest1.digest();
			bArray2 = digest2.digest();
			
			//convert the byte Array to hex string to compare 
	        StringBuffer s = new StringBuffer();
	        for (int i = 0; i < bArray1.length; i++) {
	         s.append(Integer.toString((bArray1[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        
	        StringBuffer s2 = new StringBuffer();
	        for (int i = 0; i < bArray2.length; i++) {
	         s2.append(Integer.toString((bArray2[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        //Compare
			if(s.toString().equals(s2.toString()))
				return bArray1;
			else
				System.out.println("Error: passwords do not match");
		}
	
	}
	//-------------------------------------------------------------------------------------------------------
	//Prints out a list of current employees to the boss or the users data if they are not the boss
	public static void listEmplyoee() {
		if(isBoss) {
			System.out.println("Employees in the database");
			for (employee emplyoeeSearch: employeeList) 
			{
	        	System.out.printf(emplyoeeSearch.toString());
			}
		}
		else if(loggedIn) {
			System.out.println("Your information");
			System.out.println(currentUser.toString());
		}
		else {
			System.out.println("Error: User is not logged in.");
		}
	}
	//-------------------------------------------------------------------------------------------------------
	//Allows the boss to terminate an employee or have an employee quit
	public static void terminateEmplyoee() {
		int firedID = 0;
		employee terminate;
		int counter = 0;
		
		if (isBoss) {
			System.out.println("Enter employee ID who you whish to terminate employment: ");
			firedID = scConsle.nextInt();
			terminate = employeeList.get(firedID);
			quitOrFired.add(terminate);
			
			//search for the index of the employee to be terminated
			for (employee emplyoeeSearch: employeeList) {
				if(emplyoeeSearch.employeeID == firedID) {
					employeeList.remove(counter);
					System.out.println("Employee terminated");
					break;
				}
				counter++;
			}	
		}
		else {
			//search for the index of the employee that wishes to quit
			for (employee emplyoeeSearch: employeeList) {	
				if(emplyoeeSearch.employeeID == currentUser.employeeID) {
					quitOrFired.add(currentUser);
					employeeList.remove(counter);
					loggedIn = false;
					break;
				}
				counter++;
			}
			System.out.println("Employee terminated");
		}
	}
	//-------------------------------------------------------------------------------------------------------
	//Facilitates the payment of the employee's salaries based on pay type
	public static void payEmplyoee() {
		if (isBoss) {
			System.out.println("Paying employees");
			for(employee emplyoeeSearch: employeeList) {
				emplyoeeSearch.payed = emplyoeeSearch.getPay();
			}
			
			System.out.print(header);
			for(employee emplyoeeSearch: employeeList) {
				
				String IDformatted = String.format("%05d", emplyoeeSearch.employeeID);
				String Payformatted = String.format("%.2f", emplyoeeSearch.payed);
				String row = String.format("%-19.5s   %-18.30s  %-15.30s%n", Payformatted, 
						IDformatted, emplyoeeSearch.employeeName);
				System.out.print(row);
			}
			printPayroll();
		}
		else {
			System.out.println("Error: permission denied, user is not the boss or not logged in.");
		}
	}
	//------------------------------------------------------------------------------------------------------
	//Allows the boss to change employee data
	public static void changeData() {
		int command = 0;
		String change;
		String garbage;
		double newPay = 0.0;
		employee eInfoChange;
		int counter = 0;
		
		if(isBoss) {
			System.out.println("1: Change Name");
			System.out.println("2: Change Pay");
			System.out.println("Enter the corresponding number for the data to be changed: ");
			command = scConsle.nextInt();
			garbage = scConsle.nextLine();
			
			if(command == 1) {
				System.out.println("Enter employee ID whos data you wish to change: ");
				command = scConsle.nextInt();
				garbage = scConsle.nextLine();
				for (employee emplyoeeSearch: employeeList) {
					if(emplyoeeSearch.employeeID == command) {
						eInfoChange = employeeList.get(counter);
						System.out.println("Enter the new name for the employee: ");
						change = scConsle.nextLine();
						garbage = scConsle.nextLine();
						eInfoChange.employeeName = change;
						break;
					}
					counter++;
				}	
			}
			else if (command == 2) {
				System.out.println("Enter employee ID whos data you wish to change: ");
				command = scConsle.nextInt();
				for (employee emplyoeeSearch: employeeList) {
					if(emplyoeeSearch.employeeID == command) {
						eInfoChange = employeeList.get(counter);
						System.out.println("Enter the new pay for the employee: ");
						newPay = scConsle.nextDouble();
						eInfoChange.baseSalary = newPay;
						break;
					}
					counter++;
				}	
			}
			else {
				System.out.println("Error: Please select on of the options.");
			}
		}
		else {
			System.out.println("Error: permission denied, user is not the boss or not logged in.");
		}
	}
	//------------------------------------------------------------------------------------------------------
	//Prints the employee arrayList
	public static void print() {
		try {
			Fout = new FileOutputStream(dataFile);
		} catch (FileNotFoundException e) {
			System.out.println("Error: File not created");
		} 
        try {
			ObjectOutputStream objOut = new ObjectOutputStream(Fout);
			
			for (employee emplyoeeSearch: employeeList) 
			{
				objOut.writeObject(emplyoeeSearch.employeeID);
				objOut.writeObject(emplyoeeSearch.loginName);
				objOut.writeObject(emplyoeeSearch.baseSalary);
				objOut.writeObject(emplyoeeSearch.employmentType);
				objOut.writeObject(emplyoeeSearch.hiringTime);
				objOut.writeObject(emplyoeeSearch.employeeName);
				objOut.writeObject(emplyoeeSearch.getPass());
			}
		} catch (IOException e) {
			System.out.println("Error: IOException");
		}
	}
	//------------------------------------------------------------------------------------------------------
	//Prints the payroll to the payroll file
	public static void printPayroll() {
		try {
			payrollFile.createNewFile();
		} catch (IOException e1) {
			System.out.println("Error: IOException");
		}
		try {
			PrintWriter payWriter = new PrintWriter("payroll.txt");
			
			payWriter.print(header);
			for(employee emplyoeeSearch: employeeList) {
				
				String IDformatted = String.format("%05d", emplyoeeSearch.employeeID);
				String Payformatted = String.format("%.2f", emplyoeeSearch.payed);
				String row = String.format("%-19.5s   %-18.30s  %-15.30s%n", Payformatted, 
						IDformatted, emplyoeeSearch.employeeName);
				payWriter.print(row);
			}
			
			payWriter.close();
		}
		catch (FileNotFoundException e){
			System.out.println("Error: File not created");
		}
	}
	//-------------------------------------------------------------------------------------------------------
	//Menu for the program
	public static void doMenu() { 
		fileHandler();
		int command = 1; 	//Input command
		String garbage;		//Eats the new line char
		//Menu
		System.out.println(menu);
		try {
			while (true) {
					System.out.println("\nEnter the number of the command you wish to excute: ");
					command = scConsle.nextInt();
					garbage = scConsle.nextLine();
					switch (command) {
						case 1: command = 1;
			        				doLogin(); //Login
			        				break;
						case 2: command = 2;
			        				newEmplyoee(); //Create Employee
			        				break;
						case 3: command = 3;
			        				listEmplyoee(); //List the all employees or the current user 
			        				break;
						case 4: command = 4;
			        				terminateEmplyoee(); //Terminates or allows an employee to quit
			        				break;
						case 5: command = 5;
			        				changeData();	//Allows the changing of employee data
			        				break;
						case 6: command = 6;
									payEmplyoee();	//Facilitates the payment of the employee's salaries 
									break;
						case 7:	command = 0; //exit
			        				break;
			        		default: 
			        			break;
						}//Switch
				if(command == 0)
					break;
			}//While
		}
		finally {
			//print employees that have quit or been fired this running of the payroll system
			System.out.println("The employees that have quit or been fired this running of the payroll system");
			for (employee emplyoeeSearch: quitOrFired) 
			{
	        	System.out.printf(emplyoeeSearch.employeeName);
			}
			
			print();
			
			 try {
				 	Fout.close();
				 	Fin.close();
				} catch (IOException e) {
					System.out.println("Error: IOException");
				}
			 	catch(NullPointerException e) {
			 		
			 	}
		}
		
	System.out.println("\nThank you for using the Emplyoee Database.");
	}
	//------------------------------------------------------------------------------------------------------
	//Start method for JavaFx
	@Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }
	//------------------------------------------------------------------------------------------------------
	//
	public static void main(String[] args)
	 {
		launch(args);
		System.out.println("Welcome to the Emplyoee Database, by Matthew Vastarelli");
		//Payroll menu
		try {
			Payroll.doMenu();
		}
		catch(InputMismatchException ex) {
			System.out.println("Error: I/O Mismatch.");
			ex.printStackTrace(System.out);
		}	
	}
}
//-------------------------------------------------------------------------------------------------------

	




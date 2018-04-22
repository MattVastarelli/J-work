/* 
 * Matthew Vastarelli
 * Payroll.Java
 */
import java.awt.Insets;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
//U: mvast1
//P: pass
//U: test
//P: test
public class Payroll extends Application {
	
	static employee currentUser;			//The index of the current employee using the system
	public static int currentID = -1;		//Id of the current person using the system
	static boolean loggedIn = false;
	static boolean isBoss = false;
	static ArrayList <employee> employeeList = new ArrayList <employee> (); //ArrayList of employees
	public static String date = employee.date(); 
	public static boolean isFirstRun = false;
	
	//Header for the payroll output
	private static String header = String.format("%-30.30s  %-30.30s%n %-30s%n %-18.30s   %-18.30s  %-18.30s%n", 
			" Payroll Report", date,"---------------------------------------------------","Pay" , "ID", "Name");
	
	static Scanner scConsle = new Scanner(System.in);		//Console scanner
	static File dataFile = new File("dataFile.txt");		//DatabaseFile
	static File payrollFile = new File("payroll.txt");		//Payroll file	
	static FileInputStream Fin = null;
	static FileOutputStream Fout = null;
     
    // ----------------------------- GUI Elements Start --------------------------------------------------------
	//Login Elements
	private static Scene snLog;
	private static VBox loginPane;
	private static Label loginLabel;
	private static Button loginButton;
	private static Button loginQuit;
	private static TextField loginTF;
	private static Label pwLabel;
	private static PasswordField pwTF;
	//Boss Elements
	private static Scene bossScene;
	private static Button newEmpButton;
	private static Button changeEmpButton;
	private static Button payrollButton;
	private static Button quitButton;
	private static VBox bossPane;
	private static TableView<employee> t1;
	private static ObservableList<employee> olist;
	private static Button BlogOutButton;
	//New Employee Elements
	private static Scene newEmpScene;
	private static VBox newEmpPane;
	private static Label newLoginLabel;
	private static TextField newLoginField;
	private static Label newPasswordLabel;
	private static PasswordField newPasswordField;
	private static Label confirmNewPasswordLabel;
	private static PasswordField confirmNewPasswordField;
	private static Label newSalaryLabel;
	private static TextField newSalaryField;
	private static Label newNameLabel;
	private static TextField newNameField;
	private static Button submitNewEmployee;
	private static Button newEmpBackButton;
	private static RadioButton newHRadio;
	private static RadioButton newSRadio;
	private static ToggleGroup salaryTypeGroup;
	//Change Employee data Elements
	private static Scene changeEmpScene;
	private static VBox changeEmpPane;
	private static Button changeEmpBackButton;
	private static Label employeeIdLabel;
	private static TextField employeeIdField;
	private static Label changeNameLabel;
	private static TextField changeNameField;
	private static Label changeSalaryLabel;
	private static TextField changeSalaryField;
	private static Button submitChangedEmployee;
	private static Button fireEmployee;
	//Payroll Elements
	private static Scene payrollScene;
	private static VBox payrollPane;
	private static Button okButton;
	//Employee Elements
	private static Scene empScene;
	private static VBox EmpPane;
	private static Button EmpBackButton;
	private static Label EmpIdLabel;
	private static Label EmpId;
	private static Label EmpLoginLabel;
	private static Label EmpLogin;
	private static Label EmpNameLabel;
	private static Label EmpName;
	private static Label EmpSalaryLabel;
	private static Label EmpSalary;
	private static Label EmpDateLabel;
	private static Label EmpDate;
	private static Label EmpTypeLabel;
	private static Label EmpType;
	private static Button EmpQuitButton;
	private static Button ElogOutButton;
    // ----------------------------- GUI Elements end -----------------------------------------------------
	// ----------------------------- GUI Input ------------------------------------------------------------
	//Login
	private static String username;
	private static String pass;
	//New Employee
	private static String newLogin;
	private static String newPassword;
	private static String confirmNewPassword;
	private static String newName;
	private static String newSalary;
	private static String newSalaryType;
	//Change Employee data
	private static String employeeId;
	private static String changeName;
	private static String changeSalary;
	// ----------------------------- GUI Input -------------------------------------------------------------
	
	//-------------------------------------------------------------------------------------------------------
	//Login to the system 
	private static boolean doLogin() {
		
		loggedIn = false;
		int i = 0;
		
		//search the employee arrayList
		for (employee emplyoeeSearch: employeeList) 
		{
			//Match on user
			if (username.equals(emplyoeeSearch.loginName)) {
				//check pass
				if(passMatch(emplyoeeSearch.getPass(), pass)) {
					System.out.println("Login Successful.");
					if(emplyoeeSearch.employeeID == 0) {
						isBoss = true;
						loggedIn = true;
						currentID = emplyoeeSearch.getID();
						currentUser = employeeList.get(i);
						break;
						}
					else {
						isBoss = false;
						currentID = emplyoeeSearch.getID();
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
			return false;
		}
		return true;
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
	public static boolean newEmplyoee() {
		byte[] password;
		
		if(employeeList.isEmpty())
			isBoss = true;
	
		 if (isBoss) {
			double pay;
			
			if(isFirstRun)
			{ 
				int payType = 0;	//Salaried or hourly
				
				System.out.print("\nEnter name: \n"); 									
				newLogin = scConsle.nextLine();			
			    			
			    System.out.print("\nEnter login name: ");
			    username = scConsle.next();
			    scConsle.nextLine();
			    			
			    //search for the existence of the login name
			    for (employee emplyoeeSearch: employeeList) {
			    	if(username.equals(emplyoeeSearch.loginName)) {
			    		System.out.println("Error: login name is alread in use.");
			    		System.out.print("\nEnter login name: ");
			    		username = scConsle.next();
			    	    scConsle.nextLine();
			    	}
			    }			
			  	//calls for the password
			    password = getNewPassword();
			    		        
			    System.out.println("\n1: Salaried");
			    System.out.println("\n2: Hourly");
			    System.out.print("\nEnter the corresponding number for the emplyoment type: ");
			    payType = scConsle.nextInt();
			    scConsle.nextLine();
			    			
			    //Salaried
			    if(payType == 1) {
			    	System.out.print("\nEnter salaried employee salary: ");
			        pay = scConsle.nextDouble();
			        scConsle.nextLine();
			        			
			        //create the employee
			        employee newemplyoee = new Salaried(username, pay, newLogin, password);
			        newemplyoee.employmentType = "Salaried";
			        //Add the employee to the ArrayList
			        employeeList.add(newemplyoee);
			        return true;
			    }
			    //Hourly
			    else if(payType == 2) {
			    	System.out.print("\nEnter salary: ");
			        pay = scConsle.nextDouble();
			        scConsle.nextLine();			
			        //create the employee
			        employee newemplyoee = new Hourly(username, pay, newLogin, password);
			        newemplyoee.employmentType = "Hourly";
			        //Add the employee to the ArrayList
			        employeeList.add(newemplyoee);
			        return true;
			    }
			    else {
			    	System.out.println("Error: Please select one of the two options.");
			   }
			}
			//A database file exists
			else {
				pay = Double.parseDouble(newSalary);
    			
	    		//search for the existence of the login name
	    		for (employee emplyoeeSearch: employeeList) {
	    			if(newLogin.equals(emplyoeeSearch.loginName)) {
	    				System.out.println("Error: login name is alread in use.");
	    			}
	    		}	
	    		//calls for the password
	    		password = getNewPassword();
	    			
	    		//Salaried
	    		if(newSalaryType.equals("salaried")) {
	        		//create the employee
	        		employee newemplyoee = new Salaried(newLogin, pay, newName, password);
	        		newemplyoee.employmentType = "Salaried";
	        		//Add the employee to the ArrayList
	        		employeeList.add(newemplyoee);
	    		}
	    		//Hourly
	    		else if(newSalaryType.equals("hourly")) {
	        		//create the employee
	        		employee newemplyoee = new Hourly(newLogin, pay, newName, password);
	        		newemplyoee.employmentType = "Hourly";
	       			//Add the employee to the ArrayList
	       			employeeList.add(newemplyoee);
	    		}
	    		else {
	    			System.out.println("Error: Please select one of the two options.");
	    		}
	    		return true;
			}			
	}
	//Not the boss
	else {
		System.out.println("Error: permission denied, user is not the boss or not logged in.");
		return false;
	}
	 return false;
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
		
		if(isFirstRun)
		{
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
		//Database file exists
		else
		{
			while(true) {
				try {
					digest1 = MessageDigest.getInstance("SHA-256");
					digest1.update(newPassword.getBytes());
					
				} catch (NoSuchAlgorithmException e) {
				}
				//set up the second password
				try {
					digest2 = MessageDigest.getInstance("SHA-256");
					digest2.update(confirmNewPassword.getBytes());
					
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
	}
	//-------------------------------------------------------------------------------------------------------
	//Prints out a list of current employees to the boss or the users data if they are not the boss
	public static void listEmplyoee() {
		System.out.println("Employees in the database");
		for (employee emplyoeeSearch: employeeList) 
		{
	        System.out.printf(emplyoeeSearch.toString());
		}
	}
	//-------------------------------------------------------------------------------------------------------
	//Allows the boss to terminate an employee or have an employee quit
	public static void terminateEmplyoee() {
		employee terminate;
		int counter = 0;
		
		if (isBoss) {
			int firedID = Integer.parseInt(employeeId);
			terminate = employeeList.get(firedID);
			//search for the index of the employee to be terminated
			for (employee emplyoeeSearch: employeeList) {
				if(emplyoeeSearch.employeeID == firedID) {
					employeeList.remove(counter);
					break;
				}
				counter++;
			}	
		}
		else {
			//search for the index of the employee that wishes to quit
			for (employee emplyoeeSearch: employeeList) {	
				if(emplyoeeSearch.employeeID == currentUser.employeeID) {
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
		double newPay = Double.parseDouble(changeSalary);
		employee eInfoChange;
		int counter = 0;
		int eID = Integer.parseInt(employeeId);

		if(isBoss) {
				for (employee emplyoeeSearch: employeeList) {
					if(emplyoeeSearch.employeeID == eID) {
						eInfoChange = employeeList.get(counter);
						eInfoChange.employeeName = changeName;
						eInfoChange.baseSalary = newPay;
						break;
					}
					counter++;
				}	
			}	
		else 
			System.out.println("Error: Please select on of the options.");
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
	//----------------------------------------------------------------------------------------------------
	//Method to handle quitting the system
	public static void quit() {
		print();
		
		 try {
			 	Fout.close();
			 	Fin.close();
			} catch (IOException e) {
				System.out.println("Error: IOException");
			}
		 	catch(NullPointerException e) {
		 		
		 	}
		 System.exit(0);
	}
	//------------------------------------------------------------------------------------------------------
	//Start method 
	@Override
    public void start(Stage primaryStage) {
		//Handles the reading and creation of the database file if there is none
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
		} //Try
		catch (FileNotFoundException ex) {
			isFirstRun = true;
			System.out.println("Error: No Database file, Creating one now.");
			try {
				dataFile.createNewFile();
				newEmplyoee();
				isFirstRun = false;
			} catch (IOException e) {
				System.out.println("Error: IOException");
			}
		}
		catch (IOException e) {
		} catch (ClassNotFoundException e) {
			System.out.println("Error: Class not found");
		}
		finally{
			buildGUI(primaryStage);
		}
	}
	//------------------------------------------------------------------------------------------------------
	//Method to make the GUI 
	public static void buildGUI(Stage primaryStage) {
		
	//------------------------  GUI Login Start -------------------------------------------------------------
		
		loginPane = new VBox(25);
		loginLabel = new Label("Username:");
		loginButton = new Button("Log In");
		loginTF = new TextField();
		pwLabel = new Label("Password:");
		pwTF = new PasswordField();
		loginQuit = new Button("Quit");
		
		loginQuit.setPrefWidth(750);
		loginButton.setPrefWidth(750);
		loginPane.setSpacing(30);
		
		//Add all the elements to the pane
		loginPane.getChildren().addAll(loginLabel, loginTF, pwLabel, pwTF, loginButton, loginQuit);
							
		snLog = new Scene(loginPane, 750, 375 );
		//Show the login screen 
		primaryStage.setTitle("Emplyoee Database System");
		primaryStage.setScene(snLog);
		primaryStage.show();
		
		loginButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Login");
				username = loginTF.getText();
				pass = pwTF.getText();
				
				if (doLogin()) {
					if(isBoss) {
						//Show the boss scene
						listEmplyoee();
						primaryStage.setScene(bossScene); 
						primaryStage.show();
					}
					else {
						//Set the employee data
						EmpId.setText(currentUser.getIDString());
						EmpLogin.setText(currentUser.getLoginName());
						EmpName.setText(currentUser.employeeName);
						EmpSalary.setText(currentUser.getPayString());
						EmpDate.setText(currentUser.hiringTime);
						EmpType.setText(currentUser.employmentType);
						//show the scene 
						primaryStage.setScene(empScene); 
						primaryStage.show();
					}
				}
			}
		});
		
		loginQuit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Quit");
				quit();
			}
		});
							
// --------------------------  GUI login End ----------------------------------------------------------			
// --------------------------  GUI New Employee Start -------------------------------------------------
		
		newEmpPane = new VBox(20);
		newEmpBackButton = new Button ("Return Without Saving");
		newLoginLabel = new Label("Login:");
		newLoginField = new TextField();
		newPasswordLabel = new Label("Password:");
		newPasswordField = new PasswordField();
		confirmNewPasswordLabel = new Label ("Confirm Password:");
		confirmNewPasswordField = new PasswordField();
		newNameLabel = new Label("Name:");
		newNameField = new TextField();
		newSalaryLabel = new Label("Salary:");
		newSalaryField = new TextField();
		submitNewEmployee = new Button("Submit");
		newHRadio = new RadioButton("Salaried");
		newSRadio = new RadioButton("Hourly");
		salaryTypeGroup = new ToggleGroup();
		
		newEmpBackButton.setPrefWidth(800);
		submitNewEmployee.setPrefWidth(800);
		//Add the radio buttons to the group
		newHRadio.setToggleGroup(salaryTypeGroup);
		newSRadio.setToggleGroup(salaryTypeGroup);
		
		newEmpBackButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
		    public void handle(ActionEvent event) {
		        System.out.println("Back to boss");
		        listEmplyoee();
		        primaryStage.setScene(bossScene);
		        primaryStage.show();
			  }
			});
					
		submitNewEmployee.setOnAction(new EventHandler<ActionEvent>() {
			@Override
		    public void handle(ActionEvent event) {
		        System.out.println("Submit");
		        //get the information from the fields to be accessed by the newEmplyoee Function
		        newLogin = newLoginField.getText();
		        newPassword = newPasswordField.getText();
		        confirmNewPassword = confirmNewPasswordField.getText();
				newName = newNameField.getText();
				newSalary = newSalaryField.getText();
				if (newEmplyoee())
				{
					listEmplyoee();
					primaryStage.setScene(bossScene);
					primaryStage.show();
				}
			  }
			});	
		
		newSRadio.setOnAction(new EventHandler<ActionEvent>() {
			@Override
		    public void handle(ActionEvent event) {
		        System.out.println("Salaried");
		        newSalaryType = "salaried";
			  }
			});	
		
		newHRadio.setOnAction(new EventHandler<ActionEvent>() {
			@Override
		    public void handle(ActionEvent event) {
		        System.out.println("Hourly");
		        newSalaryType = "hourly";
			  }
			});	
		//Add all the elements to the pane
		newEmpPane.getChildren().addAll(newEmpBackButton, newLoginLabel, newLoginField, newPasswordLabel, 
				newPasswordField, confirmNewPasswordLabel, confirmNewPasswordField, newNameLabel, newNameField, 
				newSalaryLabel,  newSalaryField,newSRadio, newHRadio, submitNewEmployee);

		newEmpScene = new Scene (newEmpPane, 800, 600);
								
// ---------------------------  GUI New Employee End -------------------------------------------------------
// ---------------------------  GUI Change start -----------------------------------------------------------
		changeEmpPane = new VBox(20);
		changeEmpBackButton = new Button("Back");
		employeeIdLabel = new Label("Enter ID of Employee to change");
		employeeIdField = new TextField();
		changeNameLabel = new Label("New Employee Name");
		changeNameField = new TextField();
		changeSalaryLabel = new Label("New Employee Salary");
		changeSalaryField = new TextField();
		submitChangedEmployee = new Button("Submit Changes");
		fireEmployee = new Button("Fire Employee");
		
		changeEmpBackButton.setPrefWidth(800);
		submitChangedEmployee.setPrefWidth(800);
		fireEmployee.setPrefWidth(800);
		
		changeEmpBackButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	        	System.out.println("Back");
	        	listEmplyoee();
	        	primaryStage.setScene(bossScene);
	        	primaryStage.show();
		     }
		  });
	
		submitChangedEmployee.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	        	System.out.println("Change");
	        	//Receive the changed items from the text fields 
	        	employeeId = employeeIdField.getText();
	    		changeName = changeNameField.getText();
	    		changeSalary = changeSalaryField.getText();
	    		changeData();
	    		listEmplyoee();
	        	primaryStage.setScene(bossScene);
	        	primaryStage.show();
		     }
		  });
		
		fireEmployee.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	        	//Get the ID and fire the employee
	        	employeeId = employeeIdField.getText();
	        	System.out.println("Fire Employee");
	        	terminateEmplyoee();
	        	listEmplyoee();
	        	primaryStage.setScene(bossScene);
	        	primaryStage.show();
		     }
		  });
		//add all the elements to the pane 
		changeEmpPane.getChildren().addAll(changeEmpBackButton, employeeIdLabel, employeeIdField,
				changeNameLabel, changeNameField, changeSalaryLabel, changeSalaryField, 
				submitChangedEmployee, fireEmployee);
		
		changeEmpScene = new Scene(changeEmpPane, 800, 400);
		
// ------------------------  GUI Change end ----------------------------------------------------------------
// ------------------------  GUI Boss Start ----------------------------------------------------------------
		
		newEmpButton = new Button("Create New Employee");
		changeEmpButton = new Button("Change Employee Information");
		payrollButton = new Button ("Create Payroll");
		quitButton = new Button ("Exit Program");
		BlogOutButton = new Button ("Log Out");
		bossPane = new VBox(30);
		
		newEmpButton.setPrefWidth(800);
		changeEmpButton.setPrefWidth(800);
		payrollButton.setPrefWidth(800);
		quitButton.setPrefWidth(800);
		BlogOutButton.setPrefWidth(800);
		
		newEmpButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	        	System.out.println("New employee");
	        	primaryStage.setScene(newEmpScene);
	        	primaryStage.show();
		     }
		  });
		changeEmpButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	        	System.out.println("Change");
	        	primaryStage.setScene(changeEmpScene);
	        	primaryStage.show();
		     }
		  });
		payrollButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	        	System.out.println("Payroll");
	        	payEmplyoee();
	        	primaryStage.setScene(payrollScene);
	        	primaryStage.show();
		     }
		  });
		quitButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	        	System.out.println("Quit");
	        	//write and quit 
	        	quit();
		     }
		  });
		BlogOutButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	        	System.out.println("log out");
	        	print();
	        	primaryStage.setScene(snLog);
	        	primaryStage.show();
	        	loggedIn = false;
		     }
		  });
		
		olist = FXCollections.observableArrayList(employeeList);
		
		t1 = new TableView<employee>();
		//make the columns
		TableColumn idCol = new TableColumn("ID");
		TableColumn logCol = new TableColumn("Login");
		TableColumn nameCol = new TableColumn("Name");
		TableColumn salCol = new TableColumn("Salary");
		TableColumn datCol = new TableColumn("Hiring Date and Time");
		//put the data in them
		//idCol.setCellValueFactory(new PropertyValueFactory(changeName));		
		
		
		t1.getColumns().addAll(idCol, logCol, nameCol, salCol, datCol);
		
		olist.addAll(employeeList);
		t1.setItems(olist);
		
		/*olist.clear();
		olist.addAll(employeeList);
		t1.setItems(olist);*/
		
		//add all the elements to the pane
		bossPane.getChildren().addAll(newEmpButton, changeEmpButton, payrollButton, quitButton, 
				BlogOutButton, t1);

		bossScene = new Scene(bossPane, 800, 600);
		
// ---------------------------  GUI Boss End ------------------------------------------------------------
// ---------------------------  GUI Payroll Start -------------------------------------------------------
		payrollPane = new VBox(20);
		okButton = new Button("OK");
		
		okButton.setPrefWidth(800);
		
		okButton.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent event) {
	    	System.out.println("Payed");
			primaryStage.setScene(bossScene);
			primaryStage.show();
			}
		});
				
		// Displays payroll
		payrollPane.getChildren().addAll(okButton,t1);
				
		payrollScene = new Scene(payrollPane, 800, 600);
// -----------------------------  GUI Payroll End ----------------------------------------------------
	
// -----------------------------  GUI Employee Start -------------------------------------------------
		
		EmpPane = new VBox(20);
		EmpIdLabel = new Label("ID Number: ");
		EmpId = new Label("");
		EmpLoginLabel = new Label("Login Name: ");
		EmpLogin = new Label ("");
		EmpNameLabel = new Label("Name: ");
		EmpName = new Label ("");
		EmpSalaryLabel = new Label("Salary: ");
		EmpSalary = new Label ("");
		EmpTypeLabel = new Label("Pay type: ");
		EmpType = new Label ("");
		EmpDateLabel = new Label("Date and Time Hired: ");
		EmpDate = new Label ("");
		EmpQuitButton = new Button("Quit Job");
		EmpBackButton = new Button("Exit ");
		ElogOutButton = new Button("Log Out");
		
		EmpBackButton.setPrefWidth(800);
		EmpQuitButton.setPrefWidth(800);
		ElogOutButton.setPrefWidth(800);
		
		EmpPane.setSpacing(10);
		
		EmpBackButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	        	System.out.println("Quit");
	        	//write and quit
	        	quit();
		     }
		  });
		
		EmpQuitButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event1) {
	        	System.out.println("quit");
	        	terminateEmplyoee();
	        	//write and quit
	        	quit();
		     }
		  });
		ElogOutButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	        	System.out.println("log out");
	        	print();
	        	primaryStage.setScene(snLog);
	        	primaryStage.show();
	        	loggedIn = false;
		     }
		  });
		//add all the elements to the pane
		EmpPane.getChildren().addAll(EmpIdLabel, EmpId, EmpLoginLabel, EmpLogin, 
				EmpNameLabel, EmpName, EmpSalaryLabel, EmpSalary, EmpTypeLabel, EmpType, EmpDateLabel, EmpDate, 
				EmpBackButton,EmpQuitButton, ElogOutButton);
		
		empScene = new Scene (EmpPane, 800, 425);
		 
// -------------------  GUI Employee End --------------------------------------------------------------
	}
//------------------------------------------------------------------------------------------------------
//Main
	public static void main(String[] args)
	 {
		launch(args);
	 }
}
//-------------------------------------------------------------------------------------------------------

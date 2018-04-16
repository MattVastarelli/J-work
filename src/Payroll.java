/* 
 * Matthew Vastarelli
 * Payroll.Java
 */
import java.util.*;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.io.*;
import java.security.*;
import javafx.*;
import javafx.event.*;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
//U: mvast
//P: pass
public class Payroll extends Application {
	private ArrayList<employee> empList = new ArrayList<employee>();
	private ArrayList<employee> formerEmpList = new ArrayList<employee>();
	private employee currentUser;
	Scanner isc = new Scanner(System.in);
	private PrintWriter pw;
	private String empFile = "empFile.txt";
	private String payrollFile = "payroll.txt";
	private MessageDigest d;
	
	//A login screen. When an employee successfully logs in, go to either screen 2 or screen 6,
	//depending on who logged in.
	private Scene loginScene;
	private VBox loginPane;
	private Label loginLabel;
	private Button loginButton;
	private TextField loginTF;
	private Label pwLabel;
	private PasswordField pwTF;
	//A Boss screen with a scrollable text area to display the list of employees and buttons to create
	//a new employee, make changes, or do the payroll. After doing the payroll, go to screen 5.
	private Scene bossScene;
	private Button newEmpButton;
	private Button changeEmpButton;
	private Button payrollButton;
	private Button quitButton;
	private VBox bossPane;
	private TableView t1;
	private ObservableList<employee> olist;
	//A screen for creating new Employees. Create the employee and return to screen 2 when the
	//Boss clicks one of the radio buttons to select the employee type.
	private Scene newEmpScene;
	private VBox newEmpPane;
	private Label newLoginLabel;
	private TextField newLoginField;
	private Label newPasswordLabel;
	private PasswordField newPasswordField;
	private Label confirmNewPasswordLabel;
	private PasswordField confirmNewPasswordField;
	private Label newSalaryLabel;
	private TextField newSalaryField;
	private Label newNameLabel;
	private TextField newNameField;
	private Button submitNewEmployee;
	private Button newEmpBackButton;
	//A screen for the Boss to change an Employee�s name or salary or fire him. Return to screen
	//2 when the Boss clicks �OK�.
	private Scene changeEmpScene;
	private VBox changeEmpPane;
	private Button changeEmpBackButton;
	private Label employeeIdLabel;
	private TextField employeeIdField;
	private Label changeNameLabel;
	private TextField changeNameField;
	private Label changeSalaryLabel;
	private TextField changeSalaryField;
	private Button submitChangedEmployee;
	private Button fireEmployee;
	//A screen to display the payroll data. This should have a scrollable text area for the payroll
	//output and a single �OK� button that takes control back to screen 2.
	private Scene payrollScene;
	private VBox payrollPane;
	private Button okButton;
	//A screen for the non-Boss to view his own data and possibly quit. This screen should display
	//a photograph.
	private Scene thisEmpScene;
	private VBox thisEmpPane;
	private Button thisEmpBackButton;
	private Label thisEmpIdLabel;
	private Label thisEmpId;
	private Label thisEmpLoginLabel;
	private Label thisEmpLogin;
	private Label thisEmpNameLabel;
	private Label thisEmpName;
	private Label thisEmpSalaryLabel;
	private Label thisEmpSalary;
	private Label thisEmpDateLabel;
	private Label thisEmpDate;
	private Button thisEmpQuitButton;
	
	public static void main (String[] args) {launch(args);}
	
	@Override
	public void start(Stage st) throws Exception{		
		try {
			FileInputStream fis = new FileInputStream(empFile);
			ObjectInputStream ois = new ObjectInputStream(fis); 
			for (;;) { 
				employee in = (employee) ois.readObject(); // Will not read in more than one employee.
				empList.add(in);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found, creating Boss datafile.");
			newEmployee();
		} catch (ClassNotFoundException e) {
			System.out.println("File not found, creating Boss datafile.");
			newEmployee();
		} catch (EOFException e) {
			System.out.println("Employee database successfully loaded.\n");
		} catch (IOException e) {
			System.out.println("An IO Exception has occured.");
			e.printStackTrace();
			System.exit(0);
		} finally {
			buildGUI(st);
		}
	}
	
	private void buildGUI(Stage st) {
		
		//A login screen. When an employee successfully logs in, go to either screen 2 or screen 6,
		//depending on who logged in.
		loginPane = new VBox(25);
		loginScene = new Scene(loginPane, 800, 600);
		loginLabel = new Label("Username:");
		loginTF = new TextField();
		loginButton = new Button("Log In");
		pwLabel = new Label("Password:");
		pwTF = new PasswordField();
		currentUser = empList.get(0);
		
		st.setScene(loginScene);
		st.show();
		st.setTitle("Philip Levine - Assignment 6: Payroll With Passwords and JavaFX");		
		
		loginPane.getChildren().addAll(loginLabel, loginTF, pwLabel, pwTF, loginButton);
		
		loginButton.setOnAction(e -> {
			try {d = MessageDigest.getInstance("SHA-256");}
			catch (NoSuchAlgorithmException e1) {System.out.println("Warning, algorithm not found.\n");}
			for (employee emp : empList) {
				if (loginTF.getText().equals(emp.getLoginName())) {
					try {
						byte[] a1 = null;
						byte[] a2 = null;
						a1 = d.digest(pwTF.getText().getBytes("UTF-8"));
						a2 = emp.getPass();
						StringBuilder sb1 = new StringBuilder();
						StringBuilder sb2 = new StringBuilder();
						for (byte b : a1) {sb1.append(String.format("%02X", b));}
						for (byte b : a2) {sb2.append(String.format("%02X", b));}
						if (sb1.toString().equals(sb2.toString())) {
							System.out.println("Login Successful");
							if (emp.loginName.equals("Boss")) {st.setScene(bossScene);}
							else {st.setScene(thisEmpScene);}
						}
						else {continue;}
					}catch (UnsupportedEncodingException e1) {e1.printStackTrace();}
				}
			}
		});
		
		//A Boss screen with a scrollable text area to display the list of employees and buttons to create
		//a new employee, make changes, or do the payroll. After doing the payroll, go to screen 5.
		bossPane = new VBox(20);
		bossScene = new Scene(bossPane, 800, 600);
		newEmpButton = new Button("Create New Employee");
		changeEmpButton = new Button("Change Employee Information");
		payrollButton = new Button ("Create Payroll");
		quitButton = new Button ("Exit Program");
		newEmpButton.setOnAction(e -> st.setScene(newEmpScene));
		changeEmpButton.setOnAction(e -> st.setScene(changeEmpScene));
		payrollButton.setOnAction(e -> st.setScene(payrollScene));
		quitButton.setOnAction(e -> writeAndQuit());
		
		t1 = new TableView();
		TableColumn idCol = new TableColumn("ID");
		TableColumn logCol = new TableColumn("Login");
		TableColumn nameCol = new TableColumn("Name");
		TableColumn salCol = new TableColumn("Salary");
		TableColumn datCol = new TableColumn("Hiring Date");
		t1.getColumns().addAll(idCol, logCol, nameCol, salCol, datCol);
		
		idCol.setCellValueFactory(new PropertyValueFactory<employee, String>("id"));
		logCol.setCellValueFactory(new PropertyValueFactory<employee, String>("login"));
		nameCol.setCellValueFactory(new PropertyValueFactory<employee, String>("name"));
		salCol.setCellValueFactory(new PropertyValueFactory<employee, Float>("salary"));
		datCol.setCellValueFactory(new PropertyValueFactory<employee, Date>("date"));
		
		olist = FXCollections.observableArrayList();
		updateTable();
		
		bossPane.getChildren().addAll(newEmpButton, changeEmpButton, payrollButton, quitButton, t1);
		
		//A screen for creating new Employees. Create the employee and return to screen 2 when the
		//Boss clicks one of the radio buttons to select the employee type.
		newEmpPane = new VBox(20);
		newEmpScene = new Scene (newEmpPane, 800, 600);
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
		newEmpBackButton.setOnAction(e -> st.setScene(bossScene));
		employee toAdd;
		String inp1 = "";
		byte[] inp2;
		int inp3; 
		String inp4 = "";
		String inp5 = "";
		
		submitNewEmployee.setOnAction(e -> {
			//Compile and add employee
			updateTable();
			st.setScene(bossScene);
		});
		
		newEmpPane.getChildren().addAll(newEmpBackButton, newLoginLabel, newLoginField, newPasswordLabel, newPasswordField, confirmNewPasswordLabel, confirmNewPasswordField, newNameLabel, newNameField, newSalaryLabel, newSalaryField, submitNewEmployee);
		
		//A screen for the Boss to change an Employee�s name or salary or fire him. Return to screen
		//2 when the Boss clicks �OK�.
		changeEmpPane = new VBox(20);
		changeEmpScene = new Scene(changeEmpPane, 800, 600);
		changeEmpBackButton = new Button("Return Without Saving");
		employeeIdLabel = new Label("Enter ID of Employee to change");
		employeeIdField = new TextField();
		changeNameLabel = new Label("New Employee Name");
		changeNameField = new TextField();
		changeSalaryLabel = new Label("New Employee Salary");
		changeSalaryField = new TextField();
		submitChangedEmployee = new Button("Submit Employee Changes");
		fireEmployee = new Button("Fire Employee");
		changeEmpBackButton.setOnAction(e -> st.setScene(bossScene));
		
		submitChangedEmployee.setOnAction(e -> {
			// Get employee, make changes
			updateTable();
			st.setScene(bossScene);
		});
		
		fireEmployee.setOnAction(e -> {
			// Get employee, remove from list
			updateTable();
			st.setScene(bossScene);
		});
		
		changeEmpPane.getChildren().addAll(changeEmpBackButton, employeeIdLabel, employeeIdField, changeNameLabel, changeNameField, changeSalaryLabel, changeSalaryField, submitChangedEmployee, fireEmployee);
		
		//A screen to display the payroll data. This should have a scrollable text area for the payroll
		//output and a single �OK� button that takes control back to screen 2.
		payrollPane = new VBox(20);
		payrollScene = new Scene(payrollPane, 800, 600);
		okButton = new Button("OK");
		okButton.setOnAction(e -> {st.setScene(bossScene);});
		// Displays payroll
		payrollPane.getChildren().addAll(okButton, t1);
		
		//A screen for the non-Boss to view his own data and possibly quit. This screen should display
		//a photograph.
		thisEmpPane = new VBox(20);
		thisEmpScene = new Scene (thisEmpPane, 800, 600);
		thisEmpIdLabel = new Label("ID Number: ");
		thisEmpId = new Label("");
		thisEmpLoginLabel = new Label("Login Name: ");
		thisEmpLogin = new Label ("");
		thisEmpNameLabel = new Label("Name: ");
		thisEmpName = new Label ("");
		thisEmpSalaryLabel = new Label("Salary: ");
		thisEmpSalary = new Label ("");
		thisEmpDateLabel = new Label("Date Hired: ");
		thisEmpDate = new Label ("");
		
		//thisEmpId.setText(currentUser.getIdString());
		//thisEmpLogin.setText(currentUser.getLoginName());
		//thisEmpName.setText(currentUser.getName());
		//thisEmpSalary.setText(currentUser.getSalaryString());
		//thisEmpDate.setText(currentUser.getDate());
		
		thisEmpQuitButton = new Button("Quit This Job");
		thisEmpBackButton = new Button("Exit Program");
		thisEmpBackButton.setOnAction(e -> {System.exit(1);});
		thisEmpQuitButton.setOnAction(e -> {
			if (currentUser.getID() != 0) {
				empList.remove(currentUser);
				System.exit(1);
			}
		});
		
		thisEmpPane.getChildren().addAll(thisEmpBackButton,thisEmpIdLabel, thisEmpId, thisEmpLoginLabel, thisEmpLogin, thisEmpNameLabel, thisEmpName, thisEmpSalaryLabel, thisEmpSalary, thisEmpDateLabel, thisEmpDate, thisEmpQuitButton);
	}
	
	private void updateTable() {
		olist.clear();
		olist.addAll(empList);
		t1.setItems(olist);
	}
	
	private void newEmployee(){
		String inp1 = "";
		byte[] inp2;
		String inp4 = "";
		String inp5 = "";
		int inp3;
		Boolean c = false;
		employee toAdd = null;
		while (c == false) {
			System.out.println("Please input new logon name.");
			inp1 = isc.next();
			if (empList.size() != 0) {
				for (employee e: empList){
					if (inp1.equals(e.getLoginName())){
						System.out.println("This login name is already in use. Please enter another.");
						inp1 = null;
					}
					else {c = true;}
				}
			}
			else {c = true;}
		}
		inp2 = getNewPassword();
		System.out.println("Please input new salary.");
		inp3 = isc.nextInt();
		for (;;) {
			System.out.println("Please input H for hourly, S for salaried.");
			inp4 = isc.next();
			if (inp4.equals("H") || inp4.equals("S")) {break;}
			else {System.out.println("Please make a valid selection\n");}
		}
		System.out.println("Please input new name");
		inp5 = isc.next();
		if (inp4.equals("H")) {
		//	toAdd = new Hourly(inp1, inp2, inp3, inp5);
		}
		else {
			//toAdd = new Salaried(inp1, inp2, inp3, inp5);
		}
		empList.add(toAdd);
	}
	
	private byte[] getNewPassword() {
		String s1 = "";
		String s2 = "";
		byte[] a1 = null;
		byte[] a2 = null;
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		try {
			for(;;) {
				System.out.println("Please input user password");
				s1 = isc.next();
				a1 = d.digest(s1.getBytes("UTF-8"));
				for (byte b : a1) {
					sb1.append(String.format("%02X", b));
				}
				System.out.println("Please confirm user password");
				s2 = isc.next();
				a2 = d.digest(s2.getBytes("UTF-8"));
				for (byte b : a2) {
					sb2.append(String.format("%02X", b));
				}
				System.out.println(sb1.toString());
				System.out.println(sb2.toString());
				if (sb1.toString().equals(sb2.toString())){
					return a1;
				} 
				else {
					System.out.println("Passwords do not match. Please input again.\n");
				}
			}
		} catch (UnsupportedEncodingException e) {e.printStackTrace();}
		return a1;
	}
	
	private void writeAndQuit() {
		System.out.println("Quit selected. Writing to file.");
		try {
			FileOutputStream fos = new FileOutputStream(empFile);
		    ObjectOutputStream oos  = new ObjectOutputStream(fos);
			for (employee e: empList) {
				oos.writeObject(e);
			}
		} 
		catch (IOException e1) {
			System.out.println("Output streams could not be closed. This should never happen.");
			e1.printStackTrace();
		}
		finally {System.exit(1);}
	}
	
}
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Employee Payroll System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        JLabel employeeIDLabel = new JLabel("Employee ID:");
        JTextField employeeIDField = new JTextField(10);
        JLabel positionLabel = new JLabel("Position:");
        JTextField positionField = new JTextField(15);
        JLabel hourlyRateLabel = new JLabel("Hourly Rate:");
        JTextField hourlyRateField = new JTextField(10);
        JLabel annualSalaryLabel = new JLabel("Annual Salary:");
        JTextField annualSalaryField = new JTextField(10);
        JButton addEmployeeButton = new JButton("Add Employee");
        JButton calculatePayButton = new JButton("Calculate Pay");
        JTextArea resultArea = new JTextArea(10, 40);

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(employeeIDLabel);
        panel.add(employeeIDField);
        panel.add(positionLabel);
        panel.add(positionField);
        panel.add(hourlyRateLabel);
        panel.add(hourlyRateField);
        panel.add(annualSalaryLabel);
        panel.add(annualSalaryField);
        panel.add(addEmployeeButton);
        panel.add(calculatePayButton);
        panel.add(resultArea);

        addEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText();
                    int employeeID = Integer.parseInt(employeeIDField.getText());
                    String position = positionField.getText();
                    double hourlyRate = Double.parseDouble(hourlyRateField.getText());
                    double annualSalary = Double.parseDouble(annualSalaryField.getText());

                    Employee employee = new Employee(name, employeeID, position, hourlyRate, annualSalary);
                    EmployeeDatabase.addEmployee(employee);

                    clearFields(nameField, employeeIDField, positionField, hourlyRateField, annualSalaryField);

                    JOptionPane.showMessageDialog(frame, "Employee added successfully.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input. Please enter valid numeric values.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        calculatePayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int employeeID = Integer.parseInt(employeeIDField.getText());

                    Employee employee = EmployeeDatabase.findEmployeeByID(employeeID);

                    if (employee == null) {
                        JOptionPane.showMessageDialog(frame, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double pay;
                    if (employee.getHourlyRate() > 0) {
                        pay = employee.getHourlyRate() * 40;
                    } else {
                        pay = employee.getAnnualSalary() / 52;
                    }

                    DecimalFormat df = new DecimalFormat("#.##");
                    resultArea.setText("Employee: " + employee.getName() + "\nEmployee ID: " + employee.getEmployeeID() + "\nPosition: " + employee.getPosition() +
                            "\nWeekly Pay: $" + df.format(pay));

                    generatePayStub(employee, pay);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a valid employee ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    private static void clearFields(JTextField nameField, JTextField employeeIDField, JTextField positionField,
                                    JTextField hourlyRateField, JTextField annualSalaryField) {
        nameField.setText("");
        employeeIDField.setText("");
        positionField.setText("");
        hourlyRateField.setText("");
        annualSalaryField.setText("");
    }

    private static void generatePayStub(Employee employee, double pay) {
        String payStub = "Employee: " + employee.getName() + "\nEmployee ID: " + employee.getEmployeeID() +
                "\nPosition: " + employee.getPosition() + "\nWeekly Pay: $" + pay;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("paystub_" + employee.getEmployeeID() + ".txt"))) {
            writer.write(payStub);
        } catch (IOException e) {
            System.err.println("Error writing pay stub to file: " + e.getMessage());
        }
    }
}

class Employee {
    private String name;
    private int employeeID;
    private String position;
    private double hourlyRate;
    private double annualSalary;

    public Employee(String name, int employeeID, String position, double hourlyRate, double annualSalary) {
        this.name = name;
        this.employeeID = employeeID;
        this.position = position;
        this.hourlyRate = hourlyRate;
        this.annualSalary = annualSalary;
    }

    public String getName() {
        return name;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public String getPosition() {
        return position;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public double getAnnualSalary() {
        return annualSalary;
    }
}

class EmployeeDatabase {
    private static List<Employee> employeeList = new ArrayList<>();

    public static void addEmployee(Employee employee) {
        employeeList.add(employee);
    }

    public static Employee findEmployeeByID(int employeeID) {
        for (Employee employee : employeeList) {
            if (employee.getEmployeeID() == employeeID) {
                return employee;
            }
        }
        return null;
    }
}

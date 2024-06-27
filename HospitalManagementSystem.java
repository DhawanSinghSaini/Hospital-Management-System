package HospitalManagementSystem; //Package Declaration

//imports

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "password";

    public static void main(String[] args) {
        try { // MySQL CONNECTION ESTABLISHED
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        boolean on = true;

        // program turn on
        while (on) {
            System.out.println("Who are you : ");
            System.out.println("1. Patient");
            System.out.println("2. Doctor");
            System.out.println("3.Exit");
            System.out.println("Enter your choice: ");
            int choice1 = scanner.nextInt();

            // here access in chosen
            switch (choice1) {
                case 1: // IF PATIENT THIS SECTION WILL RUN
                    try {
                        Connection connection = DriverManager.getConnection(url, username, password);
                        Patient patient = new Patient(connection, scanner);
                        Doctor doctor = new Doctor(connection);
                        while (true) {
                            System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                            System.out.println("1. Add Patient");
                            System.out.println("2. View Patients");
                            System.out.println("3. View Doctors");
                            System.out.println("4. Book Appointment");
                            System.out.println("5. Exit");
                            System.out.println("Enter your choice: ");
                            int choice = scanner.nextInt();

                            switch (choice) {
                                case 1:
                                    // Add Patient
                                    patient.addPatient();
                                    System.out.println();
                                    break;
                                case 2:
                                    // View Patient
                                    patient.viewPatients();
                                    System.out.println();
                                    break;
                                case 3:
                                    // View Doctors
                                    doctor.viewDoctors();
                                    System.out.println();
                                    break;
                                case 4:
                                    // Book Appointment
                                    bookAppointment(patient, doctor, connection, scanner);
                                    System.out.println();
                                    break;
                                case 5:
                                    System.out.println("THANK YOU! FOR USING HOSPITAL MANAGEMENT SYSTEM!!");
                                    HospitalManagementSystem.main(args);
                                    return;
                                default:
                                    System.out.println("Enter valid choice!!!");
                                    break;
                            }

                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;

                case 2: // IF DOCTOR THIS PATIENT WILL RUN
                    try {
                        Connection connection = DriverManager.getConnection(url, username, password);
                        Patient patient = new Patient(connection, scanner);
                        Doctor doctor = new Doctor(connection);
                        while (true) {
                            System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                            System.out.println("1. View Patient");
                            System.out.println("2. View Appointments");
                            System.out.println("3. View Doctors");
                            System.out.println("4. Add Doctor");
                            System.out.println("5. Exit");
                            System.out.println("Enter your choice : ");
                            int choice = scanner.nextInt();

                            switch (choice) {
                                case 1:
                                    // View Patient
                                    patient.viewPatients();
                                    System.out.println();
                                    break;
                                case 2:
                                    // View Appointments
                                    System.out.println("Enter Doctor Id :");
                                    int doc_id = scanner.nextInt();
                                    doctor.showAppointments(doc_id);
                                    System.out.println();
                                    break;
                                case 3:
                                    // View Doctors
                                    doctor.viewDoctors();
                                    System.out.println();
                                    break;
                                case 4:
                                    // Book Appointment
                                    System.out.print("Enter Doctor Name : ");
                                    String name = scanner.next();
                                    System.out.print("Enter Doctor Specialization : ");
                                    String spec = scanner.next();
                                    doctor.addDoctor(name, spec);
                                    break;
                                case 5:
                                    System.out.println("THANK YOU! FOR USING HOSPITAL MANAGEMENT SYSTEM!!");
                                    HospitalManagementSystem.main(args);
                                    return;
                                default:
                                    System.out.println("Enter valid choice!!!");
                                    break;
                            }

                        }

                    } catch (Exception e) {
                        System.out.println("error");
                    }

                case 3: // IF EXIT- OPTIONS WILL BE DISPLAYED AGAIN
                    System.out.println("THANK YOU! FOR USING HOSPITAL MANAGEMENT SYSTEM!!");
                    break;

                default: // INVALID INPUT
                    System.out.println("Enter valid choice!!!");
                    break;
            }
        }

    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.print("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Appointment Booked!");
                    } else {
                        System.out.println("Failed to Book Appointment!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Doctor not available on this date!!");
            }
        } else {
            System.out.println("Either doctor or patient doesn't exist!!!");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

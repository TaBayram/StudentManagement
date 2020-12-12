package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;

public class Database{
    public static class DatabaseConnection{
        private static String url = "jdbc:sqlserver://localhost:1433;" + "databaseName=StudentDatabase;integratedSecurity=true";

        public static ObservableList<Student> GetAllStudents(){
            try{
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();
                //CallableStatement stmt = connection.prepareCall("{}")

                String sqlScript = "{call spAllStudent()}";                                                             //PROCEDURE
                ResultSet resultSet = statement.executeQuery(sqlScript);
                final ObservableList<Student> students = FXCollections.observableArrayList();

                int i = 0;
                while(true){
                    resultSet.next();
                    if(resultSet.isAfterLast()) break;
                    Student student = new Student();
                    student.setID(resultSet.getInt("ID"));
                    student.setName(resultSet.getString("Name"));
                    student.setSurname(resultSet.getString("Surname"));
                    student.setEmail(resultSet.getString("Email"));
                    student.setPassword(resultSet.getString("Password"));
                    student.setDepartmentID(String.valueOf(resultSet.getInt("DepartmentID")));
                   // student.setRegisteredDate(resultSet.getDate("RegisteredDate").toString());
                 //   student.setSemester(resultSet.getInt("Semester"));
                   // student.setAdvisorID(resultSet.getInt("AdvisorID"));

                    students.add(student);
                }
                connection.close();
                return students;
            }catch(Exception e){
                System.out.println("Connection Error: " + e.getMessage());

            }
            return null;
        }

        public static boolean GetStudentDepartmentName(ObservableList<Student> students){
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();

                String sqlScript = "Select StudentTable.ID,DepartmentTable.Name from StudentTable " +
                        "LEFT JOIN DepartmentTable " +
                        "ON StudentTable.DepartmentId = DepartmentTable.ID";                                                             //PROCEDURE
                ResultSet resultSet = statement.executeQuery(sqlScript);
                final ObservableList<Student> studentsDepartmentNames = FXCollections.observableArrayList();

                int i = 0;
                while(true){
                    resultSet.next();
                    if(resultSet.isAfterLast()) break;
                    int id = resultSet.getInt("ID");
                    for (Student student: students) {
                        if(student.getID() == id){
                            student.setDepartmentName(resultSet.getString(("Name")));
                            break;
                        }
                    }
                }
                connection.close();
                return true;
            }catch(Exception e){
                System.out.println("Connection Error: " + e.getMessage());

            }
            return false;
        }

        public static ArrayList<String> GetDepartmentNames(){
            try{
            Connection connection = DriverManager.getConnection(url);

            //Get the names
            Statement statement = connection.createStatement();
            String sqlScript = "SELECT Name FROM DepartmentTable";                                                      //SELECT FROM
            ResultSet resultSet = statement.executeQuery(sqlScript);
            ArrayList<String> departmentNames = new ArrayList<>();
            int i = 0;
            while(true){
                resultSet.next();
                if(resultSet.isAfterLast()) break;
                departmentNames.add(resultSet.getString("Name"));
            }
            connection.close();
            return departmentNames;

            }catch(Exception e){
                System.out.println("Connection Problem");
            }
            return null;
        }
        public static ObservableList<Department> GetAllDepartments(){
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                String sqlScript = "SELECT * FROM DepartmentTable";
                ResultSet resultSet = statement.executeQuery(sqlScript);
                final ObservableList<Department> departments = FXCollections.observableArrayList();

                int i = 0;
                while(true){
                    resultSet.next();
                    if(resultSet.isAfterLast()) break;
                    Department department = new Department();
                    department.setID(resultSet.getInt("ID"));
                    department.setName(resultSet.getString("Name"));
                    department.setLanguage(resultSet.getString("Language"));
                    department.setDepartmentChair(resultSet.getInt("DepartmentChair"));
                    departments.add(department);
                }
                connection.close();
                return departments;

            }catch(Exception e){
                System.out.println("Connection Problem");
            }
            return null;
        }
        public static ObservableList<Student> GetStudentListByDepartmentID(int ID){
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                                                                                                                        //COMPLEX WHERE CLAUSE
                String sqlScript = String.format("Select * from StudentTable where DepartmentID = (SELECT ID FROM DepartmentTable where ID = '%d')", ID);                                                      //SELECT FROM Kullanildi
                ResultSet resultSet = statement.executeQuery(sqlScript);
                final ObservableList<Student> students = FXCollections.observableArrayList();

                int i = 0;
                while(true){
                    resultSet.next();
                    if(resultSet.isAfterLast()) break;
                    Student student = new Student();
                    student.setID(resultSet.getInt("ID"));
                    student.setName(resultSet.getString("Name"));
                    student.setSurname(resultSet.getString("Surname"));
                    student.setEmail(resultSet.getString("Email"));
                    student.setPassword(resultSet.getString("Password"));
                    student.setDepartmentID(String.valueOf(resultSet.getInt("DepartmentID")));
                    students.add(student);
                }
                connection.close();
                return students;

            }catch(Exception e){
                System.out.println("Connection Problem " + e.getMessage());
            }

            return null;
        }

        public static int AddStudent(Student student){
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                                                                                                                        //INSERT VALUES
                String sqlScript = String.format("spAddStudent '%s', '%s','%s', '%s', %d", student.getName(),student.getSurname(),student.getPassword(),student.getEmail(), Integer.parseInt(student.getDepartmentID()));
                ResultSet resultSet = statement.executeQuery(sqlScript);
                int id = 0;
                resultSet.next();
                if(resultSet.isAfterLast()) return 0;
                id = resultSet.getInt(1);

                connection.close();
                return id;
            }catch(Exception e){
                System.out.println("Connection Problem " + e.getMessage());
                return 0;
            }

        }
        public static boolean RemoveStudent(Student student){
            int id = student.getID();
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                                                                                                                        //Delete
                String sqlScript = String.format("Delete from StudentTable Where ID = '%d'", id);
                statement.executeUpdate(sqlScript);
                connection.close();
                return true;
            }catch(Exception e){
                System.out.println("Connection Problem " + e.getMessage());
                return false;
            }

        }

        public static int GetStudentCount(){
            try{
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();

                String sqlScript = String.format("SELECT COUNT(ID) as [Count] FROM StudentTable");
                ResultSet resultSet = statement.executeQuery(sqlScript);
                resultSet.next();
                int count = resultSet.getInt("Count");
                connection.close();
                return count;
            }catch(Exception e){
                System.out.println("Connection Problem " + e.getMessage());
                return 0;
            }
        }
        public static int GetStudentCountByDepartmentID(int ID){
            try{
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();

                String sqlScript = String.format("SELECT COUNT(ID) as [Count] FROM StudentTable Where DepartmentID = %d",ID);
                ResultSet resultSet = statement.executeQuery(sqlScript);
                resultSet.next();
                int count = resultSet.getInt("Count");
                connection.close();
                return count;
            }catch(Exception e){
                System.out.println("Connection Problem " + e.getMessage());
                return 0;
            }
        }
        public static int GetTeacherCount(){
            try{
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();

                String sqlScript = String.format("SELECT COUNT(ID) as [Count] FROM Teacher");
                ResultSet resultSet = statement.executeQuery(sqlScript);
                resultSet.next();
                int count = resultSet.getInt("Count");
                connection.close();
                return count;
            }catch(Exception e){
                System.out.println("Connection Problem " + e.getMessage());
                return 0;
            }
        }

        public static ObservableList<Teacher> GetAllTeachers(){
            try{
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();
                //CallableStatement stmt = connection.prepareCall("{}")

                String sqlScript = "SELECT * FROM Teacher";                                                             //PROCEDURE
                ResultSet resultSet = statement.executeQuery(sqlScript);
                final ObservableList<Teacher> teachers = FXCollections.observableArrayList();

                int i = 0;
                while(true){
                    resultSet.next();
                    if(resultSet.isAfterLast()) break;
                    Teacher teacher = new Teacher();
                    teacher.setID(resultSet.getInt("ID"));
                    teacher.setName(resultSet.getString("Name"));
                    teacher.setSurname(resultSet.getString("Surname"));
                    teacher.setEmail(resultSet.getString("Email"));
                    teacher.setTitle(resultSet.getString("Title"));
                    teacher.setPassword(resultSet.getString("Password"));
                    teacher.setDepartmentID(String.valueOf(resultSet.getInt("DepartmentID")));
                    teacher.setRegisteredDate(String.valueOf(resultSet.getInt("RegisteredDate")));

                    teachers.add(teacher);
                }
                connection.close();
                return teachers;
            }catch(Exception e){
                System.out.println("Connection Error: " + e.getMessage());
            }
            return null;
        }
    }
}
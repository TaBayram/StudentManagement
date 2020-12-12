package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;

public class Database{
    public static class DatabaseConnection{
        public static ObservableList<Student> GetAllStudents(){
            try{
                String url = "jdbc:sqlserver://localhost:1433;" + "databaseName=StudentDatabase;integratedSecurity=true";
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                //CallableStatement stmt = connection.prepareCall("{}")

                String sqlScript = "{call spAllStudent()}";                                                        //SELECT Kullanildi
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
                    student.setDepartmentID(resultSet.getInt("DepartmentID"));
                    students.add(student);
                }
                connection.close();
                return students;
            }catch(Exception e){
                System.out.println("Can't Login");

            }
            return null;
        }

        public static ArrayList<String> GetDepartmentNames(){
            try{
            String url = "jdbc:sqlserver://localhost:1433;" + "databaseName=StudentDatabase;integratedSecurity=true";
            Connection connection = DriverManager.getConnection(url);

            //Get the names
            Statement statement = connection.createStatement();
            String sqlScript = "SELECT Name FROM DepartmentTable";                                                      //SELECT FROM Kullanildi
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
                String url = "jdbc:sqlserver://localhost:1433;" + "databaseName=StudentDatabase;integratedSecurity=true";
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
                String url = "jdbc:sqlserver://localhost:1433;" + "databaseName=StudentDatabase;integratedSecurity=true";
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
                    student.setDepartmentID(resultSet.getInt("DepartmentID"));
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
                String url = "jdbc:sqlserver://localhost:1433;" + "databaseName=StudentDatabase;integratedSecurity=true";
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                                                                                                                        //INSERT VALUES
                String sqlScript = String.format("{call spAllStudent()}", student.getName(),student.getSurname(),student.getPassword(),student.getEmail(),student.getDepartmentID());
                PreparedStatement preparedStatement = connection.prepareStatement(sqlScript, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.executeUpdate();
                int id = 0;
                try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                    keys.next();
                    id = keys.getInt(1);
                }

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
                String url = "jdbc:sqlserver://localhost:1433;" + "databaseName=StudentDatabase;integratedSecurity=true";
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

    }


}


/*
insert into StudentTable (Name,Surname,Password,Email,DepartmentID) values(LTRIM(RTRIM('   aAaErdem')),LTRIM(RTRIM('DEMİR')),LTRIM(RTRIM('parolamız')),LTRIM(RTRIM('se@@sss')),LTRIM(RTRIM(1000)))
Insert into StudentTable (Name,Surname,Password,Email,DepartmentID) Values('%s','%s','%s','%s','%d')




Create Procedure spAddStudent

@Name nvarchar(50),
@Surname nvarchar(50),
@Password nvarchar(50),
@Email nvarchar(50),
@DepartmentID int

as
Begin
insert into StudentTable (Name,Surname,Password,Email,DepartmentID)
values(LTRIM(RTRIM('%s')),LTRIM(RTRIM('%s')),LTRIM(RTRIM('%s')),LTRIM(RTRIM('%s')),LTRIM(RTRIM('%d')))
End

spAddStudent

Create Procedure spAllStudent
as
Begin
SELECT * FROM StudentTable
End

spAllStudent


 */
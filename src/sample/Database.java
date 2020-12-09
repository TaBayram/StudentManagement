package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Database{
    public static class DatabaseConnection{
        public static void Connect(){
            try{
                String url = "jdbc:sqlserver://localhost:1433;" + "databaseName=StudentDatabase;integratedSecurity=true";
                //String url ="jdbc:sqlserver://GREENANGEL\\;databaseName=StudentDatabase;integratedSecurity=true";

                Connection connection = DriverManager.getConnection(url);
                Statement statement = null;
                statement = connection.createStatement();

                String sqlScript = "SELECT * FROM StudentTable";
                ResultSet resultSet = statement.executeQuery(sqlScript);

                resultSet.next();
                System.out.println("Name: "+ resultSet.getString("Name"));
            }catch(Exception e){
                System.out.println("Can't Login");
            }
        }

        public static ArrayList<String> GetDepartmentNames(){
            try{
            String url = "jdbc:sqlserver://localhost:1433;" + "databaseName=StudentDatabase;integratedSecurity=true";
            Connection connection = DriverManager.getConnection(url);

            //Get the names
            Statement statement = connection.createStatement();
            String sqlScript = "SELECT Name FROM DepartmentTable";
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
        public static ObservableList<Student> GetStudentListByDepartmentName(String departmentName){
            try{
                String url = "jdbc:sqlserver://localhost:1433;" + "databaseName=StudentDatabase;integratedSecurity=true";
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                String sqlScript = String.format("Select * from StudentTable where DepartmentID = (SELECT ID FROM DepartmentTable where Name = '%s')", departmentName);
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
                String sqlScript = String.format("Insert into StudentTable (Name,Surname,Password,Email,DepartmentID) Values('%s','%s','%s','%s','%d')", student.getName(),student.getSurname(),student.getPassword(),student.getEmail(),student.getDepartmentID());
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

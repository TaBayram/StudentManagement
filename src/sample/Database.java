package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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

                //Get the names
                Statement statement = connection.createStatement();
                String sqlScript = String.format("Select * from StudentTable where DepartmentID = (SELECT ID FROM DepartmentTable where Name = '%s')", departmentName);
                ResultSet resultSet = statement.executeQuery(sqlScript);
                ArrayList<Student> students = new ArrayList<>();
                final ObservableList<Student> studentss = FXCollections.observableArrayList();

                int i = 0;
                while(true){
                    resultSet.next();
                    if(resultSet.isAfterLast()) break;
                    Student student = new Student();
                    student.setID(resultSet.getInt("ID"));
                    student.setName(resultSet.getString("Name"));
                    student.setSurname(resultSet.getString("Surname"));
                    student.setEmail(resultSet.getString("Email"));
                    students.add(student);
                    studentss.add(student);
                }
                return studentss;

            }catch(Exception e){
                System.out.println("Connection Problem " + e.getMessage());
            }

            return null;
        }

    }


}

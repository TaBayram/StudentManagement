package sample;

import javafx.event.ActionEvent;

import java.sql.*;


public class Controller {


    public void testclicked(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {

        String url = "jdbc:sqlserver://localhost:1433;" + "databaseName=StudentDatabase;integratedSecurity=true";
        //String url ="jdbc:sqlserver://GREENANGEL\\;databaseName=StudentDatabase;integratedSecurity=true";

        Connection connection = DriverManager.getConnection(url);
        Statement statement = null;
        statement = connection.createStatement();

        String sqlScript = "SELECT * FROM StudentTable";
        ResultSet resultSet = statement.executeQuery(sqlScript);

        resultSet.next();
        System.out.println("Name: "+ resultSet.getString("Name"));

        




    }


}

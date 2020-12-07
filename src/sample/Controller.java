package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Controller {


    public ListView ListView_Department;

    @FXML
    public void testclicked(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {

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

        ArrayList<Button> ListButton = new ArrayList<Button>();
        Button btn = new Button();
        ListButton.add(btn);
        btn.textProperty().setValue("Bilgisayar");
        ListView_Department.getItems().add(btn);
        btn.textProperty().setValue("Yazilim");
        ListView_Department.getItems().add(btn);



    }


}

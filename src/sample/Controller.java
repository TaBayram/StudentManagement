package sample;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Controller {


    public ListView ListView_Department;
    public ListView ListView_Student;
    public TableView TableView_Student;
    public TableColumn TableColumn_StudentID;
    public TableColumn TableColumn_StudentName;
    public TableColumn TableColumn_StudentSurname;
    public TableColumn TableColumn_StudentEmail;

    @FXML
    public void testclicked(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        ListView_Department.getItems().clear();
        ArrayList<String> departmentNames =  Database.DatabaseConnection.GetDepartmentNames();
        ArrayList<Button> departmentButtons = new ArrayList<Button>();
        for (String department: departmentNames) {
            Button btn = new Button(department);
            btn.setOnAction(OnDepartmentClick);
            departmentButtons.add(btn);
            ListView_Department.getItems().add(departmentButtons.get(departmentButtons.size()-1));
        }

    }
    EventHandler<ActionEvent> OnDepartmentClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            String DepartmentName = ((Button)actionEvent.getSource()).getText();
            ObservableList<Student> students =  Database.DatabaseConnection.GetStudentListByDepartmentName(DepartmentName);
            TableView_Student.setDisable(false);
            TableView_Student.setOpacity(1);
            TableView_Student.setEditable(true);

            TableColumn_StudentName.setCellFactory(TextFieldTableCell.<String>forTableColumn());
            TableColumn_StudentSurname.setCellFactory(TextFieldTableCell.<String>forTableColumn());
            TableColumn_StudentEmail.setCellFactory(TextFieldTableCell.<String>forTableColumn());


            TableColumn_StudentID.setCellValueFactory(new PropertyValueFactory<Student, Integer>("ID"));
            TableColumn_StudentName.setCellValueFactory(new PropertyValueFactory<Student, String>("Name"));
            TableColumn_StudentSurname.setCellValueFactory(new PropertyValueFactory<Student, String>("Surname"));
            TableColumn_StudentEmail.setCellValueFactory(new PropertyValueFactory<Student, String>("Email"));

            TableView_Student.setItems(students);
            TableView_Student.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


            ListView_Department.setDisable(true);
            ListView_Department.setOpacity(0);


        }
    };



    /*ArrayList<Button> ListButton = new ArrayList<Button>();
        Button btn = new Button();
        Button btn2 = new Button();
        ListButton.add(btn2);
        btn.textProperty().setValue("Bilgisayar");
        ListView_Department.getItems().add(btn);
        btn2.textProperty().setValue("Yazilim");
        ListView_Department.getItems().add(btn2);*/

    public class LoadingInstance extends Thread{
        Alert alert;
        LoadingInstance(){
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Loading");
            alert.setHeaderText(null);
            alert.setContentText("Please wait while I connect to database!");
        }
        public void run(){
            alert.showAndWait();
        }
        public void Terminate(){
            alert.close();
        }
    }

}



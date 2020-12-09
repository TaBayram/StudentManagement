package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.*;
import java.util.ArrayList;


public class Controller {


    public ListView ListView_Department;
    public ListView ListView_Student;
    public TableView TableView_Student;
    public TableColumn TableColumn_StudentID;
    public TableColumn TableColumn_StudentName;
    public TableColumn TableColumn_StudentSurname;
    public TableColumn TableColumn_StudentEmail;
    public Button Button_StudentAdd;
    public TableColumn TableColumn_StudentPassword;
    public Button Button_StudentDelete;
    ObservableList<Student> students = FXCollections.observableArrayList();

    private int selectedDepartmentID;

    public void SetSelectedDepartmentID(int ID){
        selectedDepartmentID = ID;
    }

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

        ListView_Department.setDisable(false);
        ListView_Department.setOpacity(1);
        TableView_Student.setDisable(true);
        TableView_Student.setOpacity(0);

    }
    EventHandler<ActionEvent> OnDepartmentClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            String DepartmentName = ((Button)actionEvent.getSource()).getText();
            students =  Database.DatabaseConnection.GetStudentListByDepartmentName(DepartmentName);
            TableView_Student.setDisable(false);
            TableView_Student.setOpacity(1);
            TableView_Student.setEditable(true);
            SetSelectedDepartmentID(students.get(0).getDepartmentID());

            TableColumn_StudentName.setCellFactory(TextFieldTableCell.<String>forTableColumn());
            TableColumn_StudentSurname.setCellFactory(TextFieldTableCell.<String>forTableColumn());
            TableColumn_StudentEmail.setCellFactory(TextFieldTableCell.<String>forTableColumn());
            TableColumn_StudentPassword.setCellFactory(TextFieldTableCell.<String>forTableColumn());

            TableColumn_StudentID.setCellValueFactory(new PropertyValueFactory<Student, Integer>("ID"));
            TableColumn_StudentName.setCellValueFactory(new PropertyValueFactory<Student, String>("Name"));
            TableColumn_StudentSurname.setCellValueFactory(new PropertyValueFactory<Student, String>("Surname"));
            TableColumn_StudentEmail.setCellValueFactory(new PropertyValueFactory<Student, String>("Email"));
            TableColumn_StudentPassword.setCellValueFactory(new PropertyValueFactory<Student, String>("Password"));

            TableView_Student.setItems(students);
            TableView_Student.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


            ListView_Department.setDisable(true);
            ListView_Department.setOpacity(0);


        }
    };

    @FXML
    public void StudentAdd(ActionEvent actionEvent){
        Student student = new Student();
        student.setName("Giriniz");
        student.setSurname("Giriniz");
        student.setEmail("Giriniz");
        student.setPassword("Giriniz");
        student.setDepartmentID(selectedDepartmentID);
        student.setID(0);

        students.add(student);


    }

    public void StudentTableChangeCommit(TableColumn.CellEditEvent event){

        var column = (TableColumn)event.getSource();
        Student student = (Student)event.getRowValue();
        var columnID = column.getId();

        if(columnID.endsWith("tName")){
            student.setName((String) event.getNewValue());
        }
        if(columnID.endsWith("tSurname")){
            student.setSurname((String) event.getNewValue());
        }
        if(columnID.endsWith("tEmail")){
            student.setEmail((String) event.getNewValue());
        }
        if(columnID.endsWith("tPassword")){
            student.setPassword((String) event.getNewValue());
        }

        //CHECK IF CELLS ARE IN CORRECT FORMAT
        if(student.getName() != "Giriniz" && student.getSurname() != "Giriniz"){
            int id = Database.DatabaseConnection.AddStudent(student);
            if(id != 0){
                student.setID(id);
                TableView_Student.refresh();
            }

        }

    }


    public void StudentRemove(ActionEvent actionEvent) {
        ObservableList<Student> selectedStudents = TableView_Student.getSelectionModel().getSelectedItems();
        ObservableList<Student> removedStudents = FXCollections.observableArrayList();
        for (Student student: selectedStudents) {
            if(Database.DatabaseConnection.RemoveStudent(student)){
                removedStudents.add(student);
            }
        }
        for (Student student: removedStudents) {
            students.remove(student);
        }

    }
}



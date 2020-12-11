package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.sql.*;


public class Controller {

    @FXML
    public TableView TableView_Student;
    public TableColumn TableColumn_StudentID;
    public TableColumn TableColumn_StudentName;
    public TableColumn TableColumn_StudentSurname;
    public TableColumn TableColumn_StudentEmail;
    public TableColumn TableColumn_StudentPassword;
    public TableColumn TableColumn_StudentDepartment;

    public Button Button_StudentAdd;
    public Button Button_StudentDelete;

    public TableView TableView_Department;
    public TableColumn TableColumn_DepartmentName;
    public TableColumn TableColumn_DepartmentLanguage;
    public TableColumn TableColumn_DepartmentChair;
    public TableColumn TableColumn_DepartmentStudent;

    /*_______________________________________________________________________*/
    ObservableList<Student> students = FXCollections.observableArrayList();
    ObservableList<Department> departments = FXCollections.observableArrayList();
    private int selectedDepartmentID;


    public void SetSelectedDepartmentID(int ID){
        selectedDepartmentID = ID;
    }

    @FXML
    public void ShowDepartments(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        departments.clear();
        departments =  Database.DatabaseConnection.GetAllDepartments();
        SetStudentTableViewProperties();

        for (Department department: departments) {
            department.setButton(new Button("Show Student"));
            department.getButton().setOnAction(OnDepartmentClick);
            department.getButton().getStyleClass().add("ShowStudentButton");
        }
        SetDepartmentTableViewProperties();
        TableView_Department.refresh();


        TableView_Department.setDisable(false);
        TableView_Department.setOpacity(1);
        TableView_Student.setDisable(true);
        TableView_Student.setOpacity(0);

    }
    public void ShowAllStudent(ActionEvent actionEvent) {
        students.clear();
        students = Database.DatabaseConnection.GetAllStudents();
        SetStudentTableViewProperties();
        TableView_Student.refresh();

        TableColumn_StudentDepartment.setVisible(true);

        TableView_Student.setDisable(false);
        TableView_Student.setOpacity(1);
        TableView_Department.setDisable(true);
        TableView_Department.setOpacity(0);
    }


    public void ShowStudentsByDepartmentID(int ID) {
        students.clear();
        students = Database.DatabaseConnection.GetStudentListByDepartmentID(ID);
        SetSelectedDepartmentID(ID);
        SetStudentTableViewProperties();
        TableView_Student.refresh();

        TableColumn_StudentDepartment.setVisible(false);

        TableView_Student.setDisable(false);
        TableView_Student.setOpacity(1);
        TableView_Department.setDisable(true);
        TableView_Department.setOpacity(0);


    }


    EventHandler<ActionEvent> OnDepartmentClick = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            Button button = (Button)actionEvent.getSource();
            for (Department department: departments) {
                if(department.getButton() == button){
                    ShowStudentsByDepartmentID(department.getID());
                    break;
                }
            }
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

    public void DepartmentTableChangeCommit(TableColumn.CellEditEvent event){

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

    private void SetStudentTableViewProperties(){
        TableColumn_StudentName.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_StudentSurname.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_StudentEmail.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_StudentPassword.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_StudentDepartment.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        TableColumn_StudentID.setCellValueFactory(new PropertyValueFactory<Student, Integer>("ID"));
        TableColumn_StudentName.setCellValueFactory(new PropertyValueFactory<Student, String>("Name"));
        TableColumn_StudentSurname.setCellValueFactory(new PropertyValueFactory<Student, String>("Surname"));
        TableColumn_StudentEmail.setCellValueFactory(new PropertyValueFactory<Student, String>("Email"));
        TableColumn_StudentPassword.setCellValueFactory(new PropertyValueFactory<Student, String>("Password"));
        TableColumn_StudentDepartment.setCellValueFactory(new PropertyValueFactory<Student, Integer>("DepartmentID"));

        TableView_Student.setEditable(true);
        TableView_Student.setItems(students);
        TableView_Student.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void SetDepartmentTableViewProperties(){

        TableColumn_DepartmentName.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_DepartmentLanguage.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_DepartmentChair.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        TableColumn_DepartmentName.setCellValueFactory(new PropertyValueFactory<Department, String>("Name"));
        TableColumn_DepartmentLanguage.setCellValueFactory(new PropertyValueFactory<Department, String>("Language"));
        TableColumn_DepartmentChair.setCellValueFactory(new PropertyValueFactory<Department, Integer>("DepartmentChair"));
        TableColumn_DepartmentStudent.setCellValueFactory(new PropertyValueFactory<Department, Button>("Button"));

        TableView_Department.setEditable(true);
        TableView_Department.setItems(departments);
        TableView_Department.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }



}



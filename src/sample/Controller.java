package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.util.converter.IntegerStringConverter;

import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    public CheckBox CheckBox_StudentDepartmentName;
    public HBox HBox_Student;
    public Label Label_Count;

    /*_______________________________________________________________________*/
    /*##################### STUDENT TABLE #################################*/
    ObservableList<Student> students = FXCollections.observableArrayList();

    private boolean isDepartmentColumnID = true;
    private int selectedDepartmentID;
    String studentEmailExtension = "@std.izu.edu.tr";

    /*##################### DEPARTMENT TABLE #################################*/
    ObservableList<Department> departments = FXCollections.observableArrayList();


    /*##################### MISC ######################################*/
    String specialCharactersString = "!@#$%&*()'+,-./:;<=>?[]^_`{|}";

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

        SetStudentButtonBoxVisible(false);

    }
    public void ShowAllStudent(ActionEvent actionEvent) {
        students.clear();
        students = Database.DatabaseConnection.GetAllStudents();
        SetStudentTableViewProperties();
        TableView_Student.refresh();
        ShowAllStudentCount();

        TableColumn_StudentDepartment.setVisible(true);

        TableView_Student.setDisable(false);
        TableView_Student.setOpacity(1);
        TableView_Department.setDisable(true);
        TableView_Department.setOpacity(0);

        SetStudentButtonBoxVisible(true);
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

        SetStudentButtonBoxVisible(true);


    }

    private void SetStudentButtonBoxVisible(boolean bool){
        HBox_Student.setVisible(bool);
        HBox_Student.setDisable(!bool);

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
        student.setDepartmentID(String.valueOf(selectedDepartmentID));
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

        if(columnID.endsWith("tDepartment")){
            if(isDepartmentColumnID) student.setDepartmentID((String) event.getNewValue());
            else student.setDepartmentName((String) event.getNewValue());
        }

        //CHECK IF CELLS ARE IN CORRECT FORMAT
        if(student.getName() != "Giriniz" && student.getSurname() != "Giriniz" && student.getDepartmentID() != "0" && student.getPassword() != "Giriniz"){

            if (HasSpecialCharacters("Name",student.getName())) return;
            if (HasSpecialCharacters("Surname",student.getSurname())) return;
            if (!HasStudentCorrectEmailFormat(student,true)) return;
            if (!HasCorrectPasswordFormat(student.getPassword())) return;

            //CHECK THE DEPARTMENT IF EXIST

            int id = Database.DatabaseConnection.AddStudent(student);
            if(id != 0){
                student.setID(id);
                TableView_Student.refresh();
                ShowAllStudentCount();
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

    public void ShowAllStudentCount(){
        Label_Count.textProperty().setValue("Student Count: " + String.valueOf(Database.DatabaseConnection.GetStudentCount()));
    }

    public void ShowStudentsDepartmentName(ActionEvent actionEvent) {
        var checkBox = (CheckBox) actionEvent.getSource();
        if(checkBox.isSelected()) {
            Database.DatabaseConnection.GetStudentDepartmentName(students);
            TableColumn_StudentDepartment.setCellFactory(TextFieldTableCell.<String>forTableColumn());
            TableColumn_StudentDepartment.setCellValueFactory(new PropertyValueFactory<Student, String>("DepartmentName"));
            isDepartmentColumnID = false;
        }
        else{
            TableColumn_StudentDepartment.setCellFactory(TextFieldTableCell.<String>forTableColumn());
            TableColumn_StudentDepartment.setCellValueFactory(new PropertyValueFactory<Student, String>("DepartmentID"));
            isDepartmentColumnID = true;
        }
    }


    private void SetStudentTableViewProperties(){

        TableColumn_StudentName.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_StudentSurname.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_StudentEmail.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_StudentPassword.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_StudentDepartment.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        //TableColumn_StudentDepartment.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        TableColumn_StudentID.setCellValueFactory(new PropertyValueFactory<Student, Integer>("ID"));
        TableColumn_StudentName.setCellValueFactory(new PropertyValueFactory<Student, String>("Name"));
        TableColumn_StudentSurname.setCellValueFactory(new PropertyValueFactory<Student, String>("Surname"));
        TableColumn_StudentEmail.setCellValueFactory(new PropertyValueFactory<Student, String>("Email"));
        TableColumn_StudentPassword.setCellValueFactory(new PropertyValueFactory<Student, String>("Password"));
        TableColumn_StudentDepartment.setCellValueFactory(new PropertyValueFactory<Student, String>("DepartmentID"));

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



    public class ErrorAlert{
        Alert alert;
        ErrorAlert(String errorText){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(errorText);
            alert.showAndWait();
        }
        public void Terminate(){
            alert.close();
        }
    }


    private boolean HasSpecialCharacters(String name, String text){
        Pattern pattern = Pattern.compile("[^a-z]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        boolean hasSpecial = matcher.find();
        if(hasSpecial){
            ErrorAlert errorAlert = new ErrorAlert(name + " Contains Special Characters");
            return true;
        }
        return false;
    }

    private boolean HasStudentCorrectEmailFormat(Student student, boolean change){
        String email = student.getSurname().toLowerCase()+"." + student.getName().toLowerCase() + studentEmailExtension;
        if(student.getEmail().equals(email)){
            return true;
        }
        else{
            if(change){
                student.setEmail(email);
                TableView_Student.refresh();
                return true;
            }
            else {
                ErrorAlert errorAlert = new ErrorAlert(" Email is not on correct format. \n It should be (" + email + ")");
                return false;
            }
        }
    }

    private boolean HasCorrectPasswordFormat(String password){
        Pattern pattern = Pattern.compile("[0-9]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);
        boolean hasSpecial = matcher.find();
        if(!hasSpecial){ ErrorAlert errorAlert = new ErrorAlert("Password must contain a number"); return false;}
        pattern = Pattern.compile("[a-z]", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(password);
        hasSpecial = matcher.find();
        if(!hasSpecial){ ErrorAlert errorAlert = new ErrorAlert("Password must contain a character"); return false;}

        if(password.toLowerCase().equals(password)){ ErrorAlert errorAlert = new ErrorAlert("Password must contain a uppercase character"); return false;}
        if(password.toUpperCase().equals(password)){ ErrorAlert errorAlert = new ErrorAlert("Password must contain a lowercase character"); return false;}

        if(password.length() < 4) { ErrorAlert errorAlert = new ErrorAlert("Password must be at least 5 characters"); return false;}

        return true;
    }

}




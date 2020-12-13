package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.converter.DateTimeStringConverter;
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


    public Pane Pane_Buttons;

    public HBox HBox_Teacher;
    public HBox HBox_Student;

    public Label Label_Count;

    public TableView TableView_Teacher;
    public TableColumn TableColumn_TeacherID;
    public TableColumn TableColumn_TeacherName;
    public TableColumn TableColumn_TeacherSurname;
    public TableColumn TableColumn_TeacherEmail;
    public TableColumn TableColumn_TeacherRegisteredDate;
    public TableColumn TableColumn_TeacherPassword;
    public TableColumn TableColumn_TeacherDepartment;
    public TableColumn TableColumn_TeacherTitle;
    public TableColumn TableColumn_StudentRegisteredDate;
    public TableColumn TableColumn_StudentSemester;
    public TableColumn TableColumn_StudentAdvisorID;
    public AnchorPane AnchorPane_Main;


    public TableView TableView_Person;
    public TableColumn TableColumn_PersonID;
    public TableColumn TableColumn_PersonName;
    public TableColumn TableColumn_PersonSurname;
    public TableColumn TableColumn_PersonEmail;
    public TableColumn TableColumn_PersonDepartment;
    public TableColumn TableColumn_PersonRegisteredDate;
    public TableColumn TableColumn_PersonPassword;


    /*_______________________________________________________________________*/
    /*##################### STUDENT TABLE #################################*/
    ObservableList<Student> students = FXCollections.observableArrayList();

    private boolean isDepartmentColumnID = true;
    private int selectedDepartmentID;
    String studentEmailExtension = "@std.izu.edu.tr";

    public void ShowAllStudent(ActionEvent actionEvent) {
        students.clear();
        students = Database.DatabaseConnection.GetAllStudents();
        if(students.size() == 0){StudentAdd(null); }
        SetStudentTableViewProperties();
        TableView_Student.refresh();

        TableColumn_StudentDepartment.setVisible(true);

        MainPaneHideOthersExceptThis(TableView_Student);

    }

    public void ShowStudentsByDepartmentID(int ID) {
        students.clear();
        students = Database.DatabaseConnection.GetStudentListByDepartmentID(ID);
        SetSelectedDepartmentID(ID);
        SetStudentTableViewProperties();
        TableView_Student.refresh();


        ShowAllStudentCountByDepartment();

        TableColumn_StudentDepartment.setVisible(false);



        MainPaneHideOthersExceptThis(TableView_Student);

    }

    @FXML
    public void StudentAdd(ActionEvent actionEvent){
        Student student = new Student();
        student.setDepartmentID(String.valueOf(selectedDepartmentID));

        students.add(student);


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
            if( !Database.DatabaseConnection.DoesDepartmentExist(Integer.parseInt(student.getDepartmentID()) )){
                ErrorAlert errorAlert = new ErrorAlert("Department does not exist");
                return;
            }

            if(student.getID() == 0) {
                int id = Database.DatabaseConnection.AddStudent(student);
                if (id != 0) {
                    student.setID(id);
                    TableView_Student.refresh();

                }
            }
            else{
                Database.DatabaseConnection.AlterStudent(student);
            }

        }

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
        //TableColumn_StudentRegisteredDate.setCellFactory(TextFieldTableCell.forTableColumn(new DateTimeStringConverter()));
        TableColumn_StudentSemester.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        TableColumn_StudentAdvisorID.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        //TableColumn_StudentDepartment.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        TableColumn_StudentID.setCellValueFactory(new PropertyValueFactory<Student, Integer>("ID"));
        TableColumn_StudentName.setCellValueFactory(new PropertyValueFactory<Student, String>("Name"));
        TableColumn_StudentSurname.setCellValueFactory(new PropertyValueFactory<Student, String>("Surname"));
        TableColumn_StudentEmail.setCellValueFactory(new PropertyValueFactory<Student, String>("Email"));
        TableColumn_StudentPassword.setCellValueFactory(new PropertyValueFactory<Student, String>("Password"));
        TableColumn_StudentDepartment.setCellValueFactory(new PropertyValueFactory<Student, String>("DepartmentID"));
        TableColumn_StudentRegisteredDate.setCellValueFactory(new PropertyValueFactory<Student, String>("RegisteredDateFormatted"));
        TableColumn_StudentSemester.setCellValueFactory(new PropertyValueFactory<Student, Integer>("Semester"));
        TableColumn_StudentAdvisorID.setCellValueFactory(new PropertyValueFactory<Student, Integer>("AdvisorID"));

        TableView_Student.setEditable(true);
        TableView_Student.setItems(students);
        TableView_Student.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /*##################### DEPARTMENT TABLE #################################*/
    ObservableList<Department> departments = FXCollections.observableArrayList();

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


        MainPaneHideOthersExceptThis(TableView_Department);


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

    /*##################### TEACHER TABLE #################################*/
    ObservableList<Teacher> teachers = FXCollections.observableArrayList();
    String teacherEmailExtension = "@izu.edu.tr";


    public void ShowAllTeacher(ActionEvent actionEvent) {
        teachers.clear();
        teachers = Database.DatabaseConnection.GetAllTeachers();
        if(teachers.size() == 0){TeacherAdd(null); }
        SetTeacherTableViewProperties();
        TableView_Teacher.refresh();


        //TableColumn_StudentDepartment.setVisible(true);

        MainPaneHideOthersExceptThis(TableView_Teacher);

    }


    public void ShowTeachersDepartmentName(ActionEvent actionEvent) {
        var checkBox = (CheckBox) actionEvent.getSource();
        if(checkBox.isSelected()) {
            Database.DatabaseConnection.GetTeacherDepartmentName(teachers);
            TableColumn_TeacherDepartment.setCellFactory(TextFieldTableCell.<String>forTableColumn());
            TableColumn_TeacherDepartment.setCellValueFactory(new PropertyValueFactory<Teacher, String>("DepartmentName"));
            isDepartmentColumnID = false;
        }
        else{
            TableColumn_TeacherDepartment.setCellFactory(TextFieldTableCell.<String>forTableColumn());
            TableColumn_TeacherDepartment.setCellValueFactory(new PropertyValueFactory<Teacher, String>("DepartmentID"));
            isDepartmentColumnID = true;
        }
    }


    public void TeacherAdd(ActionEvent actionEvent){
        Teacher teacher = new Teacher();

        teachers.add(teacher);


    }

    public void TeacherRemove(ActionEvent actionEvent) {
        ObservableList<Teacher> selectedTeachers = TableView_Teacher.getSelectionModel().getSelectedItems();
        ObservableList<Teacher> removedTeachers = FXCollections.observableArrayList();
        for (Teacher teacher: selectedTeachers) {
            if(Database.DatabaseConnection.RemoveTeacher(teacher)){
                removedTeachers.add(teacher);
            }
        }
        for (Teacher teacher: removedTeachers) {
            teachers.remove(teacher);
        }

    }

    public void TeacherTableChangeCommit(TableColumn.CellEditEvent event){

        var column = (TableColumn)event.getSource();
        Teacher teacher = (Teacher)event.getRowValue();
        var columnID = column.getId();

        if(columnID.endsWith("rName")){
            teacher.setName((String) event.getNewValue());
        }
        if(columnID.endsWith("rSurname")){
            teacher.setSurname((String) event.getNewValue());
        }
        if(columnID.endsWith("rEmail")){
            teacher.setEmail((String) event.getNewValue());
        }
        if(columnID.endsWith("rPassword")){
            teacher.setPassword((String) event.getNewValue());
        }
        if(columnID.endsWith("rTitle")){
            teacher.setTitle((String) event.getNewValue());
        }

        if(columnID.endsWith("rDepartment")){
            if(isDepartmentColumnID) teacher.setDepartmentID((String) event.getNewValue());
            else teacher.setDepartmentName((String) event.getNewValue());
        }

        //CHECK IF CELLS ARE IN CORRECT FORMAT
        if(teacher.getName() != "Giriniz" && teacher.getSurname() != "Giriniz" && teacher.getDepartmentID() != "0" && teacher.getPassword() != "Giriniz"){

            if (HasSpecialCharacters("Name",teacher.getName())) return;
            if (HasSpecialCharacters("Surname",teacher.getSurname())) return;
            if (!HasTeacherCorrectEmailFormat(teacher,true)) return;
            if (!HasCorrectPasswordFormat(teacher.getPassword())) return;

            //CHECK THE DEPARTMENT IF EXIST
            try {
                int departmentID = Integer.parseInt(teacher.getDepartmentID());
                if (!Database.DatabaseConnection.DoesDepartmentExist(departmentID)) {
                    ErrorAlert errorAlert = new ErrorAlert("Department does not exist");
                    return;
                }
            }
            catch (NumberFormatException e){
                ErrorAlert errorAlert = new ErrorAlert("Department ID must be numeric");
            }

            if(teacher.getID() == 0) {
                int id = Database.DatabaseConnection.AddTeacher(teacher);
                if (id != 0) {
                    teacher.setID(id);
                    TableView_Teacher.refresh();
                }
            }
            else{
                Database.DatabaseConnection.AlterTeacher(teacher);
            }

        }

    }

    private void SetTeacherTableViewProperties(){

        TableColumn_TeacherName.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_TeacherSurname.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_TeacherEmail.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_TeacherPassword.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_TeacherDepartment.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_TeacherTitle.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        //TableColumn_TeacherRegisteredDate.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        //TableColumn_StudentDepartment.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        TableColumn_TeacherID.setCellValueFactory(new PropertyValueFactory<Teacher, Integer>("ID"));
        TableColumn_TeacherName.setCellValueFactory(new PropertyValueFactory<Teacher, String>("Name"));
        TableColumn_TeacherSurname.setCellValueFactory(new PropertyValueFactory<Teacher, String>("Surname"));
        TableColumn_TeacherEmail.setCellValueFactory(new PropertyValueFactory<Teacher, String>("Email"));
        TableColumn_TeacherRegisteredDate.setCellValueFactory(new PropertyValueFactory<Teacher, DateTimeStringConverter>("RegisteredDate"));
        TableColumn_TeacherPassword.setCellValueFactory(new PropertyValueFactory<Teacher, String>("Password"));
        TableColumn_TeacherDepartment.setCellValueFactory(new PropertyValueFactory<Teacher, String>("DepartmentID"));
        TableColumn_TeacherTitle.setCellValueFactory(new PropertyValueFactory<Teacher, String>("Title"));

        TableView_Teacher.setEditable(true);
        TableView_Teacher.setItems(teachers);
        TableView_Teacher.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /*##################### MISC ######################################*/
    ObservableList<Person> people = FXCollections.observableArrayList();

    public void ShowAllPeople(ActionEvent actionEvent) {
        people.clear();
        people = Database.DatabaseConnection.GetAllPeople();
        if(people.size() == 0){ErrorAlert errorAlert = new ErrorAlert("There are no people to show!"); return; }
        SetPersonTableViewProperties();
        TableView_Person.refresh();
        //ShowAllTeacherCount();


        //TableColumn_StudentDepartment.setVisible(true);

        MainPaneHideOthersExceptThis(TableView_Person);

    }

    private void SetPersonTableViewProperties(){

        TableColumn_PersonID.setCellValueFactory(new PropertyValueFactory<Person, Integer>("ID"));
        TableColumn_PersonName.setCellValueFactory(new PropertyValueFactory<Person, String>("Name"));
        TableColumn_PersonSurname.setCellValueFactory(new PropertyValueFactory<Person, String>("Surname"));
        TableColumn_PersonEmail.setCellValueFactory(new PropertyValueFactory<Person, String>("Email"));
        TableColumn_PersonRegisteredDate.setCellValueFactory(new PropertyValueFactory<Person, String>("RegisteredDateFormatted"));
        TableColumn_PersonPassword.setCellValueFactory(new PropertyValueFactory<Person, String>("Password"));
        TableColumn_PersonDepartment.setCellValueFactory(new PropertyValueFactory<Person, String>("DepartmentID"));

        TableView_Person.setEditable(false);
        TableView_Person.setItems(people);
        TableView_Person.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void SetSelectedDepartmentID(int ID){
        selectedDepartmentID = ID;
    }


    public void ShowAllStudentCountByDepartment(){
        Label_Count.textProperty().setValue("Student Count: " + String.valueOf(Database.DatabaseConnection.GetStudentCountByDepartmentID(selectedDepartmentID)));
    }


    public void MainPaneHideOthersExceptThis(Node control){
        for (Node childControl : AnchorPane_Main.getChildren()) {
            if (childControl == control){
                String name = control.getId().substring(control.getId().indexOf("_") + 1);
                boolean hasFoundIt = false;
                for (Node childControl2 : Pane_Buttons.getChildren()) {
                    if(childControl2.getId().contains(name)){
                        ButtonPaneHideOthersExceptThis(childControl2);
                        hasFoundIt = true;
                        break;
                    }
                }
                if(!hasFoundIt) ButtonPaneHideOthersExceptThis(null);
                Label_Count.textProperty().setValue(name + " Count: " + Database.DatabaseConnection.GetCount(name));
                if(!control.isDisable()){
                    control.setOpacity(0);
                    control.setDisable(true);
                }
                else{
                    control.setOpacity(1);
                    control.setDisable(false);
                }
            }
            else{
                childControl.setOpacity(0);
                childControl.setDisable(true);
            }

        }
    }

    private void ButtonPaneHideOthersExceptThis(Node control){
        for (Node childControl : Pane_Buttons.getChildren()) {
            if (childControl == control){
                if(control.isDisable() == false){
                    control.setOpacity(0);
                    control.setDisable(true);
                }
                else{
                    control.setOpacity(1);
                    control.setDisable(false);
                }
            }
            else{
                childControl.setOpacity(0);
                childControl.setDisable(true);
            }

        }

    }

    public static class ErrorAlert{
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
        Pattern pattern = Pattern.compile("[^a-zşöÖğĞüÜıİçÇ]", Pattern.CASE_INSENSITIVE);
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

    private boolean HasTeacherCorrectEmailFormat(Teacher student, boolean change){
        String email = student.getSurname().toLowerCase()+"." + student.getName().toLowerCase() + teacherEmailExtension;
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
        if(password.contains(" ")) { ErrorAlert errorAlert = new ErrorAlert("Password can't contain space character"); return false;}

        if(password.toLowerCase().equals(password)){ ErrorAlert errorAlert = new ErrorAlert("Password must contain a uppercase character"); return false;}
        if(password.toUpperCase().equals(password)){ ErrorAlert errorAlert = new ErrorAlert("Password must contain a lowercase character"); return false;}

        if(password.length() < 4) { ErrorAlert errorAlert = new ErrorAlert("Password must be at least 5 characters"); return false;}

        return true;
    }

}




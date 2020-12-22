package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.util.converter.DateTimeStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
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
    public TableColumn TableColumn_DepartmentID;
    public TableColumn TableColumn_DepartmentName;
    public TableColumn TableColumn_DepartmentLanguage;
    public TableColumn TableColumn_DepartmentChair;
    public TableColumn TableColumn_DepartmentStudent;
    public TableColumn TableColumn_DepartmentFacultyID;

    public TableView TableView_Faculty;
    public TableColumn TableColumn_FacultyID;
    public TableColumn TableColumn_FacultyName;
    public TableColumn TableColumn_FacultyChair;

    public Button Button_FacultyAdd;
    public Button Button_FacultyDelete;


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
    public Button Button_Faculty;
    public Button Button_AllStudent;
    public Button Button_Person;
    public AnchorPane Pane_Information;
    public ComboBox ComboBox_Math;
    public TextField TextField_MathInput;
    public TextField TextField_Output;
    public TableColumn TableColumn_FacultyCourseCount;
    public StackPane StackPane_Main;
    public Label Label_Title;
    public AnchorPane AnchorPane_LogIn;
    public PasswordField TextField_LogIn;
    public Label Label_EnterPassword;
    public TableColumn TableColumn_StudentGPA;


    public void initialize(){

        AnchorPane_LogIn.setOpacity(1);
        SetStudentTableViewProperties();
        SetDepartmentTableViewProperties();
        SetFacultyTableViewProperties();
        SetTeacherTableViewProperties();
        SetPersonTableViewProperties();

        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Kare",
                        "Küp",
                        "Karekök",
                        "Tanjant",
                        "Sinüs",
                        "Cosinüs",
                        "Alt Yuvarla",
                        "Üst Yuvarla",
                        "Mutlak"
                );
        ComboBox_Math.setItems(options);

    }



    /*_______________________________________________________________________*/
    /*##################### STUDENT TABLE #################################*/
    ObservableList<Faculty> faculties = FXCollections.observableArrayList();

    public void ShowFaculty(ActionEvent actionEvent) {
        MainPaneHideOthersExceptThis(TableView_Faculty);
        faculties.clear();
        DatabaseTaskCreate("faculty");

    }

    public void FacultyTableChangeCommit(TableColumn.CellEditEvent event) {
        var column = (TableColumn)event.getSource();
        Faculty faculty = (Faculty) event.getRowValue();
        var columnID = column.getId();

        if(columnID.endsWith("yName")){
            faculty.setName((String) event.getNewValue());
        }
        if(columnID.endsWith("FacultyChair")){
            faculty.setFacultyChair((String) event.getNewValue());
        }


        //CHECK IF CELLS ARE IN CORRECT FORMAT
        if(faculty.getName() != "Giriniz" && faculty.getFacultyChair() != "Giriniz"){


            if (HasSpecialCharacters("Name",faculty.getName())) return;
            if (!HasOnlyNumbers("Faculty Chair",faculty.getFacultyChair())) return;

            if(faculty.getID() == 0) {
                int id = Database.DatabaseConnection.AddFaculty(faculty);
                if (id != 0) {
                    faculty.setID(id);
                    TableView_Faculty.refresh();
                }
            }
            else{
                Database.DatabaseConnection.AlterFaculty(faculty);
            }

        }

    }

    public void ShowFacultyChairName(ActionEvent actionEvent) {
        var checkBox = (CheckBox) actionEvent.getSource();
        if(checkBox.isSelected()) {
            Database.DatabaseConnection.GetFacultyChairNames(faculties);
            TableColumn_FacultyChair.setEditable(false);
            TableColumn_FacultyChair.setCellFactory(TextFieldTableCell.<String>forTableColumn());
            TableColumn_FacultyChair.setCellValueFactory(new PropertyValueFactory<Faculty, String>("FacultyChairName"));
        }
        else{
            TableColumn_FacultyChair.setCellFactory(TextFieldTableCell.<String>forTableColumn());
            TableColumn_FacultyChair.setEditable(true);
            TableColumn_FacultyChair.setCellValueFactory(new PropertyValueFactory<Faculty, String>("FacultyChair"));
        }
    }

    public void FacultyAdd(ActionEvent actionEvent) {
        Faculty faculty = new Faculty();
        faculties.add(faculty);
    }

    public void FacultyRemove(ActionEvent actionEvent) {
        ObservableList<Faculty> selectedFaculties = TableView_Faculty.getSelectionModel().getSelectedItems();
        ObservableList<Faculty> removedFaculties = FXCollections.observableArrayList();
        for (Faculty faculty: selectedFaculties) {
            if(Database.DatabaseConnection.RemoveFaculty(faculty)){
                removedFaculties.add(faculty);
            }
        }
        for (Faculty faculty: removedFaculties) {
            faculties.remove(faculty);
        }
    }

    private void SetFacultyTableViewProperties(){

        TableColumn_FacultyName.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_FacultyChair.setCellFactory(TextFieldTableCell.<String>forTableColumn());

        TableColumn_FacultyID.setCellValueFactory(new PropertyValueFactory<Faculty, Integer>("ID"));
        TableColumn_FacultyName.setCellValueFactory(new PropertyValueFactory<Faculty, String>("Name"));
        TableColumn_FacultyChair.setCellValueFactory(new PropertyValueFactory<Faculty, String>("FacultyChair"));
        TableColumn_FacultyCourseCount.setCellValueFactory(new PropertyValueFactory<Faculty, Integer>("FacultyCourseCount"));

        TableView_Faculty.setEditable(true);
        TableView_Faculty.setItems(faculties);
        TableView_Faculty.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /*##################### STUDENT TABLE #################################*/
    ObservableList<Student> students = FXCollections.observableArrayList();

    private boolean isDepartmentColumnID = true;
    private int selectedDepartmentID;
    String studentEmailExtension = "@std.izu.edu.tr";

    public void ShowAllStudent(ActionEvent actionEvent) throws IOException {
        MainPaneHideOthersExceptThis(TableView_Student);
        if(TableView_Student.isDisabled()) return;
        students.clear();
        DatabaseTaskCreate("student");




    }

    public void ShowStudentsByDepartmentID(int ID) {
        MainPaneHideOthersExceptThis(TableView_Student);
        if(TableView_Student.isDisabled()) return;
        students.clear();
        DatabaseTaskCreate(ID);

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
        if(columnID.endsWith("tAdvisorID")){
            student.setAdvisorID((int)event.getNewValue());
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

            if( !Database.DatabaseConnection.DoesTeacherExist(student.getAdvisorID())){
                ErrorAlert errorAlert = new ErrorAlert("Teacher does not exist");
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
            TableColumn_StudentDepartment.setEditable(false);
            TableColumn_StudentDepartment.setCellFactory(TextFieldTableCell.<String>forTableColumn());
            TableColumn_StudentDepartment.setCellValueFactory(new PropertyValueFactory<Student, String>("DepartmentName"));
            isDepartmentColumnID = false;
        }
        else{
            TableColumn_StudentDepartment.setEditable(true);
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
        //TableColumn_StudentSemester.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
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
        TableColumn_StudentGPA.setCellValueFactory(new PropertyValueFactory<Student, Integer>("GPA"));

        TableView_Student.setEditable(true);
        TableView_Student.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /*##################### DEPARTMENT TABLE #################################*/
    ObservableList<Department> departments = FXCollections.observableArrayList();
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
    public void ShowDepartments(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {

        MainPaneHideOthersExceptThis(TableView_Department);
        departments.clear();
        DatabaseTaskCreate("department");


    }

    public void DepartmentTableChangeCommit(TableColumn.CellEditEvent event){

        var column = (TableColumn)event.getSource();
        Department department = (Department) event.getRowValue();
        var columnID = column.getId();

        if(columnID.endsWith("tName")){
            department.setName((String) event.getNewValue());
        }
        if(columnID.endsWith("tLanguage")){
            department.setLanguage((String) event.getNewValue());
        }
        if(columnID.endsWith("DepartmentChair")){
            department.setDepartmentChair((String) event.getNewValue());
        }

        if(columnID.endsWith("DepartmentFacultyID")){
            department.setDepartmentFacultyID((String) event.getNewValue());
        }

        //CHECK IF CELLS ARE IN CORRECT FORMAT
        if(department.getName() != "Giriniz" && department.getLanguage() != "Giriniz" && department.getDepartmentChair() != "Giriniz" && department.getDepartmentFacultyID()!="Giriniz"){


            if (HasSpecialCharacters("Name",department.getName())) return;
            if (HasSpecialCharacters("Language",department.getLanguage())) return;
            if (Database.DatabaseConnection.DoesFacultyExist(Integer.parseInt(department.getDepartmentFacultyID()))){
                ErrorAlert errorAlert = new ErrorAlert("Faculty does not exist");
                return;
            }

            if(department.getID() == 0) {
                int id = Database.DatabaseConnection.AddDepartment(department);
                if (id != 0) {
                    department.setID(id);
                    TableView_Department.refresh();
                }
            }
            else {
                    Database.DatabaseConnection.AlterDepartment(department);
                }


        }

    }

    public void DepartmentAdd(ActionEvent actionEvent) {
        Department department = new Department();
        departments.add(department);
    }

    public void DepartmentRemove(ActionEvent actionEvent) {
        ObservableList<Department> selectedDepartments = TableView_Department.getSelectionModel().getSelectedItems();
        ObservableList<Department> removedDepartments = FXCollections.observableArrayList();
        for (Department department: selectedDepartments) {
            if(Database.DatabaseConnection.RemoveDepartment(department)){
                removedDepartments.add(department);
            }
        }
        for (Department department: removedDepartments) {
            departments.remove(department);
        }
    }

    private void SetDepartmentTableViewProperties(){
       // TableColumn_DepartmentID.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_DepartmentName.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_DepartmentLanguage.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_DepartmentChair.setCellFactory(TextFieldTableCell.<String>forTableColumn());
        TableColumn_DepartmentFacultyID.setCellFactory(TextFieldTableCell.<String>forTableColumn());



        TableColumn_DepartmentID.setCellValueFactory(new PropertyValueFactory<Department, String>("ID"));
        TableColumn_DepartmentName.setCellValueFactory(new PropertyValueFactory<Department, String>("Name"));
        TableColumn_DepartmentLanguage.setCellValueFactory(new PropertyValueFactory<Department, String>("Language"));
        TableColumn_DepartmentChair.setCellValueFactory(new PropertyValueFactory<Department, String>("DepartmentChair"));
        TableColumn_DepartmentFacultyID.setCellValueFactory(new PropertyValueFactory<Department, String>("DepartmentFacultyID"));
        TableColumn_DepartmentStudent.setCellValueFactory(new PropertyValueFactory<Department, Button>("Button"));

        TableView_Department.setEditable(true);
        TableView_Department.setItems(departments);
        TableView_Department.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /*##################### TEACHER TABLE #################################*/
    ObservableList<Teacher> teachers = FXCollections.observableArrayList();
    String teacherEmailExtension = "@izu.edu.tr";

    public void ShowAllTeacher(ActionEvent actionEvent) {
        MainPaneHideOthersExceptThis(TableView_Teacher);
        if(TableView_Teacher.isDisabled()) return;
        teachers.clear();

        DatabaseTaskCreate("teacher");

    }

    public void ShowTeachersDepartmentName(ActionEvent actionEvent) {
        var checkBox = (CheckBox) actionEvent.getSource();
        if(checkBox.isSelected()) {
            Database.DatabaseConnection.GetTeacherDepartmentName(teachers);
            TableColumn_TeacherDepartment.setEditable(false);
            TableColumn_TeacherDepartment.setCellFactory(TextFieldTableCell.<String>forTableColumn());
            TableColumn_TeacherDepartment.setCellValueFactory(new PropertyValueFactory<Teacher, String>("DepartmentName"));
            isDepartmentColumnID = false;
        }
        else{
            TableColumn_TeacherDepartment.setEditable(true);
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
        TableColumn_TeacherPassword.setCellValueFactory(new PropertyValueFactory<Teacher, String>("HiddenPassword"));
        TableColumn_TeacherDepartment.setCellValueFactory(new PropertyValueFactory<Teacher, String>("DepartmentID"));
        TableColumn_TeacherTitle.setCellValueFactory(new PropertyValueFactory<Teacher, String>("Title"));

        TableView_Teacher.setEditable(true);
        TableView_Teacher.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /*##################### MISC ######################################*/
    ObservableList<Person> people = FXCollections.observableArrayList();

    public void ShowAllPeople(ActionEvent actionEvent) {
        MainPaneHideOthersExceptThis(TableView_Person);
        people.clear();
        DatabaseTaskCreate("person");




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
                    InfoPaneVisible(false);
                }
                else{
                    control.setOpacity(1);
                    control.setDisable(false);
                    InfoPaneVisible(true);
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

    private void InfoPaneVisible(boolean wantItShown){
        if(!wantItShown){
            Pane_Information.setOpacity(0);
            Pane_Information.setDisable(true);
        }
        else{
            Pane_Information.setOpacity(1);
            Pane_Information.setDisable(false);
        }

        if(!TableView_Student.isDisabled()){
            Label_Title.setText("Students");
        }
        else if(!TableView_Teacher.isDisabled()){
            Label_Title.setText("Teachers");
        }
        else if(!TableView_Department.isDisabled()){
            Label_Title.setText("Department");
        }
        else if(!TableView_Faculty.isDisabled()){
            Label_Title.setText("Faculties");
        }
        else if(!TableView_Person.isDisabled()){
            Label_Title.setText("People");
        }
        else{
            Label_Title.setText("Home");
        }
    }

    public void DoMath(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            String input = TextField_MathInput.getCharacters().toString();
            Pattern pattern = Pattern.compile("[^0-9.-]", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            boolean hasOnlyNumbers = matcher.find();
            if(input.contains("-") && input.indexOf('-') != 0) {
                ErrorAlert errorAlert = new ErrorAlert("Error: Please put the minus sign in front of the number!");
                return;
            }
            else if(input.indexOf(".") != input.lastIndexOf(".")) {
                ErrorAlert errorAlert = new ErrorAlert("Error: Please remove decimal point until one is left!");
                return;
            }
            if(!hasOnlyNumbers && !input.contains(",") && !ComboBox_Math.getSelectionModel().isEmpty()){
                var number = Double.parseDouble(input);
                int operation = 0;
                if(ComboBox_Math.getValue().toString().equals("Kare")) operation = 1;
                if(ComboBox_Math.getValue().toString().equals("Küp")) operation = 2;
                if(ComboBox_Math.getValue().toString().equals("Karekök")) operation = 3;
                if(ComboBox_Math.getValue().toString().equals("Tanjant")) operation = 4;
                if(ComboBox_Math.getValue().toString().equals("Sinüs")) operation = 5;
                if(ComboBox_Math.getValue().toString().equals("Cosinüs")) operation = 6;
                if(ComboBox_Math.getValue().toString().equals("Alt Yuvarla")) operation = 7;
                if(ComboBox_Math.getValue().toString().equals("Üst Yuvarla")) operation = 8;
                if(ComboBox_Math.getValue().toString().equals("Mutlak")) operation = 9;

                if(operation == 3 && input.contains("-")) {
                    ErrorAlert errorAlert = new ErrorAlert("Error: You can't take a negative number's square root!");
                    return;
                }

                TextField_Output.setText("" + Database.DatabaseConnection.GetMathResult(number,operation));
            }
            else{
                ErrorAlert errorAlert = new ErrorAlert("Error: Please enter the number correctly ( use . for decimal ) or choose the operation!");
            }

        }
    }

    public void PasswordColumnCheckBox(ActionEvent actionEvent) {
        var checkBox = (CheckBox) actionEvent.getSource();
        if(checkBox.isSelected()) {
            TableColumn_TeacherPassword.setEditable(false);
            TableColumn_TeacherPassword.setCellValueFactory(new PropertyValueFactory<Teacher, String>("HiddenPassword"));
        }
        else{
            TableColumn_TeacherPassword.setEditable(true);
            TableColumn_TeacherPassword.setCellValueFactory(new PropertyValueFactory<Teacher, String>("Password"));
        }
        TableView_Teacher.refresh();


    }

    public void KeyPressPassword(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            String input = TextField_LogIn.getText();
            if(input.equals("1234")){
                StackPane_Main.getChildren().remove(AnchorPane_LogIn);
            }
            else{
                Label_EnterPassword.setText("TRY AGAIN");
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

    private boolean HasTeacherCorrectEmailFormat(Teacher teacher, boolean change){
        String email = teacher.getName().toLowerCase() + "." + teacher.getSurname().toLowerCase()+ teacherEmailExtension;
        if(teacher.getEmail().equals(email)){
            return true;
        }
        else{
            if(change){
                teacher.setEmail(email);
                TableView_Teacher.refresh();
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

    private boolean HasOnlyNumbers(String name, String text){
        Pattern pattern = Pattern.compile("[^0-9]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        boolean hasNonNumericChar = matcher.find();

        if(hasNonNumericChar){
            ErrorAlert errorAlert = new ErrorAlert(name + " Must Contain Only Numbers");
        }

        return !hasNonNumericChar;
    }



    private void VeilIndicator(Task task){
        Region veil = new Region();
        veil.setStyle("-fx-background-color: rgba(0,0,0,0.4)");
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(200,200);
        veil.visibleProperty().bind(task.runningProperty());
        progressIndicator.visibleProperty().bind(task.runningProperty());

        StackPane_Main.getChildren().addAll(veil,progressIndicator);
    }


    public void DatabaseTaskCreate(String getAllType) {
        Task task = TaskGetAllStudent();
        if(getAllType.equals("teacher")){
            task = TaskGetAllTeacher();
        }
        else if(getAllType.equals("faculty")){
            task = TaskGetAllFaculty();
        }
        else if(getAllType.equals("department")){
            task = TaskGetAllDepartment();
        }
        else if(getAllType.equals("person")){
            task = TaskGetAllPerson();
        }

        Thread thread = new Thread(task);

        VeilIndicator(task);


        thread.setDaemon(true);
        thread.start();
        Task finalTask = task;
        task.setOnSucceeded(e->{
            if(getAllType.equals("student")){
                students = (ObservableList<Student>) finalTask.getValue();
                if(students.size() == 0){StudentAdd(null); }
                TableView_Student.setItems(students);
                TableColumn_StudentDepartment.setVisible(true);
            }
            else if(getAllType.equals("teacher")){
                teachers = (ObservableList<Teacher>) finalTask.getValue();
                if(teachers.size() == 0){TeacherAdd(null); }
                TableView_Teacher.setItems(teachers);
            }
            else if(getAllType.equals("faculty")){
                faculties = (ObservableList<Faculty>) finalTask.getValue();
                if(faculties.size() == 0){FacultyAdd(null); }
                TableView_Faculty.setItems(faculties);
            }
            else if(getAllType.equals("department")){
                departments = (ObservableList<Department>) finalTask.getValue();
                if(departments.size() == 0){DepartmentAdd(null); }
                TableView_Department.setItems(departments);
                for (Department department: departments) {
                    department.setButton(new Button("Show Student"));
                    department.getButton().setOnAction(OnDepartmentClick);
                    department.getButton().getStyleClass().add("ShowStudentButton");
                }
            }
            else if(getAllType.equals("person")){
                people = (ObservableList<Person>) finalTask.getValue();
                TableView_Person.setItems(people);
                if(people.size() == 0){ErrorAlert errorAlert = new ErrorAlert("There are no people to show!"); return; }
            }
        });

    }

    public void DatabaseTaskCreate(int DepartmentID) {
        Task task = TaskGetStudentsByDepartment(DepartmentID);
        Thread thread = new Thread(task);
        VeilIndicator(task);
        thread.setDaemon(true);
        thread.start();
        System.out.println(DepartmentID);
        Task finalTask = task;
        task.setOnSucceeded(e->{
            students = (ObservableList<Student>) finalTask.getValue();
            SetSelectedDepartmentID(DepartmentID);
            if(students.size() == 0){StudentAdd(null); }
            TableView_Student.setItems(students);
            TableColumn_StudentDepartment.setVisible(false);
            ShowAllStudentCountByDepartment();
        });

    }

    private Task TaskGetAllStudent(){
        return new Task(){
            @Override
            protected ObservableList<Student> call() throws Exception {
                //Thread.sleep(100);
                var results = Database.DatabaseConnection.GetAllStudents();
                return results;
            }
        };
    }
    private Task TaskGetAllTeacher(){
        return new Task(){
            @Override
            protected ObservableList<Teacher> call() throws Exception {
                //Thread.sleep(100);
                var results = Database.DatabaseConnection.GetAllTeachers();
                return results;
            }
        };
    }
    private Task TaskGetAllFaculty(){
        return new Task(){
            @Override
            protected ObservableList<Faculty> call() throws Exception {
                //Thread.sleep(100);
                var results = Database.DatabaseConnection.GetAllFaculties();
                return results;
            }
        };
    }
    private Task TaskGetAllDepartment(){
        return new Task(){
            @Override
            protected ObservableList<Department> call() throws Exception {
                //Thread.sleep(100);
                var results = Database.DatabaseConnection.GetAllDepartments();
                return results;
            }
        };
    }
    private Task TaskGetAllPerson(){
        return new Task(){
            @Override
            protected ObservableList<Person> call() throws Exception {
                //Thread.sleep(100);
                var results = Database.DatabaseConnection.GetAllPeople();
                return results;
            }
        };
    }
    private Task TaskGetStudentsByDepartment(int ID){
        return new Task(){
            @Override
            protected ObservableList<Student> call() throws Exception {
                Thread.sleep(100);
                var results= Database.DatabaseConnection.GetStudentListByDepartmentID(ID);
                return results;
            }
        };
    }


}






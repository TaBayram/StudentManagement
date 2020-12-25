package sample;

import javafx.util.converter.DateTimeStringConverter;

import java.util.Date;

public class Student {

    int ID;
    String Name;
    String Surname;
    String Password;
    String Email;
    String DepartmentID;
    String DepartmentName;
    Date RegisteredDate;
    String RegisteredDateFormatted;
    Integer Semester;
    Integer AdvisorID;
    float GPA;
    String enter = "Giriniz";

    public Student() {
        ID = 0;
        DepartmentName = enter;
        Name = enter;
        Surname = enter;
        Password = enter;
        Email = enter;
        DepartmentID = enter;
        AdvisorID = 0;
        GPA = 0;
    }



    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getDepartmentID() {
        return DepartmentID;
    }

    public void setDepartmentID(String departmentID) {
        DepartmentID = departmentID;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getDepartmentName() {
        return DepartmentName;
    }

    public void setDepartmentName(String departmentName) {
        DepartmentName = departmentName;
    }

    public Integer getSemester() {
        return Semester;
    }

    public void setSemester(Integer semester) {
        Semester = semester;
    }

    public Integer getAdvisorID() {
        return AdvisorID;
    }

    public void setAdvisorID(Integer advisorID) {
        AdvisorID = advisorID;
    }

    public Date getRegisteredDate() {
        return RegisteredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        RegisteredDate = registeredDate;
    }

    public String getRegisteredDateFormatted() {
        return RegisteredDateFormatted;
    }

    public void setRegisteredDateFormatted(String registeredDateFormatted) {
        RegisteredDateFormatted = registeredDateFormatted;
    }

    public float getGPA() {
        return GPA;
    }

    public void setGPA(float GPA) {
        this.GPA = GPA;
    }



}

package sample;

import java.util.Date;

public class Person {

    int ID;
    String Name;
    String Surname;
    String Password;
    String Email;
    String DepartmentID;
    String DepartmentName;
    String RegisteredDateFormatted;
    String enter = "Giriniz";

    public Person() {
        this.ID = 0;
        Name = enter;
        Surname = enter;
        Password = enter;
        Email = enter;
        DepartmentID = enter;
        DepartmentName = enter;
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

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
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

    public String getDepartmentName() {
        return DepartmentName;
    }

    public void setDepartmentName(String departmentName) {
        DepartmentName = departmentName;
    }

    public String getRegisteredDateFormatted() {
        return RegisteredDateFormatted;
    }

    public void setRegisteredDateFormatted(String registeredDateFormatted) {
        RegisteredDateFormatted = registeredDateFormatted;
    }



}

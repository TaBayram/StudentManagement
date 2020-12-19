package sample;

import java.util.Date;

public class Teacher {



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

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getDepartmentID() {
        return DepartmentID;
    }

    public void setDepartmentID(String departmentID) {
        DepartmentID = departmentID;
    }

    public Date getRegisteredDate() {
        return RegisteredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        RegisteredDate = registeredDate;
    }


    public String getHiddenPassword() {
        return HiddenPassword;
    }

    public void setHiddenPassword(String hiddenPassword) {
        HiddenPassword = hiddenPassword;
    }

    int ID;
    String Name;
    String Surname;
    String Email;
    String Title;
    String Password;
    String HiddenPassword;
    String DepartmentID;
    Date RegisteredDate;
    String DepartmentName;
    String enter = "Giriniz";

    public Teacher() {
        ID = 0;
        Name = enter;
        Surname = enter;
        Email = enter;
        Title = enter;
        Password = enter;
        DepartmentID = enter;
    }

    public String getDepartmentName() {
        return DepartmentName;
    }

    public void setDepartmentName(String departmentName) {
        DepartmentName = departmentName;
    }



}

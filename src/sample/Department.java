package sample;

import javafx.scene.control.Button;

public class Department {

    int ID;
    String Name;
    String language;
    String DepartmentChair;
    Button button;
    String DepartmentFacultyID;
    String courseCount;
    String enter = "Giriniz";

    public Department() {
        this.ID = ID;
        Name = enter;
        this.language = enter;
        DepartmentChair = enter;
        DepartmentFacultyID = enter;
        courseCount = enter;
    }

    public String getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(String courseCount) {
        this.courseCount = courseCount;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDepartmentChair() {
        return DepartmentChair;
    }

    public void setDepartmentChair(String departmentChair) {
        DepartmentChair = departmentChair;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public String getDepartmentFacultyID() {
        return DepartmentFacultyID;
    }

    public void setDepartmentFacultyID(String departmentFacultyID) {
        DepartmentFacultyID = departmentFacultyID;
    }

}

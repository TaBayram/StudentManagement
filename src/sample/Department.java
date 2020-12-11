package sample;

import javafx.scene.control.Button;

public class Department {

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

    public int getDepartmentChair() {
        return DepartmentChair;
    }

    public void setDepartmentChair(int departmentChair) {
        DepartmentChair = departmentChair;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    int ID;
    String Name;
    String language;
    int DepartmentChair;
    Button button;
}

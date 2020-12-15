package sample;

public class Faculty {
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

    public String getFacultyChair() {
        return FacultyChair;
    }

    public void setFacultyChair(String facultyChair) {
        FacultyChair = facultyChair;
    }

    int ID;
    String Name;
    String FacultyChair;
    String enter = "Giriniz";

    public Faculty() {
        this.ID = ID;
        Name = enter;
        FacultyChair = enter;
    }
}

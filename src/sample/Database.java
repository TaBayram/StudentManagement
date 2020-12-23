package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;

public class Database{
    public static class DatabaseConnection{
        //private static String url = "jdbc:sqlserver://localhost:1433;" + "databaseName=StudentDatabase;integratedSecurity=true";

        private static String url = "jdbc:sqlserver://localhost:1433;" + "databaseName=StudentDatabase;integratedSecurity=true";

        /*##################### FACULTY #################################*/

        public static ObservableList<Faculty> GetAllFaculties(){
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                String sqlScript = "SELECT * FROM FacultyTable";
                ResultSet resultSet = statement.executeQuery(sqlScript);
                final ObservableList<Faculty> faculties = FXCollections.observableArrayList();

                int i = 0;
                while(true){
                    boolean isNext =  resultSet.next();
                    if(resultSet.isAfterLast() || !isNext) break;
                    Faculty faculty = new Faculty();
                    faculty.setID(resultSet.getInt("ID"));
                    faculty.setName(resultSet.getString("Name"));
                    faculty.setFacultyChair(String.valueOf(resultSet.getInt("FacultyChair")));


                    faculties.add(faculty);
                }

                statement = connection.createStatement();
                sqlScript = "SELECT FacultyID,SUM(CourseCount) as 'Course Count' From DepartmentTable Group By FacultyID";
                resultSet = statement.executeQuery(sqlScript);

                while(true){
                    boolean isNext =  resultSet.next();
                    if(resultSet.isAfterLast() || !isNext) break;
                    for (Faculty faculty:faculties) {
                        if(faculty.getID() == resultSet.getInt("FacultyID")){
                            faculty.setFacultyCourseCount(resultSet.getInt("Course Count"));
                        }
                    }


                }

                connection.close();
                return faculties;

            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
            }
            return null;
        }

        public static boolean GetFacultyChairNames(ObservableList<Faculty> faculties){
            try{
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();
                String sqlScript = "Select FacultyTable.ID,TeacherTable.Name from FacultyTable " +
                        "LEFT JOIN TeacherTable " +
                        "ON FacultyTable.FacultyChair = TeacherTable.ID";
                ResultSet resultSet = statement.executeQuery(sqlScript);

                int i = 0;
                while(true){
                    resultSet.next();
                    if(resultSet.isAfterLast()) break;
                    int id = resultSet.getInt("ID");
                    for (Faculty faculty: faculties) {
                        if(faculty.getID() == id){
                            faculty.setFacultyChairName(resultSet.getString(("Name")));
                            break;
                        }
                    }
                }
                connection.close();
                return true;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());

            }
            return false;
        }

        public static int AddFaculty(Faculty faculty){
            try{
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();
                //INSERT VALUES
                String sqlScript = String.format("spAddFaculty '%s', %d", faculty.getName(), Integer.parseInt(faculty.getFacultyChair()));
                ResultSet resultSet = statement.executeQuery(sqlScript);

                int id = 0;
                resultSet.next();
                if(resultSet.isAfterLast()) return 0;
                id = resultSet.getInt(1);

                connection.close();
                return id;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
                return 0;
            }
        }

        public static boolean AlterFaculty(Faculty faculty){
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                //INSERT VALUES
                String sqlScript = String.format("spUpdateFaculty %d,'%s', %d", faculty.getID(),faculty.getName() ,Integer.parseInt(faculty.getFacultyChair()));
                int result = statement.executeUpdate(sqlScript);
                boolean updated = false;
                if(result != 0) updated = true;
                connection.close();
                return updated;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
                return false;
            }
        }

        public static boolean RemoveFaculty(Faculty faculty){
            int id = faculty.getID();
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                //Delete
                String sqlScript = String.format("Delete from FacultyTable Where ID = '%d'", id);
                statement.executeUpdate(sqlScript);
                connection.close();
                return true;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
                return false;
            }

        }

        public static boolean DoesFacultyExist(int ID){
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                String sqlScript = String.format("SELECT COUNT(ID) COUNT FROM FacultyTable WHERE ID = %d",ID);                                                      //SELECT FROM
                ResultSet resultSet = statement.executeQuery(sqlScript);
                boolean exists = true;
                resultSet.next();
                if(resultSet.getInt("COUNT") == 0) exists = false;
                connection.close();
                return exists;

            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
            }
            return false;
        }

        /*##################### STUDENT #################################*/

        public static ObservableList<Student> GetAllStudents(){
            try{
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();
                //CallableStatement stmt = connection.prepareCall("{}")

                String sqlScript = "Update StudentTable Set Semester = dbo.Semester(RegisteredDate)";
                statement.execute(sqlScript);
                sqlScript = "{call spAllStudent()}";
                ResultSet resultSet = statement.executeQuery(sqlScript);
                final ObservableList<Student> students = FXCollections.observableArrayList();

                int i = 0;
                while(true){
                    boolean isNext =  resultSet.next();
                    if(resultSet.isAfterLast() || !isNext) break;
                    Student student = new Student();
                    student.setID(resultSet.getInt("ID"));
                    student.setName(resultSet.getString("Name"));
                    student.setSurname(resultSet.getString("Surname"));
                    student.setEmail(resultSet.getString("Email"));
                    student.setPassword(resultSet.getString("Password"));
                    student.setDepartmentID(String.valueOf(resultSet.getInt("DepartmentID")));
                    student.setRegisteredDate(resultSet.getDate("RegisteredDate"));
                    student.setRegisteredDateFormatted(resultSet.getString("RegisteredDateFormatted"));
                    student.setSemester(resultSet.getInt("Semester"));
                    student.setAdvisorID(resultSet.getInt("AdvisorID"));
                    student.setGPA(resultSet.getInt("GPA"));

                    students.add(student);

                }
                connection.close();
                return students;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());

            }
            return null;
        }

        public static boolean GetStudentDepartmentName(ObservableList<Student> students){
            try{
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();
                String sqlScript = "Select StudentTable.ID,DepartmentTable.Name from StudentTable " +
                        "LEFT JOIN DepartmentTable " +
                        "ON StudentTable.DepartmentId = DepartmentTable.ID";                                                             //PROCEDURE
                ResultSet resultSet = statement.executeQuery(sqlScript);

                int i = 0;
                while(true){
                    resultSet.next();
                    if(resultSet.isAfterLast()) break;
                    int id = resultSet.getInt("ID");
                    for (Student student: students) {
                        if(student.getID() == id){
                            student.setDepartmentName(resultSet.getString(("Name")));
                            break;
                        }
                    }
                }
                connection.close();
                return true;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());

            }
            return false;
        }

        public static ObservableList<Student> GetStudentListByDepartmentID(int ID){
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                //COMPLEX WHERE CLAUSE
                String sqlScript = String.format("Select *, CONVERT(nvarchar, RegisteredDate, 105) AS RegisteredDateFormatted from StudentTable where DepartmentID = (SELECT ID FROM DepartmentTable where ID = '%d')", ID);                                                      //SELECT FROM Kullanildi
                ResultSet resultSet = statement.executeQuery(sqlScript);
                final ObservableList<Student> students = FXCollections.observableArrayList();

                int i = 0;
                while(true){
                    boolean isNext =  resultSet.next();
                    if(resultSet.isAfterLast() || !isNext) break;
                    Student student = new Student();
                    student.setID(resultSet.getInt("ID"));
                    student.setName(resultSet.getString("Name"));
                    student.setSurname(resultSet.getString("Surname"));
                    student.setEmail(resultSet.getString("Email"));
                    student.setPassword(resultSet.getString("Password"));
                    student.setRegisteredDate(resultSet.getDate("RegisteredDate"));
                    student.setRegisteredDateFormatted(resultSet.getString("RegisteredDateFormatted"));
                    student.setDepartmentID(String.valueOf(resultSet.getInt("DepartmentID")));
                    student.setSemester(resultSet.getInt("Semester"));
                    student.setAdvisorID(resultSet.getInt("AdvisorID"));
                    student.setGPA(resultSet.getInt("GPA"));
                    students.add(student);
                }
                connection.close();
                return students;

            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
            }

            return null;
        }

        public static int AddStudent(Student student){
            try{
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();
                //INSERT VALUES
                String sqlScript = String.format("spAddStudent '%s', '%s','%s', '%s', %d,%d", student.getName(),student.getSurname(),student.getPassword(),student.getEmail(), Integer.parseInt(student.getDepartmentID()),student.getAdvisorID());
                ResultSet resultSet = statement.executeQuery(sqlScript);
                //ResultSet resultSet = statement.executeQuery("SELECT SCOPE_IDENTITY()");
                int id = 0;
                resultSet.next();
                if(resultSet.isAfterLast()) return 0;
                id = resultSet.getInt(1);

                connection.close();
                return id;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
                return 0;
            }
        }

        public static boolean AlterStudent(Student student){
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                //INSERT VALUES
                String sqlScript = String.format("spUpdateStudent  %d,'%s', '%s','%s', '%s', %d", student.getID(),student.getName(),student.getSurname(),student.getPassword(),student.getEmail(), Integer.parseInt(student.getDepartmentID()));
                int result = statement.executeUpdate(sqlScript);
                boolean updated = false;
                if(result != 0) updated = true;
                connection.close();
                return updated;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
                return false;
            }
        }

        public static boolean RemoveStudent(Student student){
            int id = student.getID();
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                //Delete
                String sqlScript = String.format("Delete from StudentTable Where ID = '%d'", id);
                statement.executeUpdate(sqlScript);
                connection.close();
                return true;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
                return false;
            }

        }

        /*##################### DEPARTMENT #################################*/

        public static ObservableList<Department> GetAllDepartments(){
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                String sqlScript = "SELECT * FROM DepartmentTable";
                ResultSet resultSet = statement.executeQuery(sqlScript);
                final ObservableList<Department> departments = FXCollections.observableArrayList();

                int i = 0;
                while(true){
                    boolean isNext =  resultSet.next();
                    if(resultSet.isAfterLast() || !isNext) break;
                    Department department = new Department();
                    department.setID(resultSet.getInt("ID"));
                    department.setName(resultSet.getString("Name"));
                    department.setLanguage(resultSet.getString("Language"));
                    department.setDepartmentChair(resultSet.getString("DepartmentChair"));
                    department.setDepartmentFacultyID(resultSet.getString("FacultyID"));
                    departments.add(department);
                }
                connection.close();
                return departments;

            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
            }
            return null;
        }

        public static ArrayList<String> GetDepartmentNames(){
            try{
                Connection connection = DriverManager.getConnection(url);

                //Get the names
                Statement statement = connection.createStatement();
                String sqlScript = "SELECT Name FROM DepartmentTable";                                                      //SELECT FROM
                ResultSet resultSet = statement.executeQuery(sqlScript);
                ArrayList<String> departmentNames = new ArrayList<>();
                int i = 0;
                while(true){
                    resultSet.next();
                    if(resultSet.isAfterLast()) break;
                    departmentNames.add(resultSet.getString("Name"));
                }
                connection.close();
                return departmentNames;

            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
            }
            return null;
        }

        public static boolean DoesDepartmentExist(int ID){
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                String sqlScript = String.format("SELECT COUNT(ID) COUNT FROM DepartmentTable WHERE ID = %d",ID);                                                      //SELECT FROM
                ResultSet resultSet = statement.executeQuery(sqlScript);
                boolean exists = true;
                resultSet.next();
                if(resultSet.getInt("COUNT") == 0) exists = false;
                connection.close();
                return exists;

            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
            }
            return false;
        }

        public static int AddDepartment(Department department){
                try{
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();
                //INSERT VALUES
                String sqlScript = String.format("spAddDepartment '%s', '%s', %d, %d", department.getName(),department.getLanguage(),Integer.parseInt(department.getDepartmentChair()),Integer.parseInt(department.getDepartmentFacultyID()));
                ResultSet resultSet = statement.executeQuery(sqlScript);

                int id = 0;
                resultSet.next();
                if(resultSet.isAfterLast()) return 0;
                id = resultSet.getInt(1);

                connection.close();
                return id;
            }catch(Exception e){
                    DatabaseError databaseError = new DatabaseError(e.getMessage());
                return 0;
            }
        }

        public static boolean AlterDepartment(Department department){
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                //INSERT VALUES
                String sqlScript = String.format("spUpdateDepartment '%d', '%s', '%s', '%d', %d", department.getID(),department.getName(),department.getLanguage(),Integer.parseInt(department.getDepartmentChair()),Integer.parseInt(department.getDepartmentFacultyID()));
                int result = statement.executeUpdate(sqlScript);
                boolean updated = false;
                if(result != 0) updated = true;
                connection.close();
                return updated;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
                return false;
            }
        }

        public static boolean RemoveDepartment(Department department){
            int id = department.getID();
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                //Delete
                String sqlScript = String.format("Delete from DepartmentTable Where ID = '%d'", id);
                statement.executeUpdate(sqlScript);
                connection.close();
                return true;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
                return false;
            }

        }



        /*##################### TEACHER #################################*/

        public static ObservableList<Teacher> GetAllTeachers(){
            try{
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();
                //CallableStatement stmt = connection.prepareCall("{}")

                String sqlScript = "Select *, REPLACE(Password, Password, '****') as HiddenPassword From TeacherTable with(INDEX(IX_TeacherTable_Name))";
                ResultSet resultSet = statement.executeQuery(sqlScript);
                final ObservableList<Teacher> teachers = FXCollections.observableArrayList();

                int i = 0;
                while(true){
                    boolean isNext =  resultSet.next();
                    if(resultSet.isAfterLast() || !isNext) break;
                    Teacher teacher = new Teacher();
                    teacher.setID(resultSet.getInt("ID"));
                    teacher.setName(resultSet.getString("Name"));
                    teacher.setSurname(resultSet.getString("Surname"));
                    teacher.setEmail(resultSet.getString("Email"));
                    teacher.setTitle(resultSet.getString("Title"));
                    teacher.setPassword(resultSet.getString("Password"));
                    teacher.setHiddenPassword(resultSet.getString("HiddenPassword"));
                    teacher.setDepartmentID(String.valueOf(resultSet.getInt("DepartmentID")));
                    teacher.setRegisteredDate(resultSet.getDate("RegisteredDate"));

                    teachers.add(teacher);
                }
                connection.close();
                return teachers;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
            }
            return null;
        }

        public static boolean GetTeacherDepartmentName(ObservableList<Teacher> teachers){
            try{
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();
                String sqlScript = "Select TeacherTable.ID,DepartmentTable.Name from TeacherTable " +
                        "LEFT JOIN DepartmentTable " +
                        "ON TeacherTable.DepartmentId = DepartmentTable.ID";                                                             //PROCEDURE
                ResultSet resultSet = statement.executeQuery(sqlScript);
                final ObservableList<Teacher> teachersDepartmentNames = FXCollections.observableArrayList();

                int i = 0;
                while(true){
                    resultSet.next();
                    if(resultSet.isAfterLast()) break;
                    int id = resultSet.getInt("ID");
                    for (Teacher teacher: teachers) {
                        if(teacher.getID() == id){
                            teacher.setDepartmentName(resultSet.getString(("Name")));
                            break;
                        }
                    }
                }
                connection.close();
                return true;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());

            }
            return false;
        }

        public static int AddTeacher(Teacher teacher){
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                //INSERT VALUES
                String sqlScript = String.format("spAddTeacher '%s', '%s','%s','%s', '%s', %d", teacher.getName(),teacher.getSurname(),teacher.getPassword(),teacher.getEmail(),teacher.getTitle(), Integer.parseInt(teacher.getDepartmentID()));
                ResultSet resultSet = statement.executeQuery(sqlScript);
                int id = 0;
                resultSet.next();
                if(resultSet.isAfterLast()) return 0;
                id = resultSet.getInt(1);

                connection.close();
                return id;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
                return 0;
            }


        }

        public static boolean AlterTeacher(Teacher teacher){
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                //INSERT VALUES
                String sqlScript = String.format("spUpdateTeacher  %d,'%s', '%s','%s', '%s', '%s', %d", teacher.getID(),teacher.getName(),teacher.getSurname(),teacher.getPassword(),teacher.getEmail(), teacher.getTitle(),  Integer.parseInt(teacher.getDepartmentID()));
                int result = statement.executeUpdate(sqlScript);
                boolean updated = false;
                if(result != 0) updated = true;
                connection.close();
                return updated;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
                return false;
            }
        }

        public static boolean RemoveTeacher(Teacher teacher){
            int id = teacher.getID();
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                //Delete
                String sqlScript = String.format("Delete from TeacherTable Where ID = '%d'", id);
                statement.executeUpdate(sqlScript);
                connection.close();
                return true;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
                return false;
            }

        }

        public static boolean DoesTeacherExist(int ID){
            try{
                Connection connection = DriverManager.getConnection(url);

                Statement statement = connection.createStatement();
                String sqlScript = String.format("SELECT COUNT(ID) COUNT FROM TeacherTable WHERE ID = %d",ID);                                                      //SELECT FROM
                ResultSet resultSet = statement.executeQuery(sqlScript);
                boolean exists = true;
                resultSet.next();
                if(resultSet.getInt("COUNT") == 0) exists = false;
                connection.close();
                return exists;

            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
            }
            return false;
        }

        /*##################### MISC #################################*/

        public static ObservableList<Person> GetAllPeople(){
            try{
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();

                String sqlScript = "select id,name,surname,password,email,departmentID,Convert(nvarchar,RegisteredDate) as RegisteredDateFormatted from  StudentTable" +
                        " union all" +
                        " select id,name,surname,password,email,departmentID,Convert(nvarchar,RegisteredDate) as RegisteredDateFormatted from TeacherTable";                                                             //UNION
                ResultSet resultSet = statement.executeQuery(sqlScript);
                final ObservableList<Person> people = FXCollections.observableArrayList();

                int i = 0;
                while(true){
                    boolean isNext =  resultSet.next();
                    if(resultSet.isAfterLast() || !isNext) break;
                    Person person = new Person();
                    person.setID(resultSet.getInt("ID"));
                    person.setName(resultSet.getString("Name"));
                    person.setSurname(resultSet.getString("Surname"));
                    person.setEmail(resultSet.getString("Email"));
                    person.setPassword(resultSet.getString("Password"));
                    person.setDepartmentID(String.valueOf(resultSet.getInt("DepartmentID")));
                    person.setRegisteredDateFormatted(resultSet.getString("RegisteredDateFormatted"));

                    people.add(person);
                }
                connection.close();
                return people;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
            }
            return null;
        }

        public static int GetStudentCountByDepartmentID(int ID){
            try{
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();

                String sqlScript = String.format("{call dbo.spGetStudentCountByDepartment(%d) }",ID);
                /*ResultSet resultSet = statement.executeQuery(sqlScript);
                resultSet.next();
                int count = resultSet.getInt("Count");

                CallableStatement proc = connection.prepareCall(sqlScript);
                proc.registerOutParameter(1, Types.INTEGER);
                proc.execute();
                int returnValue = proc.getInt(1);*/

                CallableStatement callableStatement = connection.prepareCall("{call dbo.spGetStudentCountByDepartment(?,?) }");
                callableStatement.setInt("departmentID", ID);
                callableStatement.registerOutParameter("result", java.sql.Types.INTEGER);
                callableStatement.execute();
                int count = callableStatement.getInt("result");




                connection.close();
                return count;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
                return 0;
            }
        }

        public static int GetCount(String table){
            if(table.equals("Person")){
                return (GetCount("Student") + GetCount("Teacher"));
            }

            try{
                String fullName = table+"Table";
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();

                String sqlScript = String.format("SELECT COUNT(ID) as [Count] FROM %s",fullName);
                ResultSet resultSet = statement.executeQuery(sqlScript);
                resultSet.next();
                int count = resultSet.getInt("Count");
                connection.close();
                return count;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
                return 0;
            }
        }

        public static double GetMathResult(double number, int operation){
            double result = 0;
            try{
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();


                String sqlScript = "Select ";

                if(operation == 1) sqlScript += String.format("SQUARE(%f)",number);
                if(operation == 2) sqlScript += String.format("POWER(%f,3)",number);
                if(operation == 3) sqlScript += String.format("SQRT(%f)",number);
                if(operation == 4) sqlScript += String.format("TAN(%f)",number);
                if(operation == 5) sqlScript += String.format("SIN(%f)",number);
                if(operation == 6) sqlScript += String.format("COS(%f)",number);
                if(operation == 7) sqlScript += String.format("FLOOR(%f)",number);
                if(operation == 8) sqlScript += String.format("CEILING(%f)",number);
                if(operation == 9) sqlScript += String.format("ABS(%f)",number);





                sqlScript += "as Result";
                ResultSet resultSet = statement.executeQuery(sqlScript);
                boolean isNext =  resultSet.next();
                if(resultSet.isAfterLast() || !isNext) return  result;
                result = resultSet.getDouble("Result");

                connection.close();
                return result;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
                return result;
            }
        }


        private static class DatabaseError{
            Alert alert;
            DatabaseError(String errorText){
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Database Connection Error");
                alert.setHeaderText(null);
                alert.setContentText(errorText);
                alert.show();
            }
        }
    }
}
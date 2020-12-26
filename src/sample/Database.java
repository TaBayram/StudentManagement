package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;

public class Database{
    public static class DatabaseConnection{
        //private static String url = "jdbc:sqlserver://localhost:1433;" + "databaseName=StudentDatabase;integratedSecurity=true";

        private static String url = "jdbc:sqlserver://localhost:1433;" + "databaseName=StudentDatabase;integratedSecurity=true";
        private static String urlWDatabase = "jdbc:sqlserver://localhost:1433;" + "integratedSecurity=true";

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
                if(resultSet.getInt("COUNT") == 0) {
                    exists = false;
                }
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
                    student.setGPA(resultSet.getFloat("GPA"));

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
                    department.setCourseCount(String.valueOf(resultSet.getInt("CourseCount")));
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
                String sqlScript = String.format("spAddDepartment '%s', '%s', %d, %d, %d", department.getName(),department.getLanguage(),Integer.parseInt(department.getDepartmentChair()),Integer.parseInt(department.getDepartmentFacultyID()),Integer.parseInt(department.getCourseCount()));
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
                String sqlScript = String.format("spUpdateDepartment '%d', '%s', '%s', '%d', %d, %d", department.getID(),department.getName(),department.getLanguage(),Integer.parseInt(department.getDepartmentChair()),Integer.parseInt(department.getDepartmentFacultyID()),Integer.parseInt(department.getCourseCount()));
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

        public static ObservableList<String> GetLeftTeachers(){
            try{
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();

                String sqlScript = "Select Info From LeftTeacherTable";
                ResultSet resultSet = statement.executeQuery(sqlScript);
                final ObservableList<String> teachers = FXCollections.observableArrayList();

                int i = 0;
                while(true){
                    boolean isNext =  resultSet.next();
                    if(resultSet.isAfterLast() || !isNext) break;
                    String string = resultSet.getString("Info");
                    teachers.add(string);
                }
                connection.close();
                return teachers;
            }catch(Exception e){
                DatabaseError databaseError = new DatabaseError(e.getMessage());
            }
            return null;
        }

        public static Task CreateDatabaseFromScratch() {
            return new Task(){
                @Override
                protected Object call() throws Exception {
                    updateTitle("Checking if the database exists...");
                    try{
                        updateProgress(0,100);
                        Connection connection = DriverManager.getConnection(urlWDatabase);
                        Statement statement = connection.createStatement();
                        String sqlScript = "CREATE DATABASE [StudentDatabase]";
                        statement.executeUpdate(sqlScript);

                        updateTitle("Creating the database from scratch...");
                        updateProgress(5,100);
                        Thread.sleep(250);

                        connection = DriverManager.getConnection(url);


                        statement = connection.createStatement();
                        sqlScript = " CREATE TABLE [dbo].[DepartmentTable](\n" +
                                "\t[ID] [int] IDENTITY(1000,1) NOT NULL,\n" +
                                "\t[Name] [nvarchar](50) NULL,\n" +
                                "\t[Language] [nvarchar](50) NULL,\n" +
                                "\t[DepartmentChair] [int] NULL,\n" +
                                "\t[FacultyID] [int] NULL,\n" +
                                "\t[CourseCount] [int] NULL,\n" +
                                " CONSTRAINT [PK_DepartmentTable] PRIMARY KEY CLUSTERED \n" +
                                "(\n" +
                                "\t[ID] ASC\n" +
                                ")WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]\n" +
                                ") ON [PRIMARY]";
                        statement.executeUpdate(sqlScript);

                        updateProgress(8,100);

                        sqlScript = "CREATE TABLE [dbo].[FacultyTable](\n" +
                                "\t[ID] [int] IDENTITY(100,1) NOT NULL,\n" +
                                "\t[Name] [nvarchar](50) NULL,\n" +
                                "\t[FacultyChair] [int] NULL,\n" +
                                " CONSTRAINT [PK_FacultyTable] PRIMARY KEY CLUSTERED \n" +
                                "(\n" +
                                "\t[ID] ASC\n" +
                                ")WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]\n" +
                                ") ON [PRIMARY]";
                        statement.executeUpdate(sqlScript);

                        updateProgress(11,100);

                        sqlScript = "CREATE TABLE [dbo].[LeftTeacherTable](\n" +
                                "\t[ID] [int] IDENTITY(1,1) NOT NULL,\n" +
                                "\t[info] [nvarchar](100) NOT NULL,\n" +
                                " CONSTRAINT [PK_LeftTeacherTable] PRIMARY KEY CLUSTERED \n" +
                                "(\n" +
                                "\t[ID] ASC\n" +
                                ")WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]\n" +
                                ") ON [PRIMARY]\n";
                        statement.executeUpdate(sqlScript);

                        updateProgress(14,100);

                        sqlScript = "CREATE TABLE [dbo].[StudentTable](\n" +
                                "\t[ID] [int] IDENTITY(10000,2) NOT NULL,\n" +
                                "\t[Name] [nvarchar](50) NOT NULL,\n" +
                                "\t[Surname] [nvarchar](50) NOT NULL,\n" +
                                "\t[Password] [nvarchar](50) NOT NULL,\n" +
                                "\t[Email] [nvarchar](50) NOT NULL,\n" +
                                "\t[DepartmentID] [int] NULL,\n" +
                                "\t[RegisteredDate] [datetime] NULL,\n" +
                                "\t[Semester] [int] NULL,\n" +
                                "\t[AdvisorID] [int] NULL,\n" +
                                "\t[GPA] [float] NULL,\n" +
                                " CONSTRAINT [PK_StudentTable] PRIMARY KEY CLUSTERED \n" +
                                "(\n" +
                                "\t[ID] ASC\n" +
                                ")WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]\n" +
                                ") ON [PRIMARY]";
                        statement.executeUpdate(sqlScript);

                        updateProgress(17,100);

                        sqlScript = "CREATE TABLE [dbo].[TeacherTable](\n" +
                                "\t[ID] [int] IDENTITY(1000,1) NOT NULL,\n" +
                                "\t[Name] [nvarchar](50) NULL,\n" +
                                "\t[Surname] [nvarchar](50) NULL,\n" +
                                "\t[Email] [nvarchar](50) NULL,\n" +
                                "\t[Title] [nvarchar](50) NULL,\n" +
                                "\t[Password] [nvarchar](50) NULL,\n" +
                                "\t[DepartmentID] [int] NULL,\n" +
                                "\t[RegisteredDate] [datetime] NULL,\n" +
                                " CONSTRAINT [PK_Teacher] PRIMARY KEY CLUSTERED \n" +
                                "(\n" +
                                "\t[ID] ASC\n" +
                                ")WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]\n" +
                                ") ON [PRIMARY]";
                        statement.executeUpdate(sqlScript);

                        updateProgress(18,100);

                        sqlScript = "CREATE UNIQUE NONCLUSTERED INDEX [NonClusteredIndex-20201215-182812] ON [dbo].[FacultyTable]\n" +
                                "(\n" +
                                "\t[Name] ASC\n" +
                                ")WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]\n" +
                                "SET ANSI_PADDING ON\n" +
                                "CREATE NONCLUSTERED INDEX [NonClusteredIndex-20201215-182434] ON [dbo].[StudentTable]\n" +
                                "(\n" +
                                "\t[Name] ASC,\n" +
                                "\t[Surname] ASC\n" +
                                ")WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]\n" +
                                "SET ANSI_PADDING ON\n" +
                                "CREATE NONCLUSTERED INDEX [IX_TeacherTable_Name] ON [dbo].[TeacherTable]\n" +
                                "(\n" +
                                "\t[Name] ASC\n" +
                                ")WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]\n";
                        statement.executeUpdate(sqlScript);

                        updateProgress(20,100);

                        sqlScript = "ALTER TABLE [dbo].[StudentTable] ADD  CONSTRAINT [DF_StudentTable_Semester]  DEFAULT ((0)) FOR [Semester]\n" +

                                "ALTER TABLE [dbo].[DepartmentTable]  WITH NOCHECK ADD  CONSTRAINT [FK_DepartmentTable_FacultyTable] FOREIGN KEY([FacultyID])\n" +
                                "REFERENCES [dbo].[FacultyTable] ([ID])\n" +
                                "ON DELETE CASCADE\n" +

                                "ALTER TABLE [dbo].[DepartmentTable] CHECK CONSTRAINT [FK_DepartmentTable_FacultyTable]\n" +

                                "ALTER TABLE [dbo].[DepartmentTable]  WITH NOCHECK ADD  CONSTRAINT [FK_DepartmentTable_Teacher] FOREIGN KEY([DepartmentChair])\n" +
                                "REFERENCES [dbo].[TeacherTable] ([ID])\n" +
                                "ON DELETE SET NULL\n" +

                                "ALTER TABLE [dbo].[DepartmentTable] NOCHECK CONSTRAINT [FK_DepartmentTable_Teacher]\n" +

                                "ALTER TABLE [dbo].[StudentTable]  WITH NOCHECK ADD  CONSTRAINT [FK_StudentTable_DepartmentTable] FOREIGN KEY([DepartmentID])\n" +
                                "REFERENCES [dbo].[DepartmentTable] ([ID])\n" +
                                "ON DELETE SET NULL\n" +

                                "ALTER TABLE [dbo].[StudentTable] NOCHECK CONSTRAINT [FK_StudentTable_DepartmentTable]\n" +

                                "ALTER TABLE [dbo].[StudentTable]  WITH CHECK ADD  CONSTRAINT [FK_StudentTable_Teacher] FOREIGN KEY([AdvisorID])\n" +
                                "REFERENCES [dbo].[TeacherTable] ([ID])\n" +
                                "ON DELETE SET NULL\n" +

                                "ALTER TABLE [dbo].[StudentTable] CHECK CONSTRAINT [FK_StudentTable_Teacher]\n" +

                                "ALTER TABLE [dbo].[TeacherTable]  WITH CHECK ADD  CONSTRAINT [FK_Teacher_DepartmentTable] FOREIGN KEY([DepartmentID])\n" +
                                "REFERENCES [dbo].[DepartmentTable] ([ID])\n" +
                                "ON DELETE SET NULL\n" +

                                "ALTER TABLE [dbo].[TeacherTable] CHECK CONSTRAINT [FK_Teacher_DepartmentTable]\n" +

                                "ALTER TABLE [dbo].[StudentTable]  WITH CHECK ADD  CONSTRAINT [CK_StudentTable_GPA] CHECK  (([GPA]>=(0) AND [GPA]<=(4)))\n" +

                                "ALTER TABLE [dbo].[StudentTable] CHECK CONSTRAINT [CK_StudentTable_GPA]";
                        statement.executeUpdate(sqlScript);

                        updateProgress(30,100);

                        sqlScript = "CREATE FUNCTION [dbo].[Semester](@registeredDate date)  \n" +
                                "RETURNS INT  \n" +
                                "AS  \n" +
                                "BEGIN  \n" +
                                " DECLARE @Semester INT  \n" +
                                "\n" +
                                "set @Semester = DATEDIFF(MONTH, @registeredDate, GETDATE())\n" +
                                "set @Semester /= 6\n" +
                                "\n" +
                                "RETURN @Semester  \n" +
                                "END";
                        statement.executeUpdate(sqlScript);

                        updateProgress(32,100);

                        sqlScript = "CREATE VIEW [dbo].[vAllStudent]\n" +
                                "AS\n" +
                                "SELECT        ID, Name, Surname, Password, Email, DepartmentID, RegisteredDate, Semester, AdvisorID, COALESCE (GPA, 0) AS GPA, CONVERT(nvarchar, RegisteredDate, 105) AS RegisteredDateFormatted\n" +
                                "FROM            dbo.StudentTable";
                        statement.executeUpdate(sqlScript);

                        updateProgress(34,100);

                        sqlScript = "CREATE procedure [dbo].[spABS]\n" +
                                "@number float,\n" +
                                "@result float OUTPUT\n" +
                                "as\n" +
                                "Begin\n" +
                                "set @result = ABS(@number)\n" +
                                "End";
                        statement.executeUpdate(sqlScript);

                        updateProgress(36,100);

                        sqlScript = "create Procedure [dbo].[spAddDepartment] \n" +
                                "\n" +
                                "@Name nvarchar(50),\n" +
                                "@Language nvarchar(50),\n" +
                                "@DepartmentChair int,\n" +
                                "@FacultyID int,\n" +
                                "@CourseCount int\n" +
                                "as\n" +
                                "Begin\n" +
                                "insert into DepartmentTable(Name,Language,DepartmentChair,FacultyID,CourseCount) OUTPUT INSERTED.ID\n" +
                                "values(LTRIM(RTRIM(@Name)),LTRIM(RTRIM(@Language)),@DepartmentChair,@FacultyID,@CourseCount)\n" +
                                "End";
                        statement.executeUpdate(sqlScript);

                        updateProgress(38,100);

                        sqlScript = "CREATE Procedure [dbo].[spAddFaculty]\n" +
                                "\n" +
                                "@Name nvarchar(50),\n" +
                                "@FacultyChair int\n" +
                                "\n" +
                                "as\n" +
                                "Begin\n" +
                                "insert into FacultyTable (Name,FacultyChair) OUTPUT INSERTED.ID\n" +
                                "values(LTRIM(RTRIM(@Name)),@FacultyChair)\n" +
                                "End";
                        statement.executeUpdate(sqlScript);

                        updateProgress(40,100);

                        sqlScript = "CREATE Procedure [dbo].[spAddStudent]\n" +
                                "\n" +
                                "@Name nvarchar(50),\n" +
                                "@Surname nvarchar(50),\n" +
                                "@Password nvarchar(50),\n" +
                                "@Email nvarchar(50),\n" +
                                "@DepartmentID int,\n" +
                                "@AdvisorID int\n" +
                                "as\n" +
                                "Begin\n" +
                                "insert into StudentTable (Name,Surname,Password,Email,DepartmentID,AdvisorID)\n" +
                                "values(LTRIM(RTRIM(@Name)),UPPER(LTRIM(RTRIM(@Surname))),LTRIM(RTRIM(@Password)),LTRIM(RTRIM(@Email)),@DepartmentID,@AdvisorID)\n" +
                                "SELECT SCOPE_IDENTITY()\n" +
                                "End";
                        statement.executeUpdate(sqlScript);

                        updateProgress(42,100);

                        sqlScript = "CREATE Procedure [dbo].[spAddTeacher]\n" +
                                "\n" +
                                "@Name nvarchar(50),\n" +
                                "@Surname nvarchar(50),\n" +
                                "@Password nvarchar(50),\n" +
                                "@Email nvarchar(50),\n" +
                                "@Title nvarchar(50),\n" +
                                "@DepartmentID int\n" +
                                "as\n" +
                                "Begin\n" +
                                "insert into TeacherTable(Name,Surname,Password,Email,Title,DepartmentID,RegisteredDate) OUTPUT INSERTED.ID\n" +
                                "values(LTRIM(RTRIM(@Name)),UPPER(LTRIM(RTRIM(@Surname))),LTRIM(RTRIM(@Password)),LTRIM(RTRIM(@Email)),LTRIM(RTRIM(@Title)),@DepartmentID,GETDATE())\n" +
                                "End";
                        statement.executeUpdate(sqlScript);

                        updateProgress(44,100);

                        sqlScript = "CREATE Procedure [dbo].[spAllStudent]\n" +
                                "as\n" +
                                "Begin\n" +
                                "Select * from vAllStudent\n" +
                                "End";
                        statement.executeUpdate(sqlScript);

                        updateProgress(45,100);

                        sqlScript = "create procedure [dbo].[spGetStudentCountByDepartment]\n" +
                                "@departmentID int,\n" +
                                "@result int OUTPUT\n" +
                                "as\n" +
                                "Begin\n" +
                                "Select @result = COUNT(ID) from StudentTable where DepartmentID = @departmentID\n" +
                                "End";
                        statement.executeUpdate(sqlScript);

                        updateProgress(47,100);

                        sqlScript = "create Procedure [dbo].[spUpdateDepartment]\n" +
                                "@ID int,\n" +
                                "@Name nvarchar(50),\n" +
                                "@Language nvarchar(50),\n" +
                                "@DepartmentChair nvarchar(50),\n" +
                                "@FacultyID nvarchar(50),\n" +
                                "@CourseCount int\n" +
                                "as\n" +
                                "Begin\n" +
                                "Update DepartmentTable\n" +
                                "SET Name = @Name, Language = @Language,DepartmentChair= @DepartmentChair,FacultyID = @FacultyID, CourseCount = @CourseCount\n" +
                                "WHERE ID = @ID\n" +
                                "End";
                        statement.executeUpdate(sqlScript);

                        updateProgress(49,100);

                        sqlScript = "create Procedure [dbo].[spUpdateFaculty]\n" +
                                "\n" +
                                "@ID int,\n" +
                                "@Name nvarchar(50),\n" +
                                "@FacultyChair int\n" +
                                "as\n" +
                                "Begin\n" +
                                "Update FacultyTable\n" +
                                "SET Name = @Name, FacultyChair = ISNULL(@FacultyChair,0)\n" +
                                "WHERE ID = @ID\n" +
                                "End";
                        statement.executeUpdate(sqlScript);

                        updateProgress(51,100);

                        sqlScript = "Create Procedure [dbo].[spUpdateStudent]\n" +
                                "\n" +
                                "@ID int,\n" +
                                "@Name nvarchar(50),\n" +
                                "@Surname nvarchar(50),\n" +
                                "@Password nvarchar(50),\n" +
                                "@Email nvarchar(50),\n" +
                                "@DepartmentID int\n" +
                                "as\n" +
                                "Begin\n" +
                                "Update StudentTable\n" +
                                "SET Name = @Name, Surname = @Surname,Password= @Password,Email = @Email,DepartmentID= @DepartmentID\n" +
                                "WHERE ID = @ID\n" +
                                "End";
                        statement.executeUpdate(sqlScript);

                        updateProgress(53,100);

                        sqlScript = "Create Procedure [dbo].[spUpdateTeacher]\n" +
                                "\n" +
                                "@ID int,\n" +
                                "@Name nvarchar(50),\n" +
                                "@Surname nvarchar(50),\n" +
                                "@Password nvarchar(50),\n" +
                                "@Email nvarchar(50),\n" +
                                "@Title nvarchar(50),\n" +
                                "@DepartmentID int\n" +
                                "as\n" +
                                "Begin\n" +
                                "Update TeacherTable\n" +
                                "SET Name = @Name, Surname = @Surname,Password= @Password,Email = @Email,Title = @Title, DepartmentID= @DepartmentID\n" +
                                "WHERE ID = @ID\n" +
                                "End";
                        statement.executeUpdate(sqlScript);

                        updateProgress(55,100);

                        sqlScript = "Create TRIGGER [dbo].[dtTeacherTableLeftTeachers]\n" +
                                "ON [dbo].[TeacherTable]\n" +
                                "FOR DELETE\n" +
                                "AS\n" +
                                "BEGIN\n" +
                                "Declare @ID int\n" +
                                "Declare @Name nvarchar(50)\n" +
                                "Select @ID = ID from deleted\n" +
                                "Select @Name = Name + SPACE(1) + Surname from deleted\n" +
                                "insert into LeftTeacherTable (info)\n" +
                                "values('An existing teacher, ID  = ' + Cast(@Id as nvarchar(10)) + ', ' + @Name + ' is deleted at ' + Cast(Getdate() as nvarchar(20)))\n" +
                                "END";
                        statement.executeUpdate(sqlScript);


                        updateProgress(65,100);

                        sqlScript = "SET IDENTITY_INSERT [dbo].[FacultyTable] ON\n" +
                                "INSERT [dbo].[FacultyTable] ([ID], [Name], [FacultyChair]) VALUES (100, N'Mühendislik ve Doğa Bilimleri Fakültesi', 1000)\n" +
                                "INSERT [dbo].[FacultyTable] ([ID], [Name], [FacultyChair]) VALUES (101, N'Hukuk Fakültesi', 1001)\n" +
                                "INSERT [dbo].[FacultyTable] ([ID], [Name], [FacultyChair]) VALUES (102, N'Eğitim Fakültesi', 1002)\n" +
                                "INSERT [dbo].[FacultyTable] ([ID], [Name], [FacultyChair]) VALUES (103, N'İşletme ve Yönetim Bilimleri Fakültesi', 1003)\n" +
                                "SET IDENTITY_INSERT [dbo].[FacultyTable] OFF";
                        statement.executeUpdate(sqlScript);


                        updateProgress(70,100);

                        sqlScript = "SET IDENTITY_INSERT [dbo].[DepartmentTable] ON \n" +
                                "INSERT [dbo].[DepartmentTable] ([ID], [Name], [Language], [DepartmentChair], [FacultyID], [CourseCount]) VALUES (1000, N'Bilgisayar Mühendisliği', N'Ingilizce', 1004, 100, 36)\n" +
                                "INSERT [dbo].[DepartmentTable] ([ID], [Name], [Language], [DepartmentChair], [FacultyID], [CourseCount]) VALUES (1001, N'Gıda Mühendisliği', N'Turkce', 1005, 100, 40)\n" +
                                "INSERT [dbo].[DepartmentTable] ([ID], [Name], [Language], [DepartmentChair], [FacultyID], [CourseCount]) VALUES (1002, N'Yazılım Mühendisliği', N'Ingilizce', 1004, 100, 32)\n" +
                                "INSERT [dbo].[DepartmentTable] ([ID], [Name], [Language], [DepartmentChair], [FacultyID], [CourseCount]) VALUES (1003, N'Hukuk', N'Turkce', 1001, 101, 44)\n" +
                                "INSERT [dbo].[DepartmentTable] ([ID], [Name], [Language], [DepartmentChair], [FacultyID], [CourseCount]) VALUES (1004, N'Matematik ve Fen Bilimleri Eğitim', N'Turkce', 1006, 102, 33)\n" +
                                "INSERT [dbo].[DepartmentTable] ([ID], [Name], [Language], [DepartmentChair], [FacultyID], [CourseCount]) VALUES (1005, N'Temel Eğitim', N'Turkce', 1006, 102, 26)\n" +
                                "INSERT [dbo].[DepartmentTable] ([ID], [Name], [Language], [DepartmentChair], [FacultyID], [CourseCount]) VALUES (1006, N'Türkçe ve Sosyal Bilimler Eğitimi', N'Turkce', 1007, 102, 28)\n" +
                                "INSERT [dbo].[DepartmentTable] ([ID], [Name], [Language], [DepartmentChair], [FacultyID], [CourseCount]) VALUES (1007, N'İktisat', N'Ingilizce', 1008, 103, 30)\n" +
                                "INSERT [dbo].[DepartmentTable] ([ID], [Name], [Language], [DepartmentChair], [FacultyID], [CourseCount]) VALUES (1008, N'İşletme', N'Ingilizce', 1008, 103, 22)\n" +
                                "INSERT [dbo].[DepartmentTable] ([ID], [Name], [Language], [DepartmentChair], [FacultyID], [CourseCount]) VALUES (1009, N'Uluslararası Ticaret ve Finansman', N'Ingilizce', 1009, 103, 20)\n" +
                                "SET IDENTITY_INSERT [dbo].[DepartmentTable] OFF";
                        statement.executeUpdate(sqlScript);

                        updateProgress(75,100);

                        sqlScript = "SET IDENTITY_INSERT [dbo].[LeftTeacherTable] ON \n" +
                                "INSERT [dbo].[LeftTeacherTable] ([ID], [info]) VALUES (1, N'An existing teacher, ID  = 1000, Yahya Sirin is deleted at Dec 23 2020 11:06AM')\n" +
                                "INSERT [dbo].[LeftTeacherTable] ([ID], [info]) VALUES (2, N'An existing teacher, ID  = 1006, Temel Temel is deleted at Dec 24 2020  5:41PM')\n" +
                                "SET IDENTITY_INSERT [dbo].[LeftTeacherTable] OFF";
                        statement.executeUpdate(sqlScript);

                        updateProgress(78,100);

                        sqlScript = "SET IDENTITY_INSERT [dbo].[TeacherTable] ON \n" +
                                "INSERT [dbo].[TeacherTable] ([ID], [Name], [Surname], [Email], [Title], [Password], [DepartmentID], [RegisteredDate]) VALUES (1000, N'Ali', N'HAMITOGLU', N'ali.hamitoglu@izu.edu.tr', N'Dr.', N'Kremal24', 1000, CAST(N'2010-12-13T15:46:19.610' AS DateTime))\n" +
                                "INSERT [dbo].[TeacherTable] ([ID], [Name], [Surname], [Email], [Title], [Password], [DepartmentID], [RegisteredDate]) VALUES (1001, N'Resul', N'TANYILDIZI', N'resul.tanyildizi@izu.edu.tr', N'Prof.', N'Terelm67', 1003, CAST(N'2008-12-19T14:01:20.350' AS DateTime))\n" +
                                "INSERT [dbo].[TeacherTable] ([ID], [Name], [Surname], [Email], [Title], [Password], [DepartmentID], [RegisteredDate]) VALUES (1002, N'Seda', N'UYGUN', N'seda.uygun@izu.edu.tr', N'Dr.', N'Muhtre64', 1004, CAST(N'2019-12-13T15:46:19.610' AS DateTime))\n" +
                                "INSERT [dbo].[TeacherTable] ([ID], [Name], [Surname], [Email], [Title], [Password], [DepartmentID], [RegisteredDate]) VALUES (1003, N'Burak', N'CALISKAN', N'burak.caliskan@izu.edu.tr', N'Prof.', N'Kelom23', 1008, CAST(N'2019-12-19T14:01:20.350' AS DateTime))\n" +
                                "INSERT [dbo].[TeacherTable] ([ID], [Name], [Surname], [Email], [Title], [Password], [DepartmentID], [RegisteredDate]) VALUES (1004, N'Derya', N'Akat', N'derya.akat@izu.edu.tr', N'Dr.', N'Efeom73', 1000, CAST(N'2018-12-19T14:01:20.350' AS DateTime))\n" +
                                "INSERT [dbo].[TeacherTable] ([ID], [Name], [Surname], [Email], [Title], [Password], [DepartmentID], [RegisteredDate]) VALUES (1005, N'Sevgim', N'SUCUK', N'sevgim.sucuk@izu.edu.tr', N'Dr.', N'trease35', 1001, CAST(N'2006-12-13T15:46:19.610' AS DateTime))\n" +
                                "INSERT [dbo].[TeacherTable] ([ID], [Name], [Surname], [Email], [Title], [Password], [DepartmentID], [RegisteredDate]) VALUES (1006, N'Ceylan', N'DOGAN', N'ceylan.dogan@izu.edu.tr', N'Prof.', N'erqwet99', 1004, CAST(N'2005-12-19T14:01:20.350' AS DateTime))\n" +
                                "INSERT [dbo].[TeacherTable] ([ID], [Name], [Surname], [Email], [Title], [Password], [DepartmentID], [RegisteredDate]) VALUES (1007, N'Nilay', N'KORKUT', N'nilay.korkut@izu.edu.tr', N'Dr.', N'fererr12', 1006, CAST(N'2011-12-13T15:46:19.610' AS DateTime))\n" +
                                "INSERT [dbo].[TeacherTable] ([ID], [Name], [Surname], [Email], [Title], [Password], [DepartmentID], [RegisteredDate]) VALUES (1008, N'Resa', N'SOLUK', N'resa.soluk@izu.edu.tr', N'Prof.', N'nutfer55', 1008, CAST(N'2018-12-19T14:01:20.350' AS DateTime))\n" +
                                "INSERT [dbo].[TeacherTable] ([ID], [Name], [Surname], [Email], [Title], [Password], [DepartmentID], [RegisteredDate]) VALUES (1009, N'Selahattin', N'BUYUKTURKMEN', N'selahattin.buyukturkmen@izu.edu.tr', N'Prof.', N'dololo52', 1009, CAST(N'2012-12-19T14:01:20.350' AS DateTime))\n" +
                                "SET IDENTITY_INSERT [dbo].[TeacherTable] OFF";
                        statement.executeUpdate(sqlScript);

                        updateProgress(90,100);

                        sqlScript = "SET IDENTITY_INSERT [dbo].[StudentTable] ON \n" +
                                "INSERT [dbo].[StudentTable] ([ID], [Name], [Surname], [Password], [Email], [DepartmentID], [RegisteredDate], [Semester], [AdvisorID], [GPA]) VALUES (10000, N'Erdem', N'DEMIR', N'Tuyen14', N'demir.erdem@std.izu.edu.tr', 1000, CAST(N'2018-12-19T13:53:52.820' AS DateTime), 0, 1000, 2.5)\n" +
                                "INSERT [dbo].[StudentTable] ([ID], [Name], [Surname], [Password], [Email], [DepartmentID], [RegisteredDate], [Semester], [AdvisorID], [GPA]) VALUES (10002, N'Tayyib', N'BAYRAM', N'Tufen14', N'bayram.tayyib@std.izu.edu.tr', 1000, CAST(N'2018-12-19T13:54:14.807' AS DateTime), 0, 1000, 3)\n" +
                                "INSERT [dbo].[StudentTable] ([ID], [Name], [Surname], [Password], [Email], [DepartmentID], [RegisteredDate], [Semester], [AdvisorID], [GPA]) VALUES (10004, N'Isa', N'ERBAS', N'Tulen24', N'erbas.isa@std.izu.edu.tr', 1001, CAST(N'2017-12-19T13:54:36.023' AS DateTime), 0, 1001, 3.25)\n" +
                                "INSERT [dbo].[StudentTable] ([ID], [Name], [Surname], [Password], [Email], [DepartmentID], [RegisteredDate], [Semester], [AdvisorID], [GPA]) VALUES (10006, N'Nafiz', N'CANITEZ', N'Luten65', N'canitez.nafiz@std.izu.edu.tr', 1008, CAST(N'2020-12-19T13:56:20.687' AS DateTime), 0, 1000, 3)\n" +
                                "INSERT [dbo].[StudentTable] ([ID], [Name], [Surname], [Password], [Email], [DepartmentID], [RegisteredDate], [Semester], [AdvisorID], [GPA]) VALUES (10008, N'Abdullah', N'TURGUT', N'Maten89', N'turgut.abdullah@std.izu.edu.tr', 1001, CAST(N'2019-12-19T15:44:59.183' AS DateTime), 0, 1000, 3)\n" +
                                "INSERT [dbo].[StudentTable] ([ID], [Name], [Surname], [Password], [Email], [DepartmentID], [RegisteredDate], [Semester], [AdvisorID], [GPA]) VALUES (10010, N'Yusuf', N'USLU', N'Zaten89', N'uslu.yusuf@std.izu.edu.tr', 1001, CAST(N'2020-06-19T16:08:42.883' AS DateTime), 0, 1002, 1)\n" +
                                "INSERT [dbo].[StudentTable] ([ID], [Name], [Surname], [Password], [Email], [DepartmentID], [RegisteredDate], [Semester], [AdvisorID], [GPA]) VALUES (10012, N'Emre', N'KILIC', N'Manen41', N'kilic.emre@std.izu.edu.tr', 1002, CAST(N'2020-02-19T16:13:28.947' AS DateTime), 0, 1001, 1.75)\n" +
                                "INSERT [dbo].[StudentTable] ([ID], [Name], [Surname], [Password], [Email], [DepartmentID], [RegisteredDate], [Semester], [AdvisorID], [GPA]) VALUES (10014, N'Hasan', N'CILDIR', N'Huran19', N'cildir.hasan@std.izu.edu.tr', 1003, CAST(N'2020-01-22T20:21:54.997' AS DateTime), 0, 1004, 2)\n" +
                                "INSERT [dbo].[StudentTable] ([ID], [Name], [Surname], [Password], [Email], [DepartmentID], [RegisteredDate], [Semester], [AdvisorID], [GPA]) VALUES (10016, N'Selim', N'GÜLCE', N'Terak67', N'gülce.selim@std.izu.edu.tr', 1003, CAST(N'2018-12-22T20:26:54.047' AS DateTime), 4, 1004, 3.2)\n" +
                                "INSERT [dbo].[StudentTable] ([ID], [Name], [Surname], [Password], [Email], [DepartmentID], [RegisteredDate], [Semester], [AdvisorID], [GPA]) VALUES (10018, N'Sıla', N'KARASATI', N'Lilet55', N'karasati.sila@std.izu.edu.tr', 1004, CAST(N'2017-12-23T10:38:43.137' AS DateTime), 0, 1001, 3)\n" +
                                "INSERT [dbo].[StudentTable] ([ID], [Name], [Surname], [Password], [Email], [DepartmentID], [RegisteredDate], [Semester], [AdvisorID], [GPA]) VALUES (10020, N'Resul', N'ŞEKER', N'Fereb56', N'seker.resul@std.izu.edu.tr', 1007, CAST(N'2018-06-23T10:40:00.457' AS DateTime), 0, 1001, 4)\n" +
                                "INSERT [dbo].[StudentTable] ([ID], [Name], [Surname], [Password], [Email], [DepartmentID], [RegisteredDate], [Semester], [AdvisorID], [GPA]) VALUES (10022, N'Nurettin', N'TANGÜNEŞ', N'Ketan75', N'tangünes.nurettin@std.izu.edu.tr', 1009, CAST(N'2018-03-23T10:48:06.680' AS DateTime), 0, 1005, 4)\n" +
                                "INSERT [dbo].[StudentTable] ([ID], [Name], [Surname], [Password], [Email], [DepartmentID], [RegisteredDate], [Semester], [AdvisorID], [GPA]) VALUES (10024, N'Nazim', N'GÜRPINAR', N'Juter87', N'gurpinar.nazim@std.izu.edu.tr', 1006, CAST(N'2015-12-23T10:51:05.683' AS DateTime), 0, 1006, 3)\n" +
                                "INSERT [dbo].[StudentTable] ([ID], [Name], [Surname], [Password], [Email], [DepartmentID], [RegisteredDate], [Semester], [AdvisorID], [GPA]) VALUES (10026, N'Basak', N'TURAÇ', N'Lopez22', N'turac.basak@std.izu.edu.tr', 1005, CAST(N'2010-12-23T10:54:12.687' AS DateTime), 0, 1006, 3)\n" +
                                "SET IDENTITY_INSERT [dbo].[StudentTable] OFF";
                        statement.executeUpdate(sqlScript);

                        sqlScript = "Create TRIGGER [dbo].[dtStudentTableRegisteredDate]\n" +
                                "ON [dbo].[StudentTable]\n" +
                                "FOR INSERT\n" +
                                "AS\n" +
                                "BEGIN\n" +
                                "Update StudentTable\n" +
                                "Set RegisteredDate = GETDATE()\n" +
                                "WHERE ID = (SELECT ID from inserted)\n" +
                                "END";
                        statement.executeUpdate(sqlScript);


                        updateProgress(100,100);
                        updateTitle("Database created successfully...");
                        Thread.sleep(500);

                    }catch (SQLException sqlException){
                        if (sqlException.getErrorCode() == 1801) {
                            updateTitle("Database already exists...");

                        }
                        else {
                            updateTitle("Error: "+sqlException.getMessage());

                        }
                    }
                    catch(Exception e){
                        updateTitle("Error: "+e.getMessage());
                    }


                    return null;
                }
            };


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
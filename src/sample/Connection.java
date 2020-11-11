package sample;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;
/*
public class Connection {
        public static Connection sampleConnection(){
        Connection connection = null;
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "jdbc:sqlserver://localhost:1433,databaseName=StudentDatabase";
            connection = (Connection) DriverManager.getConnection(url);
        }catch(ClassNotFoundException | SQLException ex){
            Logger.getLogger(Connection.class.getName()).log(level,SEVERE, null,ex);
        }
        return connection;
    }
}
*/
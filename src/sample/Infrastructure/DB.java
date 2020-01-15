package sample.Infrastructure;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * The type Db.
 *
 * @author tha
 */
public class DB {
    /**
     * The constant NOMOREDATA.
     */
    public static final String NOMOREDATA ="|ND|";
    /**
     * The constant con.
     */
    private static Connection con;
    /**
     * The constant ps.
     */
    private static PreparedStatement ps;
    /**
     * The constant rs.
     */
    private static ResultSet rs;
    /**
     * The constant port.
     */
    private static String port;
    /**
     * The constant databaseName.
     */
    private static String databaseName;
    /**
     * The constant userName.
     */
    private static String userName;
    /**
     * The constant password.
     */
    private static String password;
    /**
     * The constant numberOfColumns.
     */
    private static int numberOfColumns;
    /**
     * The constant currentColumnNumber.
     */
    private static int currentColumnNumber=1;

    /**
     * STATES
     */
    private static boolean moreData=false;  // from Resultset
    /**
     * The constant pendingData.
     */
    private static boolean pendingData=false; // from select statement
    /**
     * The constant terminated.
     */
    private static boolean terminated = false;

    /**
     * Instantiates a new Db.
     */
    private DB(){
    }
    /**
     * Static initializer - no object construction
     */
    static {
        Properties props = new Properties();
        String fileName = "db.properties";
        InputStream input;
        try{
            input = new FileInputStream(fileName);
            props.load(input);
            port = props.getProperty("port","1433");
            databaseName = props.getProperty("databaseName");
            userName=props.getProperty("userName", "sa");
            password=props.getProperty("password");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("Database Ready");

        }catch(IOException | ClassNotFoundException e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Connect.
     */
    private static void connect(){
        try {
            con = DriverManager.getConnection("jdbc:sqlserver://localhost:"+port+";databaseName="+databaseName,userName,password);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    /**
     * Disconnect.
     */
    private static void disconnect(){
        try {
            con.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Manual disconnect.
     */
    public static void manualDisconnect(){
        disconnect();
        pendingData=false;
    }

    /**
     * Select sql.
     *
     * @param sql the sql string to be executed in SQLServer
     */
    public static void selectSQL(String sql){
        if (terminated){
            System.exit(0);
        }
        try{
            if (ps!=null){
                ps.close();
            }
            if (rs!=null){
                rs.close();
            }
            connect();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            pendingData=true;
            moreData = rs.next();
            ResultSetMetaData rsmd = rs.getMetaData();
            numberOfColumns = rsmd.getColumnCount();
        }catch (Exception e){
            System.err.println("Error in the sql parameter, please test this in SQLServer first");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Get display data string.
     *
     * @return The next single value (formatted) from previous select
     */
    public static String getDisplayData(){
        if (terminated){
            System.exit(0);
        }
        if (!pendingData){
            terminated=true;
            throw new RuntimeException("ERROR! No previous select, communication with the database is lost!");
        }else if (!moreData){
            disconnect();
            pendingData=false;
            return NOMOREDATA;
        }else {
            return getNextValue(true);
        }
    }

    /**
     * Get number of columns int.
     *
     * @return the int
     */
    public static int getNumberOfColumns(){
        return numberOfColumns;
    }

    /**
     * Get data string.
     *
     * @return The next single value (without formatting) from previous select
     */
    public static String getData(){
        if (terminated){
            System.exit(0);
        }
        if (!pendingData){
            terminated=true;
            throw new RuntimeException("ERROR! No previous select, communication with the database is lost!");
        }else if (!moreData){
            disconnect();
            pendingData=false;
            return NOMOREDATA;
        }else {
            return getNextValue(false).trim();
        }
    }

    /**
     * Get next value string.
     *
     * @param view boolean
     * @return the value as String
     */
    private static String getNextValue(boolean view){
        StringBuilder value= new StringBuilder();
        try{
            value.append(rs.getString(currentColumnNumber));
            if (currentColumnNumber>=numberOfColumns){
                currentColumnNumber=1;
                if (view){
                    value.append("\n");
                }
                moreData = rs.next();
            }else{
                if (view){
                    value.append(" ");
                }
                currentColumnNumber++;
            }
        }catch (SQLException e){
            System.err.println(e.getMessage());
        }
        return value.toString();
    }

    /**
     * Insert sql boolean.
     *
     * @param sql the sql
     * @return the boolean
     */
    public static boolean insertSQL(String sql){
        return executeUpdate(sql);
    }

    /**
     * Update sql boolean.
     *
     * @param sql the sql
     * @return the boolean
     */
    public static boolean updateSQL(String sql){
        return executeUpdate(sql);
    }

    /**
     * Delete sql boolean.
     *
     * @param sql the sql
     * @return the boolean
     */
    public static boolean deleteSQL(String sql){
        return executeUpdate(sql);
    }

    /**
     * Execute update boolean.
     *
     * @param sql the sql
     * @return the boolean
     */
    private static boolean executeUpdate(String sql){
        if (terminated){
            System.exit(0);
        }
        if (pendingData){
            terminated=true;
            throw new RuntimeException("ERROR! There were pending data from previous select, communication with the database is lost! ");
        }
        try{
            if (ps!=null){
                ps.close();
            }
            connect();
            ps = con.prepareStatement(sql);
            int rows = ps.executeUpdate();
            ps.close();
            if (rows>0){
                return true;
            }
        }catch (RuntimeException | SQLException e){
            System.err.println(e.getMessage());
        } finally{
            disconnect();
        }
        return false;
    }
}

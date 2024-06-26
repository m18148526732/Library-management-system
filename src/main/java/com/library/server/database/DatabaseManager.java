package main.java.com.library.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    private static final String URL = "jdbc:mysql://localhost:3306/testlibrary";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    private static final String DROP_USERS_TABLE = "DROP TABLE IF EXISTS Users";
    private static final String DROP_BOOKS_TABLE = "DROP TABLE IF EXISTS Books";
    private static final String DROP_BORROW_RECORDS_TABLE = "DROP TABLE IF EXISTS BorrowRecords";
    private static final String DROP_RETURN_RECORDS_TABLE = "DROP TABLE IF EXISTS ReturnRecords";
    private static final String DROP_FAVORITES_TABLE = "DROP TABLE IF EXISTS FavoriteRecords";
    private static final String DROP_BORROW_TRIGGER = "DROP TRIGGER IF EXISTS after_borrow_insert";
    private static final String DROP_RETURN_TRIGGER = "DROP TRIGGER IF EXISTS after_return_insert";

    private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS Users ("
            + "userID VARCHAR(255) PRIMARY KEY,"
            + "email VARCHAR(255) NOT NULL,"
            + "phone VARCHAR(255) NOT NULL,"
            + "username VARCHAR(255) UNIQUE NOT NULL,"
            + "password VARCHAR(255) NOT NULL,"
            + "role VARCHAR(50) NOT NULL,"
            + "deleted BOOLEAN DEFAULT FALSE"
            + ")";

    private static final String CREATE_BOOKS_TABLE = "CREATE TABLE IF NOT EXISTS Books ("
            + "bookID VARCHAR(255) PRIMARY KEY,"
            + "title VARCHAR(255) NOT NULL,"
            + "author VARCHAR(255) NOT NULL,"
            + "ISBN VARCHAR(255) UNIQUE NOT NULL,"
            + "status VARCHAR(50) NOT NULL,"
            + "count INTEGER NOT NULL,"
            + "availableCount INTEGER NOT NULL,"
            + "publisher VARCHAR(255) NOT NULL,"
            + "introduction VARCHAR(255) NOT NULL"
            + ")";

    private static final String CREATE_BORROW_RECORDS_TABLE = "CREATE TABLE IF NOT EXISTS BorrowRecords ("
            + "borrowID VARCHAR(255) PRIMARY KEY,"
            + "userID VARCHAR(255) NOT NULL,"
            + "bookID VARCHAR(255) NOT NULL,"
            + "borrowDate TIMESTAMP NOT NULL,"
            + "returnDate TIMESTAMP,"
            + "returned BOOLEAN NOT NULL,"
            + "FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE,"
            + "FOREIGN KEY (bookID) REFERENCES Books(bookID)"
            + ")";

    private static final String CREATE_RETURN_RECORDS_TABLE = "CREATE TABLE IF NOT EXISTS ReturnRecords ("
            + "returnID VARCHAR(255) PRIMARY KEY,"
            + "borrowID VARCHAR(255) NOT NULL,"
            + "returnDate TIMESTAMP NOT NULL,"
            + "FOREIGN KEY (borrowID) REFERENCES BorrowRecords(borrowID) ON DELETE CASCADE"
            + ")";

    private static final String CREATE_FAVORITES_TABLE = "CREATE TABLE IF NOT EXISTS FavoriteRecords ("
            + "favoriteID VARCHAR(255) PRIMARY KEY,"
            + "userID VARCHAR(255) NOT NULL,"
            + "bookID VARCHAR(255) NOT NULL,"
            + "UNIQUE KEY unique_favorite (userID, bookID),"
            + "FOREIGN KEY (userID) REFERENCES Users(userID),"
            + "FOREIGN KEY (bookID) REFERENCES Books(bookID)"
            + ")";

    private static final String CREATE_BORROW_TRIGGER = "CREATE TRIGGER after_borrow_insert "
            + "AFTER INSERT ON BorrowRecords "
            + "FOR EACH ROW "
            + "BEGIN "
            + "    UPDATE Books SET availableCount = availableCount - 1 "
            + "    WHERE bookID = NEW.bookID;"
            + "    IF (SELECT availableCount FROM Books WHERE bookID = NEW.bookID) <= 0 THEN "
            + "        UPDATE Books SET status = 'unavailable' "
            + "        WHERE bookID = NEW.bookID; "
            + "    END IF; "
            + "END;";

    private static final String CREATE_RETURN_TRIGGER = "CREATE TRIGGER after_return_insert "
            + "AFTER INSERT ON ReturnRecords "
            + "FOR EACH ROW "
            + "BEGIN "
            + "    UPDATE Books SET availableCount = availableCount + 1 "
            + "    WHERE bookID = (SELECT bookID FROM BorrowRecords WHERE borrowID = NEW.borrowID);"
            + "    IF (SELECT availableCount FROM Books WHERE bookID = (SELECT bookID FROM BorrowRecords WHERE borrowID = NEW.borrowID)) > 0 THEN "
            + "        UPDATE Books SET status = 'available' "
            + "        WHERE bookID = (SELECT bookID FROM BorrowRecords WHERE borrowID = NEW.borrowID); "
            + "    END IF; "
            + "END;";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void createTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(CREATE_USERS_TABLE);
            stmt.execute(CREATE_BOOKS_TABLE);
            stmt.execute(CREATE_BORROW_RECORDS_TABLE);
            stmt.execute(CREATE_RETURN_RECORDS_TABLE);
            stmt.execute(CREATE_FAVORITES_TABLE);

            // 创建触发器
            stmt.execute(CREATE_BORROW_TRIGGER);
            stmt.execute(CREATE_RETURN_TRIGGER);

            System.out.println("Tables and triggers created successfully");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error", e);
            System.err.println("Error creating tables/triggers: " + e.getMessage());
        }
    }

    public static void dropTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(DROP_BORROW_TRIGGER);
            stmt.execute(DROP_RETURN_TRIGGER);
            stmt.execute(DROP_FAVORITES_TABLE);
            stmt.execute(DROP_RETURN_RECORDS_TABLE);
            stmt.execute(DROP_BORROW_RECORDS_TABLE);
            stmt.execute(DROP_BOOKS_TABLE);
            stmt.execute(DROP_USERS_TABLE);
            System.out.println("Tables and triggers dropped successfully");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error", e);
            System.err.println("Error dropping tables/triggers: " + e.getMessage());
        }
    }

    public static void resetDatabase() {
        dropTables();
        createTables();
    }

    public static void main(String[] args) {
        createTables();
    }
}

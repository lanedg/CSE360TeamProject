package database;

import java.sql.*;
import entityClasses.LessonsLearned;

public class LessonsLearnedHelper {
	// DB driver name and url
	static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/FoundationDatabase";
    // DB credentials
    static final String USER = "sa";
    static final String PASS = "";
    
    //Establish Connection with Database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
    
    public static void initializeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS lessons_learned (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "author_id VARCHAR(255), " +
                     "lesson_title VARCHAR(255), " +
                     "lesson_title_locked BOOLEAN, " +
                     "experience_description TEXT, " +
                     "experience_description_locked BOOLEAN, " +
                     "hours_spent DOUBLE, " +
                     "hours_spent_locked BOOLEAN, " +
                     "effort_used VARCHAR(255), " +
                     "effort_used_locked BOOLEAN" +
                     ")";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Database table 'lessons_learned' initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database table: " + e.getMessage());
        }
    }
    
    public static void createLesson(LessonsLearned lesson) {
    	String sql = "INSERT INTO lessons_learned (author_id, lesson_title, lesson_title_locked, " +
    				 "experience_description, experience_description_locked, hours_spent, hours_spent_locked, " +
    				 "effort_used, effort_used_locked ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    	
    	try (Connection conn = getConnection();
    		PreparedStatement pstmt = conn.prepareStatement(sql)) {
    		
    		pstmt.setString(1, lesson.getAuthorID());
            pstmt.setString(2, lesson.getLessonTitle());
            pstmt.setBoolean(3, lesson.isLessonTitleLocked());
            pstmt.setString(4, lesson.getExperienceDescription());
            pstmt.setBoolean(5, lesson.isExperienceDescriptionLocked());
            pstmt.setDouble(6, lesson.getHoursSpent());
            pstmt.setBoolean(7, lesson.isHoursSpentLocked());
            pstmt.setString(8, lesson.getEffortUsed());
            pstmt.setBoolean(9, lesson.isEffortUsedLocked());
            
            pstmt.executeUpdate();
            System.out.println("Lesson succesfully saved to the database.");
    		
    	} catch (SQLException e) {
    		System.err.println("Failed to insert lesson: " + e.getMessage());
    	}
    }
    
    public static int getLessonCount() {
    	String sql = "SELECT COUNT(*) FROM lessons_learned";
    	try (Connection conn = getConnection();
    		 Statement stmt = conn.createStatement();
    		 ResultSet rs = stmt.executeQuery(sql)) {
    		if (rs.next()) {
    			return rs.getInt(1);
    		}    	
    	} catch (SQLException e) {
    		System.err.println("Error counting lessons: " + e.getMessage());
    	}
    	return 0;
    }
}

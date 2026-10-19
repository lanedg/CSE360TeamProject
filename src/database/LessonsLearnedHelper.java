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


// Below - Beck

/*******
 * <p> Method: boolean isLessonLocked(int id) </p>
 * 
 * <p> Description: Determine whether any field of the specified lesson has been locked by
 *		another role.  A contributor is not allowed to delete a lesson that is locked or that
 *		has a locked field, so this check is made before any deletion is attempted.</p>
 * 
 * @param id is the unique id of the lesson being checked
 * 
 * @return true if one or more fields of the lesson are locked, else false
 * 
 */
public static boolean isLessonLocked(int id) {
    String sql = "SELECT lesson_title_locked, experience_description_locked, " +
                 "hours_spent_locked, effort_used_locked FROM lessons_learned WHERE id = ?";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            // The lesson may not be deleted if any one of its fields has been locked
            return rs.getBoolean("lesson_title_locked")
                    || rs.getBoolean("experience_description_locked")
                    || rs.getBoolean("hours_spent_locked")
                    || rs.getBoolean("effort_used_locked");
        }
    } catch (SQLException e) {
        System.err.println("Error checking lesson lock state: " + e.getMessage());
    }
    return false;        // No such lesson, or an error occurred
}


/*******
 * <p> Method: boolean deleteLesson(int id) </p>
 * 
 * <p> Description: Remove the specified lesson learned from the database.  The deletion is
 *		refused when the lesson is locked or has a locked field, as the Contributor user
 *		story does not permit a contributor to delete such a lesson.</p>
 * 
 * @param id is the unique id of the lesson to be removed
 * 
 * @return true if the lesson was removed, else false
 * 
 */
public static boolean deleteLesson(int id) {
    // A locked lesson may not be deleted by its contributor
    if (isLessonLocked(id)) {
        System.out.println("Lesson " + id + " is locked and cannot be deleted.");
        return false;
    }

    String sql = "DELETE FROM lessons_learned WHERE id = ?";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, id);

        // executeUpdate returns the number of rows removed, so a value above zero means
        // a lesson with this id was found and deleted
        if (pstmt.executeUpdate() > 0) {
            System.out.println("Lesson successfully deleted from the database.");
            return true;
        }
        System.out.println("No lesson found with id " + id + ".");
    } catch (SQLException e) {
        System.err.println("Failed to delete lesson: " + e.getMessage());
    }
    return false;
}
}


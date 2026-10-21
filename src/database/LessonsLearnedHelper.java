package database;

import java.util.ArrayList;
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
    
    public static boolean createLesson(LessonsLearned lesson) {
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
            return true;
    		
    	} catch (SQLException e) {
    		System.err.println("Failed to insert lesson: " + e.getMessage());
    		return false;
    	}
    }
    
    public static boolean updateLesson(LessonsLearned lesson) {
        String sql = "UPDATE lessons_learned SET author_id = ?, lesson_title = ?, lesson_title_locked = ?, " +
                     "experience_description = ?, experience_description_locked = ?, hours_spent = ?, hours_spent_locked = ?, " +
                     "effort_used = ?, effort_used_locked = ? WHERE id = ?";
        
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
            pstmt.setInt(10, lesson.getId()); // needs the ID to locate the specific record
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Lesson successfully updated in the database.");
                return true;
            } else {
                System.out.println("Lesson ID not found. Update failed.");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Failed to update lesson: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean updateContributorLesson(entityClasses.User user, LessonsLearned lesson, String title, String experience, double hours, String effort) throws SQLException {
    	if (user == null || lesson == null || !user.getNewRole1()) return false;
    	String[] columns = {"lesson_title", "experience_description", "hours_spent", "effort_used"};
    	boolean[] locked = {lesson.isLessonTitleLocked(), lesson.isExperienceDescriptionLocked(), lesson.isHoursSpentLocked(), lesson.isEffortUsedLocked()};
    	Object[] values = {title, experience, hours, effort};
    	ArrayList<String> fields = new ArrayList<>();
    	String locks = "";
    	for (int i = 0; i < columns.length; i++) {
    		if (!locked[i]) {
    			fields.add(columns[i] + " = ?");
    			locks += " AND " + columns[i] + "_locked IS NOT TRUE";
    		}
    	}
    	if (fields.isEmpty()) return false;
    	String sql = "UPDATE lessons_learned SET " + String.join(", ", fields) + " WHERE id = ? AND author_id = ? AND author_id IN (SELECT userName FROM userDB WHERE newRole1 = TRUE)" + locks;
    	try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
    		int n = 1;
    		for (int i = 0; i < values.length; i++) {
    			if (!locked[i]) stmt.setObject(n++, values[i]);
    		}
    		stmt.setInt(n++, lesson.getId());
    		stmt.setString(n, user.getUserName());
    		return stmt.executeUpdate() == 1;
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
    
    public static entityClasses.LessonsLearnedList getLessonsForAuthor(String authorId) {
        entityClasses.LessonsLearnedList userLessons = new entityClasses.LessonsLearnedList();
        String sql = "SELECT * FROM lessons_learned WHERE author_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, authorId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                entityClasses.LessonsLearned lesson = new entityClasses.LessonsLearned(
                    rs.getString("author_id"), 
                    rs.getString("lesson_title")
                );
                lesson.setId(rs.getInt("id"));
                lesson.setLessonTitleLocked(rs.getBoolean("lesson_title_locked"));
                lesson.setExperienceDescription(rs.getString("experience_description"));
                lesson.setExperienceDescriptionLocked(rs.getBoolean("experience_description_locked"));
                lesson.setHoursSpent(rs.getDouble("hours_spent"));
                lesson.setHoursSpentLocked(rs.getBoolean("hours_spent_locked"));
                lesson.setEffortUsed(rs.getString("effort_used"));
                lesson.setEffortUsedLocked(rs.getBoolean("effort_used_locked"));
                
                userLessons.addLesson(lesson);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving lessons for author: " + e.getMessage());
        }
        
        return userLessons;
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
    
    public static LessonsLearned getLesson(int id) throws SQLException {
        String sql = "SELECT * FROM lessons_learned WHERE id = ?";
    try (Connection cnct = getConnection();
         PreparedStatement statement = cnct.prepareStatement(sql)) {

        statement.setInt(1, id);

        try (ResultSet set = statement.executeQuery()) {
            if (!set.next()) { return null; }

            LessonsLearned lesson = new LessonsLearned(set.getString("author_id"), set.getString("lesson_title"));

            lesson.setId(set.getInt("id"));
            lesson.setLessonTitleLocked(set.getBoolean("lesson_title_locked"));
            lesson.setExperienceDescription(set.getString("experience_description"));
            lesson.setExperienceDescriptionLocked(set.getBoolean("experience_description_locked"));
            lesson.setHoursSpent(set.getDouble("hours_spent"));
            lesson.setHoursSpentLocked(set.getBoolean("hours_spent_locked"));
            lesson.setEffortUsed(set.getString("effort_used"));
            lesson.setEffortUsedLocked(set.getBoolean("effort_used_locked"));

            return lesson;
        }
    }
}

// Id return tester
/*******
 * <p> Method: int getHighestLessonId() </p>
 * 
 * <p> Description: Return the largest lesson id currently in the table.  The testing
 *		automation uses this to find the lesson it has just created, as createLesson does
 *		not return the id the database assigned.</p>
 * 
 * @return the highest lesson id, or 0 when the table is empty
 * 
 */
public static int getHighestLessonId() {
    String sql = "SELECT MAX(id) FROM lessons_learned";
    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        if (rs.next()) {
            return rs.getInt(1);
        }
    } catch (SQLException e) {
        System.err.println("Error finding the highest lesson id: " + e.getMessage());
    }
    return 0;
}


}


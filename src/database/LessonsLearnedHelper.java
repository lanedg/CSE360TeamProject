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
            stmt.execute("ALTER TABLE lessons_learned ADD COLUMN IF NOT EXISTS lesson_locked BOOLEAN DEFAULT FALSE");
            stmt.execute("ALTER TABLE lessons_learned ADD COLUMN IF NOT EXISTS merged_time_effort_stats TEXT");
            stmt.execute("ALTER TABLE lessons_learned ADD COLUMN IF NOT EXISTS stats_visible_to_contributor BOOLEAN DEFAULT FALSE");
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
    
    /**
     * Saves a Contributor's changes to the unlocked fields of their lesson.
     *
     * @param user the Contributor making the changes
     * @param lesson the lesson being edited
     * @param title the new lesson title
     * @param experience the new experience description
     * @param hours the new hours spent
     * @param effort the new effort used
     * @return true if one lesson was updated, false otherwise
     * @throws SQLException if the database update fails
     */
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
	String sql = "SELECT lesson_locked, lesson_title_locked, experience_description_locked, " +
            "hours_spent_locked, effort_used_locked FROM lessons_learned WHERE id = ?";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            // The lesson may not be deleted if any one of its fields has been locked
        	return rs.getBoolean("lesson_locked")
        	        || rs.getBoolean("lesson_title_locked")
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
            lesson.setLessonLocked(set.getBoolean("lesson_locked"));
            lesson.setMergedTimeEffortStats(set.getString("merged_time_effort_stats"));
            lesson.setStatsVisibleToContributor(set.getBoolean("stats_visible_to_contributor"));

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

/***
<p> Method: List getLessonListForAuthor(String authorID) </p>
<p> Description: Generate a list of Strings, one for each lesson the specified contributor
has added, in the form "id - title", starting with a prompt at the head of the list.
Only this contributor's lessons are listed, as a contributor is unable to see the
submissions of other contributors.</p>
@param authorID is the username of the contributor whose lessons are to be listed
@return a list of this contributor's lessons
*/
public static java.util.List<String> getLessonListForAuthor(String authorID) {
 java.util.List<String> lessonList = new java.util.ArrayList<String>();
 lessonList.add("<Select a Lesson>");

        String sql = "SELECT id, lesson_title FROM lessons_learned WHERE author_id = ? ORDER BY id";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, authorID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lessonList.add(rs.getInt("id") + " - " + rs.getString("lesson_title"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching the lesson list: " + e.getMessage());
        }
        return lessonList;
    }

/**
 * <p> Method: getAllLessons </p>
 * <p> Description: Retrieves all lessons from lessons_learned for Curator merge and lock views. </p>
 * <p> Validated by: MergedDataTest#testSaveAndRetrieveMergedStats </p>
 * @return ArrayList of all LessonsLearned records
 */
public static ArrayList<LessonsLearned> getAllLessons() {
	ArrayList<LessonsLearned> list = new ArrayList<>();
	String sql = "SELECT * FROM lessons_learned ORDER BY id";

	try (Connection conn = getConnection();
		 PreparedStatement pstmt = conn.prepareStatement(sql);
		 ResultSet rs = pstmt.executeQuery()) {

		while (rs.next()) {
			LessonsLearned lesson = new LessonsLearned(
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
			lesson.setLessonLocked(rs.getBoolean("lesson_locked"));
			lesson.setMergedTimeEffortStats(rs.getString("merged_time_effort_stats"));
			lesson.setStatsVisibleToContributor(rs.getBoolean("stats_visible_to_contributor"));
			list.add(lesson);
		}
	} catch (SQLException e) {
		System.err.println("Error retrieving all lessons: " + e.getMessage());
	}
	return list;
}

/**
 * <p> Method: updateMergedStats </p>
 * <p> Description: Saves merged time/effort statistics and contributor visibility flag for a lesson. </p>
 * <p> Validated by: MergedDataTest#testSaveAndRetrieveMergedStats </p>
 * @param lessonId the target lesson ID
 * @param stats the formatted statistical summary
 * @param visibleToContributor true to make stats visible to the contributor
 * @return true if the record was updated
 */
public static boolean updateMergedStats(int lessonId, String stats, boolean visibleToContributor) {
	String sql = "UPDATE lessons_learned SET merged_time_effort_stats = ?, stats_visible_to_contributor = ? WHERE id = ?";

	try (Connection conn = getConnection();
		 PreparedStatement pstmt = conn.prepareStatement(sql)) {

		pstmt.setString(1, stats);
		pstmt.setBoolean(2, visibleToContributor);
		pstmt.setInt(3, lessonId);
		return pstmt.executeUpdate() > 0;
	} catch (SQLException e) {
		System.err.println("Failed to update merged stats: " + e.getMessage());
		return false;
	}
}

/**
 * <p> Method: updateLessonLocks </p>
 * <p> Description: Updates whole-lesson and per-field lock flags for a lesson.
 * If lessonLocked is true, all individual field locks are also forced true. </p>
 * <p> Validated by: CuratorLockTest#testPersistLessonLocks </p>
 * @param lesson the LessonsLearned object with updated lock states
 * @return true if the record was updated
 */
public static boolean updateLessonLocks(LessonsLearned lesson) {
	// Enforce all field locks when the whole lesson is locked so Contributor SQL checks block every field
	boolean fullLock = lesson.isLessonLocked();
	String sql = "UPDATE lessons_learned SET lesson_locked = ?, lesson_title_locked = ?, " +
				 "experience_description_locked = ?, hours_spent_locked = ?, effort_used_locked = ? WHERE id = ?";

	try (Connection conn = getConnection();
		 PreparedStatement pstmt = conn.prepareStatement(sql)) {

		pstmt.setBoolean(1, fullLock);
		pstmt.setBoolean(2, fullLock || lesson.isLessonTitleLocked());
		pstmt.setBoolean(3, fullLock || lesson.isExperienceDescriptionLocked());
		pstmt.setBoolean(4, fullLock || lesson.isHoursSpentLocked());
		pstmt.setBoolean(5, fullLock || lesson.isEffortUsedLocked());
		pstmt.setInt(6, lesson.getId());
		return pstmt.executeUpdate() > 0;
	} catch (SQLException e) {
		System.err.println("Failed to update lesson locks: " + e.getMessage());
		return false;
	}
}





/*******
 * <p> Method: void initializeSearchTermsTable() </p>
 *
 * <p> Description: Create the table that holds the AI-assisted search terms a curator has
 *		approved for a lesson, if that table does not already exist.
 *
 *		The terms are held in a table of their own rather than as a column on the lesson for
 *		three reasons.  A lesson has many terms, and a repeating group does not fit in a
 *		single column.  A term can be approved or withdrawn without rewriting the lesson row.
 *		And because a term lives outside the lesson, adding or removing one never touches a
 *		field that a curator has locked, so this epic and the locking epic do not interfere
 *		with one another.</p>
 *
 * <p> Validated by: LessonSearchTermsTest.testTableIsCreated </p>
 *
 */
public static void initializeSearchTermsTable() {
    String sql = "CREATE TABLE IF NOT EXISTS lesson_search_terms (" +
                 "id INT AUTO_INCREMENT PRIMARY KEY, " +
                 "lesson_id INT, " +
                 "term VARCHAR(50), " +
                 "added_by VARCHAR(255), " +
                 "ai_suggested BOOLEAN" +
                 ")";

    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement()) {
        stmt.execute(sql);
        System.out.println("Database table 'lesson_search_terms' initialized successfully.");
    } catch (SQLException e) {
        System.err.println("Error initializing search terms table: " + e.getMessage());
    }
}


/*******
 * <p> Method: boolean addSearchTerm(int lessonId, String term, String addedBy,
 *		boolean aiSuggested) </p>
 *
 * <p> Description: Attach a search term to a lesson so that a viewer whose wording differs
 *		from the author's can still find it.  The term is stored trimmed and in lower case so
 *		that a later search does not have to allow for the curator's capitalization, and a
 *		term already held for that lesson is refused rather than stored twice.</p>
 *
 * @param lessonId		is the id of the lesson the term belongs to
 *
 * @param term			is the search term being attached
 *
 * @param addedBy		is the username of the curator approving the term
 *
 * @param aiSuggested	is true when the AI proposed the term, false when the curator typed it
 *
 * @return true if the term was stored, else false
 *
 * <p> Validated by: LessonSearchTermsTest.testAddTerm,
 *		LessonSearchTermsTest.testDuplicateTermRefused </p>
 *
 */
public static boolean addSearchTerm(int lessonId, String term, String addedBy,
        boolean aiSuggested) {

    // An empty term would match every search, so it is refused before the database is touched
    if (term == null || term.trim().isEmpty()) {
        return false;
    }
    String cleanTerm = term.trim().toLowerCase();

    // The length limit matches the column width, so an over-long term is reported rather than
    // being truncated or raising a value-too-long exception from H2
    if (cleanTerm.length() > 50) {
        System.out.println("A search term may not exceed 50 characters.");
        return false;
    }

    if (hasSearchTerm(lessonId, cleanTerm)) {
        System.out.println("That search term is already attached to this lesson.");
        return false;
    }

    String sql = "INSERT INTO lesson_search_terms (lesson_id, term, added_by, ai_suggested) " +
                 "VALUES (?, ?, ?, ?)";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, lessonId);
        pstmt.setString(2, cleanTerm);
        pstmt.setString(3, addedBy);
        pstmt.setBoolean(4, aiSuggested);

        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error adding a search term: " + e.getMessage());
        return false;
    }
}


/*******
 * <p> Method: boolean hasSearchTerm(int lessonId, String term) </p>
 *
 * <p> Description: Determine whether a lesson already holds the specified search term.  The
 *		curator is shown the terms already attached before new suggestions are requested, but
 *		this check is made in the database as well, because the AI may propose a term the
 *		curator approved during an earlier session.</p>
 *
 * @param lessonId	is the id of the lesson being checked
 *
 * @param term		is the search term being looked for, already trimmed and in lower case
 *
 * @return true if the lesson already holds that term, else false
 *
 * <p> Validated by: LessonSearchTermsTest.testDuplicateTermRefused </p>
 *
 */
public static boolean hasSearchTerm(int lessonId, String term) {
    String sql = "SELECT COUNT(*) FROM lesson_search_terms WHERE lesson_id = ? AND term = ?";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, lessonId);
        pstmt.setString(2, term);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (SQLException e) {
        System.err.println("Error checking for a search term: " + e.getMessage());
    }
    return false;
}


/*******
 * <p> Method: List getSearchTermsForLesson(int lessonId) </p>
 *
 * <p> Description: Fetch the search terms currently attached to a lesson, in the order they
 *		were approved, so the curator can see what the lesson already carries before asking
 *		the AI for more.</p>
 *
 * @param lessonId is the id of the lesson whose terms are wanted
 *
 * @return the list of terms attached to that lesson, empty when it has none
 *
 * <p> Validated by: LessonSearchTermsTest.testGetTermsForLesson </p>
 *
 */
public static java.util.List<String> getSearchTermsForLesson(int lessonId) {
    java.util.List<String> termList = new ArrayList<String>();
    String sql = "SELECT term FROM lesson_search_terms WHERE lesson_id = ? ORDER BY id";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, lessonId);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            termList.add(rs.getString("term"));
        }
    } catch (SQLException e) {
        System.err.println("Error fetching the search terms: " + e.getMessage());
    }
    return termList;
}


/*******
 * <p> Method: boolean removeSearchTerm(int lessonId, String term) </p>
 *
 * <p> Description: Withdraw a search term from a lesson, so a term that proves unhelpful no
 *		longer causes the lesson to be returned by a search.  The term is matched in the same
 *		trimmed, lower-case form in which it was stored.</p>
 *
 * @param lessonId	is the id of the lesson the term belongs to
 *
 * @param term		is the search term to be withdrawn
 *
 * @return true if a term was removed, else false
 *
 * <p> Validated by: LessonSearchTermsTest.testRemoveTerm </p>
 *
 */
public static boolean removeSearchTerm(int lessonId, String term) {
    if (term == null) {
        return false;
    }
    String sql = "DELETE FROM lesson_search_terms WHERE lesson_id = ? AND term = ?";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, lessonId);
        pstmt.setString(2, term.trim().toLowerCase());

        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error removing a search term: " + e.getMessage());
        return false;
    }
}


/*******
 * <p> Method: boolean removeAllSearchTermsForLesson(int lessonId) </p>
 *
 * <p> Description: Remove every search term attached to a lesson.  This is called when a
 *		lesson is deleted: the terms are held in their own table, so without this the rows
 *		would be left behind with no lesson to point at, and a later lesson that was given the
 *		same id by the database would inherit them.</p>
 *
 * @param lessonId is the id of the lesson whose terms are to be removed
 *
 * @return true if one or more terms were removed, else false
 *
 * <p> Validated by: LessonSearchTermsTest.testTermsRemovedWithLesson </p>
 *
 */
public static boolean removeAllSearchTermsForLesson(int lessonId) {
    String sql = "DELETE FROM lesson_search_terms WHERE lesson_id = ?";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, lessonId);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error removing the lesson's search terms: " + e.getMessage());
        return false;
    }
}



}


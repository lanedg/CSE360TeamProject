package database;

import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;

/**
 * <p> Title: TestListLessons Class. </p>
 * 
 * <p> Description: This class provides automated testing for the LessonsLearnedList 
 * data structure and the specific database helper methods related to viewing and 
 * searching lists of lessons. It verifies that keyword subset filtering and author 
 * retrieval function correctly.</p>
 * 
 * @author Koushik Sai Chintha
 * @version 1.00
 */

public class TestListLessons {

    /**
     * <p> Method: main(String[] args) </p>
     * 
     * <p> Description: The main entry point for the automated test suite. 
     * It executes individual test methods and outputs the results to the console.</p>
     * 
     * @param args Command line arguments (not used)
     */
	
    public static void main(String[] args) {
        System.out.println("--- Starting Automated Tests for View/List Lessons ---");
        
        testSearchByKeyword();
        testDatabaseRetrieval();
        
        System.out.println("--- Testing Complete ---");
    }

    /**
     * <p> Method: testSearchByKeyword() </p>
     * 
     * <p> Description: Tests the searchByKeyword method in the LessonsLearnedList class.
     * It creates a dummy list, adds test lessons, searches for a specific keyword, 
     * and verifies that the resulting subset contains only the matching lessons.</p>
     */
    
    public static void testSearchByKeyword() {
        System.out.println("\nRunning Test 1: testSearchByKeyword...");
        
        // 1. Setup mock data
        LessonsLearnedList mockList = new LessonsLearnedList();
        
        LessonsLearned lesson1 = new LessonsLearned("user1", "JavaFX UI setup");
        lesson1.setExperienceDescription("Learned how to build buttons.");
        
        LessonsLearned lesson2 = new LessonsLearned("user1", "Database testing");
        lesson2.setExperienceDescription("Writing automated tests in SQL.");
        
        LessonsLearned lesson3 = new LessonsLearned("user1", "Java MVC pattern");
        lesson3.setExperienceDescription("Separating model, view, and controller.");
        
        mockList.addLesson(lesson1);
        mockList.addLesson(lesson2);
        mockList.addLesson(lesson3);
        
        // 2. Execute search
        LessonsLearnedList searchResults = mockList.searchByKeyword("java");
        
        // 3. Verify results (Should find exactly 2: lesson1 and lesson3)
        if (searchResults.getSize() == 2) {
            System.out.println("PASS: searchByKeyword correctly filtered the list.");
        } else {
            System.out.println("FAIL: searchByKeyword returned " + searchResults.getSize() + " results instead of 2.");
        }
    }

    /**
     * <p> Method: testDatabaseRetrieval() </p>
     * 
     * <p> Description: Tests the getLessonsForAuthor method in the LessonsLearnedHelper class.
     * It ensures that lessons associated with a specific author ID are correctly pulled 
     * from the H2 database and formatted into a LessonsLearnedList.</p>
     */
    public static void testDatabaseRetrieval() {
        System.out.println("\nRunning Test 2: testDatabaseRetrieval...");
        
        // 1. Setup: Ensure table exists and add a dummy lesson for a unique test user
        LessonsLearnedHelper.initializeTable();
        String testUser = "testAuthor999";
        LessonsLearned dbLesson = new LessonsLearned(testUser, "Retrieval Test Title");
        dbLesson.setExperienceDescription("Testing database fetch.");
        LessonsLearnedHelper.createLesson(dbLesson);
        
        // 2. Execute retrieval
        LessonsLearnedList retrievedList = LessonsLearnedHelper.getLessonsForAuthor(testUser);
        
        // 3. Verify results
        if (retrievedList.getSize() > 0 && retrievedList.getLessonsList().get(0).getAuthorID().equals(testUser)) {
            System.out.println("PASS: getLessonsForAuthor successfully retrieved data from the DB.");
        } else {
            System.out.println("FAIL: getLessonsForAuthor failed to fetch the correct records.");
        }
    }
}
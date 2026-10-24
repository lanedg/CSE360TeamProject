package entityClasses;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: LessonsLearnedList Class. </p>
 * 
 * <p> Description: A collection class that manages a list of LessonsLearned objects. 
 * It provides methods to add, remove, retrieve, and search the list by keyword. </p>
 * 
 * @author Koushik Sai Chintha
 * 
 * @version 1.00    2026-10-24 Initial version
 */

public class LessonsLearnedList {
    private List<LessonsLearned> lessonsList;

    /**********
     * <p> Title: LessonsLearnedList Default Constructor </p>
     * <p> Description: Initializes an empty list to store lessons. </p>
     */
    
    public LessonsLearnedList() {
        this.lessonsList = new ArrayList<>();
    }

    /**********
     * <p> Title: LessonsLearnedList Constructor </p>
     * <p> Description: Initializes the object with an existing list of lessons. </p>
     * @param initialList The initial list of LessonsLearned objects
     */
    
    public LessonsLearnedList(List<LessonsLearned> initialList) {
        this.lessonsList = initialList;
    }

    /**********
     * <p> Title: addLesson </p>
     * <p> Description: Adds a new lesson to the list. </p>
     * @param lesson The lesson to add
     */
    
    public void addLesson(LessonsLearned lesson) {
        this.lessonsList.add(lesson);
    }

    /**********
     * <p> Title: removeLesson </p>
     * <p> Description: Removes a specific lesson from the list. </p>
     * @param lesson The lesson to remove
     */
    
    public void removeLesson(LessonsLearned lesson) {
        this.lessonsList.remove(lesson);
    }

    /**********
     * <p> Title: getLessonsList </p>
     * <p> Description: Retrieves the complete list of lessons. </p>
     * @return The List of LessonsLearned objects
     */
    
    public List<LessonsLearned> getLessonsList() {
        return this.lessonsList;
    }

    /**********
     * <p> Title: getSize </p>
     * <p> Description: Gets the total number of lessons currently in the list. </p>
     * @return The integer size of the list
     */
    
    public int getSize() {
        return this.lessonsList.size();
    }

    /**********
     * <p> Title: searchByKeyword </p>
     * <p> Description: Creates a subset of lessons that contain a specific keyword 
     * in their title or experience description. </p>
     * 
     * @param keyword The search term entered by the user
     * @return A new LessonsLearnedList containing only the matches
     */
    
    public LessonsLearnedList searchByKeyword(String keyword) {
        LessonsLearnedList subsetList = new LessonsLearnedList();
        if (keyword == null || keyword.trim().isEmpty()) {
            return this; // Return full list if there is no keyword
        }

        String searchStr = keyword.toLowerCase();
        for (LessonsLearned lesson : lessonsList) {
            boolean titleMatch = lesson.getLessonTitle() != null && 
                                 lesson.getLessonTitle().toLowerCase().contains(searchStr);
            boolean descMatch = lesson.getExperienceDescription() != null && 
                                lesson.getExperienceDescription().toLowerCase().contains(searchStr);
            
            if (titleMatch || descMatch) {
                subsetList.addLesson(lesson);
            }
        }
        return subsetList;
    }
}

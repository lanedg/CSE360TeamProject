package guiListLessons;

import entityClasses.LessonsLearnedList;

/*******
 * <p> Title: ControllerListLessons Class. </p>
 * 
 * <p> Description: The Java/FX-based controller for the View/List Lessons feature.
 * This class handles the actions triggered by the user interacting with the GUI widgets
 * defined in the ViewListLessons class, such as filtering the list, launching the edit window,
 * and navigating back to the home page. </p>
 * 
 * @author Koushik Sai Chintha
 * 
 * @version 1.00    2026-10-24 Initial version
 */
public class ControllerListLessons {

    /**********
     * <p> Method: performSearch(String keyword) </p>
     * 
     * <p> Description: Filters the user's lessons based on a search keyword. It queries the 
     * full list for matches in the title or description and updates the list view with the results. </p>
     * 
     * @param keyword The string entered by the user in the search field
     */
	
    protected static void performSearch(String keyword) {
        LessonsLearnedList filteredList = ViewListLessons.fullList.searchByKeyword(keyword);
        ViewListLessons.updateListView(filteredList);
    }

    /**********
     * <p> Method: performEdit(String selectedLesson) </p>
     * 
     * <p> Description: Parses the selected lesson string from the GUI to extract the lesson ID, 
     * invokes the Edit Lesson GUI for the user to make changes, and refreshes the list view 
     * from the database once the edit window is closed to display any updated data. </p>
     * 
     * @param selectedLesson The string representation of the lesson selected from the UI list view
     */
    
    protected static void performEdit(String selectedLesson) {
    	if (selectedLesson != null) {
            String idString = selectedLesson.split(" ")[1]; 
            int lessonId = Integer.parseInt(idString);
            
            guiLessonEdit.ViewLesson.editLessonGUI(ViewListLessons.theStage, ViewListLessons.theUser, lessonId);
            ViewListLessons.fullList = database.LessonsLearnedHelper.getLessonsForAuthor(ViewListLessons.theUser.getUserName());
            ViewListLessons.updateListView(ViewListLessons.fullList);
            
        } else {
            System.out.println("Please select a lesson to edit.");
        }
    }

    /**********
     * <p> Method: performBack() </p>
     * 
     * <p> Description: Returns the user to the Contributor Home Page by closing the 
     * list view and reloading the Role 1 Home Page GUI. </p>
     */
    
    protected static void performBack() {
        // Returns the user to the Contributor Home Page (Update package name if they renamed guiRole1)
        guiRole1.ViewRole1Home.displayRole1Home(ViewListLessons.theStage, ViewListLessons.theUser);
    }
}
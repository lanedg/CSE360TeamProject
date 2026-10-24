package guiListLessons;

import entityClasses.LessonsLearnedList;

public class ControllerListLessons {

    protected static void performSearch(String keyword) {
        LessonsLearnedList filteredList = ViewListLessons.fullList.searchByKeyword(keyword);
        ViewListLessons.updateListView(filteredList);
    }

    protected static void performEdit(String selectedLesson) {
        if (selectedLesson != null) {
            String idString = selectedLesson.split(" ")[1]; 
            int lessonId = Integer.parseInt(idString);
            
            System.out.println("Ready to call Farid's edit method for Lesson ID: " + lessonId);
            
        } else {
            System.out.println("Please select a lesson to edit.");
        }
    }

    protected static void performBack() {
        // Returns the user to the Contributor Home Page (Update package name if they renamed guiRole1)
        guiRole1.ViewRole1Home.displayRole1Home(ViewListLessons.theStage, ViewListLessons.theUser);
    }
}
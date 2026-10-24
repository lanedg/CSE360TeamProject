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
            
            guiLessonEdit.ViewLesson.editLessonGUI(ViewListLessons.theStage, ViewListLessons.theUser, lessonId);
            ViewListLessons.fullList = database.LessonsLearnedHelper.getLessonsForAuthor(ViewListLessons.theUser.getUserName());
            ViewListLessons.updateListView(ViewListLessons.fullList);
            
        } else {
            System.out.println("Please select a lesson to edit.");
        }
    }

    protected static void performBack() {
        // Returns the user to the Contributor Home Page (Update package name if they renamed guiRole1)
        guiRole1.ViewRole1Home.displayRole1Home(ViewListLessons.theStage, ViewListLessons.theUser);
    }
}
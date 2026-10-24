package guiContributorCreateLesson;

import database.Database;
import entityClasses.LessonsLearned;
import database.LessonsLearnedHelper;

public class ControllerContributorCreateLesson {
	public ControllerContributorCreateLesson() {
    }
    
    private static Database theDatabase = applicationMain.FoundationsMain.database;		

    protected static void repaintTheWindow() {
        ViewContributorCreateLesson.theRootPane.getChildren().clear();
        
        ViewContributorCreateLesson.theRootPane.getChildren().addAll(
            ViewContributorCreateLesson.label_PageTitle, 
            ViewContributorCreateLesson.label_UserDetails, 
            ViewContributorCreateLesson.button_UpdateThisUser, 
            ViewContributorCreateLesson.line_Separator1,
            ViewContributorCreateLesson.label_LessonTitle, 
            ViewContributorCreateLesson.textfield_LessonTitle,
            ViewContributorCreateLesson.label_ExperienceDesc, 
            ViewContributorCreateLesson.textarea_ExperienceDesc,
            ViewContributorCreateLesson.label_HoursSpent, 
            ViewContributorCreateLesson.textfield_HoursSpent,
            ViewContributorCreateLesson.label_EffortUsed, 
            ViewContributorCreateLesson.textfield_EffortUsed,
            ViewContributorCreateLesson.button_SaveLesson,
            ViewContributorCreateLesson.label_Message,
            ViewContributorCreateLesson.line_Separator4, 
            ViewContributorCreateLesson.button_Return,
            ViewContributorCreateLesson.button_Logout,
            ViewContributorCreateLesson.button_Quit
        );
        
        ViewContributorCreateLesson.theStage.setTitle("CSE 360 Foundation Code: Create Lesson Page");
        ViewContributorCreateLesson.theStage.setScene(ViewContributorCreateLesson.theCreateLessonScene);
        ViewContributorCreateLesson.theStage.show();
    }
    
    protected static void performSaveLesson() {
        String title = ViewContributorCreateLesson.textfield_LessonTitle.getText().trim();
        String description = ViewContributorCreateLesson.textarea_ExperienceDesc.getText().trim();
        String hoursStr = ViewContributorCreateLesson.textfield_HoursSpent.getText().trim();
        String effort = ViewContributorCreateLesson.textfield_EffortUsed.getText().trim();
        String authorId = ViewContributorCreateLesson.theUser.getUserName();
        
        if (title.isEmpty()) {
            ViewContributorCreateLesson.label_Message.setStyle("-fx-text-fill: red;");
            ViewContributorCreateLesson.label_Message.setText("Error: Lesson Title cannot be empty.");
            return;
        }
        
        double hours = 0.0;
        try {
            if (!hoursStr.isEmpty()) {
                hours = Double.parseDouble(hoursStr);
            }
        } catch (NumberFormatException e) {
            ViewContributorCreateLesson.label_Message.setStyle("-fx-text-fill: red;");
            ViewContributorCreateLesson.label_Message.setText("Error: Hours Spent must be a valid number.");
            return;
        }
        
        // Instantiate the lesson using the entity class constructor[cite: 1]
        LessonsLearned newLesson = new LessonsLearned(authorId, title);
        newLesson.setExperienceDescription(description);
        newLesson.setHoursSpent(hours);
        newLesson.setEffortUsed(effort);
        
        // Save lesson to database using the helper
        boolean success = LessonsLearnedHelper.createLesson(newLesson);
        
        
        if (success) {
            ViewContributorCreateLesson.label_Message.setStyle("-fx-text-fill: green;");
            ViewContributorCreateLesson.label_Message.setText("Lesson successfully created!");
            // Clear fields after successful save
            ViewContributorCreateLesson.textfield_LessonTitle.clear();
            ViewContributorCreateLesson.textarea_ExperienceDesc.clear();
            ViewContributorCreateLesson.textfield_HoursSpent.clear();
            ViewContributorCreateLesson.textfield_EffortUsed.clear();
        } else {
            ViewContributorCreateLesson.label_Message.setStyle("-fx-text-fill: red;");
            ViewContributorCreateLesson.label_Message.setText("Error saving lesson to database.");
        }
    }
    
    protected static void performReturn() {
    	guiRole1.ViewRole1Home.displayRole1Home(ViewContributorCreateLesson.theStage, ViewContributorCreateLesson.theUser);
    }
    
    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewContributorCreateLesson.theStage);
    }
    
    protected static void performQuit() {
        System.exit(0);
    }
}

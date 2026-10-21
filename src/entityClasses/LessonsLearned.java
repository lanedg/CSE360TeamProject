package entityClasses;

public class LessonsLearned {
	// Essential information
	private int id; //Unique id for each lesson
	private String authorID;
	private String lessonTitle; // Title of lesson that specifies core information
	private boolean lessonTitleLocked;
	
	//Experience information
	private String experienceDescription; // A string meant for the user to describe waht was done and how it was done
	private boolean experienceDescriptionLocked;
	private double hoursSpent; // Amount of time spent on the task / lesson in hours
	private boolean hoursSpentLocked;
	private String effortUsed; 
	private boolean effortUsedLocked;
	
	public LessonsLearned(String authorID, String lessonTitle) {
		this.authorID = authorID;
		this.lessonTitle = lessonTitle;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAuthorID() {
		return authorID;
	}
	public void setAuthorID(String authorID) {
		this.authorID = authorID;
	}
	public String getLessonTitle() {
		return lessonTitle;
	}
	public void setLessonTitle(String lessonTitle) {
		this.lessonTitle = lessonTitle;
	}
	public boolean isLessonTitleLocked() {
		return lessonTitleLocked;
	}
	public void setLessonTitleLocked(boolean lessonTitleLocked) {
		this.lessonTitleLocked = lessonTitleLocked;
	}
	public String getExperienceDescription() {
		return experienceDescription;
	}
	public void setExperienceDescription(String experienceDescription) {
		this.experienceDescription = experienceDescription;
	}
	public double getHoursSpent() {
		return hoursSpent;
	}
	public void setHoursSpent(double hoursSpent) {
		this.hoursSpent = hoursSpent;
	}
	public String getEffortUsed() {
		return effortUsed;
	}
	public void setEffortUsed(String effortUsed) {
		this.effortUsed = effortUsed;
	}
	public boolean isExperienceDescriptionLocked() {
		return experienceDescriptionLocked;
	}
	public void setExperienceDescriptionLocked(boolean experienceDescriptionLocked) {
		this.experienceDescriptionLocked = experienceDescriptionLocked;
	}
	public boolean isHoursSpentLocked() {
		return hoursSpentLocked;
	}
	public void setHoursSpentLocked(boolean hoursSpentLocked) {
		this.hoursSpentLocked = hoursSpentLocked;
	}
	public boolean isEffortUsedLocked() {
		return effortUsedLocked;
	}
	public void setEffortUsedLocked(boolean effortUsedLocked) {
		this.effortUsedLocked = effortUsedLocked;
	}
	
	
	
}

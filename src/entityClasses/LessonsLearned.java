package entityClasses;

/*******
 * <p> Title: LessonsLearned Class. </p>
 * 
 * <p> Description: An entity class that represents a single Lesson Learned record. 
 * It stores important information like the title and author, as well as experience 
 * details like hours spent and effort used, along with their lock statuses. </p>
 * 
 * @author Koushik Sai Chintha
 * 
 * @version 1.00    2026-10-24 Initial version
 */

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
	
	/**********
	 * <p> Title: LessonsLearned Constructor </p>
	 * 
	 * <p> Description: Creates a new LessonsLearned object with a specified author and title. </p>
	 * 
	 * @param authorID The username of the author who created the lesson
	 * @param lessonTitle The core title of the lesson
	 */
	
	public LessonsLearned(String authorID, String lessonTitle) {
		this.authorID = authorID;
		this.lessonTitle = lessonTitle;
	}
	
	/**********
	 * <p> Title: getId </p>
	 * <p> Description: Retrieves the unique ID of the lesson. </p>
	 * @return The integer ID
	 */
	
	public int getId() {
		return id;
	}
	
	/**********
	 * <p> Title: setId </p>
	 * <p> Description: Sets the unique ID of the lesson. </p>
	 * @param id The integer ID to set
	 */
	
	public void setId(int id) {
		this.id = id;
	}
	
	/**********
	 * <p> Title: getAuthorID </p>
	 * <p> Description: Retrieves the author's ID. </p>
	 * @return The author ID string
	 */
	
	public String getAuthorID() {
		return authorID;
	}
	
	/**********
	 * <p> Title: setAuthorID </p>
	 * <p> Description: Sets the author's ID. </p>
	 * @param authorID The author ID string to set
	 */
	
	public void setAuthorID(String authorID) {
		this.authorID = authorID;
	}
	
	/**********
	 * <p> Title: getLessonTitle </p>
	 * <p> Description: Retrieves the title of the lesson. </p>
	 * @return The lesson title
	 */
	
	public String getLessonTitle() {
		return lessonTitle;
	}
	
	/**********
	 * <p> Title: setLessonTitle </p>
	 * <p> Description: Sets the title of the lesson. </p>
	 * @param lessonTitle The lesson title to set
	 */
	
	public void setLessonTitle(String lessonTitle) {
		this.lessonTitle = lessonTitle;
	}

	/**********
	 * <p> Title: isLessonTitleLocked </p>
	 * <p> Description: Checks if the lesson title is locked for editing. </p>
	 * @return True if locked, false otherwise
	 */
	
	public boolean isLessonTitleLocked() {
		return lessonTitleLocked;
	}

	/**********
	 * <p> Title: setLessonTitleLocked </p>
	 * <p> Description: Sets the lock status for the lesson title. </p>
	 * @param lessonTitleLocked True to lock, false to unlock
	 */
	
	public void setLessonTitleLocked(boolean lessonTitleLocked) {
		this.lessonTitleLocked = lessonTitleLocked;
	}

	/**********
	 * <p> Title: getExperienceDescription </p>
	 * <p> Description: Retrieves the experience description. </p>
	 * @return The experience description string
	 */
	
	public String getExperienceDescription() {
		return experienceDescription;
	}

	/**********
	 * <p> Title: setExperienceDescription </p>
	 * <p> Description: Sets the experience description. </p>
	 * @param experienceDescription The description string to set
	 */
	
	public void setExperienceDescription(String experienceDescription) {
		this.experienceDescription = experienceDescription;
	}

	/**********
	 * <p> Title: getHoursSpent </p>
	 * <p> Description: Retrieves the amount of time spent in hours. </p>
	 * @return The hours spent
	 */
	
	public double getHoursSpent() {
		return hoursSpent;
	}

	/**********
	 * <p> Title: setHoursSpent </p>
	 * <p> Description: Sets the amount of time spent in hours. </p>
	 * @param hoursSpent The hours to set
	 */
	
	public void setHoursSpent(double hoursSpent) {
		this.hoursSpent = hoursSpent;
	}

	/**********
	 * <p> Title: getEffortUsed </p>
	 * <p> Description: Retrieves the effort used metric. </p>
	 * @return The effort used string
	 */
	
	public String getEffortUsed() {
		return effortUsed;
	}

	/**********
	 * <p> Title: setEffortUsed </p>
	 * <p> Description: Sets the effort used metric. </p>
	 * @param effortUsed The effort string to set
	 */
	
	public void setEffortUsed(String effortUsed) {
		this.effortUsed = effortUsed;
	}

	/**********
	 * <p> Title: isExperienceDescriptionLocked </p>
	 * <p> Description: Checks if the experience description is locked. </p>
	 * @return True if locked, false otherwise
	 */
	
	public boolean isExperienceDescriptionLocked() {
		return experienceDescriptionLocked;
	}

	/**********
	 * <p> Title: setExperienceDescriptionLocked </p>
	 * <p> Description: Sets the lock status for the experience description. </p>
	 * @param experienceDescriptionLocked True to lock, false to unlock
	 */
	
	public void setExperienceDescriptionLocked(boolean experienceDescriptionLocked) {
		this.experienceDescriptionLocked = experienceDescriptionLocked;
	}

	/**********
	 * <p> Title: isHoursSpentLocked </p>
	 * <p> Description: Checks if the hours spent field is locked. </p>
	 * @return True if locked, false otherwise
	 */
	
	public boolean isHoursSpentLocked() {
		return hoursSpentLocked;
	}

	/**********
	 * <p> Title: setHoursSpentLocked </p>
	 * <p> Description: Sets the lock status for the hours spent field. </p>
	 * @param hoursSpentLocked True to lock, false to unlock
	 */
	
	public void setHoursSpentLocked(boolean hoursSpentLocked) {
		this.hoursSpentLocked = hoursSpentLocked;
	}

	/**********
	 * <p> Title: isEffortUsedLocked </p>
	 * <p> Description: Checks if the effort used field is locked. </p>
	 * @return True if locked, false otherwise
	 */
	
	public boolean isEffortUsedLocked() {
		return effortUsedLocked;
	}

	/**********
	 * <p> Title: setEffortUsedLocked </p>
	 * <p> Description: Sets the lock status for the effort used field. </p>
	 * @param effortUsedLocked True to lock, false to unlock
	 */
	
	public void setEffortUsedLocked(boolean effortUsedLocked) {
		this.effortUsedLocked = effortUsedLocked;
	}	
}

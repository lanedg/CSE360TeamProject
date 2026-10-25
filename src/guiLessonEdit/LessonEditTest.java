package guiLessonEdit;
import applicationMain.FoundationsMain;
import database.Database;
import database.LessonsLearnedHelper;
import entityClasses.LessonsLearned;
import entityClasses.User;
/**
Tests the Edit Lesson model and prints PASS or FAIL.
 */
public class LessonEditTest {
	/**
Creates test data, runs five tests and deletes the test data
@param args not used
	 */
	public static void main(String[] args) {
		Database db = FoundationsMain.database;
		User tUser = new User("testContributor", "test123", "Test", "", "User", "Test", "test@gmail.com", false, true, false);
		LessonsLearned tLesson = new LessonsLearned("testContributor", "title");
		LessonModelEditor editor = new LessonModelEditor();
		int id = 0, passed = 0, failed = 0;
		boolean created = false, ok;
		try {
			db.connectToDatabase();
			LessonsLearnedHelper.initializeTable();
			if (db.doesUserExist("testContributor")) { System.out.println("Account already exists"); return; }

			db.register(tUser);
			created = true;
			tLesson.setExperienceDescription("experience");
			if (!LessonsLearnedHelper.createLesson(tLesson)) throw new Exception();

			id = LessonsLearnedHelper.getHighestLessonId();
			tLesson = LessonsLearnedHelper.getLesson(id);
			if (tLesson == null || !"testContributor".equals(tLesson.getAuthorID())) { id = 0; throw new Exception(); }
			editor.lesson = editor.loadLesson(tUser, id);

			editor.saveLesson(tUser, id, "title2", "experience2", "2", "effort");
			ok = "title2".equals(LessonsLearnedHelper.getLesson(id).getLessonTitle());
			System.out.println("Valid edit: " + (ok ? "PASS" : "FAIL"));
			if (ok) passed++; else failed++;

			ok = false;
			try { editor.saveLesson(tUser, id, "", "experience", "2", "effort"); }
			catch (IllegalArgumentException e) { ok = true; }
			ok = ok && "title2".equals(LessonsLearnedHelper.getLesson(id).getLessonTitle());
			System.out.println("Empty title: " + (ok ? "PASS" : "FAIL"));
			if (ok) passed++; else failed++;

			ok = false;
			try { editor.saveLesson(tUser, id, "title2", "experience", "-1", "effort"); }
			catch (IllegalArgumentException e) { ok = true; }
			System.out.println("Negative hours: " + (ok ? "PASS" : "FAIL"));
			if (ok) passed++; else failed++;

			tLesson = LessonsLearnedHelper.getLesson(id);
			tLesson.setLessonTitleLocked(true);
			if (!LessonsLearnedHelper.updateLesson(tLesson)) throw new Exception();
			editor.lesson = editor.loadLesson(tUser, id);
			editor.saveLesson(tUser, id, "wrong", "experience3", "3", "effort");
			tLesson = LessonsLearnedHelper.getLesson(id);
			ok = "title2".equals(tLesson.getLessonTitle()) && "experience3".equals(tLesson.getExperienceDescription());
			System.out.println("Locked title: " + (ok ? "PASS" : "FAIL"));
			if (ok) passed++; else failed++;

			tLesson.setAuthorID("other");
			if (!LessonsLearnedHelper.updateLesson(tLesson)) throw new Exception();
			ok = false;
			try { editor.saveLesson(tUser, id, "wrong", "experience", "2", "effort"); }
			catch (IllegalArgumentException e) { ok = true; }
			System.out.println("Wrong owner: " + (ok ? "PASS" : "FAIL"));
			if (ok) passed++; else failed++;
		} catch (Exception e) { System.out.println("Test error: FAIL"); failed++; } 
		finally {
			if (id > 0) {
				try {
					tLesson = LessonsLearnedHelper.getLesson(id);
					if (tLesson != null) {
						tLesson.setLessonTitleLocked(false);
						tLesson.setAuthorID("testContributor");
						if (!LessonsLearnedHelper.updateLesson(tLesson) || !LessonsLearnedHelper.deleteLesson(id)) { System.out.println("Cleanup: FAIL"); failed++; }
					}
				} catch (Exception e) { System.out.println("Cleanup: FAIL"); failed++; }
			}

			if (created && !db.deleteUser("testContributor")) {System.out.println("Cleanup: FAIL"); failed++; }
			db.closeConnection();
			System.out.println("Passed: " + passed + "  Failed: " + failed);
		}
	}
}

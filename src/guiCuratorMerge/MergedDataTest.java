package guiCuratorMerge;

import database.LessonsLearnedHelper;
import entityClasses.LessonsLearned;
import java.util.ArrayList;
import java.util.List;

/**
 * <p> Title: MergedDataTest Class. </p>
 *
 * <p> Description: Automated test suite validating CuratorDataMerger calculations,
 * input validation, and H2 database persistence for merged time/effort statistics
 * and Contributor visibility flags. </p>
 *
 * @author Koushik Sai Chintha
 * 
 */
public class MergedDataTest {

	private static int passed = 0;
	private static int failed = 0;

	public static void main(String[] args) {
		LessonsLearnedHelper.initializeTable();

		testValidDataMerge();
		testInsufficientLessonsMerge();
		testGenerateStatisticalSummary();
		testStatsVisibilityFlag();
		testSaveAndRetrieveMergedStats();

		System.out.println("\nMergedDataTest Summary -> Passed: " + passed + " | Failed: " + failed);
	}

	/**
	 * <p> Method: testValidDataMerge </p>
	 * <p> Description: Validates total, average, min, and max hour calculations across 2+ lessons. </p>
	 */
	public static void testValidDataMerge() {
		List<LessonsLearned> list = new ArrayList<>();
		LessonsLearned l1 = new LessonsLearned("user1", "Lesson 1");
		l1.setHoursSpent(2.0);
		LessonsLearned l2 = new LessonsLearned("user2", "Lesson 2");
		l2.setHoursSpent(4.0);
		LessonsLearned l3 = new LessonsLearned("user3", "Lesson 3");
		l3.setHoursSpent(6.0);
		list.add(l1);
		list.add(l2);
		list.add(l3);

		boolean ok = CuratorDataMerger.calculateTotalHours(list) == 12.0
				&& CuratorDataMerger.calculateAverageHours(list) == 4.0
				&& CuratorDataMerger.calculateMinHours(list) == 2.0
				&& CuratorDataMerger.calculateMaxHours(list) == 6.0;

		record("testValidDataMerge", ok);
	}

	/**
	 * <p> Method: testInsufficientLessonsMerge </p>
	 * <p> Description: Validates that null, empty, and single-lesson lists throw IllegalArgumentException. </p>
	 */
	public static void testInsufficientLessonsMerge() {
		boolean nullRejected = false;
		boolean emptyRejected = false;
		boolean singleRejected = false;

		try {
			CuratorDataMerger.calculateAverageHours(null);
		} catch (IllegalArgumentException e) {
			nullRejected = true;
		}

		try {
			CuratorDataMerger.calculateAverageHours(new ArrayList<>());
		} catch (IllegalArgumentException e) {
			emptyRejected = true;
		}

		List<LessonsLearned> single = new ArrayList<>();
		single.add(new LessonsLearned("user1", "Only One"));
		try {
			CuratorDataMerger.generateStatisticalSummary(single);
		} catch (IllegalArgumentException e) {
			singleRejected = true;
		}

		record("testInsufficientLessonsMerge", nullRejected && emptyRejected && singleRejected);
	}

	/**
	 * <p> Method: testGenerateStatisticalSummary </p>
	 * <p> Description: Validates that generateStatisticalSummary formats count, total, average, min, max, and effort notes. </p>
	 */
	public static void testGenerateStatisticalSummary() {
		List<LessonsLearned> list = new ArrayList<>();
		LessonsLearned l1 = new LessonsLearned("u1", "API Setup");
		l1.setHoursSpent(2.0);
		l1.setEffortUsed("2 devs");
		LessonsLearned l2 = new LessonsLearned("u2", "DB Migration");
		l2.setHoursSpent(4.0);
		l2.setEffortUsed("1 dev");
		list.add(l1);
		list.add(l2);

		String summary = CuratorDataMerger.generateStatisticalSummary(list);
		boolean ok = summary.contains("2 Lessons")
				&& summary.contains("Total Hours: 6.00")
				&& summary.contains("Average Hours: 3.00")
				&& summary.contains("Min Hours: 2.00 | Max Hours: 4.00")
				&& summary.contains("API Setup: 2 devs");

		record("testGenerateStatisticalSummary", ok);
	}

	/**
	 * <p> Method: testStatsVisibilityFlag </p>
	 * <p> Description: Validates the statsVisibleToContributor getter/setter on LessonsLearned. </p>
	 */
	public static void testStatsVisibilityFlag() {
		LessonsLearned l = new LessonsLearned("u1", "Visibility Test");
		boolean defaultFalse = !l.isStatsVisibleToContributor();
		l.setStatsVisibleToContributor(true);
		boolean setTrue = l.isStatsVisibleToContributor();
		l.setStatsVisibleToContributor(false);
		boolean setFalse = !l.isStatsVisibleToContributor();

		record("testStatsVisibilityFlag", defaultFalse && setTrue && setFalse);
	}

	/**
	 * <p> Method: testSaveAndRetrieveMergedStats </p>
	 * <p> Description: Validates saving and retrieving merged stats and visibility flag in H2. </p>
	 */
	public static void testSaveAndRetrieveMergedStats() {
		int id = 0;
		try {
			LessonsLearned l = new LessonsLearned("testCuratorMergeUser", "Merge DB Test");
			l.setHoursSpent(3.5);
			l.setEffortUsed("1 dev");
			LessonsLearnedHelper.createLesson(l);
			id = LessonsLearnedHelper.getHighestLessonId();

			String stats = "Merged Time & Effort Summary (2 Lessons)\nTotal Hours: 7.00";
			boolean updated = LessonsLearnedHelper.updateMergedStats(id, stats, true);
			LessonsLearned fetched = LessonsLearnedHelper.getLesson(id);

			boolean ok = updated && fetched != null
					&& stats.equals(fetched.getMergedTimeEffortStats())
					&& fetched.isStatsVisibleToContributor();
			record("testSaveAndRetrieveMergedStats", ok);
		} catch (Exception e) {
			record("testSaveAndRetrieveMergedStats", false);
		} finally {
			if (id > 0) {
				LessonsLearnedHelper.deleteLesson(id);
			}
		}
	}

	private static void record(String name, boolean ok) {
		System.out.println(name + ": " + (ok ? "PASSED" : "FAILED"));
		if (ok) passed++; else failed++;
	}
}
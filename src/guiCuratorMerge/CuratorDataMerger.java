package guiCuratorMerge;

import entityClasses.LessonsLearned;
import java.util.List;

/*******
 * <p> Title: CuratorDataMerger Class. </p>
 * 
 * <p> Description: Prototype logic for Curators to merge time and effort 
 * data from multiple lessons into statistically useful metrics. </p>
 * 
 * @author Koushik Sai Chintha
 */

public class CuratorDataMerger {
	
	/**
	 * <p> Method: validateLessonList </p>
	 * <p> Description: Ensures at least two lessons are provided before merging. </p>
	 * <p> Validated by: MergedDataTest#testInsufficientLessonsMerge </p>
	 *
	 * @param lessons list of LessonsLearned objects to validate
	 * @throws IllegalArgumentException if the list is null or has fewer than 2 lessons
	 */
	private static void validateLessonList(List<LessonsLearned> lessons) {
		// The Curator epic requires merging two or more instances; rejecting < 2 prevents meaningless stats and division by zero
		if (lessons == null || lessons.size() < 2) {
			throw new IllegalArgumentException("At least two lessons are required to merge time and effort data.");
		}
	}

	/**
	 * <p> Method: calculateTotalHours </p>
	 * <p> Description: Calculates the sum of hours spent across all selected lessons. </p>
	 * <p> Validated by: MergedDataTest#testValidDataMerge </p>
	 *
	 * @param lessons list of 2+ LessonsLearned objects
	 * @return total hours spent
	 */
	public static double calculateTotalHours(List<LessonsLearned> lessons) {
		validateLessonList(lessons);
		double total = 0.0;
		for (LessonsLearned lesson : lessons) {
			total += lesson.getHoursSpent();
		}
		return total;
	}
    
	/**
	 * <p> Method: calculateAverageHours </p>
	 * <p> Description: Calculates the mean hours spent across all selected lessons. </p>
	 * <p> Validated by: MergedDataTest#testValidDataMerge </p>
	 *
	 * @param lessons list of 2+ LessonsLearned objects
	 * @return average hours spent
	 */
	public static double calculateAverageHours(List<LessonsLearned> lessons) {
		return calculateTotalHours(lessons) / lessons.size();
	}

	/**
	 * <p> Method: calculateMinHours </p>
	 * <p> Description: Finds the minimum hours spent among the selected lessons. </p>
	 * <p> Validated by: MergedDataTest#testValidDataMerge </p>
	 *
	 * @param lessons list of 2+ LessonsLearned objects
	 * @return minimum hours spent
	 */
	public static double calculateMinHours(List<LessonsLearned> lessons) {
		validateLessonList(lessons);
		double min = lessons.get(0).getHoursSpent();
		for (LessonsLearned lesson : lessons) {
			if (lesson.getHoursSpent() < min) {
				min = lesson.getHoursSpent();
			}
		}
		return min;
	}
	
	/**
	 * <p> Method: calculateMaxHours </p>
	 * <p> Description: Finds the maximum hours spent among the selected lessons. </p>
	 * <p> Validated by: MergedDataTest#testValidDataMerge </p>
	 *
	 * @param lessons list of 2+ LessonsLearned objects
	 * @return maximum hours spent
	 */
	public static double calculateMaxHours(List<LessonsLearned> lessons) {
		validateLessonList(lessons);
		double max = lessons.get(0).getHoursSpent();
		for (LessonsLearned lesson : lessons) {
			if (lesson.getHoursSpent() > max) {
				max = lesson.getHoursSpent();
			}
		}
		return max;
	}

	/**
	 * <p> Method: generateStatisticalSummary </p>
	 * <p> Description: Produces a formatted statistical summary of time and effort data
	 * across two or more lessons for storage and display. </p>
	 * <p> Validated by: MergedDataTest#testGenerateStatisticalSummary </p>
	 *
	 * @param lessons list of 2+ LessonsLearned objects
	 * @return formatted multi-line statistical summary string
	 */
	public static String generateStatisticalSummary(List<LessonsLearned> lessons) {
		validateLessonList(lessons);

		int count = lessons.size();
		double total = calculateTotalHours(lessons);
		double avg = total / count;
		double min = calculateMinHours(lessons);
		double max = calculateMaxHours(lessons);

		StringBuilder effortNotes = new StringBuilder();
		for (LessonsLearned lesson : lessons) {
			String effort = lesson.getEffortUsed();
			if (effort != null && !effort.trim().isEmpty()) {
				effortNotes.append("- ").append(lesson.getLessonTitle())
						   .append(": ").append(effort.trim()).append("\n");
			}
		}

		return String.format(
			"Merged Time & Effort Summary (%d Lessons)\n" +
			"Total Hours: %.2f\n" +
			"Average Hours: %.2f\n" +
			"Min Hours: %.2f | Max Hours: %.2f\n" +
			"Effort Notes:\n%s",
			count, total, avg, min, max,
			effortNotes.length() > 0 ? effortNotes.toString() : "- None recorded\n"
		);
	}
}
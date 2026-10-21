package entityClasses;

import java.util.ArrayList;
import java.util.List;

public class LessonsLearnedList {
	private List<LessonsLearned> lessonsList;

    public LessonsLearnedList() {
        this.lessonsList = new ArrayList<>();
    }

    public LessonsLearnedList(List<LessonsLearned> initialList) {
        this.lessonsList = initialList;
    }

    public void addLesson(LessonsLearned lesson) {
        this.lessonsList.add(lesson);
    }

    public void removeLesson(LessonsLearned lesson) {
        this.lessonsList.remove(lesson);
    }

    public List<LessonsLearned> getLessonsList() {
        return this.lessonsList;
    }

    public int getSize() {
        return this.lessonsList.size();
    }

     //Creates a subset of lessons that contain a specific keyword in the title or description.
    public LessonsLearnedList searchByKeyword(String keyword) {
        LessonsLearnedList subsetList = new LessonsLearnedList();
        if (keyword == null || keyword.trim().isEmpty()) {
            return this; // Return full list if thers no keyword
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


package guiCuratorLock;

import database.LessonsLearnedHelper;
import entityClasses.LessonsLearned;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * <p> Title: ViewCuratorLock Class. </p>
 *
 * <p> Description: JavaFX window allowing a Curator to lock or unlock an entire
 * lesson or specific fields so Contributors cannot edit locked fields or delete
 * locked lessons. </p>
 *
 * <p> Validated by: Manual Tests.pdf (MT-3: Lock Lesson/Fields, MT-4: Contributor Lock Enforcement) </p>
 *
 * @author Koushik Sai Chintha
 * @version 1.00 2026-11-25 Initial TP3 version
 */
public class ViewCuratorLock {

	/**
	 * <p> Method: display </p>
	 * <p> Description: Opens the Curator Lock Lessons window. </p>
	 * <p> Validated by: Manual Tests.pdf (MT-3) </p>
	 */
	public static void display() {
		Stage stage = new Stage();
		stage.setTitle("Curator - Lock Lessons & Fields");

		ObservableList<LessonsLearned> allLessons =
				FXCollections.observableArrayList(LessonsLearnedHelper.getAllLessons());

		Label selectLabel = new Label("Select a lesson to manage locks:");
		ComboBox<LessonsLearned> lessonComboBox = new ComboBox<>(allLessons);
		lessonComboBox.setPrefWidth(400);
		lessonComboBox.setConverter(new StringConverter<>() {
			@Override
			public String toString(LessonsLearned l) {
				return l == null ? "" : "ID " + l.getId() + " - " + l.getLessonTitle();
			}
			@Override
			public LessonsLearned fromString(String s) { return null; }
		});

		CheckBox lockAllBox = new CheckBox("Lock Entire Lesson (blocks all edits and deletion)");
		CheckBox lockTitleBox = new CheckBox("Lock Lesson Title");
		CheckBox lockExpBox = new CheckBox("Lock Experience Description");
		CheckBox lockHoursBox = new CheckBox("Lock Hours Spent");
		CheckBox lockEffortBox = new CheckBox("Lock Effort Used");

		// Populate checkboxes when a lesson is selected
		lessonComboBox.setOnAction(e -> {
			LessonsLearned selected = lessonComboBox.getValue();
			if (selected != null) {
				lockAllBox.setSelected(selected.isLessonLocked());
				lockTitleBox.setSelected(selected.isLessonTitleLocked());
				lockExpBox.setSelected(selected.isExperienceDescriptionLocked());
				lockHoursBox.setSelected(selected.isHoursSpentLocked());
				lockEffortBox.setSelected(selected.isEffortUsedLocked());
			}
		});

		// Checking "Lock Entire Lesson" automatically checks all individual field locks
		lockAllBox.setOnAction(e -> {
			if (lockAllBox.isSelected()) {
				lockTitleBox.setSelected(true);
				lockExpBox.setSelected(true);
				lockHoursBox.setSelected(true);
				lockEffortBox.setSelected(true);
			}
		});

		Button saveButton = new Button("Save Locks");
		Button closeButton = new Button("Close");

		saveButton.setOnAction(e -> {
			LessonsLearned selected = lessonComboBox.getValue();
			if (selected == null) {
				showAlert(stage, Alert.AlertType.ERROR, "Error", "Select a lesson first.");
				return;
			}
			boolean saved = ControllerCuratorLock.saveLocks(
					selected,
					lockAllBox.isSelected(),
					lockTitleBox.isSelected(),
					lockExpBox.isSelected(),
					lockHoursBox.isSelected(),
					lockEffortBox.isSelected()
			);
			if (saved) {
				showAlert(stage, Alert.AlertType.INFORMATION, "Saved",
						"Lock settings updated for Lesson ID " + selected.getId() + ".");
			} else {
				showAlert(stage, Alert.AlertType.ERROR, "Database Error", "Failed to save lock settings.");
			}
		});

		closeButton.setOnAction(e -> stage.close());

		HBox actionBar = new HBox(10, saveButton, closeButton);
		VBox root = new VBox(10, selectLabel, lessonComboBox,
				lockAllBox, lockTitleBox, lockExpBox, lockHoursBox, lockEffortBox, actionBar);
		root.setPadding(new Insets(15));

		stage.setScene(new Scene(root, 440, 320));
		stage.show();
	}

	private static void showAlert(Stage owner, Alert.AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.initOwner(owner);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
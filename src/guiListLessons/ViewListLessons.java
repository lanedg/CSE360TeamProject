package guiListLessons;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;
import entityClasses.User;
import database.LessonsLearnedHelper;

public class ViewListLessons {
    
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;
    private static Scene theScene;
    private static ViewListLessons theView;

    protected static Label label_Title = new Label("Your Lessons Learned");
    protected static ListView<String> listView = new ListView<>();
    protected static TextField searchField = new TextField();
    protected static Button button_Search = new Button("Search");
    protected static Button button_Edit = new Button("Edit Selected Lesson");
    protected static Button button_Back = new Button("Back to Home");

    protected static LessonsLearnedList fullList;

    public static void displayListLessons(Stage ps, User user) {
        theStage = ps;
        theUser = user;
        
        if (theView == null) {
            theView = new ViewListLessons();
        }
        
        // Fetch fresh data from the database every time the page loads
        fullList = LessonsLearnedHelper.getLessonsForAuthor(theUser.getUserName());
        updateListView(fullList);
        
        theStage.setTitle("View/List Lessons");
        theStage.setScene(theScene);
        theStage.show();
    }

    private ViewListLessons() {
        theRootPane = new Pane();
        theScene = new Scene(theRootPane, width, height);

        setupLabelUI(label_Title, "Arial", 24, width, Pos.CENTER, 0, 20);

        searchField.setPromptText("Enter keyword to search...");
        searchField.setLayoutX(50);
        searchField.setLayoutY(70);
        searchField.setPrefWidth(300);

        setupButtonUI(button_Search, "Dialog", 14, 100, Pos.CENTER, 370, 70);
        button_Search.setOnAction(e -> ControllerListLessons.performSearch(searchField.getText()));

        listView.setLayoutX(50);
        listView.setLayoutY(120);
        listView.setPrefSize(700, 300);

        setupButtonUI(button_Edit, "Dialog", 16, 200, Pos.CENTER, 50, 450);
        button_Edit.setOnAction(e -> ControllerListLessons.performEdit(listView.getSelectionModel().getSelectedItem()));

        setupButtonUI(button_Back, "Dialog", 16, 150, Pos.CENTER, 600, 450);
        button_Back.setOnAction(e -> ControllerListLessons.performBack());

        theRootPane.getChildren().addAll(label_Title, searchField, button_Search, listView, button_Edit, button_Back);
    }

    protected static void updateListView(LessonsLearnedList listToDisplay) {
        ObservableList<String> displayItems = FXCollections.observableArrayList();
        for (LessonsLearned lesson : listToDisplay.getLessonsList()) {
            displayItems.add("ID: " + lesson.getId() + " - Title: " + lesson.getLessonTitle());
        }
        listView.setItems(displayItems);
    }

    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);        
    }

    private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);        
    }
}
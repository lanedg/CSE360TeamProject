module FoundationsF26 {
	requires javafx.controls;
	requires java.sql;
	requires java.net.http;
	requires com.google.gson;
	requires static org.junit.jupiter.api;
	opens guiTools to org.junit.platform.commons;
	requires javafx.graphics;
	requires javafx.base;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
}

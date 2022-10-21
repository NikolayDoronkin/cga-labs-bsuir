module com.example.cgalabs {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.web;

	requires org.controlsfx.controls;
	requires com.dlsc.formsfx;
	requires org.kordamp.ikonli.javafx;
	requires org.kordamp.bootstrapfx.core;
	requires eu.hansolo.tilesfx;
	requires javafx.graphics;
	requires commons.math3;
	requires static lombok;

	opens com.example.cgalabs to javafx.fxml;
	exports com.example.cgalabs;
}
module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.github.guocay.javafx to javafx.fxml;
    exports com.github.guocay.javafx;
}

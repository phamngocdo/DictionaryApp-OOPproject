module app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires javafx.base;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires java.net.http;    
    requires tess4j;
    requires jlayer;
    requires org.json;
    

    opens app.main to javafx.fxml;
    opens app.base to javafx.fxml;
    opens app.controller to javafx.fxml;

    exports app.base;
    exports app.controller;
    exports app.main;
}

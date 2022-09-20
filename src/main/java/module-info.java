module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires org.apache.logging.log4j;
    requires lombok;
    requires java.persistence;
    requires java.sql;
    requires org.apache.derby.tools;
    requires org.hibernate.orm.core;

    opens com.java2.hon0102 to javafx.fxml, org.hibernate.orm.core;

    exports com.java2.hon0102;
}
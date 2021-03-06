package ru.stk.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.stk.client.Client;


import java.io.IOException;
import java.io.InputStream;

public class MainApp extends Application {
    /**
     * Starts the application
     * @param args
     *  * @version 1.1 27 Oct 2020
     *  * @author    Sergei Tkachev
     */
    private static final Logger logger = LogManager.getLogger(Client.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        final String APP_TITLE = "Сетевое хранилище cloud drive";

        // set size of app forms
        final double LOGIN_FORM_WIDTH = 400;
        final double LOGIN_FORM_HEIGHT = 400;
        final double MAIN_FORM_WIDTH = 550;
        final double MAIN_FORM_HEIGHT = 400;

        // load fxml and create login scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();
        Scene loginScene = new Scene(root, LOGIN_FORM_WIDTH, LOGIN_FORM_HEIGHT);

        // create and initialize login controller
        LoginFxCtl loginCtl = loader.getController();

        // create main scene
        FXMLLoader loaderMain = new FXMLLoader(getClass().getResource("/main.fxml"));
        Parent rootMain = loaderMain.load();
        Scene mainScene = new Scene(rootMain, MAIN_FORM_WIDTH, MAIN_FORM_HEIGHT);
        MainFxCtl mainCtl = loaderMain.getController();

        // set-up controllers
        loginCtl.setCurStage(primaryStage);
        loginCtl.setMainScene(mainScene);
        loginCtl.setMainCtl(mainCtl);
        loginCtl.setMainFormWidth(MAIN_FORM_WIDTH);
        loginCtl.setMainFormHeight(MAIN_FORM_HEIGHT);
        mainCtl.setCurStage(primaryStage);
        mainCtl.setMainScene(mainScene);
        Client.setScene(mainScene);
        logger.info("Controllers are successfully initialised");

        // set app title
        primaryStage.setTitle(APP_TITLE);

        // set up icon for the form header
        InputStream iconStream = getClass().getResourceAsStream("/disk.png");
        Image image = new Image(iconStream);
        primaryStage.getIcons().add(image);

        // set scene and show window
        primaryStage.setScene(loginScene);
        primaryStage.show();
        logger.info("Application started");
    }
}

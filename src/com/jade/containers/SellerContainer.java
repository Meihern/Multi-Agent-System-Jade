package com.jade.containers;

import com.jade.agents.SellerAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SellerContainer extends Application {

    private SellerAgent sellerAgent;
    private AgentContainer agentContainer;


    public static final int WIDTH = 400;
    public static final int HEIGHT = 600;
    private BorderPane root = new BorderPane();
    private Scene scene;
    private HBox hBox = new HBox();
    private VBox vBox = new VBox();
    private Label label = new Label("Agent Name : ");
    private TextField agentNameField = new TextField();
    private Button deployButton = new Button("Deploy");
    private ObservableList<String> observableList = FXCollections.observableArrayList();
    private ListView<String> listView = new ListView<>(observableList);


    private void initPane(){
        scene = new Scene(root, WIDTH, HEIGHT);
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        root.setTop(hBox);
        root.setCenter(vBox);
    }

    private void drawElements(){
        hBox.getChildren().addAll(label, agentNameField, deployButton);
        vBox.getChildren().add(listView);
    }

    private void handleEvents(){
        deployButton.setOnAction(event -> {
            try {
                String agentName = agentNameField.getText();
                AgentController agentController = agentContainer.createNewAgent(agentName, "com.jade.agents.SellerAgent", new Object[]{this});
                agentController.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        });
    }

    private void startContainer() throws ControllerException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        agentContainer = runtime.createAgentContainer(profile);
        agentContainer.start();
    }

    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
            observableList.add(aclMessage.getContent());
        });
    }

    public void setSellerAgent(SellerAgent sellerAgent) {
        this.sellerAgent = sellerAgent;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        initPane();
        drawElements();
        handleEvents();
        stage.setTitle("Seller Container");
        stage.setScene(scene);
        stage.show();
    }
}

package com.jade.containers;

import com.jade.agents.BuyerAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BuyerContainer extends Application {
    private BuyerAgent buyerAgent;

    public static final int WIDTH = 400;
    public static final int HEIGHT = 600;
    private BorderPane root = new BorderPane();
    private Scene scene;
    private HBox hBox = new HBox();
    private VBox vBox = new VBox();
    private Label label = new Label("Messages reçues : ");
    private ObservableList<String> observableList = FXCollections.observableArrayList();
    private ListView<String> listView = new ListView<>(observableList);

    private void initPane(){
        scene = new Scene(root, WIDTH, HEIGHT);
        hBox.setPadding(new Insets(10));
        vBox.setPadding(new Insets(10));
        root.setTop(hBox);
        root.setCenter(vBox);
    }

    private void drawElements(){
        hBox.getChildren().add(label);
        vBox.getChildren().add(listView);
    }

    private void startContainer() throws ControllerException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);
        AgentController agentController = agentContainer.createNewAgent("Buyer", "com.jade.agents.BuyerAgent", new Object[]{this});
        agentContainer.start();
        agentController.start();
    }

    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
            observableList.add(aclMessage.getContent());
        });
    }

    public void setBuyerAgent(BuyerAgent buyerAgent) {
        this.buyerAgent = buyerAgent;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        initPane();
        drawElements();
        stage.setTitle("Buyer Container");
        stage.setScene(scene);
        stage.show();
    }
}

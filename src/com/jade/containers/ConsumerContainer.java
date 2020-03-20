package com.jade.containers;

import com.jade.agents.ConsumerAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
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



public class ConsumerContainer extends Application {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;
    private BorderPane root = new BorderPane();
    private Scene scene;
    private HBox hBox = new HBox();
    private VBox vBox = new VBox();
    private Label label = new Label("Livre : ");
    private Button addButton = new Button("Ajouter");
    private TextField textField = new TextField();
    private ObservableList observableListMessages = FXCollections.observableArrayList();
    private ListView listViewMessages = new ListView<>(observableListMessages);


    private ConsumerAgent consumerAgent;

    private void initPane(){
        scene = new Scene(root, WIDTH, HEIGHT);
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        root.setTop(hBox);
        root.setCenter(vBox);
    }

    private void drawElements(){
        hBox.getChildren().addAll(label, textField, addButton);
        vBox.getChildren().add(listViewMessages);
    }

    private void handleEvents(){
        addButton.setOnAction(event -> {
            String livre = textField.getText();
           // observableListMessages.add(livre);
            GuiEvent guiEvent = new GuiEvent(this, 1);
            guiEvent.addParameter(livre);
            consumerAgent.onGuiEvent(guiEvent);
        });
    }

    private void startContainer() throws ControllerException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        AgentContainer consumerAgentContainer = runtime.createAgentContainer(profile);
        AgentController agentController = consumerAgentContainer.createNewAgent("Consumer", "com.jade.agents.ConsumerAgent", new Object[] {this});
        consumerAgentContainer.start();
        agentController.start();
        System.out.println(this.consumerAgent);
    }

    public void setConsumerAgent(ConsumerAgent consumerAgent) {
        this.consumerAgent = consumerAgent;
    }

    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
            observableListMessages.add(aclMessage.getContent()+", Sender : "+aclMessage.getSender().getName());
        });
    }

    public static void main(String[] args) throws ControllerException {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        stage.setTitle("Consumer Agent");
        initPane();
        drawElements();
        handleEvents();
        stage.setScene(scene);
        stage.show();
    }
}

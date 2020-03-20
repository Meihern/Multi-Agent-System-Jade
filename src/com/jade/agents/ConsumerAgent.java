package com.jade.agents;

import com.jade.containers.ConsumerContainer;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class ConsumerAgent extends GuiAgent {
    private transient ConsumerContainer consumerContainer;
    @Override
    protected void setup(){
        System.out.println("+++++++++++++++++++++++++++++++++++++++");
        System.out.println("Initialisation du ConsumerAgent...."+this.getAID().getName());
        System.out.println("+++++++++++++++++++++++++++++++++++++++");
        if(this.getArguments().length == 1){
            consumerContainer = (ConsumerContainer) this.getArguments()[0];
            consumerContainer.setConsumerAgent(this);
        }
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage = receive();
                if (aclMessage != null) {
                    switch (aclMessage.getPerformative()){
                        case ACLMessage.CONFIRM:
                            consumerContainer.logMessage(aclMessage);
                            break;
                        default:
                            break;
                    }

                }
                else{
                    block();
                }
            }
        });



    }
    @Override
    protected void beforeMove(){
        System.out.println("++++++++++++++++++++++++++++++++++++");
        System.out.println("Avant migration.....");
        System.out.println("+++++++++++++++++++++++++++++++++++++++");
    }

    @Override
    protected void afterMove(){
        System.out.println("++++++++++++++++++++++++++++++++++++");
        System.out.println("Après migration....");
        System.out.println("+++++++++++++++++++++++++++++++++++++++");
    }
    @Override
    protected void takeDown(){
        System.out.println("+++++++++++++++++++++++++++++++++++");
        System.out.println("Fin de l'agent .....");
        System.out.println("+++++++++++++++++++++++++++++++++++++++");
    }


    @Override
    public void onGuiEvent(GuiEvent guiEvent) {
        if(guiEvent.getType() == 1){
            String livre = guiEvent.getParameter(0).toString();
            ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setContent(livre);
            aclMessage.addReceiver(new AID("Buyer", AID.ISLOCALNAME));
            send(aclMessage);
        }
    }
}

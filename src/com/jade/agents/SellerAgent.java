package com.jade.agents;

import com.jade.containers.SellerContainer;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class SellerAgent extends GuiAgent {
    SellerContainer sellerContainer;
    @Override
    protected void setup() {
        System.out.println("+++++++++++++++++++++++++++++++++++++++");
        System.out.println("Initialisation du SellerAgent...."+this.getAID().getName());
        System.out.println("+++++++++++++++++++++++++++++++++++++++");
        if(this.getArguments().length == 1){
            sellerContainer = (SellerContainer) this.getArguments()[0];
            sellerContainer.setSellerAgent(this);
        }

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription agentDescription = new DFAgentDescription();
                agentDescription.setName(getAID());
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("transaction");
                serviceDescription.setName("book-selling");
                agentDescription.addServices(serviceDescription);
                try {
                    DFService.register(myAgent, agentDescription);
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage = receive();
                if(aclMessage != null){
                    sellerContainer.logMessage(aclMessage);
                    switch (aclMessage.getPerformative()){
                        case ACLMessage.CFP:
                            ACLMessage reply = aclMessage.createReply();
                            reply.setPerformative(ACLMessage.PROPOSE);
                            reply.setContent(String.valueOf(500 + new Random().nextInt(1000)));
                            send(reply);
                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:
                            ACLMessage replyAccept = aclMessage.createReply();
                            replyAccept.setPerformative(ACLMessage.AGREE);
                            replyAccept.setContent("Livre prêt pour la vente");
                            send(replyAccept);
                            break;
                        default:
                            break;
                    }
                }
                else {
                    block();
                }
            }
        });
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }
}

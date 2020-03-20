package com.jade.agents;

import com.jade.containers.BuyerContainer;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class BuyerAgent extends GuiAgent {

    private BuyerContainer buyerContainer;
    private AID[] sellers;

    @Override
    protected void setup(){
        System.out.println("+++++++++++++++++++++++++++++++++++++++");
        System.out.println("Initialisation du BuyerAgent...."+this.getAID().getName());
        System.out.println("+++++++++++++++++++++++++++++++++++++++");
        if(this.getArguments().length == 1){
            buyerContainer = (BuyerContainer) this.getArguments()[0];
            buyerContainer.setBuyerAgent(this);
        }

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this, 3000) {
            @Override
            protected void onTick() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("transaction");
                serviceDescription.setName("book-selling");
                template.addServices(serviceDescription);
                try {
                    DFAgentDescription[] results = DFService.search(myAgent, template);
                    sellers = new AID[results.length];
                    for(int i=0; i<sellers.length; i++){
                        sellers[i] = results[i].getName();
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            private int counter = 0;
            private List<ACLMessage> replies = new ArrayList<>();
            @Override
            public void action() {
                ACLMessage aclMessage = receive();
                if(aclMessage != null){
                    String livre = aclMessage.getContent();
                    buyerContainer.logMessage(aclMessage);
                    switch (aclMessage.getPerformative()){
                        case ACLMessage.REQUEST:
                            ACLMessage message = new ACLMessage(ACLMessage.CFP);
                            message.setContent(livre);
                            for(AID seller:sellers){
                                message.addReceiver(seller);
                            }
                            send(message);
                            break;

                        case ACLMessage.PROPOSE:
                           ++counter;
                            replies.add(aclMessage);
                            if(counter ==  sellers.length){
                                ACLMessage meilleurOffre = replies.get(0);
                                double mini=Double.parseDouble(meilleurOffre.getContent());
                                for(ACLMessage offre:replies){
                                    double price = Double.parseDouble(offre.getContent());
                                    if(price < mini){
                                        meilleurOffre = offre;
                                        mini = price;
                                    }
                                }
                                ACLMessage acceptMessage = meilleurOffre.createReply();
                                acceptMessage.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                acceptMessage.setContent("Votre offre a été acceptée pour le prix : "+mini);
                                send(acceptMessage);
                            }
                            break;
                        case ACLMessage.AGREE:
                            ACLMessage agreeMessage = new ACLMessage(ACLMessage.CONFIRM);
                            agreeMessage.addReceiver(new AID("Consumer", AID.ISLOCALNAME));
                            agreeMessage.setContent("Achat de livre effectué avec succès");
                            send(agreeMessage);
                            break;
                        case ACLMessage.REFUSE:
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

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}

package com.jade.containers;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;

public class MyMainContainer {
    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.GUI, "true");
        AgentContainer agentContainer = runtime.createMainContainer(profile);
        try {
            agentContainer.start();
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }

}

package designPattern;

import model.Agent;
import model.PositionAgent;

public interface AgentFactory {
    Agent createAgent(PositionAgent positionAgent);
}
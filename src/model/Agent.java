package model;

import designPattern.StrategieAgent;

public abstract class Agent {
    PositionAgent position;
    PositionAgent oldPosition;
    public StrategieAgent strategie;

    public Agent(PositionAgent positionAgent) {
        this.position = positionAgent;
    }

    public void setStrategie(StrategieAgent positionAgent) {
        this.strategie = positionAgent;
    }

    public StrategieAgent getStrategie() {
        return this.strategie;
    }

    public PositionAgent getPosition() {
        return this.position;
    }

    public PositionAgent getOldPosition() {
        return this.oldPosition;
    }

    public void setPosition(int x, int y) {
        this.oldPosition = new PositionAgent(this.position.getX(), this.position.getY(), this.position.getDir());
        this.position.setX(x);
        this.position.setY(y);
    }
}
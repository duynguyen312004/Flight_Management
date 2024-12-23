package models;

public class Gate {
    private int gateNumber;
    private boolean isAvailable;

    public Gate(int gateNumber, boolean isAvailable) {
        this.gateNumber = gateNumber;
        this.isAvailable = isAvailable;
    }

    public int getGateNumber() {
        return gateNumber;
    }

    public void setGateNumber(int gateNumber) {
        this.gateNumber = gateNumber;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    @Override
    public String toString() {
        return "Gate{" +
                "gateNumber=" + gateNumber +
                ", isAvailable=" + isAvailable +
                '}';
    }
}

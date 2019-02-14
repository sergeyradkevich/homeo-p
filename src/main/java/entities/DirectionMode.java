package entities;

public class DirectionMode {

    private DirectionModeType type = DirectionModeType.CONSTANTLY;
    private int taken;
    private int interval;

    public DirectionMode() {
    }

    public DirectionMode(DirectionModeType type) {
        this.type = type;
    }

    public void setTaken(int taken) {
        this.taken = taken;
    }

    public int getTaken() {
        return taken;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getInterval() {
        return interval;
    }

    public boolean isDaily() {
        return type == DirectionModeType.CONSTANTLY;
    }

    public boolean isPeriodically() {
        return type == DirectionModeType.PERIODICALLY;
    }
}

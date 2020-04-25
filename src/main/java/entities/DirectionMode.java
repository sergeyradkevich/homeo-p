package entities;

public class DirectionMode {

    private DirectionModeType type;
    private int taken;
    private int interval;
    private int delta;
    private int limit;

    public DirectionMode() {
        type = DirectionModeType.byDefault();
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

    public boolean isDecreasing() {
        return type == DirectionModeType.DECREASINGLY;
    }

    public int getDelta() {
        return delta;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int calcDecreasingDaysAmountUntilLimitInclusive(int source) {
        int amountTillLimit = (source - limit);
        int daysAmount = (amountTillLimit / delta) + 1;

        if (amountTillLimit % delta == 0)
            return daysAmount;

        return daysAmount + 1;
    }
}

package usecases.directionusage.directionalmodes;

import usecases.directionusage.Directional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PeriodicalMode implements Directional {

    private int taken;
    private int interval;

    public PeriodicalMode() {}

    public PeriodicalMode(int taken, int interval) {
        this.taken = taken;
        this.interval = interval;
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

    public int length() {
        return getTaken() + getInterval();
    }


    @Override
    public boolean isUsedOn(LocalDate startsOn, LocalDate date) {
        return numberOfDayInPeriod(date, startsOn) <= getTaken();
    }

    private long numberOfDayInPeriod(LocalDate date, LocalDate startsOn) {
        long totalDays = ChronoUnit.DAYS.between(startsOn, date.plusDays(1));

        long completeOccurrences = totalDays / length();
        long daysInCompetePeriods = completeOccurrences * length();

        long dayNumber = totalDays - daysInCompetePeriods;

        return (dayNumber == 0)? length(): dayNumber;
    }
}

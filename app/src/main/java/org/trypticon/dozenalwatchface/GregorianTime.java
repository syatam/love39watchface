package org.trypticon.dozenalwatchface;

import com.ustwo.clockwise.WatchFaceTime;

/**
 * Stand-in utility for holding a gregorian time to avoid using any of Java's built-in horrors.
 */
class GregorianTime extends Time {
    private static final int GREGORIAN_MILLIS_PER_HALF_DAY = 1000 * 60 * 60 * 12;

    private static final int MINUTE_TURNS_PER_HOUR_TURN = 12;
    private static final int SECONDS_TURNS_PER_MINUTE_TURN = 60;

    // AD year
    int year;

    // 1..12
    int month;

    // 1..31
    int dayOfMonth;

    // 0..6
    int dayOfWeek;

    // 0..23
    int hour;

    // 0..59
    int minute;

    // 0..59
    int second;

    // 0..999
    int millisecond;

    long epochMillis;

    private float hourTurns;
    private float minuteTurns;
    private float secondTurns;

    @Override
    void setTo(WatchFaceTime time) {
        year = time.year;
        month = time.month + 1; // WatchFaceTime inherits this weirdness
        dayOfMonth = time.monthDay;
        dayOfWeek = time.weekDay;
        hour = time.hour;
        minute = time.minute;
        second = time.second;
        millisecond = time.millis;
        // Workaround here for WatchFaceTime not including millis
        epochMillis = time.toMillis(false) + time.millis;
        recompute();
    }

    void setTo(GregorianTime time) {
        year = time.year;
        month = time.month;
        dayOfMonth = time.dayOfMonth;
        dayOfWeek = time.dayOfWeek;
        hour = time.hour;
        minute = time.minute;
        second = time.second;
        millisecond = time.millisecond;
        epochMillis = time.epochMillis;
        recompute();
    }

    @Override
    int getYear() {
        return year;
    }

    @Override
    int getMonth() {
        return month;
    }

    @Override
    int getDayOfMonth() {
        return dayOfMonth;
    }

    @Override
    int getDayOfWeek() {
        return dayOfWeek;
    }

    @Override
    float getHourTurns() {
        return hourTurns;
    }

    @Override
    float getMinuteTurns() {
        return minuteTurns;
    }

    @Override
    float getSecondTurns() {
        return secondTurns;
    }

    @Override
    float getThirdTurns() {
        throw new UnsupportedOperationException();
    }

    @Override
    long getEpochMillis() {
        return epochMillis;
    }

    @Override
    boolean hasThirds() {
        return false;
    }

    private void recompute() {
        // We do recompute this from the calendar, because sometimes an hour in the day
        // is missing. So this gives us the effect of getting free daylight savings support.
        int dayMillis = computeDayMillis();

        hourTurns = dayMillis / (float) GREGORIAN_MILLIS_PER_HALF_DAY;
        minuteTurns = hourTurns * MINUTE_TURNS_PER_HOUR_TURN;
        secondTurns = minuteTurns * SECONDS_TURNS_PER_MINUTE_TURN;
    }

    public int computeDayMillis() {
        return hour * 60 * 60 * 1000 +
                minute * 60 * 1000 +
                second * 1000 +
                millisecond;
    }
}
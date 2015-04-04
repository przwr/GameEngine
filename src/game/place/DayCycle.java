/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

/**
 *
 * @author Domi
 */
public class DayCycle {

	short minutes = 0;
	long midnightTime;
	long currentTime, diffrence, tempMinutes;
	short REAL_MINUTES_IN_HOUR = 4;

	public DayCycle() {
		midnightTime = System.currentTimeMillis();
	}

	public void updateTime() {
		updateDiffrence();
		calculateMinutes(diffrence / 1000);
	}

	private void updateDiffrence() {
		currentTime = System.currentTimeMillis();
		diffrence = currentTime - midnightTime;
	}

	private void calculateMinutes(long seconds) {
		tempMinutes = (seconds / REAL_MINUTES_IN_HOUR);
		if (tempMinutes > 1440) {
			tempMinutes -= 1440;
			resetMidnightTime();
		}
		minutes = (short) tempMinutes;
	}

	private void resetMidnightTime() {
		midnightTime = currentTime;
	}

	public short getTime() {
		return minutes;
	}

	public void setTime(int hour, int minutes) {
		updateDiffrence();
		midnightTime -= (((hour * 60 + minutes) * REAL_MINUTES_IN_HOUR * 1000) - diffrence);
		updateTime();
	}

	public void addOneHour() {
		addHours(1);
	}

	public void addHours(int hours) {
		midnightTime -= hours * REAL_MINUTES_IN_HOUR * 60000;
		updateTime();
	}
}

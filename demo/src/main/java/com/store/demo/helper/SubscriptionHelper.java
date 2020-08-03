package com.store.demo.helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.store.demo.entity.Subscription;

public class SubscriptionHelper {

	public static List<LocalDate> getInvoiceDates(final Subscription sub) {

		if (sub.getType() == null) {
			// TODO log and handle error
			return null;
		}

		LocalDate startDate = sub.getStartDate();
		LocalDate endDate = sub.getEndDate();
		List<LocalDate> result = new ArrayList<>();
		int dayDifference;

		switch (sub.getType()) {

		case DAILY: {
			dayDifference = 1;
			result = getPeriodicalDatesBetween(startDate, endDate, dayDifference);
			break;
		}

		case WEEKLY: {
			dayDifference = 7;
			result = getPeriodicalDatesBetween(startDate, endDate, dayDifference);
			break;
		}

		case MONTHLY: {

			while (!startDate.isAfter(endDate)) {
				result.add(startDate);
				startDate = startDate.plusMonths(1);
			}

			break;
		}

		default: {
			// TODO log and handle error
			// do nothing for now
			return null;
		}
		}

		return result;
	}

	private static List<LocalDate> getPeriodicalDatesBetween(LocalDate startDate, LocalDate endDate,
			int dayDifference) {

		List<LocalDate> result = new ArrayList();

		while (!startDate.isAfter(endDate)) {
			result.add(startDate);
			startDate = startDate.plusDays(dayDifference);
		}

		return result;
	}
}
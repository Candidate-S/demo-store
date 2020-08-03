package com.store.demo.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import com.store.demo.constant.SubscriptionType;
import com.store.demo.entity.Subscription;
import com.store.demo.helper.SubscriptionHelper;
import com.store.demo.repository.SubscriptionRepository;
import com.store.demo.response.NewSubscriptionResponse;

@org.springframework.web.bind.annotation.RestController
public class RestController {

	private final SubscriptionRepository repository;
	private static final Logger log = LoggerFactory.getLogger(RestController.class);

	RestController(SubscriptionRepository repository) {
		this.repository = repository;
	}

	@CrossOrigin
	@PostMapping("/subscriptions")
	private NewSubscriptionResponse newSubscription(final @RequestBody Subscription newSubscription) {

		log.debug("newSubscription: " + newSubscription);

		// input validations
		if (newSubscription == null) {
			log.error("empty newSubscription: " + newSubscription);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
		}

		String userId = newSubscription.getUserId();

		// TODO: do sanity checks, user existence checks etc

		if (StringUtils.isEmpty(userId)) {
			log.error("empty userId: " + userId);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
		}

		BigDecimal amount = newSubscription.getAmount();

		if (amount == null) {
			log.error("empty amount: " + amount);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount is required");
		}

		if (amount.doubleValue() < 0) {
			log.error("invalid amount: " + amount);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid amount");
		}

		SubscriptionType type = newSubscription.getType();

		if (type == null) {
			log.error("empty type: " + type);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subscription type is required");
		}

		LocalDate startDate = newSubscription.getStartDate();

		if (startDate == null) {
			log.error("empty startDate: " + startDate);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "StartDate is required");
		}

		if (startDate.isBefore(LocalDate.now())) {
			log.error("invalid startDate: " + startDate);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid StartDate");
		}

		LocalDate endDate = newSubscription.getEndDate();

		if (endDate == null) {
			log.error("empty endDate: " + endDate);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate is required");
		}

		if (endDate.isBefore(LocalDate.now())) {
			log.error("invalid endDate: " + endDate);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid endDate");
		}

		if (endDate.isBefore(startDate)) {
			log.error("invalid startDate or endDate, startDate: " + startDate + ", endDate: " + endDate);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid startDate or endDate");
		}

		long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
		final int MAXSUBSCRIPTIONPERIOD = 90;

		if (daysBetween > MAXSUBSCRIPTIONPERIOD) {
			log.error("daysBetween exceeded MAXSUBSCRIPTIONPERIOD, daysBetween: " + daysBetween
					+ ", MAXSUBSCRIPTIONPERIOD: " + MAXSUBSCRIPTIONPERIOD);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"The days between startDate and endDate exceeded maximum subscription period.");
		}

		repository.save(newSubscription);

		NewSubscriptionResponse response = new NewSubscriptionResponse();

		response.setAmount(newSubscription.getAmount());
		response.setSubscriptionType(newSubscription.getType());
		response.setInvoiceDates(SubscriptionHelper.getInvoiceDates(newSubscription));

		return response;
	}
}
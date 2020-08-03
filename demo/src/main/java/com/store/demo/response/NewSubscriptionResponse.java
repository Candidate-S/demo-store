package com.store.demo.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.store.demo.constant.SubscriptionType;

public class NewSubscriptionResponse {

	private BigDecimal amount;

	private SubscriptionType subscriptionType;

	private List<LocalDate> invoiceDates;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public SubscriptionType getSubscriptionType() {
		return subscriptionType;
	}

	public void setSubscriptionType(SubscriptionType subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	public List<LocalDate> getInvoiceDates() {
		return invoiceDates;
	}

	public void setInvoiceDates(List<LocalDate> invoiceDates) {
		this.invoiceDates = invoiceDates;
	}
}
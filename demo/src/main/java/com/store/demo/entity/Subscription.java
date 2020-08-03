package com.store.demo.entity;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.store.demo.constant.SubscriptionType;

@Entity
public class Subscription {

	@Id
	@GeneratedValue
	private Long id;

	private String userId;

	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	private SubscriptionType type;

	@Enumerated(EnumType.STRING)
	private DayOfWeek dayOfWeek;

	private int dateOfMonth;

	private LocalDate startDate, endDate;

	public Subscription() {
	}

	public Subscription(String userId, BigDecimal amount, SubscriptionType type, LocalDate startDate,
			LocalDate endDate) {

		this.userId = userId;
		this.amount = amount;
		this.type = type;
		this.startDate = startDate;
		this.endDate = endDate;

		if (type == null) {
			// TODO handle and log error
			return;
		}

		switch (type) {

		case WEEKLY: {
			dayOfWeek = startDate.getDayOfWeek();
			break;
		}

		case MONTHLY: {
			dateOfMonth = startDate.getDayOfMonth();
			break;
		}

		default:
			// do nothing
			break;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public SubscriptionType getType() {
		return type;
	}

	public void setType(SubscriptionType type) {
		this.type = type;
	}

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public int getDateOfMonth() {
		return dateOfMonth;
	}

	public void setDateOfMonth(int dateOfMonth) {
		this.dateOfMonth = dateOfMonth;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "Subscription [id=" + id + ", userId=" + userId + ", amount=" + amount + ", type=" + type
				+ ", dayOfWeek=" + dayOfWeek + ", dateOfMonth=" + dateOfMonth + ", startDate=" + startDate
				+ ", endDate=" + endDate + "]";
	}

}
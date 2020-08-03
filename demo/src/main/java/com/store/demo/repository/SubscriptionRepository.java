package com.store.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.demo.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

}
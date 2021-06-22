package com.rocinante.datastore.dao;

import com.rocinante.datastore.entities.Subscriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionsDao extends JpaRepository<Subscriptions, String> {}

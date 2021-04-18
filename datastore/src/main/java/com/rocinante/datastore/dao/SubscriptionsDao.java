package com.rocinante.datastore.dao;

import com.rocinante.datastore.entities.Subscriptions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionsDao extends JpaRepository<Subscriptions, String> {

}

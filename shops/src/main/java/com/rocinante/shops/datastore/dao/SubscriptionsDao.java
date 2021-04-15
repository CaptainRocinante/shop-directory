package com.rocinante.shops.datastore.dao;

import com.rocinante.shops.datastore.entities.Subscriptions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionsDao extends JpaRepository<Subscriptions, String> {

}

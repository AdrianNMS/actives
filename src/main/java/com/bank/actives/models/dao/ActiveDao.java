package com.bank.actives.models.dao;

import com.bank.actives.models.documents.Active;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ActiveDao extends ReactiveMongoRepository<Active, String> {
}

package com.bank.actives.services;

import com.bank.actives.models.utils.ResponseClient;
import reactor.core.publisher.Mono;

public interface ClientService {
    Mono<ResponseClient> findByCode(String id);
}

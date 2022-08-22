package com.bank.actives.services;

import com.bank.actives.models.documents.Active;
import com.bank.actives.models.documents.Mont;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ActiveService
{
    Mono<List<Active>> findAll();
    Mono<Active> find(String id);
    Mono<Active> create(Active active);
    Mono<Active> update(String id, Active active);
    Mono<Object> delete(String id);
    Mono<Active> getActiveCreditCard(String id, Integer type);
    Mono<Mont> creditMont(String id, String idCredit);
}

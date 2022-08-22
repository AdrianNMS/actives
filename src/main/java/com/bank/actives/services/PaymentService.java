package com.bank.actives.services;

import com.bank.actives.models.utils.ResponsePayment;
import reactor.core.publisher.Mono;

public interface PaymentService
{
    Mono<ResponsePayment> getDebt(String idActive, String idCredit);
    Mono<ResponsePayment> getBalanceClient(String idClient);
}

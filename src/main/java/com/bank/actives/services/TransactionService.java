package com.bank.actives.services;

import com.bank.actives.models.utils.ResponseTransaction;
import reactor.core.publisher.Mono;

public interface TransactionService
{
     Mono<ResponseTransaction> getDebt(String idActive, String idCredit);
     Mono<ResponseTransaction> getBalance(String idActive, String idCredit);
}

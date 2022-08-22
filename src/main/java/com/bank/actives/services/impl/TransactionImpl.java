package com.bank.actives.services.impl;

import com.bank.actives.models.utils.ResponseClient;
import com.bank.actives.models.utils.ResponseTransaction;
import com.bank.actives.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class TransactionImpl implements TransactionService
{
    @Autowired
    @Qualifier("getWebClientTransaction")
    WebClient webClient;

    @Override
    public Mono<ResponseTransaction> getDebt(String idActive, String idCredit)
    {
        return webClient.get()
                .uri("/api/transaction/debt/"+ idActive+"/"+idCredit)
                .retrieve()
                .bodyToMono(ResponseTransaction.class);
    }

    @Override
    public Mono<ResponseTransaction> getBalance(String idActive, String idCredit)
    {
        return webClient.get()
                .uri("/api/transaction/balance/"+ idActive+"/"+idCredit)
                .retrieve()
                .bodyToMono(ResponseTransaction.class);
    }

    @Override
    public Mono<ResponseTransaction> getBalanceClient(String idClient) {
        return webClient.get()
                .uri("/api/transaction/balance/client/"+ idClient)
                .retrieve()
                .bodyToMono(ResponseTransaction.class);
    }
}

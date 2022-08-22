package com.bank.actives.services.impl;

import com.bank.actives.models.utils.ResponsePayment;
import com.bank.actives.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PaymentImpl implements PaymentService
{
    @Autowired
    @Qualifier("getWebClientPayment")
    WebClient webClient;

    @Override
    public Mono<ResponsePayment> getDebt(String idActive, String idCredit)
    {
        return webClient.get()
                .uri("/api/payment/debt/"+ idActive+"/"+idCredit)
                .retrieve()
                .bodyToMono(ResponsePayment.class);
    }
}

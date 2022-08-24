package com.bank.actives.controllers.helpers;

import com.bank.actives.handler.ResponseHandler;
import com.bank.actives.models.documents.Mont;
import com.bank.actives.services.PaymentService;
import com.bank.actives.services.TransactionService;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public class ActiveGetCreditDebt
{
    public static Mono<ResponseEntity<Object>> getDebtTransaction(Logger log, TransactionService transactionService, String id, String idCredit, Float debtPayment)
    {
        return transactionService.getDebt(id,idCredit)
                .flatMap(responseTransaction ->
                {
                    if(responseTransaction.getData()!=null)
                    {
                        var mont = Mont.builder()
                                .mont(responseTransaction.getData() - debtPayment)
                                .build();

                        log.info(mont.toString());
                        return Mono.just(ResponseHandler.response("Done", HttpStatus.OK, mont));
                    }
                    else
                        return Mono.just(ResponseHandler.response("Transaction movements not found", HttpStatus.BAD_REQUEST, null));
                });
    }

    public static Mono<ResponseEntity<Object>> getDebtPayment(Logger log, TransactionService transactionService, PaymentService paymentService, String id, String idCredit)
    {
        return paymentService.getDebt(id,idCredit)
                .flatMap(responsePayment ->
                {
                    if(responsePayment.getData()!=null)
                    {
                        log.info(responsePayment.toString());
                        return getDebtTransaction(log,transactionService,id,idCredit, responsePayment.getData());
                    }
                    else
                        return Mono.just(ResponseHandler.response("Payment movements not found", HttpStatus.BAD_REQUEST, null));
                });
    }



    public static Mono<ResponseEntity<Object>> getCreditDebtSequence(Logger log, TransactionService transactionService, PaymentService paymentService, String id, String idCredit)
    {
        return getDebtPayment(log,transactionService,paymentService,id,idCredit);
    }

}

package com.bank.actives.controllers.helpers;

import com.bank.actives.handler.ResponseHandler;
import com.bank.actives.models.documents.Mont;
import com.bank.actives.services.ActiveService;
import com.bank.actives.services.TransactionService;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public class ActiveGetCurrentCredit
{
    public static Mono<ResponseEntity<Object>> getDebt(Logger log, TransactionService transactionService, String id, String idCredit, Mont mont)
    {
        return transactionService.getBalance(id,idCredit)
                .flatMap(responseTransaction ->
                {
                    if(responseTransaction.getData()!=null)
                    {
                        log.info(responseTransaction.toString());
                        mont.setMont(mont.getMont() - responseTransaction.getData());
                        return Mono.just(ResponseHandler.response("Done", HttpStatus.OK, mont));
                    }
                    else
                        return Mono.just(ResponseHandler.response("Transaction movements not found", HttpStatus.BAD_REQUEST, null));
                });
    }

    public static Mono<ResponseEntity<Object>> getActiveCredit(Logger log, TransactionService transactionService, ActiveService activeService, String id, String idCredit)
    {
        return activeService.creditMont(id,idCredit)
                .flatMap(mont -> {
                    if(mont!=null)
                    {
                        log.info(mont.toString());
                        return getDebt(log,transactionService,id,idCredit,mont);
                    }
                    else
                        return Mono.just(ResponseHandler.response("Credit not found", HttpStatus.BAD_REQUEST, null));

                });
    }

    public static Mono<ResponseEntity<Object>> getCurrentCreditSequence(Logger log, TransactionService transactionService, ActiveService activeService, String id, String idCredit)
    {
        return getActiveCredit(log,transactionService, activeService,id,idCredit);
    }
}

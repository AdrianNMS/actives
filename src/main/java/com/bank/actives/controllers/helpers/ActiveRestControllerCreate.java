package com.bank.actives.controllers.helpers;

import com.bank.actives.handler.ResponseHandler;
import com.bank.actives.models.documents.Active;
import com.bank.actives.models.documents.Credit;
import com.bank.actives.services.ActiveService;
import com.bank.actives.services.ClientService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class ActiveRestControllerCreate
{
    public static Mono<ResponseEntity<Object>> SaveActive(Logger log, ActiveService activeService, Active act)
    {
        return activeService.create(act)
                .doOnNext(active -> log.info(active.toString()))
                .flatMap(active -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, active))            )
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));

    }

    public static Mono<ResponseEntity<Object>> CheckClient(Logger log, ActiveService activeService, ClientService clientService, Active act)
    {
        return clientService.findByCode(act.getClientId())
                .doOnNext(transaction -> log.info(transaction.toString()))
                .flatMap(responseClient -> {
                    if(responseClient.getData() == null){
                        return Mono.just(ResponseHandler.response("Does not have client", HttpStatus.BAD_REQUEST, null));
                    }

                    if(responseClient.getData().getType().equals("PERSONAL")){
                        if(act.getCredits().size()>1){
                            return Mono.just(ResponseHandler.response(
                                    "Only one credit per person is allowed", HttpStatus.BAD_REQUEST, null));
                        }
                    }

                    return SaveActive(log,activeService,act);
                });
    }

    public static Mono<ResponseEntity<Object>> CreateActiveSequence(Logger log, ActiveService activeService, ClientService clientService, Active act)
    {
        return CheckClient(log,activeService,clientService,act);
    }
}

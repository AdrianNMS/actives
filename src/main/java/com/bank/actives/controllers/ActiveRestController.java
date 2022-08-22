package com.bank.actives.controllers;

import com.bank.actives.controllers.helpers.ActiveGetCreditDebt;
import com.bank.actives.controllers.helpers.ActiveGetCurrentCredit;
import com.bank.actives.controllers.helpers.ActiveRestControllerCreate;
import com.bank.actives.handler.ResponseHandler;
import com.bank.actives.models.documents.Active;
import com.bank.actives.models.documents.Mont;
import com.bank.actives.services.ActiveService;
import com.bank.actives.services.ClientService;
import com.bank.actives.services.PaymentService;
import com.bank.actives.services.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/active")
public class ActiveRestController
{
    @Autowired
    private ActiveService activeService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private PaymentService paymentService;

    private static final Logger log = LoggerFactory.getLogger(ActiveRestController.class);

    @GetMapping
    public Mono<ResponseEntity<Object>> findAll()
    {
        log.info("[INI] findAll Active");
        return activeService.findAll()
                .flatMap(actives -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, actives)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] findAll Active"));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> find(@PathVariable String id)
    {
        log.info("[INI] find Active");
        return activeService.find(id)
                .flatMap(active -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, active)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] find Active"));
    }

    @PostMapping
    public Mono<ResponseEntity<Object>> create(@Valid @RequestBody  Active act)
    {
        log.info("[INI] create Active");
        return ActiveRestControllerCreate.CreateActiveSequence(log,activeService,clientService,act)
                .switchIfEmpty(Mono.just(ResponseHandler.response("Client No Content", HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] create Active"));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Object>> update(@PathVariable("id") String id,@Valid @RequestBody Active act)
    {
        log.info("[INI] update Active");

        return activeService.update(id,act)
                .flatMap(active -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, active))
                .doFinally(fin -> log.info("[END] update Active")));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> delete(@PathVariable("id") String id)
    {
        log.info("[INI] delete Active");

        return activeService.delete(id)
                .flatMap(active -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, null))
                .doFinally(fin -> log.info("[END] delete Active")));

    }

    @GetMapping("/mont/{id}/{idCredit}")
    public Mono<ResponseEntity<Object>> getMontData(@PathVariable String id,@PathVariable String idCredit) {
        log.info("[INI] getMontData");
        return activeService.creditMont(id,idCredit)
                .flatMap(mont -> {
                    if(mont!=null)
                        return Mono.just(ResponseHandler.response("Done", HttpStatus.OK, mont));
                    else
                        return Mono.just(ResponseHandler.response("Empty", HttpStatus.NO_CONTENT, null));

                })
                .doFinally(fin -> log.info("[END] getMontData"));
    }

    @GetMapping("/creditcard/{id}/{type}")
    public Mono<ResponseEntity<Object>> checkCreditCard(@PathVariable String id,@PathVariable Integer type)
    {
        log.info("[INI] check active credit card");
        return activeService.getActiveCreditCard(id,type)
                .flatMap(active -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, active)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("Empty", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] check active credit card"));
    }

    @GetMapping("/type/{id}")
    public Mono<ResponseEntity<Object>> findType(@PathVariable String id)
    {
        log.info("[INI] findType Active");
        return activeService.find(id)
                .flatMap(active ->
                     Mono.just(ResponseHandler.response("Done", HttpStatus.OK, active.getActiveType().value))
                )
                .doFinally(fin -> log.info("[END] findType Active"));
    }

    @GetMapping("/debt/{id}/{idCredit}")
    public Mono<ResponseEntity<Object>> getCreditDebt(@PathVariable String id,@PathVariable String idCredit)
    {
        log.info("[INI] getCreditDebt Active");

        return ActiveGetCreditDebt.getCreditDebtSequence(log,transactionService,paymentService,id,idCredit)
                .doFinally(fin -> log.info("[END] getCreditDebt Active"));
    }

    @GetMapping("/credit/{id}/{idCredit}")
    public Mono<ResponseEntity<Object>> getCurrentCredit(@PathVariable String id,@PathVariable String idCredit)
    {
        log.info("[INI] getCurrentCredit Active");

        return ActiveGetCurrentCredit.getCurrentCreditSequence(log,transactionService,activeService,id,idCredit)
                .doFinally(fin -> log.info("[END] getCurrentCredit Active"));
    }

}

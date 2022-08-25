package com.bank.actives.controllers;

import com.bank.actives.controllers.helpers.ActiveGetClientDebt;
import com.bank.actives.controllers.helpers.ActiveGetCreditDebt;
import com.bank.actives.controllers.helpers.ActiveGetCurrentCredit;
import com.bank.actives.controllers.helpers.ActiveRestControllerCreate;
import com.bank.actives.handler.ResponseHandler;
import com.bank.actives.models.documents.Active;
import com.bank.actives.services.ActiveService;
import com.bank.actives.services.ClientService;
import com.bank.actives.services.PaymentService;
import com.bank.actives.services.TransactionService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
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

    private static final String RESILENCE_SERVICE = "defaultConfig";

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
    @TimeLimiter(name = RESILENCE_SERVICE)
    @CircuitBreaker(name = RESILENCE_SERVICE,fallbackMethod ="failedCreate")
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
    @TimeLimiter(name = RESILENCE_SERVICE)
    @CircuitBreaker(name = RESILENCE_SERVICE,fallbackMethod ="failedCreditDebt")
    public Mono<ResponseEntity<Object>> getCreditDebt(@PathVariable String id,@PathVariable String idCredit)
    {
        log.info("[INI] getCreditDebt Active");

        return ActiveGetCreditDebt.getCreditDebtSequence(log,transactionService,paymentService,id,idCredit)
                .doFinally(fin -> log.info("[END] getCreditDebt Active"));
    }

    @GetMapping("/credit/{id}/{idCredit}")
    @TimeLimiter(name = RESILENCE_SERVICE)
    @CircuitBreaker(name = RESILENCE_SERVICE,fallbackMethod ="failedCurrentCredit")
    public Mono<ResponseEntity<Object>> getCurrentCredit(@PathVariable String id,@PathVariable String idCredit)
    {
        log.info("[INI] getCurrentCredit Active");

        return ActiveGetCurrentCredit.getCurrentCreditSequence(log,transactionService,activeService,id,idCredit)
                .doFinally(fin -> log.info("[END] getCurrentCredit Active"));
    }

    @GetMapping("/debt/client/{idClient}")
    @TimeLimiter(name = RESILENCE_SERVICE)
    @CircuitBreaker(name = RESILENCE_SERVICE,fallbackMethod ="failedCurrentClientCredit")
    public Mono<ResponseEntity<Object>> getCurrentClientCredit(@PathVariable String idClient)
    {
        log.info("[INI] getCurrentClientCredit Active");

        return ActiveGetClientDebt.getClientDebtSequence(log,transactionService,paymentService,idClient)
                .doFinally(fin -> log.info("[END] getCurrentClientCredit Active"));
    }

    public Mono<ResponseEntity<Object>> failedCreate(Active act, RuntimeException e)
    {
        log.error("[INIT] failedCreate");
        log.error(e.getMessage());
        log.error(act.toString());
        log.error("[END] failedCreate");
        return Mono.just(ResponseHandler.response("Overcharged method", HttpStatus.OK, null));
    }

    public Mono<ResponseEntity<Object>> failedCreditDebt(String id, String idCredit, RuntimeException e)
    {
        log.error("[INIT] failedCreditDebt");
        log.error(e.getMessage());
        log.error(id);
        log.error(idCredit);
        log.error("[END] failedCreditDebt");
        return Mono.just(ResponseHandler.response("Overcharged method", HttpStatus.OK, null));
    }

    public Mono<ResponseEntity<Object>> failedCurrentCredit(String id, String idCredit, RuntimeException e)
    {
        log.error("[INIT] failedCurrentCredit");
        log.error(e.getMessage());
        log.error(id);
        log.error(idCredit);
        log.error("[END] failedCurrentCredit");
        return Mono.just(ResponseHandler.response("Overcharged method", HttpStatus.OK, null));
    }

    public Mono<ResponseEntity<Object>> failedCurrentClientCredit(String idClient, RuntimeException e)
    {
        log.error("[INIT] failedCurrentClientCredit");
        log.error(e.getMessage());
        log.error(idClient);
        log.error("[END] failedCurrentClientCredit");
        return Mono.just(ResponseHandler.response("Overcharged method", HttpStatus.OK, null));
    }





}

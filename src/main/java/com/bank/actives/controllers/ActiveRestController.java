package com.bank.actives.controllers;

import com.bank.actives.handler.ResponseHandler;
import com.bank.actives.models.dao.ActiveDao;
import com.bank.actives.models.documents.Active;
import com.bank.actives.models.documents.Credit;
import com.bank.actives.models.documents.Mont;
import com.bank.actives.services.ClientService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/active")
public class ActiveRestController
{
    @Autowired
    private ActiveDao dao;
    @Autowired
    private ClientService clientService;
    private static final Logger log = LoggerFactory.getLogger(ActiveRestController.class);

    @GetMapping
    public Mono<ResponseEntity<Object>> findAll()
    {
        log.info("[INI] findAll Active");
        return dao.findAll()
                .doOnNext(active -> log.info(active.toString()))
                .collectList()
                .flatMap(actives -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, actives)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] findAll Active"));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> find(@PathVariable String id)
    {
        log.info("[INI] find Active");
        return dao.findById(id)
                .doOnNext(active -> log.info(active.toString()))
                .flatMap(active -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, active)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] find Active"));
    }

    @PostMapping
    public Mono<ResponseEntity<Object>> create(@Valid @RequestBody  Active act)
    {
        log.info("[INI] create Active");
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

                    act.getCredits().forEach(credit -> credit.setId(new ObjectId().toString()));
                    act.setDateRegister(LocalDateTime.now());
                    return dao.save(act)
                            .doOnNext(active -> log.info(active.toString()))
                            .map(active -> ResponseHandler.response("Done", HttpStatus.OK, active)                )
                            .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                            ;

                })
                .switchIfEmpty(Mono.just(ResponseHandler.response("Client No Content", HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] create Active"));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Object>> update(@PathVariable("id") String id,@Valid @RequestBody Active act)
    {
        log.info("[INI] update Active");

        return dao.existsById(id).flatMap(check -> {
            if (check){
                act.setDateUpdate(LocalDateTime.now());
                return dao.save(act)
                        .doOnNext(active -> log.info(active.toString()))
                        .map(active -> ResponseHandler.response("Done", HttpStatus.OK, active)                )
                        .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
            }
            else
                return Mono.just(ResponseHandler.response("Not found", HttpStatus.NOT_FOUND, null));

        }).doFinally(fin -> log.info("[END] update Active"));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> delete(@PathVariable("id") String id)
    {
        log.info("[INI] delete Active");

        return dao.existsById(id).flatMap(check -> {
            if (check)
                return dao.deleteById(id).then(Mono.just(ResponseHandler.response("Done", HttpStatus.OK, null)));
            else
                return Mono.just(ResponseHandler.response("Not found", HttpStatus.NOT_FOUND, null));
        }).doFinally(fin -> log.info("[END] update Active"));

    }

    @GetMapping("/mont/{id}/{idCredit}")
    public Mono<ResponseEntity<Object>> getMontData(@PathVariable String id,@PathVariable String idCredit) {
        log.info("[INI] Find Pasive");
        return dao.findById(id)
                .doOnNext(active -> log.info(active.toString()))
                .flatMap(active -> {
                    Optional<Credit> existCredit = active.getCredits()
                            .stream()
                            .filter(credit -> credit.getId().equals(idCredit))
                            .findFirst();

                    if(existCredit.isPresent())
                    {
                        Mont mont = new Mont();
                        mont.setMont(existCredit.get().getCreditMont());
                        return Mono.just(ResponseHandler.response("Done", HttpStatus.OK, mont));
                    }
                    else
                        return Mono.just(ResponseHandler.response("Empty", HttpStatus.NO_CONTENT, null));

                })
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("Empty", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] Find Pasive"));
    }

    @GetMapping("/creditcard/{id}/{idCreditCard}")
    public Mono<ResponseEntity<Object>> checkCreditCard(@PathVariable String id,@PathVariable int idCreditCard)
    {
        log.info("[INI] check active credit card");
        return dao.findAll()
                .filter(active ->
                        active.getClientId().equals(id) && !active.getCredits().isEmpty() &&active.getActiveType().value == idCreditCard)
                .collectList()
                .flatMap(actives -> {
                    Optional<Active> existActive = actives.stream().findFirst();

                    if(existActive.isPresent())
                        return Mono.just(ResponseHandler.response("Done", HttpStatus.OK, true));
                    else
                        return Mono.just(ResponseHandler.response("Not Found", HttpStatus.BAD_REQUEST, null));
                })
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] check active credit card"));
    }

    @GetMapping("/{type}/{id}")
    public Mono<ResponseEntity<Object>> findType(@PathVariable Integer type, @PathVariable String id)
    {
        log.info("[INI] findType Active");
        return dao.findById(id)
                .doOnNext(active -> log.info(active.toString()))
                .flatMap(active ->
                {
                    if(active.getActiveType().getValue() == type)
                        return Mono.just(ResponseHandler.response("Done", HttpStatus.OK, true));
                    else
                        return Mono.just(ResponseHandler.response("Types not equals", HttpStatus.BAD_REQUEST, null));
                })
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] findType Active"));
    }
}

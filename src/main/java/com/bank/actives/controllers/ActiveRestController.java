package com.bank.actives.controllers;

import com.bank.actives.handler.ResponseHandler;
import com.bank.actives.models.dao.ActiveDao;
import com.bank.actives.models.documents.Active;
import com.bank.actives.services.ParameterService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/active")
public class ActiveRestController
{
    @Autowired
    private ActiveDao dao;

    @Autowired
    private ParameterService parameterService;
    private static final Logger log = LoggerFactory.getLogger(ActiveRestController.class);

    @GetMapping
    public Mono<ResponseEntity<Object>> findAll()
    {
        log.info("[INI] findAll Active");
        return dao.findAll()
                .doOnNext(active -> log.info(active.toString()))
                .collectList()
                .map(actives -> ResponseHandler.response("Done", HttpStatus.OK, actives))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] findAll Active"));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> find(@PathVariable String id)
    {
        log.info("[INI] find Active");
        return dao.findById(id)
                .doOnNext(active -> log.info(active.toString()))
                .map(active -> ResponseHandler.response("Done", HttpStatus.OK, active))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] find Active"));
    }

    @PostMapping
    public Mono<ResponseEntity<Object>> create(@RequestBody Active act)
    {
        log.info("[INI] create Active");

        act.getCredits().forEach(credit -> credit.setId(new ObjectId().toString()));

        return dao.save(act)
                .doOnNext(active ->
                {
                    log.info(active.toString());

                })
                .map(active -> ResponseHandler.response("Done", HttpStatus.OK, active)                )
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] create Active"));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Object>> update(@PathVariable("id") String id, @RequestBody Active act)
    {
        log.info("[INI] update Active");

        return dao.existsById(id).flatMap(check -> {
            if (check)
                return dao.save(act)
                        .doOnNext(active -> log.info(active.toString()))
                        .map(active -> ResponseHandler.response("Done", HttpStatus.OK, active)                )
                        .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
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

    @GetMapping("/type/{id}")
    public Mono<ResponseEntity<Object>> FindType(@PathVariable String id) {
        log.info("[INI] Find Type Active");
        return dao.findById(id)
                .doOnNext(pasive -> log.info(pasive.toString()))
                .flatMap(pasive ->
                        {
                            return parameterService.findByCode(pasive.getActiveType().getValue())
                                    .doOnNext(responseParameter -> log.info(responseParameter.toString()))
                                    .flatMap(responseParameter ->
                                    {
                                        if(!responseParameter.getData().isEmpty())
                                        {
                                            return Mono.just(ResponseHandler.response("Done", HttpStatus.OK, responseParameter.getData()));
                                        }
                                        else
                                        {
                                            return Mono.just(ResponseHandler.response("Empty", HttpStatus.NO_CONTENT, null));
                                        }
                                    });
                        }
                )
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("Empty", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] Find Type Pasive"));

    }
}

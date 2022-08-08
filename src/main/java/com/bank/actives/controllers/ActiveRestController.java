package com.bank.actives.controllers;

import com.bank.actives.handler.ResponseHandler;
import com.bank.actives.models.dao.ActiveDao;
import com.bank.actives.models.documents.Active;
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
    private static final Logger log = LoggerFactory.getLogger(ActiveRestController.class);

    @GetMapping
    public Mono<ResponseEntity<Object>> findAll()
    {
        return dao.findAll()
                .doOnNext(active -> log.info(active.toString()))
                .collectList()
                .map(actives -> ResponseHandler.response("Done", HttpStatus.OK, actives))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> find(@PathVariable String id)
    {
        return dao.findById(id)
                .doOnNext(active -> log.info(active.toString()))
                .map(active -> ResponseHandler.response("Done", HttpStatus.OK, active))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
    }

    @PostMapping
    public Mono<ResponseEntity<Object>> create(@RequestBody Active act)
    {

        return dao.save(act)
                .doOnNext(active -> log.info(active.toString()))
                .map(active -> ResponseHandler.response("Done", HttpStatus.OK, active)                )
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Object>> update(@PathVariable("id") String id, @RequestBody Active act)
    {
        return dao.existsById(id).flatMap(check -> {
            if (check)
                return dao.save(act)
                        .doOnNext(active -> log.info(active.toString()))
                        .map(active -> ResponseHandler.response("Done", HttpStatus.OK, active)                )
                        .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
            else
                return Mono.just(ResponseHandler.response("Not found", HttpStatus.NOT_FOUND, null));

        });
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> delete(@PathVariable("id") String id)
    {
        log.info(id);

        return dao.existsById(id).flatMap(check -> {
            if (check)
                return dao.deleteById(id).then(Mono.just(ResponseHandler.response("Done", HttpStatus.OK, null)));
            else
                return Mono.just(ResponseHandler.response("Not found", HttpStatus.NOT_FOUND, null));
        });
    }
}

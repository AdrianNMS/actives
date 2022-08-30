package com.bank.actives.services.impl;

import com.bank.actives.models.dao.ActiveDao;
import com.bank.actives.models.documents.Active;
import com.bank.actives.models.documents.Credit;
import com.bank.actives.models.documents.Mont;
import com.bank.actives.models.enums.ActiveType;
import com.bank.actives.services.ActiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActiveImpl implements ActiveService
{
    @Autowired
    private ActiveDao dao;

    @Override
    public Mono<List<Active>> findAll() {
        return dao.findAll()
                .collectList();
    }

    @Override
    public Mono<Active> find(String id) {
        return dao.findById(id);
    }

    @Override
    public Mono<Active> create(Active active) {
        return dao.save(active);
    }

    @Override
    public Mono<Active> update(String id, Active active) {
        return dao.existsById(id).flatMap(check -> {
            if (Boolean.TRUE.equals(check))
            {
                active.setId(id);
                active.setDateUpdate(LocalDateTime.now());
                return dao.save(active);
            }
            else
                return Mono.empty();
        });
    }

    @Override
    public Mono<Object> delete(String id) {
        return dao.existsById(id).flatMap(check -> {
            if (Boolean.TRUE.equals(check))
                return dao.deleteById(id).then(Mono.just(true));
            else
                return Mono.empty();
        });
    }
    @Override
    public Mono<Active> getActiveCreditCard(String id, Integer type)
    {
        return  findAll()
                .flatMap(actives -> {
                    Optional<Active> existActive = actives.stream()
                            .filter(active -> active.getClientId().equals(id)
                                    && !active.getCredits().isEmpty()
                                    && active.getActiveType() != ActiveType.PERSONAL_CREDIT
                                    && active.getActiveType() != ActiveType.COMPANY_CREDIT
                                    && active.getActiveType().value == type)
                            .findFirst();

                    if(existActive.isPresent())
                        return Mono.just(null);
                    else
                        return Mono.just(existActive.get());

                });
    }

    @Override
    public Mono<Mont> creditMont(String id, String idCredit)
    {
        return  find(id).flatMap(active -> {
            Optional<Credit> existCredit = active.getCredits()
                    .stream()
                    .filter(credit -> credit.getId().equals(idCredit))
                    .findFirst();

            if(existCredit.isPresent())
            {
                var mont = Mont.builder()
                        .mont(existCredit.get().getCreditMont())
                        .build();
                return Mono.just(mont);
            }
            else
                return Mono.just(null);

        });
    }
}

package com.bank.actives.controller;

import com.bank.actives.controller.models.ResponseActive;
import com.bank.actives.models.documents.Active;
import com.bank.actives.models.documents.Credit;
import com.bank.actives.models.enums.ActiveType;
import com.bank.actives.services.ActiveService;
import com.bank.actives.services.ClientService;
import com.bank.actives.services.PaymentService;
import com.bank.actives.services.TransactionService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@WebFluxTest
public class ActiveRestControllersTest
{
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    @Autowired
    ActiveService activeService;

    @MockBean
    @Autowired
    ClientService clientService;

    @MockBean
    @Autowired
    TransactionService transactionService;

    @MockBean
    @Autowired
    PaymentService paymentService;


    @Test
    void findAll()
    {
        var listCredit = new ArrayList<Credit>(){{
            add(Credit.builder()
                    .id("10")
                    .creditMont(1000f)
                    .build());
        }};

        var active = Active.builder()
                .id("1")
                .clientId("1")
                .activeType(ActiveType.PERSONAL_CREDIT)
                .credits(listCredit)
                .build();

        var activeFlux = Flux.just(active);

        Mockito.when(activeService.findAll()).thenReturn(activeFlux.collectList());

        webTestClient.get().uri("/api/active")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ResponseActive<List<Active>>>(){})
                .value(responseParameterFindAll -> {
                    var parameterList = responseParameterFindAll.getData();
                    parameterList.forEach(parameter1 -> {
                        Assertions.assertThat(parameter1.getId()).isEqualTo("1");
                    });
                });
    }

    @Test
    void find()
    {
        var listCredit = new ArrayList<Credit>(){{
            add(Credit.builder()
                    .id("10")
                    .creditMont(1000f)
                    .build());
        }};

        var active = Active.builder()
                .id("1")
                .clientId("1")
                .activeType(ActiveType.PERSONAL_CREDIT)
                .credits(listCredit)
                .build();

        var activeMono = Mono.just(active);
        Mockito.when(activeService.find("1")).thenReturn(activeMono);

        webTestClient.get().uri("/api/active/{id}","1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ResponseActive<Active>>(){})
                .value(responseActive -> {
                    var activeR = responseActive.getData();
                    Assertions.assertThat(activeR.getId()).isEqualTo("1");
                });
    }

    @Test
    void create()
    {

    }

    @Test
    void update()
    {
        var listCredit = new ArrayList<Credit>(){{
            add(Credit.builder()
                    .id("10")
                    .creditMont(1000f)
                    .build());
        }};

        var active = Active.builder()
                .id("1")
                .clientId("1")
                .activeType(ActiveType.PERSONAL_CREDIT)
                .credits(listCredit)
                .build();

        Mono<Active> activeMono = Mono.just(active);
        Mockito.when(activeService.update("1",active)).thenReturn(activeMono);

        webTestClient.put()
                .uri("/api/active/{id}","1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(active), Active.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ResponseActive<Active>>(){})
                .value(responseActive -> {
                    var activeR = responseActive.getData();
                    Assertions.assertThat(activeR.getId()).isEqualTo("1");
                });
    }

    @Test
    void delete()
    {
        Mono<Object> obj = Mono.just(true);
        Mockito.when(activeService.delete("1")).thenReturn(obj);

        webTestClient.delete().uri("/api/active/{id}","1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ResponseActive<Active>>(){})
                .value(responseActive -> {
                    Assertions.assertThat(responseActive.getStatus()).isEqualTo("OK");
                });
    }
}

package com.bank.actives.controller;

import com.bank.actives.controller.models.ResponseActive;
import com.bank.actives.models.documents.Active;
import com.bank.actives.models.documents.Client;
import com.bank.actives.models.documents.Credit;
import com.bank.actives.models.documents.Mont;
import com.bank.actives.models.enums.ActiveType;
import com.bank.actives.models.utils.ResponseClient;
import com.bank.actives.services.ActiveService;
import com.bank.actives.services.ClientService;
import com.bank.actives.services.PaymentService;
import com.bank.actives.services.TransactionService;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@WebFluxTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ActiveRestControllersTest
{
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    @Autowired
    private ActiveService activeService;

    @MockBean
    @Autowired
    private ClientService clientService;

    @MockBean
    @Autowired
    private TransactionService transactionService;

    @MockBean
    @Autowired
    private PaymentService paymentService;

    @MockBean
    private Active active;

    @BeforeEach
    public void setupMock()
    {
        active = Active.builder()
            .id("1")
            .clientId("1")
            .activeType(ActiveType.PERSONAL_CREDIT)
            .credits(new ArrayList<>(){{
                add(Credit.builder()
                        .id("10")
                        .creditMont(1000f)
                        .build());
            }})
            .build();

        var responseClient = ResponseClient.builder()
                .data(Client.builder()
                        .type("PERSONAL")
                        .build())
                .build();

        var mont = Mont.builder()
                .mont(100f)
                .build();

        var activeFlux = Flux.just(active);
        var activeMono = Mono.just(active);
        var responseClientMono = Mono.just(responseClient);


        Mockito.when(activeService.findAll()).thenReturn(activeFlux.collectList());
        Mockito.when(activeService.find("1")).thenReturn(activeMono);
        Mockito.when(activeService.create(active)).thenReturn(activeMono);
        Mockito.when(activeService.update("1",active)).thenReturn(activeMono);
        Mockito.when(activeService.delete("1")).thenReturn(Mono.just(true));
        Mockito.when(activeService.creditMont("1","10")).thenReturn(Mono.just(mont));

        Mockito.when(clientService.findByCode(active.getId())).thenReturn(responseClientMono);
    }

    @Test
    void findAll()
    {
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
        webTestClient.post()
                .uri("/api/active/")
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
    void update()
    {
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

    @Test
    void getMontData()
    {
        webTestClient.get().uri("/api/active/mont/{id}/{idCredit}","1","10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ResponseActive<Mont>>(){})
                .value(responseActive -> {
                    Assertions.assertThat(responseActive.getData().getMont()).isEqualTo(100f);
                });
    }

    @Test
    void checkCreditCard()
    {

    }

    @Test
    void findType()
    {

    }

    @Test
    void getCreditDebt()
    {

    }

    @Test
    void getCurrentCredit()
    {

    }

    @Test
    void getCurrentClientCredit()
    {

    }


}

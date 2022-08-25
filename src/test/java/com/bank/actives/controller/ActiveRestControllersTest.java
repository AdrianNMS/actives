package com.bank.actives.controller;

import com.bank.actives.controller.models.ResponseActive;
import com.bank.actives.models.documents.Active;
import com.bank.actives.models.documents.Client;
import com.bank.actives.models.documents.Credit;
import com.bank.actives.models.documents.Mont;
import com.bank.actives.models.enums.ActiveType;
import com.bank.actives.models.utils.ResponseClient;
import com.bank.actives.models.utils.ResponsePayment;
import com.bank.actives.models.utils.ResponseTransaction;
import com.bank.actives.services.ActiveService;
import com.bank.actives.services.ClientService;
import com.bank.actives.services.PaymentService;
import com.bank.actives.services.TransactionService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
            .clientId("11")
            .activeType(ActiveType.PERSONAL_CREDIT_CARD)
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
                .mont(1000f)
                .build();

        var responsePayment = ResponsePayment.builder()
                .data(100f)
                .build();

        var responseTransaction = ResponseTransaction.builder()
                .data(100f)
                .build();

        var activeFlux = Flux.just(active);
        var activeMono = Mono.just(active);
        var responseClientMono = Mono.just(responseClient);


        Mockito.when(activeService.findAll())
                .thenReturn(activeFlux.collectList());
        Mockito.when(activeService.find("1"))
                .thenReturn(activeMono);
        Mockito.when(activeService.create(active))
                .thenReturn(activeMono);
        Mockito.when(activeService.update("1",active))
                .thenReturn(activeMono);
        Mockito.when(activeService.delete("1"))
                .thenReturn(Mono.just(true));
        Mockito.when(activeService.creditMont("1","10"))
                .thenReturn(Mono.just(mont));
        Mockito.when(activeService.getActiveCreditCard("1",2))
                .thenReturn(activeMono);

        Mockito.when(clientService.findByCode("11")).thenReturn(responseClientMono);

        Mockito.when(paymentService.getDebt("1","10"))
                .thenReturn(Mono.just(responsePayment));
        Mockito.when(paymentService.getBalanceClient("11"))
                .thenReturn(Mono.just(responsePayment));

        Mockito.when(transactionService.getDebt("1","10"))
                .thenReturn(Mono.just(responseTransaction));
        Mockito.when(transactionService.getBalance("1","10"))
                .thenReturn(Mono.just(responseTransaction));
        Mockito.when(transactionService.getBalanceClient("11"))
                .thenReturn(Mono.just(responseTransaction));
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
                    Assertions.assertThat(responseActive.getData().getMont()).isEqualTo(1000f);
                });
    }

    @Test
    void checkCreditCard()
    {
        webTestClient.get().uri("/api/active/creditcard/{id}/{type}","1",2)
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
    void findType()
    {
        webTestClient.get().uri("/api/active/type/{id}","1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ResponseActive<Integer>>(){})
                .value(responseActive -> {
                    var activeR = responseActive.getData();
                    Assertions.assertThat(activeR).isEqualTo(2002);
                });
    }

    @Test
    void getCreditDebt()
    {
        webTestClient.get().uri("/api/active/debt/{id}/{idCredit}","1","10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ResponseActive<Mont>>(){})
                .value(responseActive -> {
                    var activeR = responseActive.getData();
                    Assertions.assertThat(activeR.getMont()).isEqualTo(0f);
                });
    }

    @Test
    void getCurrentCredit()
    {
        webTestClient.get().uri("/api/active/credit/{id}/{idCredit}","1","10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ResponseActive<Mont>>(){})
                .value(responseActive -> {
                    var activeR = responseActive.getData();
                    Assertions.assertThat(activeR.getMont()).isEqualTo(900f);
                });
    }

    @Test
    void getCurrentClientCredit()
    {
        webTestClient.get().uri("/api/active/debt/client/{idClient}","11")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ResponseActive<Mont>>(){})
                .value(responseActive -> {
                    var activeR = responseActive.getData();
                    Assertions.assertThat(activeR.getMont()).isEqualTo(0f);
                });
    }


}

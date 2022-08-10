package com.bank.actives.services;

import com.bank.actives.models.documents.Parameter;
import com.bank.actives.models.utils.ResponseParameter;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ParameterService {

    Mono<ResponseParameter> findByCode(Integer code);

    List<Parameter> getParameter(List<Parameter> listParameter, Integer code);
}

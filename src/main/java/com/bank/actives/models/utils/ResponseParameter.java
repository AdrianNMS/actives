package com.bank.actives.models.utils;

import com.bank.actives.models.documents.Parameter;
import lombok.Data;

import java.util.List;

@Data
public class ResponseParameter
{
    private List<Parameter> data;

    private String message;

    private String status;

}

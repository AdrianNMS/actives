package com.bank.actives.models.utils;

import com.bank.actives.models.documents.Client;
import lombok.Data;

@Data
public class ResponseClient
{
    private Client data;

    private String message;

    private String status;

}

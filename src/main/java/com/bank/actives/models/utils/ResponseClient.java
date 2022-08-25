package com.bank.actives.models.utils;

import com.bank.actives.models.documents.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseClient
{
    private Client data;

    private String message;

    private String status;

}

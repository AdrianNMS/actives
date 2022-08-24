package com.bank.actives.controller.models;

import lombok.Data;

@Data
public class ResponseActive <T>
{
    private T data;

    private String message;

    private String status;
}

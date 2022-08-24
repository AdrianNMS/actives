package com.bank.actives.models.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Client
{
    private String id;
    private String type;
    private String firstname;
    private String lastName;
    private String genre;
    private String documentId;
    private String phoneNumber;
    private String email;
}

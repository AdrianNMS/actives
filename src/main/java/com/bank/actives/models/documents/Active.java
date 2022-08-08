package com.bank.actives.models.documents;

import com.bank.actives.models.enums.ActiveType;
import com.bank.actives.models.utils.Audit;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "actives")
public class Active extends Audit
{
    @Id
    private String id;
    private String clientId;
    private ActiveType activeType;
    private List<Credit> credits;
}

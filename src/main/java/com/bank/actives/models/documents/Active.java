package com.bank.actives.models.documents;

import com.bank.actives.models.enums.ActiveType;
import com.bank.actives.models.utils.Audit;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Document(collection = "actives")
public class Active extends Audit
{
    @Id
    private String id;

    @NotNull(message = "clientId must not be null")
    private String clientId;
    @NotNull(message = "activeType must not be null")
    private ActiveType activeType;
    @NotNull(message = "credits must not be null")
    private List<Credit> credits;
}

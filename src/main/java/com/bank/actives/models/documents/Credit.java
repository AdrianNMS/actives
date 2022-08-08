package com.bank.actives.models.documents;

import com.bank.actives.models.utils.Audit;
import lombok.Data;

@Data
public class Credit extends Audit
{
    private String id;
    private Float creditMont;
}

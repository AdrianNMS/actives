package com.bank.actives.models.documents;

import com.bank.actives.models.utils.Audit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Credit extends Audit
{
    private String id;
    private Float creditMont;
}

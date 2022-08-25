package com.bank.actives.models.documents;

import com.bank.actives.models.utils.Audit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@Builder
public class Credit extends Audit
{
    private String id;
    private Float creditMont;

    public Credit() {
        id = new ObjectId().toString();
    }
}

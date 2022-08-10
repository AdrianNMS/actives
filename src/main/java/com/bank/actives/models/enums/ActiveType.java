package com.bank.actives.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActiveType {
    PERSONAL_CREDIT(2000),
    COMPANY_CREDIT(2001),
    PERSONAL_CREDIT_CARD(2002),
    COMPANY_CREDIT_CARD(2003);

    public final int value;
}

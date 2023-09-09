package com.example.account.validation;

import com.example.account.entity.Currency;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.List;

public class CurrencyListValidator implements ConstraintValidator<ValidateCurrencyList, List<Currency>> {
    private List<Currency> allowedValues;
    @Override
    public void initialize(ValidateCurrencyList constraintAnnotation) {

        allowedValues = List.of(Currency.class.getEnumConstants());
    }

    @Override
    public boolean isValid(List<Currency> value, ConstraintValidatorContext context) {
        return new HashSet<>(allowedValues).containsAll(value);
    }
}

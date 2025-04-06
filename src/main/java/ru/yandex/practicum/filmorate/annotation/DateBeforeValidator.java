package ru.yandex.practicum.filmorate.annotation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateBeforeValidator implements ConstraintValidator<DateBefore, LocalDate> {

    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        if (releaseDate == null) {
            return true;
        }
        return !releaseDate.isBefore(MIN_DATE);
    }
}

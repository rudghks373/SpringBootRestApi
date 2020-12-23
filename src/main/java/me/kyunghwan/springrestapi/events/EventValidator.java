package me.kyunghwan.springrestapi.events;

import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Controller
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors) {
        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0) {
            errors.reject("wrongPrices", "Values fo prices are wrong");
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
                endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
                endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong");
        }

        // TODO BeginEventDateTime
        // TODO CloseEnrollmentDateTime
    }

}

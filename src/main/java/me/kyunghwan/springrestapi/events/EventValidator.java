package me.kyunghwan.springrestapi.events;

import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Controller
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors){
        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0){
            errors.rejectValue("basePrice" , "WrongValue", "Base is wrong");
            errors.rejectValue("maxPrice" , "WrongValue", "Max is wrong");

        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if(endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
        endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
        endEventDateTime.isBefore(eventDto.getBeginEventDateTime())){
            errors.rejectValue("endEventDateTime" , "wrongValue", "Wrong endEventDateTime");
        }


    }

}

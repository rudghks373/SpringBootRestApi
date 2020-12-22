package me.kyunghwan.springrestapi.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.EntityModel;

public class EventResource extends EntityModel {

    @JsonUnwrapped
    private Event event;

    public EventResource(Event event){
        this.event = event;
    }

    public Event getEvent(){
        return getEvent();
    }

}

package me.kyunghwan.springrestapi.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends EntityModel {

    public EventResource(Event event, Link... links){
        super(event, links);
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }

//    @JsonUnwrapped
//    private Event event;
//
//    public EventResource(Event event){
//        this.event = event;
//    }
//
//    public Event getEvent(){
//        return getEvent();
//    }

}

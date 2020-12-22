package me.kyunghwan.springrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.kyunghwan.springrestapi.common.RestDocsConfigureation;
import me.kyunghwan.springrestapi.common.TestDescription;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfigureation.class)
@ActiveProfiles("test")
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("Rest Api")
                .beginEnrollmentDateTime(LocalDateTime.of(2019,12,20,11,9))
                .closeEnrollmentDateTime(LocalDateTime.of(2019,12,20,11,10))
                .beginEventDateTime(LocalDateTime.of(2019,12,20,13,10))
                .endEventDateTime(LocalDateTime.of(2019,12,23,11,10))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("삼성역")
                .build();


        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated()) //is.("201")
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE , MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())

                .andDo(document("create-event",
                        links(
                            linkWithRel("self").description("link to self"),
                            linkWithRel("query-events").description("lint to query events"),
                            linkWithRel("update-event").description("lint to update an exist event"),
                            linkWithRel("profile").description("lint to update an exist event")
                        ),
                        requestHeaders(
                             headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                             headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                             fieldWithPath("name").description("Name of new event"),
                             fieldWithPath("description").description("description of new event"),
                             fieldWithPath("beginEnrollmentDateTime").description("date time begin of new event"),
                             fieldWithPath("closeEnrollmentDateTime").description("date time close of new event"),
                             fieldWithPath("beginEventDateTime").description("date time begin of new event"),
                             fieldWithPath("endEventDateTime").description("date time end of new event"),
                             fieldWithPath("location").description("location of new event"),
                             fieldWithPath("basePrice").description("base price of new event"),
                             fieldWithPath("maxPrice").description("max price of new event"),
                             fieldWithPath("limitOfEnrollment").description("limit of new event")
                        ),
                        responseHeaders(
                              headerWithName(HttpHeaders.LOCATION).description("Location header"),
                              headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
                        ),
                        //Relaxed 접두어
                        //장점:문서 일부분만 테스트 할 수 있다.
                        //단점:정확한 문서를 생성하지 못한다.
                        responseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                  fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of new event"),
                                fieldWithPath("free").description("it tells is this event is free or not"),
                                fieldWithPath("offline").description("it tells is this offline event or  not"),
                                fieldWithPath("eventStatus").description("event eventStatus"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                        )

                )
        ;

    }

    //#Bad_Request 로 응답 vs 받기로 한 이외는 무시는 생각해보기
    @Test
    @TestDescription("입력 값이 받을 수 없는 값을 사용하는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("Rest Api")
                .beginEnrollmentDateTime(LocalDateTime.of(2019,12,20,11,9))
                .closeEnrollmentDateTime(LocalDateTime.of(2019,12,20,11,10))
                .beginEventDateTime(LocalDateTime.of(2019,12,20,13,10))
                .endEventDateTime(LocalDateTime.of(2019,12,23,11,10))
                .basePrice(100)
                .maxPrice(200)
                .free(true)
                .offline(false)
                .limitOfEnrollment(100)
                .location("삼성역")
                .eventStatus(EventStatus.PUBLISHED)
                .build();


        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @TestDescription("입력 값이 비어 있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("Rest Api")
                .beginEnrollmentDateTime(LocalDateTime.of(2019,12,26,11,9))
                .closeEnrollmentDateTime(LocalDateTime.of(2019,12,26,11,10))
                .beginEventDateTime(LocalDateTime.of(2019,12,20,13,10))
                .endEventDateTime(LocalDateTime.of(2019,12,23,11,10))
                .basePrice(1000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("삼성역")
                .build();

        this.mockMvc. perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMeesage").exists())
                .andExpect(jsonPath("$[0].code").exists())
        ;
    }


}

package me.kyunghwan.springrestapi.events;

import me.kyunghwan.springrestapi.accounts.Account;
import me.kyunghwan.springrestapi.accounts.AccountRepository;
import me.kyunghwan.springrestapi.accounts.AccountRole;
import me.kyunghwan.springrestapi.accounts.AccountService;
import me.kyunghwan.springrestapi.common.BaseControllerTest;
import me.kyunghwan.springrestapi.common.TestDescription;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class  EventControllerTests extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    public void setUp(){
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("Rest Api")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 12, 20, 11, 9))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 12, 20, 11, 10))
                .beginEventDateTime(LocalDateTime.of(2019, 12, 20, 13, 10))
                .endEventDateTime(LocalDateTime.of(2019, 12, 23, 11, 10))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("삼성역")
                .build();


        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerTonken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated()) //is.("201")
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
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
                        relaxedResponseFields(
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

    private String getBearerTonken() throws Exception {
        return "Bearer " + getAccessToken();
    }

    private String getAccessToken() throws Exception {
        String username = "ohtest";
        String password = "pass";
        Account testAccount = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(testAccount);

        String clientId = "myApp";
        String clientSecret = "pass";

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());
        MockHttpServletResponse response = perform.andReturn().getResponse();
        String responseBody = response.getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();

    }


    //#Bad_Request 로 응답 vs 받기로 한 이외는 무시는 생각해보기
    @Test
    @TestDescription("입력 값이 받을 수 없는 값을 사용하는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("Rest Api")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 12, 20, 11, 9))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 12, 20, 11, 10))
                .beginEventDateTime(LocalDateTime.of(2019, 12, 20, 13, 10))
                .endEventDateTime(LocalDateTime.of(2019, 12, 23, 11, 10))
                .basePrice(100)
                .maxPrice(200)
                .free(true)
                .offline(false)
                .limitOfEnrollment(100)
                .location("삼성역")
                .eventStatus(EventStatus.PUBLISHED)
                .build();


        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerTonken())
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
                .header(HttpHeaders.AUTHORIZATION, getBearerTonken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("Rest Api")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 12, 26, 11, 9))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 12, 26, 11, 10))
                .beginEventDateTime(LocalDateTime.of(2019, 12, 20, 13, 10))
                .endEventDateTime(LocalDateTime.of(2019, 12, 23, 11, 10))
                .basePrice(1000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("삼성역")
                .build();

        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerTonken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        //Given
        IntStream.range(0, 30).forEach(i -> {
            this.generateEvent(i);
        });

        //When & Then
        this.mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "!0")
                .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;
    }


    @Test
    @DisplayName("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception {
        //Given
        Event event = this.generateEvent(100);

        //When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
        ;
    }

    @Test
    @DisplayName("없는 이벤트를 조회했을 때 404 응답받기")
    public void getEvent404() throws Exception{
        //Given

        //When & Then
        this.mockMvc.perform(get("/api/events/11883"))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @DisplayName("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception{
        //Given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String eventName = "Update Event";
        eventDto.setName(eventName);

        //When && Then
        this.mockMvc.perform(put("/api/events/{id}" , event.getId())
                    .header(HttpHeaders.AUTHORIZATION, getBearerTonken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name").value(eventName))
                    .andExpect(jsonPath("_links.self").exists())
                ;
    }

    @Test
    @DisplayName("입력값이 잘못된 경우에 이벤트 수정 실패")
    public void updateEvent400_Wrong() throws Exception{
        Event event = this.generateEvent(200);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                 .header(HttpHeaders.AUTHORIZATION, getBearerTonken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력값이 비어있는 경우에 이벤트 수정 실패")
    public void updateEvent400_Empty() throws Exception{
        //Given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event,EventDto.class);

        //When && Then
        this.mockMvc.perform(put("/api/events/123123213")
                .header(HttpHeaders.AUTHORIZATION, getBearerTonken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @DisplayName("존재하지 않는 이벤트 수정 실패")
    public void updateEvent404() throws Exception{
        //Given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        // When & Then
        this.mockMvc.perform(put("/api/events/123123")
                .header(HttpHeaders.AUTHORIZATION, getBearerTonken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
        ;
    }



    private Event generateEvent(int index){
        Event event = Event.builder()
                .name("event " + index)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 12, 20, 11, 9))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 12, 20, 11, 10))
                .beginEventDateTime(LocalDateTime.of(2019, 12, 20, 13, 10))
                .endEventDateTime(LocalDateTime.of(2019, 12, 23, 11, 10))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("삼성역")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

        return this.eventRepository.save(event);
    }


}

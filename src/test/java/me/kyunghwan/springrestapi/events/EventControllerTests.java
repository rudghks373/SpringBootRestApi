package me.kyunghwan.springrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.kyunghwan.springrestapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
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

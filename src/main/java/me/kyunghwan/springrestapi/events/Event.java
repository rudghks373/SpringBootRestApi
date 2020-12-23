package me.kyunghwan.springrestapi.events;

import ch.qos.logback.classic.spi.LoggingEventVO;
import lombok.*;
import me.kyunghwan.springrestapi.accounts.Account;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id") //연관관계 묶음 NO! //엔티티 에는 @Data 어노테이션 NO!
@Entity
public class Event {

    @Id @GeneratedValue
    private Integer id;

    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;

    @ManyToOne
    private Account manager;

    public void update() {
        //Update Free

        if(this.basePrice == 0 && this.maxPrice == 0){
            this.free = true;
        }else{
            this.free = false;
        }

        //Update offLine
        if(this.location == null || this.location.isBlank() ){
            this.offline = false;
        }else{
            this.offline = true;
        }
    }
}

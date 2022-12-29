package fcode.backend.management.model.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class MemberCardDTO {
    private Integer memberId;
    private String cardHashCode;
}

package org.pet.management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@ToString
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccineDTO {
    private Long id;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime time;
}

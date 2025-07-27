package org.pet.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class VaccineDTO {
    private final Long id;
    private final String name;
    private final LocalDateTime date;
}

package org.pet.management.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pet.management.dto.response.VaccineDTO;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class UpdateVaccineDTO {
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime vaccineTime;

    public static UpdateVaccineDTO from(VaccineDTO vaccineDTO) {
        return new UpdateVaccineDTO(vaccineDTO.getName(), vaccineDTO.getTime());
    }
}

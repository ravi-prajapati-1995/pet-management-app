package org.pet.management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PetUpdateDto {
    private String name;
    private Integer age;

    public static PetUpdateDto from(final String petName, final int age) {
        return new PetUpdateDto(petName, age);
    }

}

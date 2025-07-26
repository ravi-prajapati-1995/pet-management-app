package org.pet.management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OwnerUpdateDTO {
    private String ownerName;
    private String ownerPhone;

    public static OwnerUpdateDTO from(final String ownerName, final String ownerPhone) {
        return new OwnerUpdateDTO(ownerName, ownerPhone);
    }

}

package org.pet.management.petlist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.swing.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PetDetailsDTO {
    private int id;
    private String name;
    private int age;
    private int ownerId;
    private String owner;
    private String lastVaccine;
    private String phoneNumber;

    public static Object[] toArray(final PetDetailsDTO dto) {
        return new Object[]{
                dto.getId(),
                dto.name,
                dto.age,
                dto.lastVaccine,
                dto.owner,
                dto.phoneNumber
        };
    }

    public static PetDetailsDTO from(final JTable table, final int row) {
       return PetDetailsDTO.builder()
                .id((int) table.getValueAt(row, 0))
                .name((String) table.getValueAt(row, 1))
                .age((int) table.getValueAt(row, 2))
                .lastVaccine((String) table.getValueAt(row, 3))
                .owner((String) table.getValueAt(row, 4))
                .build();

    }
}

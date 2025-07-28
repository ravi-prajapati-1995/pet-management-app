package org.pet.management.common;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.List;

import static java.util.Arrays.asList;

@Slf4j
public class CompositeVerifier extends InputVerifier {
    private final List<InputVerifier> verifiers;

    public CompositeVerifier(final InputVerifier... verifiers) {
        this.verifiers = asList(verifiers);
    }

    @Override
    public boolean verify(final JComponent input) {
        for (final var v : verifiers) {
            if (!v.verify(input)) {
                return false;
            }
        }
        return true;
    }
}

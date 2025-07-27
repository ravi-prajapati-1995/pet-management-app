package org.pet.management.listeners;

@FunctionalInterface
public interface SessionListener {
    void onSessionExpired();
}

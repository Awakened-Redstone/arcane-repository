package dev.enjarai.arcane_repository.util.request;

import java.util.Set;

public interface IndexInteractable {
    Set<IndexSource> arcane_repository$getSources();
    default void arcane_repository$onInteractionComplete() {}
}

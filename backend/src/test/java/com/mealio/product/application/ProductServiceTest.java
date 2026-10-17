package com.mealio.product.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import com.mealio.product.api.ProductNotFoundException;
import com.mealio.product.application.port.out.ProductRepository;
import com.mealio.product.domain.*;
import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProductServiceTest {
    private final ProductRepository repository = mock(ProductRepository.class);
    private final Instant now = Instant.parse("2026-10-17T10:00:00Z");
    private final ProductService service = new ProductService(repository, Clock.fixed(now, ZoneOffset.UTC));
    private final ProductDraft draft = ProductDraft.builder().name("Rice").nutrition(NutritionFacts.builder().basis(NutritionBasis.PER_100_G).build()).build();

    @Test
    void createsManualSourceAndReadsSnapshotWithoutDomainTypes() {
        when(repository.create(any())).thenAnswer(call -> call.getArgument(0));
        var product = service.create(Optional.empty(), draft);
        assertThat(product.version().source().source()).isEqualTo(ProductSourceSnapshot.Source.USER_INPUT);
        when(repository.find(product.id(), OptionalInt.empty())).thenReturn(Optional.of(product));
        var snapshot = service.snapshot(product.id());
        assertThat(snapshot.verificationStatus()).isEqualTo("INCOMPLETE");
        assertThat(snapshot.source()).isEqualTo("USER_INPUT");
        assertThat(snapshot.energyKcal()).isEmpty();
        assertThat(snapshot.recordedAt()).isEqualTo(now);
    }

    @Test
    void labelSourceUsesNormalizedCodeAndVersionCanBeAppended() {
        when(repository.create(any())).thenAnswer(call -> call.getArgument(0));
        var product = service.create(Optional.of("5901234123457"), draft);
        assertThat(product.version().source().source()).isEqualTo(ProductSourceSnapshot.Source.USER_LABEL);
        when(repository.find(product.id(), OptionalInt.empty())).thenReturn(Optional.of(product));
        when(repository.appendVersion(any())).thenAnswer(call -> call.getArgument(0));
        assertThat(service.revise(product.id(), 1, draft).version().number()).isEqualTo(2);
        when(repository.findByGtin(product.gtin().orElseThrow())).thenReturn(Optional.of(product));
        assertThat(service.byGtin("05901234123457")).isEqualTo(product);
    }

    @Test
    void invalidRequestsDoNotReachRepositoryAndUnknownProductsAreDistinct() {
        assertThatThrownBy(() -> service.search("rice", -1, 20)).isInstanceOf(InvalidProductException.class);
        assertThatThrownBy(() -> service.search("rice", 0, 101)).isInstanceOf(InvalidProductException.class);
        assertThatThrownBy(() -> service.search("rice", 0, 0)).isInstanceOf(InvalidProductException.class);
        assertThatThrownBy(() -> service.search("x".repeat(221), 0, 20)).isInstanceOf(InvalidProductException.class);
        assertThatThrownBy(() -> service.search(null, 0, 20)).isInstanceOf(InvalidProductException.class);
        assertThatThrownBy(() -> service.getVersion(UUID.randomUUID(), 0)).isInstanceOf(InvalidProductException.class);
        verifyNoInteractions(repository);
        var id = UUID.randomUUID();
        when(repository.find(id, OptionalInt.empty())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(id)).isInstanceOf(ProductNotFoundException.class);
        when(repository.findByGtin(Gtin.of("5901234123457"))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.byGtin("5901234123457")).isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void trimsSearchBeforeCallingStorage() {
        when(repository.search("Rice", 0, 20)).thenReturn(List.of());
        assertThat(service.search(" Rice ", 0, 20)).isEmpty();
        verify(repository).search("Rice", 0, 20);
    }
}

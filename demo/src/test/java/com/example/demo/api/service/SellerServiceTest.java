package com.example.demo.api.service;

import com.example.demo.api.exception.seller.SellerBadRequestException;
import com.example.demo.api.exception.seller.SellerNotFoundException;
import com.example.demo.store.entity.SellerEntity;
import com.example.demo.store.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SellerServiceTest {
    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SellerService sellerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllSellersStream() {
        SellerEntity seller = SellerEntity.builder()
                .id(1L)
                .name("Test Seller")
                .contactInfo("test@example.com")
                .build();

        when(sellerRepository.streamAllBy()).thenReturn(Stream.of(seller));

        Stream<SellerEntity> sellers = sellerService.getAllSellersStream();

        assertNotNull(sellers);
        assertEquals(1, sellers.count());

        verify(sellerRepository, times(1)).streamAllBy();
    }

    @Test
    void testGetSellerOrThrowException_Success() {
        // given
        Long sellerId = 1L;
        SellerEntity seller = SellerEntity.builder()
                .id(sellerId)
                .name("Test Seller")
                .contactInfo("test@example.com")
                .build();

        when(sellerRepository.findById(sellerId)).thenReturn(Optional.of(seller));

        // when
        SellerEntity foundSeller = sellerService.getSellerOrThrowException(sellerId);

        // then
        assertNotNull(foundSeller);
        assertEquals("Test Seller", foundSeller.getName());
        verify(sellerRepository, times(1)).findById(sellerId);
    }

    @Test
    void testGetSellerOrThrowException_NotFound() {
        // given
        Long sellerId = 1L;
        when(sellerRepository.findById(sellerId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(SellerNotFoundException.class, () -> sellerService.getSellerOrThrowException(sellerId));
        verify(sellerRepository, times(1)).findById(sellerId);
    }

    @Test
    void testCreateSeller_Success() {
        // given
        String sellerName = "New Seller";
        String sellerContactInfo = "new@example.com";
        SellerEntity seller = SellerEntity.builder()
                .name(sellerName)
                .contactInfo(sellerContactInfo)
                .registrationDate(LocalDateTime.now())
                .build();

        when(sellerRepository.saveAndFlush(any(SellerEntity.class))).thenReturn(seller);

        // when
        SellerEntity createdSeller = sellerService.createSeller(sellerName, sellerContactInfo);

        // then
        assertNotNull(createdSeller);
        assertEquals(sellerName, createdSeller.getName());
        verify(sellerRepository, times(1)).saveAndFlush(any(SellerEntity.class));
    }

    @Test
    void testCreateSeller_EmptyNameOrContactInfo() {
        // given
        String emptyName = "";
        String contactInfo = "new@example.com";

        // when & then
        assertThrows(SellerBadRequestException.class, () -> sellerService.createSeller(emptyName, contactInfo));
        verify(sellerRepository, never()).saveAndFlush(any(SellerEntity.class));
    }

    @Test
    void testUpdateSeller_Success() {
        // given
        Long sellerId = 1L;
        String updatedName = "Updated Seller";
        String updatedContactInfo = "updated@example.com";

        SellerEntity existingSeller = SellerEntity.builder()
                .id(sellerId)
                .name("Old Seller")
                .contactInfo("old@example.com")
                .build();

        when(sellerRepository.findById(sellerId)).thenReturn(Optional.of(existingSeller));
        when(sellerRepository.saveAndFlush(any(SellerEntity.class))).thenReturn(existingSeller);

        // when
        SellerEntity updatedSeller = sellerService.updateSeller(
                sellerId,
                Optional.of(updatedName),
                Optional.of(updatedContactInfo)
        );

        // then
        assertNotNull(updatedSeller);
        assertEquals(updatedName, updatedSeller.getName());
        assertEquals(updatedContactInfo, updatedSeller.getContactInfo());
        verify(sellerRepository, times(1)).findById(sellerId);
        verify(sellerRepository, times(1)).saveAndFlush(any(SellerEntity.class));
    }

    @Test
    void testUpdateSeller_EmptyOptionalFields() {
        // given
        Long sellerId = 1L;
        SellerEntity existingSeller = SellerEntity.builder()
                .id(sellerId)
                .name("Old Seller")
                .contactInfo("old@example.com")
                .build();

        when(sellerRepository.findById(sellerId)).thenReturn(Optional.of(existingSeller));
        when(sellerRepository.saveAndFlush(any(SellerEntity.class))).thenReturn(existingSeller);

        // when
        SellerEntity updatedSeller = sellerService.updateSeller(
                sellerId,
                Optional.empty(),
                Optional.empty()
        );

        // then
        assertNotNull(updatedSeller);
        assertEquals("Old Seller", updatedSeller.getName());
        assertEquals("old@example.com", updatedSeller.getContactInfo());
        verify(sellerRepository, times(1)).findById(sellerId);
        verify(sellerRepository, times(1)).saveAndFlush(any(SellerEntity.class));
    }

    @Test
    void testDeleteSeller_Success() {
        // given
        Long sellerId = 1L;
        SellerEntity seller = SellerEntity.builder()
                .id(sellerId)
                .name("Seller to delete")
                .contactInfo("delete@example.com")
                .build();

        when(sellerRepository.findById(sellerId)).thenReturn(Optional.of(seller));

        // when
        sellerService.deleteSeller(sellerId);

        // then
        verify(sellerRepository, times(1)).delete(seller);
    }

    @Test
    void testDeleteSeller_NotFound() {
        // given
        Long sellerId = 1L;
        when(sellerRepository.findById(sellerId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(SellerNotFoundException.class, () -> sellerService.deleteSeller(sellerId));
        verify(sellerRepository, never()).delete(any(SellerEntity.class));
    }
}

package com.example.demo.api.controller;

import com.example.demo.api.dto.AckDto;
import com.example.demo.api.dto.SellerDto;
import com.example.demo.api.factory.SellerDtoFactory;
import com.example.demo.api.service.SellerService;
import com.example.demo.store.entity.SellerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SellerControllerTest {

    @Mock
    private SellerService sellerService;

    @Mock
    private SellerDtoFactory sellerDtoFactory;

    @InjectMocks
    private SellerController sellerController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(sellerController).build();
    }

    @Test
    void fetchSellers_ShouldReturnListOfSellers() throws Exception {
        SellerEntity sellerEntity = SellerEntity.builder()
                .id(1L)
                .name("Seller 1")
                .contactInfo("contact@example.com")
                .registrationDate(LocalDateTime.now())
                .build();

        SellerDto sellerDto = SellerDto.builder()
                .id(1L)
                .name("Seller 1")
                .contactInfo("contact@example.com")
                .registrationDate(sellerEntity.getRegistrationDate())
                .build();

        when(sellerService.getAllSellersStream()).thenReturn(Stream.of(sellerEntity));
        when(sellerDtoFactory.makeSellerDto(sellerEntity)).thenReturn(sellerDto);

        mockMvc.perform(get(SellerController.FETCH_SELLERS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Seller 1"))
                .andExpect(jsonPath("$[0].contact_info").value("contact@example.com"))
                .andExpect(jsonPath("$[0].registration_date").isNotEmpty());

        verify(sellerService, times(1)).getAllSellersStream();
        verify(sellerDtoFactory, times(1)).makeSellerDto(sellerEntity);
    }

    @Test
    void fetchSellerById_ShouldReturnSeller() throws Exception {
        Long sellerId = 1L;
        SellerEntity sellerEntity = SellerEntity.builder()
                .id(sellerId)
                .name("Seller 1")
                .contactInfo("contact@example.com")
                .registrationDate(LocalDateTime.now())
                .build();

        SellerDto sellerDto = SellerDto.builder()
                .id(sellerId)
                .name("Seller 1")
                .contactInfo("contact@example.com")
                .registrationDate(sellerEntity.getRegistrationDate())
                .build();

        when(sellerService.getSellerOrThrowException(sellerId)).thenReturn(sellerEntity);
        when(sellerDtoFactory.makeSellerDto(sellerEntity)).thenReturn(sellerDto);

        mockMvc.perform(get(SellerController.FETCH_SELLER_BY_ID, sellerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sellerId))
                .andExpect(jsonPath("$.name").value("Seller 1"))
                .andExpect(jsonPath("$.contact_info").value("contact@example.com"))
                .andExpect(jsonPath("$.registration_date").isNotEmpty());

        verify(sellerService, times(1)).getSellerOrThrowException(sellerId);
        verify(sellerDtoFactory, times(1)).makeSellerDto(sellerEntity);
    }

    @Test
    void createSeller_ShouldReturnCreatedSeller() throws Exception {
        String sellerName = "Seller 1";
        String contactInfo = "contact@example.com";

        SellerEntity sellerEntity = SellerEntity.builder()
                .id(1L)
                .name(sellerName)
                .contactInfo(contactInfo)
                .registrationDate(LocalDateTime.now())
                .build();

        SellerDto sellerDto = SellerDto.builder()
                .id(1L)
                .name(sellerName)
                .contactInfo(contactInfo)
                .registrationDate(sellerEntity.getRegistrationDate())
                .build();

        when(sellerService.createSeller(sellerName, contactInfo)).thenReturn(sellerEntity);
        when(sellerDtoFactory.makeSellerDto(sellerEntity)).thenReturn(sellerDto);

        mockMvc.perform(post(SellerController.CREATE_SELLER)
                        .param("seller_name", sellerName)
                        .param("seller_contact_info", contactInfo)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(sellerName))
                .andExpect(jsonPath("$.contact_info").value(contactInfo))
                .andExpect(jsonPath("$.registration_date").isNotEmpty());

        verify(sellerService, times(1)).createSeller(sellerName, contactInfo);
        verify(sellerDtoFactory, times(1)).makeSellerDto(sellerEntity);
    }

    @Test
    void updateSeller_ShouldReturnUpdatedSeller() throws Exception {
        Long sellerId = 1L;
        String updatedName = "Updated Seller";
        String updatedContactInfo = "updatedcontact@example.com";

        SellerEntity updatedSellerEntity = SellerEntity.builder()
                .id(sellerId)
                .name(updatedName)
                .contactInfo(updatedContactInfo)
                .registrationDate(LocalDateTime.now())
                .build();

        SellerDto updatedSellerDto = SellerDto.builder()
                .id(sellerId)
                .name(updatedName)
                .contactInfo(updatedContactInfo)
                .registrationDate(updatedSellerEntity.getRegistrationDate())
                .build();

        when(sellerService.updateSeller(eq(sellerId), any(), any())).thenReturn(updatedSellerEntity);
        when(sellerDtoFactory.makeSellerDto(updatedSellerEntity)).thenReturn(updatedSellerDto);

        mockMvc.perform(patch(SellerController.UPDATE_SELLER, sellerId)
                        .param("seller_name", updatedName)
                        .param("seller_contact_info", updatedContactInfo)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sellerId))
                .andExpect(jsonPath("$.name").value(updatedName))
                .andExpect(jsonPath("$.contact_info").value(updatedContactInfo))
                .andExpect(jsonPath("$.registration_date").isNotEmpty());

        verify(sellerService, times(1)).updateSeller(eq(sellerId), any(), any());
        verify(sellerDtoFactory, times(1)).makeSellerDto(updatedSellerEntity);
    }

    @Test
    void deleteSeller_ShouldReturnAck() throws Exception {
        Long sellerId = 1L;

        mockMvc.perform(delete(SellerController.DELETE_SELLER, sellerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value(true));

        verify(sellerService, times(1)).deleteSeller(sellerId);
    }
}

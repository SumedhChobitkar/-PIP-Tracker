package com.pipTracker.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pipTracker.Controller.PipController;
import com.pipTracker.Entity.Employee;
import com.pipTracker.Entity.Pip;
import com.pipTracker.Exception.PipNotFoundException;
import com.pipTracker.ServiceImpl.PipServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PipControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PipServiceImpl pipService;

    @InjectMocks
    private PipController pipController;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Use the custom ObjectMapper in MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(pipController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void testCreatePip_success() throws Exception {
        Pip pip = new Pip();
        pip.setPipId(1L);
        Employee emp = new Employee();
        emp.setEmployeeId(10L);
        emp.setName("Test Emp");
        emp.setEmail("test@example.com");
        pip.setEmployee(emp);
        pip.setReviewerId(2L);
        pip.setGoals("Improve performance");
        pip.setStartDate(LocalDate.of(2024, 1, 1));
        pip.setEndDate(LocalDate.of(2024, 2, 1));

        when(pipService.createPip(any(Pip.class))).thenReturn(pip);

        mockMvc.perform(post("/api/pip/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pip)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pipId").value(1))
                .andExpect(jsonPath("$.goals").value("Improve performance"))
                // fix: match dates as strings
                .andExpect(jsonPath("$.startDate").value("2024-01-01"))
                .andExpect(jsonPath("$.endDate").value("2024-02-01"));

        verify(pipService, times(1)).createPip(any(Pip.class));
    }

    @Test
    void testGetAllPips_success() throws Exception {
        Pip pip = new Pip();
        pip.setPipId(1L);
        pip.setGoals("g1");

        when(pipService.getAllPips()).thenReturn(List.of(pip));

        mockMvc.perform(get("/api/pip/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pipId").value(1))
                .andExpect(jsonPath("$[0].goals").value("g1"));

        verify(pipService, times(1)).getAllPips();
    }

    @Test
    void testGetPipById_found() throws Exception {
        Pip pip = new Pip();
        pip.setPipId(1L);
        pip.setGoals("found-goal");

        when(pipService.getPipById(1L)).thenReturn(pip);

        mockMvc.perform(get("/api/pip/getById/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pipId").value(1))
                .andExpect(jsonPath("$.goals").value("found-goal"));

        verify(pipService, times(1)).getPipById(1L);
    }

    @Test
    void testGetPipById_notFound() throws Exception {
        when(pipService.getPipById(2L)).thenThrow(new PipNotFoundException("PIP not found with id 2"));

        mockMvc.perform(get("/api/pip/getById/{id}", 2L))
                .andExpect(status().isNotFound());

        verify(pipService, times(1)).getPipById(2L);
    }

    @Test
    void testUpdatePip_success() throws Exception {
        Pip incoming = new Pip();
        incoming.setGoals("updated goals");

        Pip updated = new Pip();
        updated.setPipId(1L);
        updated.setGoals("updated goals");

        when(pipService.updatePip(eq(1L), any(Pip.class))).thenReturn(updated);

        mockMvc.perform(put("/api/pip/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incoming)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pipId").value(1))
                .andExpect(jsonPath("$.goals").value("updated goals"));

        verify(pipService, times(1)).updatePip(eq(1L), any(Pip.class));
    }

    @Test
    void testUpdatePip_notFound() throws Exception {
        Pip incoming = new Pip();
        incoming.setGoals("updated goals");

        when(pipService.updatePip(eq(2L), any(Pip.class)))
                .thenThrow(new PipNotFoundException("PIP not found with id 2"));

        mockMvc.perform(put("/api/pip/update/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incoming)))
                .andExpect(status().isNotFound());

        verify(pipService, times(1)).updatePip(eq(2L), any(Pip.class));
    }



    @Test
    void testDeletePip_success() throws Exception {
        doNothing().when(pipService).deletePip(1L);

        mockMvc.perform(delete("/api/pip/delete/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json("\"PIP deleted successfully\""));

        verify(pipService, times(1)).deletePip(1L);
    }


    @Test
    void testDeletePip_notFound() throws Exception {
        doThrow(new PipNotFoundException("PIP not found with id 2")).when(pipService).deletePip(2L);

        mockMvc.perform(delete("/api/pip/delete/{id}", 2L))
                .andExpect(status().isNotFound());

        verify(pipService, times(1)).deletePip(2L);
    }
}

package com.firefighter.aenitto.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class IntegrationTest {
    @Autowired
    protected EntityManager em;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext webApplicationContext;
    protected MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .alwaysDo(print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    protected void flushAndClear() {
        em.flush();
        em.clear();
    }

    protected MockMultipartFile createJsonFile(Object request) throws JsonProcessingException {
        return new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );
    }
}


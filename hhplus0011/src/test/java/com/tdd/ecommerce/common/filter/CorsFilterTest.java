package com.tdd.ecommerce.common.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CorsFilterTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("🟢Cors Filter가 실행되면 헤더에 Access-Control-Allow-Origin 가 담겨있다.")
    void testCorsDoFilter() throws Exception {
        mockMvc.perform(get("/products")
                        .header("Origin", "http://localhost:8080"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }
    @Test
    @DisplayName("🔴허용되지 않은 url은 4xx에러가 뜬다. ")
    void testCorsDoFilter_NotFound() throws Exception {
        mockMvc.perform(get("/invalid-url")
                        .header("Origin", "http://localhost:8080"))
                .andExpect(status().is4xxClientError());
    }
}

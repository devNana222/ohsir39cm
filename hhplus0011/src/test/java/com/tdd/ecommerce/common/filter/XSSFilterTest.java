package com.tdd.ecommerce.common.filter;

import com.tdd.ecommerce.common.config.FilterConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(FilterConfig.class)
class XSSFilterTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("🟢스크립트를 보내면 컨텐츠에는 보낸 스크립트가 포함되면 안된다.")
    void testXSSDoFilter_SUCCESS() throws Exception {
        mockMvc.perform(patch("/customers/1/balance/charge")
                      //  .content("comment", "<script>alert('XSS');</script>"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"comment\": \"<script>alert('XSS');</script>\", \"balance\": 1000 }"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(not(containsString("<script>alert('XSS');</script>"))))
                .andExpect(content().string(not(containsString("<script>"))))
                .andExpect(content().string(containsString("& lt;")));
    }

}

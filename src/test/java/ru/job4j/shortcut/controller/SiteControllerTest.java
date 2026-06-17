package ru.job4j.shortcut.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.shortcut.repository.SiteRepository;
import ru.job4j.shortcut.repository.UrlRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class SiteControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UrlRepository urlRepository;

    @Autowired
    SiteRepository siteRepository;

    @BeforeEach
    void cleanDb() {
        urlRepository.deleteAll();
        siteRepository.deleteAll();
    }

    @Test
    void whenRegisterNewSiteThenSuccess() throws Exception {
        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "site": "job4j.ru"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.registration").value(true))
                .andExpect(jsonPath("$.login").isNotEmpty())
                .andExpect(jsonPath("$.password").isNotEmpty());
    }

    @Test
    void whenRegisterSameSiteTwiceThenFalse() throws Exception {
        String json = """
                {
                    "site": "job4j.ru"
                }
                """;

        mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.registration").value(false));
    }

    @Test
    void whenGetStatisticThenReturnUrls() throws Exception {
        String registrationResponse = mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "site": "job4j.ru"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String login = JsonPath.read(registrationResponse, "$.login");

        mockMvc.perform(post("/convert")
                        .with(user(login))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "url": "https://job4j.ru/profile/exercise/106/task-view/532"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/statistic")
                        .with(user(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].url")
                        .value("https://job4j.ru/profile/exercise/106/task-view/532"))
                .andExpect(jsonPath("$[0].total").value(0));
    }
}
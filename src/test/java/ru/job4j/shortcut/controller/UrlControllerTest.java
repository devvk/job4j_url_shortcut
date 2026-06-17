package ru.job4j.shortcut.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.jayway.jsonpath.JsonPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.job4j.shortcut.repository.SiteRepository;
import ru.job4j.shortcut.repository.UrlRepository;

@SpringBootTest
@AutoConfigureMockMvc
class UrlControllerTest {

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

    private String registerSiteAndGetLogin() throws Exception {
        String registrationResponse = mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "site": "job4j.ru"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.registration").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return JsonPath.read(registrationResponse, "$.login");
    }

    @Test
    void whenConvertUrlThenReturnCode() throws Exception {
        String login = registerSiteAndGetLogin();

        mockMvc.perform(post("/convert")
                        .with(user(login))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "url": "https://job4j.ru/profile/exercise/106/task-view/532"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").isNotEmpty());
    }

    @Test
    void whenConvertSameUrlTwiceThenReturnSameCode() throws Exception {
        String login = registerSiteAndGetLogin();
        String json = """
                {
                    "url": "https://job4j.ru/profile/exercise/106/task-view/532"
                }
                """;

        String firstResponse = mockMvc.perform(post("/convert")
                        .with(user(login))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String firstCode = JsonPath.read(firstResponse, "$.code");

        mockMvc.perform(post("/convert")
                        .with(user(login))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(firstCode));
    }

    @Test
    void whenRedirectByCodeThenReturnFoundAndLocation() throws Exception {
        String login = registerSiteAndGetLogin();
        String originalUrl = "https://job4j.ru/profile/exercise/106/task-view/532";

        String convertResponse = mockMvc.perform(post("/convert")
                        .with(user(login))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "url": "https://job4j.ru/profile/exercise/106/task-view/532"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String code = JsonPath.read(convertResponse, "$.code");

        mockMvc.perform(get("/redirect/{code}", code))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl));
    }

    @Test
    void whenRedirectByUnknownCodeThenReturnNotFound() throws Exception {
        mockMvc.perform(get("/redirect/{code}", "unknown"))
                .andExpect(status().isNotFound());
    }
}

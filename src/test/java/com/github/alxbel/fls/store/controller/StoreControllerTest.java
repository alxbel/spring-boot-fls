package com.github.alxbel.fls.store.controller;

import com.github.alxbel.fls.store.App;
import com.github.alxbel.fls.store.entity.Application;
import com.github.alxbel.fls.store.entity.Contact;
import com.github.alxbel.fls.store.repository.ApplicationRepository;
import com.github.alxbel.fls.store.repository.ContactRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@WebAppConfiguration
public class StoreControllerTest {
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private Contact contact;

    private List<Application> applications = new ArrayList<>();

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        applicationRepository.deleteAllInBatch();
        contactRepository.deleteAllInBatch();

        contact = contactRepository.save(new Contact());
        applications.add(applicationRepository.save(new Application(contact, LocalDateTime.of(2018, 6, 1, 0, 0), "product1")));
        applications.add(applicationRepository.save(new Application(contact, LocalDateTime.of(2018, 7, 1, 0, 0), "product2")));
    }

    @Test
    public void contactNotFound() throws Exception {
        mockMvc.perform(get(String.format("/store/applications/%d", contact.getContactId() + 1)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getApplications() throws Exception {
        mockMvc.perform(get(String.format("/store/applications/%d", contact.getContactId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(applications.size())))
                .andExpect(jsonPath("$[0].applicationId", is(applications.get(0).getApplicationId())))
                .andExpect(jsonPath("$[0].productName", is(applications.get(0).getProductName())))
                .andExpect(jsonPath("$[1].applicationId", is(applications.get(1).getApplicationId())))
                .andExpect(jsonPath("$[1].productName", is(applications.get(1).getProductName())))
                .andDo(mvcResult -> {
                    assertEquals(fromRs(mvcResult, 0), applications.get(0).getDtCreated());
                    assertEquals(fromRs(mvcResult, 1), applications.get(1).getDtCreated());
                });

    }

    @Test
    public void getLatestApplication() throws Exception {
        mockMvc.perform(get("/store/applications/latest"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.applicationId", is(applications.get(1).getApplicationId())));
    }

    @Test
    public void addApplication() throws Exception {
        String appJson = toJson(new Application(contact, LocalDateTime.of(2018, 5, 5, 22, 30), "product3"));

        mockMvc.perform(post(String.format("/store/applications/add/%d", contact.getContactId()))
                .contentType(contentType)
                .content(appJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void updateApplication() throws Exception {
        Application application = new Application(contact, applications.get(0).getDtCreated(), "product v2");
        application.setApplicationId(applications.get(0).getApplicationId());
        String appJson = toJson(new Application(application.getContact(), application.getDtCreated(), application.getProductName()));

        mockMvc.perform(put(String.format("/store/applications/update/%d/%d",
                application.getContact().getContactId(), application.getApplicationId()))

                .contentType(contentType)
                .content(appJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationId", is(application.getApplicationId())))
                .andExpect(jsonPath("$.productName", is(application.getProductName())))
                .andDo(mvcResult -> assertEquals(fromRs(mvcResult), application.getDtCreated()));
    }

    /**
     * Convert entity into json representation.
     *
     * @param entity
     * @return json representation
     * @throws IOException
     */
    private String toJson(Object entity) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        mappingJackson2HttpMessageConverter.write(
                entity, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    /**
     * Parse json date to {@link LocalDateTime}.
     *
     * @param mvcResult request result
     * @param index     index of the result element
     * @return          localDateTime instance
     */
    private static LocalDateTime fromRs(MvcResult mvcResult, Integer ... index) {
        try {
            return LocalDateTime.parse(
                    JsonPath.parse(mvcResult.getResponse().getContentAsString()).read(
                            index.length == 0 ? "$.dtCreated" : String.format("$[%d].dtCreated", index[0])),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
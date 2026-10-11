package com.eventflow.event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc
class EventServiceApplicationTests {
 @Autowired MockMvc mvc;
 @Test void listsSeededEvents() throws Exception { mvc.perform(get("/events")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3)); }
 @Test void createsEvent() throws Exception { mvc.perform(post("/events").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"DevOps Day\",\"date\":\"1 October 2026\",\"venue\":\"Hall A\",\"seats\":50,\"description\":\"Talks\"}")).andExpect(status().isCreated()).andExpect(jsonPath("$.title").value("DevOps Day")); }
}

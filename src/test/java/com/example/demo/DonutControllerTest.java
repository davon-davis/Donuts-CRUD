package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.build.ToStringPlugin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.is;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DonutControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    DonutRepository repository;

    @Test
    @Rollback
    @Transactional
    public void testGetAllDonuts() throws Exception{
        Donut donut = new Donut();
        donut.setName("glazed");
        donut.setTopping("nothing");
        donut.setExpiration(new Date());

        repository.save(donut);

        MockHttpServletRequestBuilder request = get("/donuts")
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name", is("glazed")));
    }

    @Test
    @Rollback
    @Transactional
    public void testPostDonut() throws Exception{
        Donut donut = new Donut();
        donut.setName("jelly");
        donut.setTopping("sprinkles");
        donut.setExpiration(new Date());

        repository.save(donut);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(donut);

        MockHttpServletRequestBuilder request = post("/donuts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("jelly")))
                .andExpect(jsonPath("$.topping", is("sprinkles")));
    }

    @Test
    @Rollback
    @Transactional
    public void testGetDonut() throws Exception{
        Donut donut = new Donut();
        donut.setName("jelly");
        donut.setTopping("sprinkles");
        donut.setExpiration(new Date());

        Donut donut2 = new Donut();
        donut2.setName("glazed");
        donut2.setTopping("nothing");
        donut2.setExpiration(new Date());

        repository.save(donut);
        repository.save(donut2);

        MockHttpServletRequestBuilder request = get("/donuts/2")
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("glazed")))
                .andExpect((jsonPath("$.topping", is("nothing"))));
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteDonut() throws Exception{
        Donut donut = new Donut();
        donut.setName("jelly");
        donut.setTopping("sprinkles");
        donut.setExpiration(new Date());

        Donut donut2 = new Donut();
        donut2.setName("glazed");
        donut2.setTopping("nothing");
        donut2.setExpiration(new Date());

        repository.save(donut);
        repository.save(donut2);

        MockHttpServletRequestBuilder request = delete("/donuts/1")
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string("A donut was deleted. Donuts remaining: 1"));

        MockHttpServletRequestBuilder request2 = get("/donuts/1")
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request2)
                .andExpect(status().isOk())
                .andExpect(content().string("This donut does not exist"));

    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateDonut() throws Exception{
        Donut donut = new Donut();
        donut.setName("jelly");
        donut.setTopping("sprinkles");
        donut.setExpiration(new Date());

        repository.save(donut);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(Map.of
                ("name", "maple", "topping", "bacon"));

        MockHttpServletRequestBuilder request = patch("/donuts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        this.mvc.perform(request)
                .andExpect(jsonPath("$.name", is("maple")))
                .andExpect((jsonPath("$.topping", is("bacon"))));
    }

}

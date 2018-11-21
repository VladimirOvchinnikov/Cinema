package com.test.cinema.integrator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.cinema.Application;
import com.test.cinema.exception.ExceptionResponse;
import com.test.cinema.integrator.util.GenerateData;
import com.test.cinema.model.entity.BaseEntity;
import com.test.cinema.model.request.FilmRequestBean;
import com.test.cinema.model.request.RequestBean;
import com.test.cinema.model.response.ResponseBean;
import com.test.cinema.service.util.MessageService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.test.cinema.util.Constant.*;
import static com.test.cinema.util.Constant.FILTER_NAME_PAGE;
import static com.test.cinema.util.Constant.HEADER_NAME_PAGE;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public abstract class BaseControllerTest<E extends BaseEntity, T extends RequestBean, R extends ResponseBean> {

    @Value("${test.is-print-info-about-result}")
    private Boolean isPrint;

    private MockMvc mockMvc;

    protected String path;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    protected MessageService messageService;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected MultiValueMap<String, String> mapFilter;

    @Value("${test.count-generate-entity}")
    protected int countEntity;

    protected List<E> entities;

//    public MultiValueMap<String, String> getMapFilter() {
//        return mapFilter;
//    }

    public Boolean isPrint() {
        return isPrint;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    protected HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    protected void setConverters(HttpMessageConverter<?>[] converters) {

        for (HttpMessageConverter converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                this.mappingJackson2HttpMessageConverter = converter;
            }
        }

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    protected MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("UTF-8"));

    public ResultActions get() throws Exception {
        MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get(path)
                .params(mapFilter)
                .contentType(contentType);
        ResultActions ra = isPrint(mockMvc.perform(get))
                .andExpect(status().isOk());
        ra = analisePaginationHeader(ra)
                .andExpect(jsonPath("$", hasSize(getCount())));
        for (int i = getStartInd(), j = 0; i < getEndInd(); i++, j++) {
            R bean = entityToResponseBean(entities.get(i));
            prepareResponseBean(bean);
            analiseResponse(ra, bean, j);
        }
        return ra;
    }

    public ResultActions get(Integer id) throws Exception {
        MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get(path + "/" + id);
        get = get.contentType(contentType);
        ResultActions ra = isPrint(mockMvc.perform(get))
                .andExpect(status().isOk());
        return ra;
    }

    public void prepareResponseBean(R bean){
    }

    public abstract void analiseResponse(ResultActions ra, R bean, Integer index) throws Exception;

    public void exceptionResponse(ResultActions ra, ExceptionResponse er) throws Exception {
        ra.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(er.getCode())))
                .andExpect(jsonPath("$.message", is(er.getMessage())));

    }

    public ResultActions analisePaginationHeader(ResultActions ra) throws Exception {
        Integer perPage = Integer.valueOf(mapFilter.get(FILTER_NAME_PER_PAGE).get(0));
        Integer numberPage = countEntity / perPage + ((countEntity % perPage) > 0 ? 1 : 0);
        ra = ra.andExpect(header().string(HEADER_NAME_PAGE_SIZE, is(mapFilter.get(FILTER_NAME_PER_PAGE).get(0))))
                .andExpect(header().string(HEADER_NAME_PAGE, is(mapFilter.get(FILTER_NAME_PAGE).get(0))))
                .andExpect(header().string(HEADER_NAME_PAGES, is(numberPage.toString())));
        return ra;
    }

    public void putMapFilter(String key, String value) {
        mapFilter.remove(key);
        mapFilter.add(key, value);
    }

    protected int getStartInd() {
        Integer page = Integer.valueOf(mapFilter.get(FILTER_NAME_PAGE).get(0));
        Integer perPage = Integer.valueOf(mapFilter.get(FILTER_NAME_PER_PAGE).get(0));
        if (countEntity - ((page - 1) * perPage) <= 0) {
            return 0;
        }
        return (page - 1) * perPage;
    }

    protected int getEndInd() {
        Integer perPage = Integer.valueOf(mapFilter.get(FILTER_NAME_PER_PAGE).get(0));
        Integer page = Integer.valueOf(mapFilter.get(FILTER_NAME_PAGE).get(0));
        Integer startInd = (page - 1) * perPage;
        if (countEntity - startInd < 0) {
            return 0;
        }
        return (startInd + perPage) < countEntity ? (startInd + perPage) : (countEntity - startInd) > 0 ? (countEntity) : 0;
    }

    protected int getCount() {
        return getEndInd() - getStartInd();
    }


    private ResultActions isPrint(ResultActions ra) throws Exception {
        if (isPrint()) {
            ra = ra.andDo(print());
        }
        return ra;
    }

    public ResultActions post(Object content) throws Exception {
        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post(path);
        post = post.contentType(contentType)
                .content(toJson(content));
        return isPrint(mockMvc.perform(post));
    }

    public ResultActions put(RequestBean content) throws Exception {
        MockHttpServletRequestBuilder put = MockMvcRequestBuilders.put(path + "/" + content.getId());
        put = put.contentType(contentType)
                .content(toJson(content));
        return isPrint(mockMvc.perform(put));
    }


    public ResultActions delete(Integer id) throws Exception {
        get(id).andExpect(jsonPath("$.id", is(id)));

        MockHttpServletRequestBuilder delete = MockMvcRequestBuilders.delete(path + "/" + id);
        delete = delete.contentType(contentType);
        ResultActions ra = isPrint(mockMvc.perform(delete))
                .andExpect(status().isOk());

        get(id).andExpect(status().isOk())
                .andExpect(content().string(isEmptyOrNullString()));
        return ra;
    }

    protected String toJson(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).addFilter(encodingFilter()).build();
        prepareTestData();
    }

    @After
    public void tearDown() throws Exception {
        dropTestData();
    }

    protected void prepareTestData() throws Exception {
        GenerateData.setJdbcTemplate(jdbcTemplate);
        dropTestData();
        entities = new ArrayList<>();
        mapFilter = new LinkedMultiValueMap<>();
        mapFilter.add(FILTER_NAME_PAGE, FILTER_NAME_PAGE_DEFAULT_VALUE);
        mapFilter.add(FILTER_NAME_PER_PAGE, FILTER_NAME_PER_PAGE_DEFAULT_VALUE);
    }

    protected void dropTestData() throws Exception {
//        countEntity = 0;
    }

    protected String getTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern(PATTERN_TIME));
    }

    protected String getDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(PATTERN_DATE));
    }

    protected String getDateTime(LocalDateTime dateTime){
        return dateTime.format(DateTimeFormatter.ofPattern(PATTERN_DATE_TIME));
    }

    private static CharacterEncodingFilter encodingFilter() {
        final CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("utf8");
        filter.setForceEncoding(true);
        return filter;
    }


    public abstract T entityToRequestBean(E entity);

    public abstract R entityToResponseBean(E entity);

    public abstract E responseBeanToEntity(R bean);
}

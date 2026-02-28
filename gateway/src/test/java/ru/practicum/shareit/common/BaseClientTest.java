package ru.practicum.shareit.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BaseClientTest {

    private RestTemplate restTemplate;
    private TestBaseClient baseClient;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        baseClient = new TestBaseClient(restTemplate);
    }

    @Test
    void get_WithoutUserIdAndParameters_ShouldCallRestTemplate() {
        String path = "/test";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.testGet(path);

        assertNotNull(response);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void get_WithUserId_ShouldCallRestTemplateWithHeaders() {
        String path = "/test";
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.testGet(path, userId);

        assertNotNull(response);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void get_WithParameters_ShouldCallRestTemplate() {
        String path = "/test";
        long userId = 1L;
        Map<String, Object> parameters = Map.of("key", "value");
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.testGet(path, userId, parameters);

        assertNotNull(response);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), eq(parameters));
    }

    @Test
    void post_WithBody_ShouldCallRestTemplate() {
        String path = "/test";
        Object body = new Object();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.testPost(path, body);

        assertNotNull(response);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void post_WithUserIdAndBody_ShouldCallRestTemplate() {
        String path = "/test";
        long userId = 1L;
        Object body = new Object();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.testPost(path, userId, body);

        assertNotNull(response);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void patch_WithUserIdAndBody_ShouldCallRestTemplate() {
        String path = "/test";
        long userId = 1L;
        Object body = new Object();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.testPatch(path, userId, body);

        assertNotNull(response);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void put_WithUserIdAndBody_ShouldCallRestTemplate() {
        String path = "/test";
        long userId = 1L;
        Object body = new Object();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(eq(path), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.testPut(path, userId, body);

        assertNotNull(response);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void delete_WithUserId_ShouldCallRestTemplate() {
        String path = "/test";
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(eq(path), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.testDelete(path, userId);

        assertNotNull(response);
        verify(restTemplate).exchange(eq(path), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void makeAndSendRequest_WhenHttpStatusCodeException_ShouldReturnErrorResponse() {
        String path = "/test";
        HttpMethod method = HttpMethod.GET;

        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
        when(exception.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(exception.getResponseBodyAsByteArray()).thenReturn("Error".getBytes());

        when(restTemplate.exchange(eq(path), eq(method), any(HttpEntity.class), eq(Object.class)))
                .thenThrow(exception);

        ResponseEntity<Object> response = baseClient.testGet(path);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private static class TestBaseClient extends BaseClient {
        public TestBaseClient(RestTemplate rest) {
            super(rest);
        }

        public ResponseEntity<Object> testGet(String path) {
            return get(path);
        }

        public ResponseEntity<Object> testGet(String path, long userId) {
            return get(path, userId);
        }

        public ResponseEntity<Object> testGet(String path, long userId, Map<String, Object> parameters) {
            return get(path, userId, parameters);
        }

        public ResponseEntity<Object> testPost(String path, Object body) {
            return post(path, body);
        }

        public ResponseEntity<Object> testPost(String path, long userId, Object body) {
            return post(path, userId, body);
        }

        public ResponseEntity<Object> testPatch(String path, long userId, Object body) {
            return patch(path, userId, body);
        }

        public ResponseEntity<Object> testPut(String path, long userId, Object body) {
            return put(path, userId, body);
        }

        public ResponseEntity<Object> testDelete(String path, long userId) {
            return delete(path, userId);
        }
    }
}
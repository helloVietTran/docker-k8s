package com.viettran.reading_story_web.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettran.reading_story_web.dto.response.PageResponse;
import com.viettran.reading_story_web.dto.response.StoryResponse;

class RedisCacheAsideServiceTest {
    @Test
    void shouldBuildStoryAndChapterCacheKeys() {
        assertEquals("cache:story:detail:42", RedisCacheAsideService.storyDetailKey(42));
        assertEquals("cache:chapter:detail:chapter-123", RedisCacheAsideService.chapterDetailKey("chapter-123"));
    }

    @Test
    void shouldGenerateJitteredTtlWithinExpectedBounds() {
        Duration ttl = RedisCacheAsideService.randomTtl(Duration.ofMinutes(1), Duration.ofSeconds(30));

        assertTrue(ttl.compareTo(Duration.ofMinutes(1)) >= 0);
        assertTrue(ttl.compareTo(Duration.ofMinutes(2)) <= 0);
    }

    @Test
    void shouldRoundTripPageResponseAsJsonString() throws Exception {
        RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        PageResponse<StoryResponse> expected = PageResponse.<StoryResponse>builder()
                .currentPage(1)
                .pageSize(32)
                .totalPages(2)
                .totalElements(45)
                .data(List.of(StoryResponse.builder()
                        .id(1)
                        .name("Test Story")
                        .slug("test-story")
                        .genres(Set.of("Action", "Comedy"))
                        .build()))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String cachedJson = objectMapper.writeValueAsString(expected);
        when(valueOperations.get("cache:story:list:page:1:size:32")).thenReturn(cachedJson);

        RedisCacheAsideService service = new RedisCacheAsideService(redisTemplate, objectMapper);
        PageResponse<?> result = service.getOrLoad(
                "cache:story:list:page:1:size:32",
                new com.fasterxml.jackson.core.type.TypeReference<PageResponse<StoryResponse>>() {},
                () -> expected);

        assertEquals(expected.getCurrentPage(), result.getCurrentPage());
        assertEquals(expected.getPageSize(), result.getPageSize());
        assertEquals(expected.getData().size(), result.getData().size());
        assertEquals("Test Story", ((StoryResponse) result.getData().get(0)).getName());
        assertEquals(
                Set.of("Action", "Comedy"), ((StoryResponse) result.getData().get(0)).getGenres());
    }
}

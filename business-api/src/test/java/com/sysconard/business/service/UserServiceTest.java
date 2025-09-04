package com.sysconard.business.service;

import com.sysconard.business.dto.UserSearchRequest;
import com.sysconard.business.dto.UserSearchResponse;
import com.sysconard.business.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnUsersWithCounts() {
        // Given
        UserSearchRequest request = UserSearchRequest.builder()
                .name("test")
                .page(0)
                .size(10)
                .sortBy("name")
                .sortDir("asc")
                .build();

        // Mock repository responses
        when(userRepository.countActiveUsers()).thenReturn(5L);
        when(userRepository.countInactiveUsers()).thenReturn(2L);
        when(userRepository.countBlockedUsers()).thenReturn(1L);

        // Mock the existing findUsersWithFilters method
        // This would need to be properly mocked in a real test
        // For now, we'll just verify the structure

        // When
        UserSearchResponse response = userService.findUsersWithFiltersAndCounts(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCounts()).isNotNull();
        assertThat(response.getCounts().getTotalActive()).isEqualTo(5L);
        assertThat(response.getCounts().getTotalInactive()).isEqualTo(2L);
        assertThat(response.getCounts().getTotalBlocked()).isEqualTo(1L);
        assertThat(response.getCounts().getTotalUsers()).isEqualTo(8L);
    }
}

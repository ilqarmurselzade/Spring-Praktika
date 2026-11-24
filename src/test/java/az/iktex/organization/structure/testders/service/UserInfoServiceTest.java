package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.component.RecentlyViewedUsers;
import az.iktex.organization.structure.testders.component.RequestContext;
import az.iktex.organization.structure.testders.component.UserAuditLog;
import az.iktex.organization.structure.testders.component.UserStatistics;
import az.iktex.organization.structure.testders.dto.UserInfoRequest;
import az.iktex.organization.structure.testders.dto.UserInfoResponse;
import az.iktex.organization.structure.testders.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class UserInfoServiceTest {

    private final UserRepository repository = Mockito.mock(UserRepository.class);
    private final RequestContext requestContext = Mockito.mock(RequestContext.class);
    private final RecentlyViewedUsers recentlyViewedUsers = Mockito.mock(RecentlyViewedUsers.class);
    private final UserStatistics userStatistics = Mockito.mock(UserStatistics.class);

    @SuppressWarnings("unchecked")
    private final ObjectProvider<UserAuditLog> auditProvider = Mockito.mock(ObjectProvider.class);

    private UserInfoService createService() {
        return new UserInfoService(
                repository,
                auditProvider,
                requestContext,
                recentlyViewedUsers,
                List.of(repository),
                userStatistics
        );
    }

    @Test
    void getUserById_returnsUserFromRepository() {
        when(repository.getUserById(1L)).thenReturn(new UserInfoResponse(1L, "Ali", "ali@mail.az"));

        UserInfoResponse result = createService().getUserById(1L);

        assertEquals("Ali", result.name());
    }

    @Test
    void createUser_returnsCreatedUser() {
        UserInfoRequest request = new UserInfoRequest("Veli", "veli@mail.az");
        when(auditProvider.getObject()).thenReturn(new UserAuditLog());
        when(repository.createUser(request)).thenReturn(new UserInfoResponse(5L, "Veli", "veli@mail.az"));

        UserInfoResponse result = createService().createUser(request);

        assertEquals(Long.valueOf(5L), result.id());
    }
}

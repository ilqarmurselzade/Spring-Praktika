package az.iktex.organization.structure.testders.component;

import az.iktex.organization.structure.testders.dto.UserInfoRequest;
import az.iktex.organization.structure.testders.dto.UserInfoResponse;
import az.iktex.organization.structure.testders.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;

@Repository
public class MockUserRepository implements UserRepository {

    private static final String MOCK_NAME = "Mock User";

    private static final List<UserInfoResponse> MOCK_USERS = List.of(
            new UserInfoResponse(1L, MOCK_NAME, "mock1@mail.az"),
            new UserInfoResponse(2L, MOCK_NAME, "mock2@mail.az"));

    @Override
    public UserInfoResponse getUserById(Long id) {
        return MOCK_USERS.stream()
                .filter(user -> user.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Mock user tapilmadi: id=" + id));
    }

    @Override
    public List<UserInfoResponse> getAllUsers() {
        return MOCK_USERS;
    }

    @Override
    public UserInfoResponse createUser(UserInfoRequest request) {
        return new UserInfoResponse(0L, MOCK_NAME, request.email());
    }
}

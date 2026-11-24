package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.dto.UserInfoRequest;
import az.iktex.organization.structure.testders.dto.UserInfoResponse;

import java.util.List;

public interface UserRepository {

    UserInfoResponse getUserById(Long id);

    List<UserInfoResponse> getAllUsers();

    UserInfoResponse createUser(UserInfoRequest request);
}

package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.component.NotificationService;
import az.iktex.organization.structure.testders.component.RecentlyViewedUsers;
import az.iktex.organization.structure.testders.component.RequestContext;
import az.iktex.organization.structure.testders.component.UserAuditLog;
import az.iktex.organization.structure.testders.component.UserStatistics;
import az.iktex.organization.structure.testders.dto.UserInfoRequest;
import az.iktex.organization.structure.testders.dto.UserInfoResponse;
import az.iktex.organization.structure.testders.repository.UserRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoService {

    private final UserRepository userRepository;
    private final ObjectProvider<UserAuditLog> auditLogProvider;
    private final RequestContext requestContext;
    private final RecentlyViewedUsers recentlyViewedUsers;
    private final UserStatistics userStatistics;
    private NotificationService notificationService;

    public UserInfoService(
            UserRepository userRepository,
            ObjectProvider<UserAuditLog> auditLogProvider,
            RequestContext requestContext,
            RecentlyViewedUsers recentlyViewedUsers,
            List<UserRepository> repositories,
            UserStatistics userStatistics
    ) {
        this.userRepository = userRepository;
        this.auditLogProvider = auditLogProvider;
        this.requestContext = requestContext;
        this.recentlyViewedUsers = recentlyViewedUsers;
        this.userStatistics = userStatistics;
        System.out.println("UserInfoService yaradildi: " + this.hashCode());
        System.out.println("Tapilan UserRepository sayi: " + repositories.size());
    }

    @Autowired(required = false)
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public UserInfoResponse getUserById(Long id) {
        System.out.println("[Service] requestId=" + requestContext.getRequestId());
        UserInfoResponse user = userRepository.getUserById(id);
        recentlyViewedUsers.add(id);
        userStatistics.incrementGetByIdCalls();
        return user;
    }

    public List<UserInfoResponse> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public List<UserInfoResponse> getRecentlyViewedUsers() {
        return recentlyViewedUsers.getIds().stream()
                .map(userRepository::getUserById)
                .toList();
    }

    public UserInfoResponse createUser(UserInfoRequest request) {
        UserAuditLog auditLog = auditLogProvider.getObject();
        auditLog.setAction("CREATE_USER");
        System.out.println("Audit: id=" + auditLog.getAuditId() + ", action=" + auditLog.getAction());
        UserInfoResponse created = userRepository.createUser(request);
        if (notificationService != null) {
            notificationService.sendWelcome(request.email());
        }
        userStatistics.incrementCreatedUsers();
        return created;
    }
}

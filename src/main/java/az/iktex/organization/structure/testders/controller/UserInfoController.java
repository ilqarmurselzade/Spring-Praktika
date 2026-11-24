package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.component.RequestContext;
import az.iktex.organization.structure.testders.dto.UserInfoRequest;
import az.iktex.organization.structure.testders.dto.UserInfoResponse;
import az.iktex.organization.structure.testders.service.UserInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-info")
public class UserInfoController {

    private final UserInfoService userInfoService;
    private final RequestContext requestContext;

    public UserInfoController(
            UserInfoService userInfoService,
            RequestContext requestContext
    ) {
        this.userInfoService = userInfoService;
        this.requestContext = requestContext;
        System.out.println("UserInfoController yaradildi: " + this.hashCode());
    }

    @PostMapping("/create")
    public UserInfoResponse createUser(@RequestBody UserInfoRequest request) {
        return userInfoService.createUser(request);
    }

    @GetMapping("/all")
    public List<UserInfoResponse> getAllUsers() {
        return userInfoService.getAllUsers();
    }

    @GetMapping("/recent")
    public List<UserInfoResponse> getRecentlyViewedUsers() {
        return userInfoService.getRecentlyViewedUsers();
    }

    @GetMapping("/{id}")
    public UserInfoResponse getUserById(@PathVariable Long id) {
        System.out.println("[Controller] requestId=" + requestContext.getRequestId());
        System.out.println("[Controller] requestContext sinfi: " + requestContext.getClass());
        UserInfoResponse response = userInfoService.getUserById(id);
        long took = System.currentTimeMillis() - requestContext.getStartTime();
        System.out.println("[Controller] sorgu " + took + " ms cekdi");
        return response;
    }
}

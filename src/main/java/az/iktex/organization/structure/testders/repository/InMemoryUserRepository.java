package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.component.RequestContext;
import az.iktex.organization.structure.testders.dto.UserInfoRequest;
import az.iktex.organization.structure.testders.dto.UserInfoResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Primary
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, UserInfoResponse> storage = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();
    private final RequestContext requestContext;

    public InMemoryUserRepository(RequestContext requestContext) {
        this.requestContext = requestContext;
        System.out.println("InMemoryUserRepository yaradildi: " + this.hashCode());
    }

    @PostConstruct
    public void init() {
        createUser(new UserInfoRequest("Ali Aliyev", "ali@mail.az"));
        createUser(new UserInfoRequest("Leyla Mammadova", "leyla@mail.az"));
        createUser(new UserInfoRequest("Orxan Huseynov", "orxan@mail.az"));
        System.out.println("UserRepository hazirdir, " + storage.size() + " user yuklendi");
    }

    @PreDestroy
    public void cleanup() {
        storage.clear();
        System.out.println("UserRepository temizlendi (storage bosaldildi)");
    }

    @Override
    public UserInfoResponse getUserById(Long id) {
        System.out.println("[Repository] requestId=" + requestContext.getRequestId());
        UserInfoResponse user = storage.get(id);
        if (user == null) {
            throw new NoSuchElementException("User tapilmadi: id=" + id);
        }
        return user;
    }

    @Override
    public List<UserInfoResponse> getAllUsers() {
        return storage.values().stream()
                .sorted(Comparator.comparing(UserInfoResponse::id))
                .toList();
    }

    @Override
    public UserInfoResponse createUser(UserInfoRequest request) {
        long id = idCounter.incrementAndGet();
        UserInfoResponse user = new UserInfoResponse(id, request.name(), request.email());
        storage.put(id, user);
        return user;
    }
}

package az.iktex.organization.structure.testders.component;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.concurrent.atomic.AtomicLong;

@Component
@ApplicationScope
public class UserStatistics {

    private final AtomicLong createdUsers = new AtomicLong();
    private final AtomicLong getByIdCalls = new AtomicLong();

    public void incrementCreatedUsers() {
        createdUsers.incrementAndGet();
    }

    public void incrementGetByIdCalls() {
        getByIdCalls.incrementAndGet();
    }

    public long getCreatedUsers() {
        return createdUsers.get();
    }

    public long getGetByIdCalls() {
        return getByIdCalls.get();
    }
}

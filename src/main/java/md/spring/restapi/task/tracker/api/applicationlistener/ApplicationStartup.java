package md.spring.restapi.task.tracker.api.applicationlistener;

import lombok.RequiredArgsConstructor;
import md.spring.restapi.task.tracker.api.services.UsersService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
@RequiredArgsConstructor
@Component
class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

    private final UsersService userService;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        userService.createDefaultAdmin();
    }
}
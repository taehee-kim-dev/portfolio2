package portfolio2.module.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ProfileController {
    private final Environment env;

    @GetMapping("/profile")
    public String profile() {
        List<String> currentActivatedProfiles = Arrays.asList(env.getActiveProfiles());
        List<String> deployProfiles = Arrays.asList("deploy", "deploy1", "deploy2");

        return currentActivatedProfiles.stream()
                .filter(deployProfiles::contains)
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }
}
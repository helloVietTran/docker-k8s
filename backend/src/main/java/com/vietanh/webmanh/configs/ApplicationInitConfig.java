package com.vietanh.webmanh.configs;

import java.util.Set;

import com.vietanh.webmanh.constants.Gender;
import com.vietanh.webmanh.dbs.postgres.models.Permission;
import com.vietanh.webmanh.dbs.postgres.models.Role;
import com.vietanh.webmanh.dbs.postgres.models.User;
import com.vietanh.webmanh.dbs.postgres.repositories.PermissionRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.RoleRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    private static final Logger log = LoggerFactory.getLogger(ApplicationInitConfig.class);

    static final String ADMIN_EMAIL = "admin@webmanh.com";
    static final String ADMIN_PASSWORD = "admin123";

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            log.info("ACTION=APP_INIT STATUS=START");

            initPermissions();
            initRoles();
            initAdminUser();

            log.info("ACTION=APP_INIT STATUS=FINISHED");
        };
    }

    /* ================= PERMISSION ================= */

    private void initPermissions() {
        createPermissionIfNotExists("COMMENT", "User can comment");
        createPermissionIfNotExists("UPDATED_STORY", "Add or update story");
        createPermissionIfNotExists("DELETE_STORY", "Delete story");
        createPermissionIfNotExists("FULL_POWER", "Full system permissions");
    }

    private Permission createPermissionIfNotExists(String name, String description) {
        return permissionRepository.findById(name)
                .orElseGet(() -> {
                    log.info("ACTION=CREATE_PERMISSION STATUS=START name={}", name);
                    return permissionRepository.save(
                            Permission.builder()
                                    .name(name)
                                    .description(description)
                                    .build()
                    );
                });
    }

    /* ================= ROLE ================= */

    private void initRoles() {
        Permission comment = permissionRepository.getReferenceById("COMMENT");
        Permission updateStory = permissionRepository.getReferenceById("UPDATED_STORY");
        Permission deleteStory = permissionRepository.getReferenceById("DELETE_STORY");
        Permission fullPower = permissionRepository.getReferenceById("FULL_POWER");

        createRoleIfNotExists("USER", "Normal user", Set.of(comment));

        createRoleIfNotExists("AUTHOR", "Author role",
                Set.of(comment, updateStory, deleteStory));

        createRoleIfNotExists("ADMIN", "Administrator",
                Set.of(fullPower));
    }

    private void createRoleIfNotExists(String name, String description, Set<Permission> permissions) {
        roleRepository.findById(name)
                .orElseGet(() -> {
                    log.info("ACTION=CREATE_ROLE STATUS=SUCCESS name={}", name);
                    return roleRepository.save(
                            Role.builder()
                                    .name(name)
                                    .description(description)
                                    .permissions(permissions)
                                    .build()
                    );
                });
    }

    /* ================= ADMIN USER ================= */

    private void initAdminUser() {
        if (userRepository.findByEmail(ADMIN_EMAIL).isPresent()) {
            log.info("ACTION=INIT_ADMIN_USER STATUS=SKIPPED reason=EXISTS email={}",
                    ADMIN_EMAIL);
            return;
        }

        Role adminRole = roleRepository.getReferenceById("ADMIN");

        User admin = User.builder()
                .email(ADMIN_EMAIL)
                .username("admin")
                .password(passwordEncoder.encode(ADMIN_PASSWORD))
                .roles(Set.of(adminRole))
                .gender(Gender.MALE)
                .isVerified(true)
                .build();

        userRepository.save(admin);

        log.warn("ACTION=INIT_ADMIN_USER STATUS=CREATED email={} reason=DEFAULT_PASSWORD",
                ADMIN_EMAIL);
    }
}

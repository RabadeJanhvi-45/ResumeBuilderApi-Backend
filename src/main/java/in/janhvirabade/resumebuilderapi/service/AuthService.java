package in.janhvirabade.resumebuilderapi.service;

import in.janhvirabade.resumebuilderapi.document.User;
import in.janhvirabade.resumebuilderapi.dto.AuthResponse;
import in.janhvirabade.resumebuilderapi.dto.RegisterRequest;
import in.janhvirabade.resumebuilderapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    public AuthResponse register(RegisterRequest request) {
        log.info("Inside AuthService.register(): {}", request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User already exists with this email");
        }

        User newUser = toDocument(request);
        userRepository.save(newUser);

        // TODO: Send verification email

        return toResponse(newUser);
    }
    private AuthResponse toResponse(User newUser){
        return  AuthResponse.builder()
                .id(newUser.getId())
                .name(newUser.getName())
                .email(newUser.getEmail())
                .profileImageUrl(newUser.getProfileImageUrl())
                .subscriptionPlan(newUser.getSubscriptionPlan())
                .emailVerified(newUser.isEmailVerified())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .build();

    }

    private User toDocument(RegisterRequest request){
      return User.builder()
              .name(request.getName())
              .email(request.getEmail())
              .password(request.getPassword()) // In real application, hash the password before saving
              .profileImageUrl(request.getProfileImageUrl())
              .verificationToken(UUID.randomUUID().toString())
              .verificationExpires(LocalDateTime.now().plusDays(1))
              .build();
    }

}

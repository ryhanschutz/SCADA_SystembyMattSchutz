package com.mattschutz.scada.service;

import com.mattschutz.scada.dto.AuthenticationRequest;
import com.mattschutz.scada.dto.AuthenticationResponse;
import com.mattschutz.scada.dto.RegisterRequest;
import com.mattschutz.scada.entity.User;
import com.mattschutz.scada.entity.UserRole;
import com.mattschutz.scada.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthenticationResponse register(RegisterRequest request) {
        // Verificar se usuário já existe
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Usuário já existe: " + request.getUsername());
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado: " + request.getEmail());
        }
        
        // Criar novo usuário
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : UserRole.VISITOR);
        user.setActive(true);
        
        userRepository.save(user);
        
        log.info("Novo usuário registrado: {} ({})", user.getUsername(), user.getRole());
        
        String token = jwtService.generateToken(user);
        
        return AuthenticationResponse.builder()
                .token(token)
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
    
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        if (!user.getActive()) {
            throw new IllegalStateException("Usuário desativado");
        }
        
        // Atualizar último login
        user.updateLastLogin();
        userRepository.save(user);
        
        log.info("Usuário autenticado: {} ({})", user.getUsername(), user.getRole());
        
        String token = jwtService.generateToken(user);
        
        return AuthenticationResponse.builder()
                .token(token)
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}

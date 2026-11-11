package br.com.consultorio.erp.autenticacao.service;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.consultorio.erp.autenticacao.dto.CadastroRequest;
import br.com.consultorio.erp.autenticacao.dto.LoginRequest;
import br.com.consultorio.erp.autenticacao.dto.LoginResponse;
import br.com.consultorio.erp.autenticacao.entity.PerfilUsuario;
import br.com.consultorio.erp.autenticacao.entity.Usuario;
import br.com.consultorio.erp.autenticacao.repository.UsuarioRepository;
import br.com.consultorio.erp.autenticacao.security.JwtService;

@Service
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AutenticacaoService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public LoginResponse cadastrarAdministrador(
            CadastroRequest request) {

        if (usuarioRepository.count() > 0) {
            throw new IllegalArgumentException(
                    "O administrador inicial já foi cadastrado"
            );
        }

        String email = normalizarEmail(request.email());

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException(
                    "Já existe um usuário com esse e-mail"
            );
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome().trim());
        usuario.setEmail(email);
        usuario.setSenha(
                passwordEncoder.encode(request.senha())
        );
        usuario.setPerfil(PerfilUsuario.ADMIN);
        usuario.setAtivo(true);

        Usuario usuarioSalvo =
                usuarioRepository.save(usuario);

        return criarResposta(usuarioSalvo);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = normalizarEmail(request.email());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            request.senha()
                    )
            );
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "E-mail ou senha inválidos"
            );
        }

        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "E-mail ou senha inválidos"
                ));

        return criarResposta(usuario);
    }

    private LoginResponse criarResposta(Usuario usuario) {
        String token = jwtService.gerarToken(
                usuario.getEmail()
        );

        return new LoginResponse(
                "Bearer",
                token,
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil().name()
        );
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
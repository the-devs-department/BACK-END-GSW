package com.gsw.taskmanager.service;

import com.gsw.taskmanager.dto.LoginRequest;
import com.gsw.taskmanager.dto.UsuarioAlteracaoDto;
import com.gsw.taskmanager.dto.UsuarioResponseDto;
import com.gsw.taskmanager.entity.Usuario;
import com.gsw.taskmanager.exception.BusinessException;
import com.gsw.taskmanager.repository.UsuarioRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;


    public List<UsuarioResponseDto> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .filter(Usuario::isAtivo)
                .map(usuario -> new UsuarioResponseDto(
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getDataCadastro(),
                        true,
                        usuario.getTarefas()
                ))
                .toList();
    }

    public UsuarioResponseDto buscarUsuarioPorId(String uuid) {
        Optional<Usuario> usuarioBuscado = usuarioRepository.findById(uuid);

        return usuarioBuscado.map(usuario -> new UsuarioResponseDto(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDataCadastro(),
                usuario.isAtivo(),
                usuario.getTarefas()
        )).orElseThrow();
    }

    public UsuarioResponseDto criarUsuario(Usuario user){
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByEmail(user.getEmail());

        if (usuarioEncontrado.isPresent()){
            throw new DataIntegrityViolationException("Usuário já existe");
        }
        user.setSenha(passwordEncoder.encode(user.getSenha()));
        user.setAtivo(true);
        user.setDataCadastro(LocalDateTime.now());

        usuarioRepository.save(user);

        return new UsuarioResponseDto(
                user.getNome(),
                user.getEmail(),
                user.getDataCadastro(),
                user.isAtivo(),
                user.getTarefas()
            );
    }

    public UsuarioResponseDto atualizarUsuario(UsuarioAlteracaoDto usuario) {
        Usuario usuarioBanco = usuarioRepository.findById(usuario.id()).orElseThrow();

        if (usuario.email() != null) {
            usuarioBanco.setEmail(usuario.email());
        }
        if (usuario.nome() != null) {
            usuarioBanco.setNome(usuario.nome());
        }
        usuarioRepository.save(usuarioBanco);
        return new UsuarioResponseDto(
                usuarioBanco.getNome(),
                usuarioBanco.getEmail(),
                usuarioBanco.getDataCadastro(),
                usuarioBanco.isAtivo(),
                usuarioBanco.getTarefas()
        );
    }

    public void deletarById(String id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        if (usuario.isAtivo()) {
            usuario.setAtivo(false);
            usuarioRepository.save(usuario);
        } else {
            throw new BusinessException("Usuário já está deletado.");
        }
    }

    public String autenticar(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não cadastrado."));
        if (!usuario.isAtivo()) {
            throw new IllegalStateException("Usuário inativo");
        }

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new BadCredentialsException("Usuário ou senha inválidos");
        }
        return jwtService.generateToken(usuario);
    }
}
package br.com.forum_hub.domain.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.forum_hub.infra.email.EmailService;
import br.com.forum_hub.infra.exception.RegraDeNegocioException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Lazy
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmailIgnoreCaseAndVerificadoTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("O usuário não foi encontrado!"));
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCaseAndVerificadoTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
    }

    public Usuario buscarUsuarioPorId(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
    }

    @Transactional
    public Usuario cadastrar(DadosCadastroUsuario dados) {
        String senhaCriptografada = passwordEncoder.encode(dados.senha());
        var usuario = new Usuario(dados, senhaCriptografada);

        emailService.enviarEmailVerificacao(usuario);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void verificarEmail(String codigo) {
        Usuario usuario = usuarioRepository.findByToken(codigo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
        usuario.verificar();
    }

    public Usuario buscarUsuarioPorNomeUsuario(String nomeUsuario) {
        return usuarioRepository.findByNomeUsuarioIgnoreCaseAndVerificadoTrueAndAtivoTrue(nomeUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
    }

    @Transactional
    public Usuario editarPerfil(Usuario usuario, DadosEdicaoUsuario dados) {
        return usuario.alterarDados(dados);
    }

    @Transactional
    public void alterarSenha(DadosAlteracaoSenha dados, Usuario usuario) {
        if (!passwordEncoder.matches(dados.senhaAtual(), usuario.getSenha()))
            throw new RegraDeNegocioException("Senha digitada não confere com a senha atual!");
        if (!dados.novaSenha().equals(dados.novaSenhaConfirmacao()))
            throw new RegraDeNegocioException("Senha e confirmação não conferem!");

        String senhaCriptografada = passwordEncoder.encode(dados.novaSenha());
        usuario.alterarSenha(senhaCriptografada);
    }

    @Transactional
    public void desativarUsuario(Usuario usuario) {
        usuario.desativar();
    }
}

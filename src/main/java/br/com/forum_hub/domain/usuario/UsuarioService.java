package br.com.forum_hub.domain.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("O usuário não foi encontrado!"));
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
    }

    public Usuario buscarUsuarioPorId(Long idUsuario) {
        return usuarioRepository.findById(idUsuario).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
    }
}

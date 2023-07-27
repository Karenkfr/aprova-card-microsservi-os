package io.github.microsservicos.msclientes.application;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import io.github.microsservicos.msclientes.domain.Cliente;
import io.github.microsservicos.msclientes.infra.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {
	
	private final ClienteRepository clienteRepository;
	
	@Transactional
	public Cliente save(Cliente cliente) {
		return clienteRepository.save(cliente);
			
	}
	
	public Optional<Cliente> getByCpf(String cpf){
		return clienteRepository.findByCpf(cpf);
	}

}

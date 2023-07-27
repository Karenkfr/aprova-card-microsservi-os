package io.github.microsservicos.mscartoes.application;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.microsservicos.mscartoes.domain.ClienteCartao;
import io.github.microsservicos.mscartoes.infra.repository.ClienteCartaoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteCartaoService {
	
	private final ClienteCartaoRepository clienteCartaoRepository;
	
	public List<ClienteCartao> listCartoesByCpf(String cpf){
		return clienteCartaoRepository.findByCpf(cpf);
	}

}

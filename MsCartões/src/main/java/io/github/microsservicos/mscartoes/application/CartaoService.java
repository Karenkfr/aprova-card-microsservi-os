package io.github.microsservicos.mscartoes.application;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.microsservicos.mscartoes.domain.Cartao;
import io.github.microsservicos.mscartoes.infra.repository.CartaoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartaoService {
	
	private final CartaoRepository cartaoRepository;
	
	@Transactional
	public Cartao save(Cartao cartao) {
		return cartaoRepository.save(cartao);
	}
	
	
	public List<Cartao> getCartoesRendaMenorIgual(Long renda){
		var  rendaBigDecimal = BigDecimal.valueOf(renda);
		return cartaoRepository.findByRendaLessThanEqual(rendaBigDecimal);
	}

}

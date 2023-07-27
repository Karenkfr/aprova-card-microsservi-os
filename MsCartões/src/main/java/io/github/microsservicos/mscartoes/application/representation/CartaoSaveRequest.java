package io.github.microsservicos.mscartoes.application.representation;

import java.math.BigDecimal;

import io.github.microsservicos.mscartoes.domain.BandeiraCartao;
import io.github.microsservicos.mscartoes.domain.Cartao;
import lombok.Data;

@Data
public class CartaoSaveRequest {

	private String nome;
	private BandeiraCartao bandeira;
	private BigDecimal renda;
	private BigDecimal limite;
	
	
	public Cartao toModel() {
		return new Cartao(nome, bandeira, renda, limite);
	}
	
}

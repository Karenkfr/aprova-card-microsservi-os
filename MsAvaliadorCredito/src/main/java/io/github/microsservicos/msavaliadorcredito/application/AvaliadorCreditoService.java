package io.github.microsservicos.msavaliadorcredito.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import feign.FeignException;
import io.github.microsservicos.msavaliadorcredito.application.ex.DadosClienteNotFoundException;
import io.github.microsservicos.msavaliadorcredito.application.ex.ErroComunicacaoMicroservicesException;
import io.github.microsservicos.msavaliadorcredito.domain.model.Cartao;
import io.github.microsservicos.msavaliadorcredito.domain.model.CartaoAprovado;
import io.github.microsservicos.msavaliadorcredito.domain.model.CartaoCliente;
import io.github.microsservicos.msavaliadorcredito.domain.model.DadosCliente;
import io.github.microsservicos.msavaliadorcredito.domain.model.RetornoAvaliacaoCliente;
import io.github.microsservicos.msavaliadorcredito.domain.model.SituacaoCliente;
import io.github.microsservicos.msavaliadorcredito.infra.clients.CartoesResourceClient;
import io.github.microsservicos.msavaliadorcredito.infra.clients.ClienteResourceClient;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

	private final ClienteResourceClient clienteResourceClient;
	private final CartoesResourceClient cartoesResourceClient;

	public SituacaoCliente obterSituacaoCliente(String cpf)
			throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException {

		try {
			ResponseEntity<DadosCliente> dadosClienteResponse = clienteResourceClient.dadosCliente(cpf);
			ResponseEntity<List<CartaoCliente>> cartoesResponse = cartoesResourceClient.getCartoesByCliente(cpf);
			return SituacaoCliente.builder().cliente(dadosClienteResponse.getBody()).cartoes(cartoesResponse.getBody())
					.build();
		} catch (FeignException.FeignClientException e) {

			int status = e.status();

			if (HttpStatus.NOT_FOUND.value() == status) {
				throw new DadosClienteNotFoundException();
			}
			throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);

		}

	}

	public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda)
			throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException {

		try {
			ResponseEntity<DadosCliente> dadosClienteResponse = clienteResourceClient.dadosCliente(cpf);
			ResponseEntity<List<Cartao>> cartoesResponse = cartoesResourceClient.getCartoesRendaAteh(renda);

			List<Cartao> cartoes = cartoesResponse.getBody();
			var listaCartoesAprovados = cartoes.stream().map(cartao -> {

				DadosCliente dadosCliente = dadosClienteResponse.getBody();

				BigDecimal limiteBasico = cartao.getLimiteBasico();
				BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());
				var fator = idadeBD.divide(BigDecimal.valueOf(10));
				BigDecimal limiteAprovado = fator.multiply(limiteBasico);

				CartaoAprovado aprovado = new CartaoAprovado();
				aprovado.setCartao(cartao.getNome());
				aprovado.setBandeira(cartao.getBandeira());
				aprovado.setLimiteAprovado(limiteAprovado);

				return aprovado;

			}).collect(Collectors.toList());

			return new RetornoAvaliacaoCliente(listaCartoesAprovados);

		} catch (FeignException.FeignClientException e) {
			int status = e.status();
			if (HttpStatus.NOT_FOUND.value() == status) {
				throw new DadosClienteNotFoundException();
			}
			throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
		}

	}

}

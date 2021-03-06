package org.lab.insurance.portfolio.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.lab.insurance.portfolio.common.exception.PortfolioException;
import org.lab.insurance.portfolio.common.model.Asset;
import org.lab.insurance.portfolio.common.model.Investment;
import org.lab.insurance.portfolio.common.model.Portfolio;
import org.lab.insurance.portfolio.common.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PorfolioService {

	@Autowired
	private PortfolioRepository portfolioRepository;

	/**
	 * Busca o crea el Investment de un asset asociado a un portfolio. Como el investment tiene fechas de inicio/fin
	 * comprueba que el Investment este en el rango valido a partir de la fecha de entrada.
	 * 
	 * @param portfolio
	 * @param asset
	 * @param date
	 * @return
	 */
	public Investment findOrCreateActiveInvestment(Portfolio portfolio, Asset asset, Date date) {
		Investment result;
		if (portfolio.getInvestments() == null) {
			portfolio.setInvestments(new ArrayList<>());
		}

		//@formatter:off
		List<Investment> list = portfolio.getInvestments().stream().filter(x ->
				x.getAsset().getId().equals(asset.getId()) &&
				x.getStartDate().compareTo(date) >= 0 &&
				(x.getEndDate() == null || x.getEndDate().compareTo(date) <= 0))
				.collect(Collectors.toList());
		//@formatter:on

		switch (list.size()) {
		case 0:
			result = new Investment();
			result.setAsset(asset);
			result.setStartDate(date);
			portfolio.getInvestments().add(result);
			portfolioRepository.save(portfolio);
			break;
		case 1:
			result = list.iterator().next();
			break;
		default:
			// TODO add extra info
			throw new PortfolioException("Several active investments have been found in the portfolio");
		}
		return result;
	}

}

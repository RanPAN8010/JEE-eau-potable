package com.qualieau.service;

import com.qualieau.dao.AnalyseDAO;
import com.qualieau.model.Analyse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public class QualiteEauService {
	private AnalyseDAO analyseDAO;

	public QualiteEauService(AnalyseDAO analyseDAO) {
		this.analyseDAO = analyseDAO;
	}
	
	/**
     * Calculer les statistiques relatives au taux de conformité pour une ville donnée
     */
    public double calculerTauxConformite(String codeInsee) throws Exception {
        List<Analyse> analyses = analyseDAO.getAnalyseByCommune(codeInsee);
        if (analyses == null || analyses.isEmpty()) return 100.0;

        long nbConformes = analyses.stream()
                .filter(a -> a.getConforme() != null && a.getConforme().equalsIgnoreCase("C"))
                .count();
        double tauxRaw = (double) nbConformes / analyses.size() * 100;
        
        // 返回保留两位小数的结果，方便前端 maquette 展示
        return BigDecimal.valueOf(tauxRaw)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
    
    /**
     * Récupérer la liste des non-conformités
     */
    public List<Analyse> recupererAlertes(String codeInsee) throws Exception {
    	List<Analyse> allAnalyses = analyseDAO.getAnalyseByCommune(codeInsee);
    	if (allAnalyses == null) return new java.util.ArrayList<>();
    	
    	return allAnalyses.stream()
                .filter(a -> a.getConforme() != null && a.getConforme().equalsIgnoreCase("N"))
                .collect(Collectors.toList());
    }
    
    /**
     * 新增功能需求：获取最近一次检测的状态 
     * 用于首页或结果页顶部的“Dernier relevé”状态显示。
     */
    public String getEtatGlobalRecent(String codeInsee) throws Exception {
        List<Analyse> analyses = analyseDAO.getAnalyseByCommune(codeInsee);
        if (analyses == null || analyses.isEmpty()) return "Inconnu";
        
        // 由于 DAO 已经按日期 DESC 排序，第一条即为最新记录
        return analyses.get(0).getConforme().equalsIgnoreCase("C") ? "Globalement Conforme" : "Alerte Sanitaire";
    }
}

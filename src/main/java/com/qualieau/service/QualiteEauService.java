package com.qualieau.service;

import com.qualieau.dao.AnalyseDAO;
import com.qualieau.model.Analyse;
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
        // Appel de la nouvelle méthode searchAnalyses
        List<Analyse> analyses = analyseDAO.searchAnalyses("insee", codeInsee);
        
        if (analyses.isEmpty()) return 100.0;

        long nbConformes = analyses.stream().filter(Analyse::isConforme).count();
        return (double) nbConformes / analyses.size() * 100;
    }
    
    /**
     * Récupérer la liste des non-conformités
     */
    public List<Analyse> recupererAlertes(String codeInsee) throws Exception {
        // Appel de la nouvelle méthode searchAnalyses
        return analyseDAO.searchAnalyses("insee", codeInsee).stream()
                .filter(a -> !a.isConforme())
                .collect(Collectors.toList());
    }
}
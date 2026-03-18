import { useState } from 'react';
import { AreaChart, Area, ResponsiveContainer, Tooltip as RechartsTooltip, BarChart, Bar, XAxis } from 'recharts';
import { ChevronDown, Download, Activity, Database } from 'lucide-react';

export default function App() {
  const [view, setView] = useState('home'); 
  const [filter, setFilter] = useState('all');
  const [expandedRow, setExpandedRow] = useState(null);

  // DONNÉES DE TENDANCE (Clés corrigées pour les graphiques)
  const trendData = [
    { month: 'Sep', value: 12 }, { month: 'Oct', value: 15 },
    { month: 'Nov', value: 14 }, { month: 'Dec', value: 22 },
    { month: 'Jan', value: 18 }, { month: 'Fév', value: 20 },
    { month: 'Mar', value: 18 }
  ];

  const mockData = [
    { id: 1, date: "2026-03-10", param: "Nitrates", cat: "Chimie", val: "18", unit: "mg/L", limit: "< 50", ok: true, labo: "Labo Santé Environnement 76", comment: "Analyse conforme aux normes." },
    { id: 2, date: "2026-02-15", param: "Escherichia coli", cat: "Bactério", val: "0", unit: "n/100mL", limit: "0", ok: true, labo: "Labo Santé Environnement 76", comment: "Absence de bactéries pathogènes." },
    { id: 3, date: "2026-01-22", param: "Entérocoques", cat: "Bactério", val: "2", unit: "n/100mL", limit: "0", ok: false, labo: "Labo Santé Environnement 76", comment: "Dépassement détecté. Vigilance sanitaire." },
    { id: 4, date: "2025-12-05", param: "pH", cat: "Physico-chimie", val: "7.4", unit: "-", limit: "6.5 – 9", ok: true, labo: "Analyse Terrain", comment: "pH stable et équilibré." },
    { id: 5, date: "2025-11-10", param: "Turbidité", cat: "Physico-chimie", val: "1.2", unit: "NFU", limit: "< 2", ok: true, labo: "Labo Santé Environnement 76", comment: "Eau parfaitement limpide." },
  ];

  const stats = {
    total: mockData.length,
    conforme: mockData.filter(d => d.ok).length,
    alerte: mockData.filter(d => !d.ok).length
  };

  const exportCSV = () => {
    const headers = "Date,Parametre,Valeur,Unite,Statut\n";
    const rows = mockData.map(d => `${d.date},${d.param},${d.val},${d.unit},${d.ok ? 'OK' : 'ALERTE'}`).join("\n");
    const blob = new Blob([headers + rows], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'QualiEau_Export.csv';
    a.click();
  };

  const filteredData = filter === 'all' ? mockData : filter === 'ok' ? mockData.filter(d => d.ok) : mockData.filter(d => !d.ok);

  return (
    <div className="min-h-screen bg-[#F8FAFC] text-[#0F172A] font-sans selection:bg-[#0D9488] selection:text-white flex flex-col transition-all duration-700">
      
      {/* HEADER COMPLET */}
      <header className="px-8 md:px-16 py-6 flex justify-between items-center border-b border-[#E2E8F0] sticky top-0 bg-[#F8FAFC]/90 backdrop-blur-md z-50">
        <div className="flex items-center gap-3 cursor-pointer group" onClick={() => setView('home')}>
          <div className="w-10 h-10 bg-[#0F172A] rounded-sm flex items-center justify-center group-hover:bg-[#0D9488] transition-colors">
            <Activity className="text-white w-5 h-5" />
          </div>
          <span className="font-serif text-3xl font-bold tracking-tight italic">QualiEau.</span>
        </div>
        
        <div className="flex items-center gap-12">
          <nav className="hidden md:flex gap-12 text-[10px] uppercase tracking-[0.25em] font-bold text-[#64748B]">
            <button onClick={() => setView('home')} className={`${view === 'home' ? 'text-[#0F172A] border-b-2 border-[#0D9488]' : ''} pb-1 transition-all`}>Recherche</button>
            <button onClick={() => setView('results')} className={`${view === 'results' ? 'text-[#0F172A] border-b-2 border-[#0D9488]' : ''} pb-1 transition-all`}>Résultats</button>
            <button onClick={() => setView('stats')} className={`${view === 'stats' ? 'text-[#0F172A] border-b-2 border-[#0D9488]' : ''} pb-1 transition-all`}>Statistiques</button>
          </nav>
          {view === 'results' && (
            <button onClick={exportCSV} className="hidden md:flex items-center gap-2 px-5 py-2.5 border-2 border-[#0F172A] rounded text-[10px] uppercase tracking-widest font-bold text-[#0F172A] hover:bg-[#0F172A] hover:text-white transition-all">
              <Download size={14} /> Exporter CSV
            </button>
          )}
        </div>
      </header>

      {/* VUE 1 : ACCUEIL ÉDITORIAL COMPLET */}
      {view === 'home' && (
        <main className="animate-in fade-in duration-1000">
          <section className="px-8 md:px-16 lg:px-24 py-24 md:py-32 border-b border-[#E2E8F0] relative overflow-hidden">
            <div className="absolute top-0 right-0 -mr-20 -mt-20 w-[600px] h-[600px] bg-[#E0F2FE] rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-pulse"></div>
            
            <div className="max-w-6xl relative z-10">
              <h1 className="font-serif text-5xl md:text-7xl leading-[1.2] mb-12 text-[#0F172A]">
                Afficher la qualité de l'eau potable à
                <br />
                <span className="inline-block relative mt-4 group">
                  <input
                    type="text"
                    placeholder="Rouen, 75001, INSEE..."
                    className="bg-transparent border-b-[3px] border-[#CBD5E1] text-[#0F172A] placeholder-[#94A3B8] focus:outline-none focus:border-[#0D9488] w-full md:w-[650px] pb-2 font-sans text-3xl md:text-5xl font-light transition-all duration-700"
                  />
                </span>
                <span className="text-[#0D9488] font-serif ml-2 animate-pulse">.</span>
              </h1>

              {/* Paramètres de recherche RÉINTÉGRÉS (Corrigé image_309e1b.png) */}
              <div className="mt-12 mb-16 flex flex-wrap gap-12 items-center font-sans text-xs text-[#64748B]">
                <span className="uppercase tracking-[0.2em] text-[10px] font-bold text-[#0F172A]">Paramètres —</span>
                <div className="flex items-center gap-3">
                  <span>Secteur :</span>
                  <select className="bg-transparent border-b border-[#CBD5E1] text-[#0F172A] focus:outline-none cursor-pointer pb-1 appearance-none pr-6 hover:border-[#0D9488] transition-colors">
                    <option>Tous les départements</option>
                    <option>76 — Seine-Maritime</option>
                  </select>
                </div>
                <div className="flex items-center gap-3">
                  <span>Région :</span>
                  <select className="bg-transparent border-b border-[#CBD5E1] text-[#0F172A] focus:outline-none cursor-pointer pb-1 appearance-none pr-6 hover:border-[#0D9488] transition-colors">
                    <option>Toutes les régions</option>
                    <option>Normandie</option>
                  </select>
                </div>
                <div className="flex items-center gap-3">
                  <span>Période :</span>
                  <select className="bg-transparent border-b border-[#CBD5E1] text-[#0F172A] focus:outline-none cursor-pointer pb-1 appearance-none pr-6 hover:border-[#0D9488] transition-colors">
                    <option>Dernier relevé</option>
                    <option>12 derniers mois</option>
                  </select>
                </div>
              </div>

              <button onClick={() => setView('results')} className="px-12 py-6 bg-[#0F172A] text-white text-[11px] uppercase tracking-[0.3em] font-bold hover:bg-[#0D9488] hover:shadow-xl hover:-translate-y-1 transition-all duration-300">
                Lancer l'analyse des données
              </button>
            </div>
          </section>

          {/* Section Mission & Découverte */}
          <section className="px-8 md:px-16 lg:px-24 py-20 grid grid-cols-1 md:grid-cols-3 gap-16 bg-white">
            <div className="space-y-6">
              <h3 className="text-[10px] uppercase tracking-widest font-bold text-[#0D9488]">01 — La Mission</h3>
              <p className="font-serif text-xl leading-relaxed">Démocratiser l'accès aux données sanitaires.</p>
              <p className="text-sm text-[#64748B] leading-relaxed">Nous agrégeons les contrôles officiels pour offrir une lecture claire de votre environnement immédiat.</p>
            </div>
            <div className="space-y-6">
              <h3 className="text-[10px] uppercase tracking-widest font-bold text-[#0D9488]">02 — Exploration</h3>
              <ul className="space-y-4 font-serif text-lg italic text-[#64748B]">
                <li className="hover:text-[#0D9488] cursor-pointer transition-colors">Qualité de l'eau à Paris →</li>
                <li className="hover:text-[#0D9488] cursor-pointer transition-colors">Qualité de l'eau à Lyon →</li>
                <li className="hover:text-[#0D9488] cursor-pointer transition-colors">Qualité de l'eau à Rouen →</li>
              </ul>
            </div>
            <div className="bg-[#0F172A] p-10 text-white flex flex-col justify-between hover:bg-[#1e293b] transition-colors duration-700 shadow-xl group cursor-default">
              <p className="text-[9px] uppercase tracking-[0.3em] opacity-60 font-bold group-hover:opacity-100 transition-opacity">Mise à jour SISE-Eaux</p>
              <p className="text-4xl font-serif italic my-6 leading-tight">17 Mars <br/> 2026</p>
              <p className="text-[9px] leading-relaxed opacity-60 uppercase tracking-widest font-bold underline underline-offset-4 decoration-[#0D9488]">Synchronisation validée</p>
            </div>
          </section>
        </main>
      )}

      {/* VUE 2 : RÉSULTATS AVEC FILTRES, GRAPH ET DÉTAILS */}
      {view === 'results' && (
        <main className="px-8 md:px-16 lg:px-24 py-16 animate-in slide-in-from-bottom-8 duration-1000">
          <div className="max-w-6xl mx-auto">
            
            {/* 1. Recherche Active Typographique */}
            <div className="mb-16 pb-6 border-b border-[#E2E8F0] flex flex-col md:flex-row justify-between items-baseline gap-6 shadow-[0_4px_12px_-6px_rgba(0,0,0,0.03)] p-4 bg-white/50">
              <div className="text-[10px] uppercase tracking-[0.2em] font-bold text-[#64748B]">
                Recherche : <span className="text-[#0F172A] font-medium border-b border-[#CBD5E1] pb-1 cursor-pointer hover:border-[#0F172A]">Rouen (76)</span>
                <span className="mx-6 text-[#CBD5E1]">|</span> 
                Période : <span className="text-[#0F172A] font-medium border-b border-[#CBD5E1] pb-1 cursor-pointer hover:border-[#0F172A]">12 mois</span>
              </div>
              <div className="flex gap-10 text-[10px] uppercase tracking-[0.2em] font-bold">
                <div className="text-[#0D9488] flex items-center gap-2"><div className="w-1.5 h-1.5 rounded-full bg-[#0D9488]"></div> Conformes : {stats.conforme}</div>
                <div className="text-[#E11D48] flex items-center gap-2"><div className="w-1.5 h-1.5 rounded-full bg-[#E11D48]"></div> Alertes : {stats.alerte}</div>
                <div className="text-[#0F172A] flex items-center gap-2"><div className="w-1.5 h-1.5 rounded-full bg-[#0F172A]"></div> Total : {stats.total}</div>
              </div>
            </div>

            {/* Header des résultats : Synthèse & Graphique RESTAURÉ (Corrigé image_309ed6.png) */}
            <div className="flex flex-col lg:flex-row justify-between items-start lg:items-end mb-16 gap-12">
              <div className="max-w-xl">
                <button onClick={() => setView('home')} className="text-[9px] uppercase tracking-[0.3em] font-bold text-[#64748B] mb-8 block hover:text-[#0F172A]">← Nouvelle recherche</button>
                <h2 className="font-serif text-5xl md:text-6xl mb-4 italic text-[#0F172A]">Rouen.</h2>
                <p className="text-[#64748B] font-light text-xl leading-relaxed">
                  Prélèvements effectués sur le réseau de distribution. <br />
                  <span className="text-[#0D9488] font-semibold underline underline-offset-8 decoration-1 italic">L'eau est conforme</span> aux exigences de santé.
                </p>
              </div>

              {/* Sparkline de Tendance minimaliste (Correction Height et Clés de données) */}
              <div className="w-full lg:w-80 h-32 bg-white/50 p-4 border border-[#E2E8F0] rounded-xl relative group">
                <ResponsiveContainer width="100%" height="100%">
                  <AreaChart data={trendData}>
                    <defs>
                      <linearGradient id="colorValue" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor="#0D9488" stopOpacity={0.15}/>
                        <stop offset="95%" stopColor="#0D9488" stopOpacity={0}/>
                      </linearGradient>
                    </defs>
                    <Area type="monotone" dataKey="value" stroke="#0D9488" strokeWidth={2} fill="url(#colorValue)" />
                    <RechartsTooltip content={({ active, payload }) => {
                      if (active && payload && payload.length) {
                        return <div className="bg-[#0F172A] text-white text-[10px] px-2 py-1 font-bold">{payload[0].value} mg/L</div>;
                      }
                      return null;
                    }} />
                  </AreaChart>
                </ResponsiveContainer>
                <div className="flex justify-between text-[8px] uppercase tracking-widest font-bold text-[#94A3B8] mt-3 group-hover:text-[#0D9488] transition-colors">
                  <span>Historique Nitrates</span>
                  <span>Semestre 2026</span>
                </div>
              </div>
            </div>

            {/* Filtres "Texte" (Fini les pastilles "jouet") */}
            <div className="flex flex-col md:flex-row justify-between items-center py-6 border-y border-[#0F172A] mb-12 gap-6 bg-white/40 px-4">
              <div className="flex gap-10 text-[10px] uppercase tracking-[0.2em] font-bold">
                <button onClick={() => setFilter('all')} className={`${filter === 'all' ? 'text-[#0F172A] border-b border-[#0F172A]' : 'text-[#64748B]'} pb-1 hover:text-[#0F172A] transition-all`}>Tous les relevés</button>
                <button onClick={() => setFilter('ok')} className={`${filter === 'ok' ? 'text-[#0D9488] border-b border-[#0D9488]' : 'text-[#64748B]'} pb-1 hover:text-[#0F172A] transition-all`}>Conformes</button>
                <button onClick={() => setFilter('alert')} className={`${filter === 'alert' ? 'text-[#E11D48] border-b border-[#E11D48]' : 'text-[#64748B]'} pb-1 hover:text-[#E11D48] transition-all`}>Alertes uniquement</button>
              </div>
              <div className="text-[9px] text-[#94A3B8] font-bold uppercase tracking-[0.3em] italic">
                Affichage de {filteredData.length} prélèvements analysés
              </div>
            </div>

            {/* TABLEAU DES ANALYSES TYPOGRAPHIQUE */}
            <div className="space-y-0">
              {filteredData.map((item) => (
                <div key={item.id} className="border-b border-[#E2E8F0] group transition-all duration-500">
                  <div onClick={() => setExpandedRow(expandedRow === item.id ? null : item.id)} className="grid grid-cols-2 md:grid-cols-6 py-10 items-center cursor-pointer hover:bg-white px-6 -mx-6 transition-colors duration-300">
                    <div className="col-span-1 text-[10px] text-[#94A3B8] font-bold uppercase tracking-widest">{item.date}</div>
                    <div className="col-span-1 md:col-span-2 font-serif text-2xl md:text-3xl flex items-center gap-4 group-hover:translate-x-3 transition-transform duration-500">
                      {item.param}
                      <ChevronDown size={16} className={`text-[#CBD5E1] transition-transform duration-500 ${expandedRow === item.id ? 'rotate-180 text-[#0F172A]' : ''}`} />
                    </div>
                    <div className="hidden md:block col-span-1 text-right font-sans font-light text-2xl italic text-[#0F172A]">
                      {item.val} <span className="text-[10px] text-[#94A3B8] not-italic font-bold">{item.unit}</span>
                    </div>
                    <div className="hidden md:block col-span-1 text-right text-[10px] text-[#94A3B8] uppercase tracking-widest font-bold">Limite : {item.limit}</div>
                    <div className="col-span-1 text-right flex items-center justify-end gap-5">
                      <span className={`text-[10px] uppercase tracking-[0.25em] font-bold ${item.ok ? 'text-[#0D9488]' : 'text-[#E11D48]'}`}>
                        {item.ok ? 'Conforme' : 'Alerte'}
                      </span>
                      <span className={`w-2 h-2 rounded-full ${item.ok ? 'bg-[#0D9488]' : 'bg-[#E11D48] animate-pulse shadow-[0_0_10px_rgba(225,29,72,0.5)]'}`}></span>
                    </div>
                  </div>

                  {/* Section de détail Dépliée : Fini les boîtes massives, plus d'élégance */}
                  {expandedRow === item.id && (
                    <div className="px-10 py-12 bg-[#F1F5F9]/50 border-l-[6px] border-[#0F172A] grid grid-cols-1 md:grid-cols-2 gap-12 animate-in slide-in-from-top-4 duration-500">
                      <div className="space-y-6 pr-8">
                        <h4 className="text-[10px] uppercase tracking-widest font-bold text-[#64748B]">Origine de l'analyse</h4>
                        <p className="text-lg font-serif italic text-[#0F172A]">{item.labo}</p>
                        <p className="text-sm leading-relaxed text-[#64748B] font-light">"{item.comment}"</p>
                      </div>
                      <div className="space-y-6 md:border-l md:border-[#E2E8F0] md:pl-12 flex flex-col justify-center">
                        <h4 className="text-[10px] uppercase tracking-widest font-bold text-[#64748B]">Analyse de conformité</h4>
                        <div className={`p-6 border ${item.ok ? 'border-[#0D9488]/30 bg-[#F0FDFA] text-[#0D9488]' : 'border-[#E11D48]/30 bg-[#FFF1F2] text-[#E11D48]'}`}>
                          <p className="text-xs font-bold uppercase tracking-widest mb-2 flex items-center gap-2">
                             <span className={`w-1.5 h-1.5 rounded-full ${item.ok ? 'bg-[#0D9488]' : 'bg-[#E11D48]'}`}></span> 
                             {item.ok ? "✓ Validé" : "⚠ Vigilance"}
                          </p>
                          <p className="text-sm leading-relaxed">
                            {item.ok 
                              ? "Ce paramètre respecte les exigences de qualité du code de la santé publique." 
                              : "Ce relevé présente une anomalie ponctuelle par rapport aux normes sanitaires."}
                          </p>
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              ))}
            </div>

            <footer className="mt-32 pt-16 border-t border-[#E2E8F0] flex flex-col items-center gap-6">
              <div className="font-serif text-2xl font-bold opacity-20 italic">QualiEau.</div>
              <p className="text-[9px] uppercase tracking-[0.4em] font-bold text-[#94A3B8] text-center">
                Portail Citoyen — Données d'intérêt public Ministère de la Santé
              </p>
            </footer>
          </div>
        </main>
      )}

      {/* VUE 3 : STATISTIQUES ENRICHIES (Corrigé image_3048e3.png - Plus de flou !) */}
      {view === 'stats' && (
        <main className="p-8 md:p-24 animate-in fade-in duration-700 max-w-7xl mx-auto w-full flex-grow">
          <h2 className="font-serif text-6xl mb-16 italic border-b border-[#0F172A] pb-10 text-[#0F172A]">Tendances Nationales.</h2>
          
          {/* Grille de statistiques concrètes */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-12 mb-20">
              <div className="bg-white p-8 border border-[#E2E8F0] shadow-sm flex flex-col gap-3 group hover:border-[#0D9488] transition-colors">
                  <span className="text-[10px] uppercase tracking-widest font-bold text-[#64748B]">Nombre total d'analyses</span>
                  <p className="font-serif text-6xl italic text-[#0F172A]">14 832</p>
                  <p className="text-xs text-[#94A3B8] mt-2 underline decoration-[#0D9488]/30">SISE-Eaux, dernier semestre</p>
              </div>
              <div className="bg-white p-8 border border-[#E2E8F0] shadow-sm flex flex-col gap-3 group hover:border-[#0D9488] transition-colors">
                  <span className="text-[10px] uppercase tracking-widest font-bold text-[#64748B]">Taux de conformité</span>
                  <p className="font-serif text-6xl italic text-[#0D9488]">94.2%</p>
                  <p className="text-xs text-[#94A3B8] mt-2">Moyenne nationale sur la période</p>
              </div>
              <div className="bg-white p-8 border border-[#E2E8F0] shadow-sm flex flex-col gap-3 group hover:border-[#E11D48] transition-colors">
                  <span className="text-[10px] uppercase tracking-widest font-bold text-[#64748B]">Alertes Actives</span>
                  <p className="font-serif text-6xl italic text-[#E11D48]">17</p>
                  <p className="text-xs text-[#94A3B8] mt-2 underline decoration-[#E11D48]/30">Prélèvements non-conformes en cours</p>
              </div>
              <div className="bg-white p-8 border border-[#E2E8F0] shadow-sm flex flex-col gap-3 group hover:border-[#0F172A] transition-colors">
                  <span className="text-[10px] uppercase tracking-widest font-bold text-[#64748B]">Date de synchronisation</span>
                  <p className="font-serif text-4xl mt-3 text-[#0F172A]">17/03/2026</p>
                  <p className="text-xs text-[#94A3B8] mt-2">Dernière mise à jour du portail</p>
              </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-20 items-center">
            <div className="bg-white p-12 border border-[#E2E8F0] shadow-sm rounded-lg relative overflow-hidden group">
              <div className="absolute top-0 right-0 -mr-16 -mt-16 w-[200px] h-[200px] bg-[#E0F2FE] rounded-full mix-blend-multiply filter blur-3xl opacity-20 group-hover:opacity-40 transition-opacity"></div>
              <h3 className="text-[10px] uppercase font-bold tracking-[0.3em] mb-12 text-[#94A3B8]">Volume des prélèvements par mois</h3>
              <div className="h-64 relative z-10">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={trendData}>
                    <XAxis dataKey="month" stroke="#94A3B8" fontSize={12} axisLine={false} tickLine={false} />
                    <Bar dataKey="value" fill="#0F172A" radius={[2, 2, 0, 0]} hover={{ fill: '#0D9488' }} transition={{ duration: 0.5 }} />
                    <RechartsTooltip cursor={{ fill: '#F8FAFC' }} content={({ active, payload }) => {
                        if (active && payload && payload.length) {
                            return <div className="bg-[#0F172A] text-white text-[10px] px-2 py-1 font-bold">{payload[0].value} analyses</div>;
                        }
                        return null;
                    }} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </div>
            
            <div className="flex flex-col justify-center gap-12">
              <div className="space-y-4 border-l-4 border-[#0D9488] pl-8 flex flex-col gap-2">
                <span className="text-[10px] uppercase font-bold text-[#0D9488] tracking-widest">Tendance Régionale</span>
                <p className="font-serif text-3xl text-[#0F172A]">Qualité maintenue en Normandie.</p>
                <p className="text-sm text-[#64748B] font-light leading-relaxed">Les 76 départements maintiennent un haut standard de qualité, avec des conformités dépassant 95% dans les zones surveillées.</p>
              </div>
              <div className="space-y-4 border-l-4 border-[#E11D48] pl-8 flex flex-col gap-2">
                <span className="text-[10px] uppercase font-bold text-[#E11D48] tracking-widest">Alerte Nitrates</span>
                <p className="font-serif text-3xl text-[#0F172A]">Pic de nitrates détecté en zone agricole Nord.</p>
                <p className="text-sm text-[#64748B] font-light leading-relaxed">Une vigilance sanitaire est maintenue suite à la détection de niveaux supérieurs à 40mg/L sur 3 prélèvements consécutifs en Mars.</p>
              </div>
            </div>
          </div>
        </main>
      )}

    </div>
  );
}
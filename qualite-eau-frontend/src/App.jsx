import { useState, useEffect, useMemo } from 'react';
import { AreaChart, Area, ResponsiveContainer, Tooltip as RechartsTooltip, BarChart, Bar, XAxis, YAxis } from 'recharts';
import { ChevronDown, Download, Activity, Filter, Calendar, MapPin } from 'lucide-react';

export default function App() {
  const [view, setView] = useState('home'); 
  const [filter, setFilter] = useState('all'); 
  const [paramFilter, setParamFilter] = useState('all'); 
  const [periodFilter, setPeriodFilter] = useState('all');
  const [expandedRow, setExpandedRow] = useState(null);

  const [searchType, setSearchType] = useState('insee'); 
  const [searchQuery, setSearchQuery] = useState('');
  
  const [inputValue, setInputValue] = useState('');
  const [selectedDept, setSelectedDept] = useState('all');
  const [selectedCityCode, setSelectedCityCode] = useState(''); 
  const [selectedCityName, setSelectedCityName] = useState(''); 
  
  const [realData, setRealData] = useState([]);
  const [cityName, setCityName] = useState(''); 
  const [availableInsee, setAvailableInsee] = useState([]); 
  const [deptCommunes, setDeptCommunes] = useState([]); 
  const [nomsDepartements, setNomsDepartements] = useState({});
  const [isLoadingCities, setIsLoadingCities] = useState(false);

  const todayFormatted = new Date().toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' });

  // 1. Chargement des bases (Ton backend + Noms des départements du gouv)
  useEffect(() => {
    fetch(`http://localhost:8081/QualiEau/api/analyses?type=list_villes`)
      .then(res => res.json())
      .then(data => {
        if (Array.isArray(data)) setAvailableInsee(data);
        else setAvailableInsee([]);
      })
      .catch(() => setAvailableInsee([])); 

    fetch(`https://geo.api.gouv.fr/departements`)
      .then(res => res.json())
      .then(data => {
        const dictionnaireDepts = {};
        if (Array.isArray(data)) {
          data.forEach(d => { dictionnaireDepts[d.code] = d.nom; });
        }
        setNomsDepartements(dictionnaireDepts);
      })
      .catch(() => console.error("Erreur API Départements"));
  }, []);

  const availableDepts = useMemo(() => {
    if (!availableInsee.length) return [];
    const depts = new Set(availableInsee.map(code => code.startsWith('97') ? code.substring(0, 3) : code.substring(0, 2)));
    return Array.from(depts).sort();
  }, [availableInsee]);

  // 2. Chargement des villes du gouvernement selon le département
  useEffect(() => {
    if (selectedDept !== 'all') {
      setIsLoadingCities(true);
      fetch(`https://geo.api.gouv.fr/departements/${selectedDept}/communes?fields=nom,code`)
        .then(res => {
            if (!res.ok) throw new Error("Erreur réseau API");
            return res.json();
        })
        .then(data => {
          if (selectedDept === '75' && Array.isArray(data) && !data.find(c => c.code === '75056')) {
             data.push({ nom: "Paris", code: "75056" });
          }
          if (Array.isArray(data)) setDeptCommunes(data);
          else setDeptCommunes([]);
          setIsLoadingCities(false);
        })
        .catch(() => {
          setDeptCommunes([]);
          setIsLoadingCities(false);
        });
    } else {
      setDeptCommunes([]);
      setIsLoadingCities(false);
    }
  }, [selectedDept]);

  // 3. Charger les analyses
  useEffect(() => {
    if (!searchQuery) return;
    const fetchData = async () => {
      // Gestion du nom pour le titre H1
      if (searchType === 'departement') {
        const nomDept = nomsDepartements[searchQuery] || searchQuery;
        setCityName(`Département (${nomDept})`);
      } else if (!cityName || cityName === '') { // 🟢 Évite de re-télécharger le nom si on le connaît déjà
        try {
          const res = await fetch(`https://geo.api.gouv.fr/communes/${searchQuery}?fields=nom`);
          const data = await res.json();
          if (data.nom) setCityName(data.nom);
          else setCityName(`Commune (${searchQuery})`);
        } catch { 
          setCityName(`Commune (${searchQuery})`); 
        }
      }

      // Appel au Backend Java
      fetch(`http://localhost:8081/QualiEau/api/analyses?type=${searchType}&valeur=${searchQuery}`) 
        .then(res => res.json())
        .then(data => {
          if(!Array.isArray(data)) { setRealData([]); return; }
          const mapped = data.map((item, index) => ({
            id: index,
            date: item.date,
            param: item.parametre,
            val: Number(item.valeur) || 0,
            unit: item.unite !== "X" ? item.unite : "", 
            limit: "STANDARD", 
            ok: item.conforme,
            labo: "Laboratoire Santé Environnement",
            comment: item.conforme ? "Conforme aux normes de santé publique." : "Seuil de vigilance dépassé."
          }));
          mapped.sort((a, b) => new Date(b.date) - new Date(a.date));
          setRealData(mapped);
          setParamFilter('all'); 
        })
        .catch(() => setRealData([]));
    };
    fetchData();
  }, [searchQuery, searchType, nomsDepartements]); // cityName retiré des dépendances pour éviter les boucles

  // --- FILTRES ---
  const filteredData = useMemo(() => {
    let data = [...realData];
    if (periodFilter !== 'all' && data.length > 0) {
        const latest = new Date(data[0].date);
        const days = periodFilter === '3m' ? 90 : periodFilter === '6m' ? 180 : 365;
        data = data.filter(d => (latest - new Date(d.date)) / (1000*3600*24) <= days);
    }
    if (filter !== 'all') data = data.filter(d => filter === 'ok' ? d.ok : !d.ok);
    if (paramFilter !== 'all') data = data.filter(d => d.param === paramFilter);
    return data;
  }, [realData, filter, paramFilter, periodFilter]);

  const uniqueParams = useMemo(() => [...new Set(realData.map(d => d.param))].sort(), [realData]);

  const stats = useMemo(() => {
      const total = filteredData.length;
      const conforme = filteredData.filter(d => d.ok).length;
      const alerte = total - conforme;
      return { total, conforme, alerte, taux: total > 0 ? ((conforme / total) * 100).toFixed(1) : "0.0" }
  }, [filteredData]);

  // --- VILLES DYNAMIQUES (Croisement BDD & Gouvernement) ---
  const dropdownCities = useMemo(() => {
    const list = Array.isArray(availableInsee) ? availableInsee : [];
    if (selectedDept === 'all') return [];
    
    const codesInDept = list.filter(code => code.startsWith(selectedDept));
    return codesInDept.map(code => {
        const found = Array.isArray(deptCommunes) ? deptCommunes.find(c => c.code === code) : null;
        return { 
          code: code, 
          nom: found ? found.nom : `Commune (${code})` 
        };
    }).sort((a, b) => a.nom.localeCompare(b.nom));
  }, [availableInsee, selectedDept, deptCommunes]);

  const chartData = useMemo(() => {
    const counts = {};
    filteredData.forEach(d => {
        const dateObj = new Date(d.date);
        const label = dateObj.toLocaleString('fr-FR', { month: 'short', year: 'numeric' });
        counts[label] = (counts[label] || 0) + 1;
    });
    return Object.keys(counts).map(k => ({ label: k, value: counts[k] }));
  }, [filteredData]);

  const handleSearch = async () => {
    if (inputValue && inputValue.trim() !== '') { 
      if (isNaN(inputValue)) { 
        try {
          const res = await fetch(`https://geo.api.gouv.fr/communes?nom=${inputValue}&boost=population&limit=1`);
          const data = await res.json();
          if (data && data[0]) {
            setSearchQuery(data[0].code);
            setSearchType('insee');
            setCityName(data[0].nom); 
            setView('results');
          } else { alert("Ville introuvable."); }
        } catch { alert("Erreur réseau géographique."); }
      } else {
        setSearchQuery(inputValue);
        setSearchType('insee');
        setCityName(''); // Réinitialiser le nom pour forcer l'API à chercher
        setView('results');
      }
      return;
    }

    if (selectedCityCode) {
      setSearchQuery(selectedCityCode);
      setSearchType('insee');
      setCityName(selectedCityName); 
      setView('results');
    } else if (selectedDept !== 'all') {
      setSearchQuery(selectedDept);
      setSearchType('departement');
      setView('results');
    } else {
      alert("Veuillez saisir une ville ou choisir un secteur.");
    }
  };

  const exportCSV = () => {
    const headers = "Date,Parametre,Valeur,Unite,Statut\n";
    const rows = filteredData.map(d => `${d.date},"${d.param}",${d.val},"${d.unit}",${d.ok ? 'CONFORME' : 'ALERTE'}`).join("\n");
    const blob = new Blob([headers + rows], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a'); a.href = url; a.download = `QualiEau_${searchQuery}.csv`; a.click();
  };

  return (
    <div className="min-h-screen bg-[#F8FAFC] text-[#0F172A] font-sans flex flex-col">
      {/* HEADER */}
      <header className="px-8 md:px-16 py-6 flex justify-between items-center border-b border-[#E2E8F0] sticky top-0 bg-[#F8FAFC]/90 backdrop-blur-md z-50">
        <div className="flex items-center gap-3 cursor-pointer group" onClick={() => setView('home')}>
          <div className="w-10 h-10 bg-[#0F172A] flex items-center justify-center">
            <Activity className="text-white w-5 h-5" />
          </div>
          <span className="font-serif text-3xl font-bold tracking-tight italic">QualiEau.</span>
        </div>
        
        <div className="flex items-center gap-12">
          <nav className="hidden md:flex gap-12 text-[10px] uppercase tracking-[0.25em] font-bold text-[#64748B]">
            <button onClick={() => setView('home')} className={view === 'home' ? 'text-[#0D9488] border-b-2 border-[#0D9488] pb-1' : 'hover:text-[#0F172A] pb-1'}>Recherche</button>
            <button onClick={() => { if(searchQuery) setView('results') }} className={view === 'results' ? 'text-[#0D9488] border-b-2 border-[#0D9488] pb-1' : 'hover:text-[#0F172A] pb-1'}>Résultats</button>
            <button onClick={() => { if(searchQuery) setView('stats') }} className={view === 'stats' ? 'text-[#0F172A] border-b-2 border-[#0D9488] pb-1' : 'hover:text-[#0F172A] pb-1'}>Statistiques</button>
          </nav>
          {view !== 'home' && (
            <button onClick={exportCSV} className="hidden md:flex items-center gap-2 px-5 py-2.5 border border-[#0F172A] text-[10px] uppercase tracking-widest font-bold hover:bg-[#0F172A] hover:text-white transition-all">
              <Download size={14} /> Exporter
            </button>
          )}
        </div>
      </header>

      {/* VUE 1 : ACCUEIL */}
      {view === 'home' && (
        <main className="animate-in fade-in duration-700">
          <section className="px-8 md:px-16 lg:px-24 py-32 border-b border-[#E2E8F0]">
            <div className="max-w-4xl">
              <h1 className="font-serif text-6xl md:text-8xl leading-[1.1] mb-12 text-[#0F172A]">
                Afficher la qualité de l'eau potable à
              </h1>
              
              <div className="flex items-end mb-12 w-full md:w-[70%]">
                <input
                  type="text"
                  value={inputValue}
                  onChange={(e) => {
                      setInputValue(e.target.value);
                      setSelectedDept('all'); 
                      setSelectedCityCode('');
                  }}
                  onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
                  placeholder="Rouen, 75001, INSEE..."
                  className="bg-transparent text-[#0F172A] placeholder-[#94A3B8] focus:outline-none w-full border-b-[2px] border-[#CBD5E1] pb-4 font-sans text-4xl md:text-5xl font-light"
                />
                <div className="w-3 h-3 rounded-full bg-[#0D9488] mb-6 ml-2 shrink-0"></div>
              </div>

              <div className="flex flex-wrap gap-8 items-center font-sans text-xs text-[#0F172A] font-bold mb-16">
                <span className="uppercase tracking-[0.2em] text-[10px]">Paramètres —</span>
                
                <div className="flex items-center gap-3 border-b border-[#CBD5E1] pb-1">
                  <span className="text-[#64748B] font-normal">Secteur :</span>
                  <select 
                    value={selectedDept} 
                    onChange={(e) => {
                      setSelectedDept(e.target.value);
                      setSelectedCityCode(''); 
                      setInputValue('');
                    }} 
                    className="bg-transparent focus:outline-none cursor-pointer appearance-none pr-4"
                  >
                    <option value="all">Choisir un département...</option>
                    {availableDepts.map(d => (
                      <option key={d} value={d}>{d} — {nomsDepartements[d] || 'Département'}</option>
                    ))}
                  </select>
                </div>

                <div className="flex items-center gap-3 border-b border-[#CBD5E1] pb-1">
                  <span className="text-[#64748B] font-normal">Ville :</span>
                  <select 
                    value={selectedCityCode}
                    onChange={(e)=> {
                      const code = e.target.value;
                      setSelectedCityCode(code);
                      const cityObj = dropdownCities.find(c => c.code === code);
                      if (cityObj) setSelectedCityName(cityObj.nom);
                      setInputValue('');
                    }} 
                    disabled={selectedDept === 'all' || isLoadingCities}
                    className="bg-transparent focus:outline-none cursor-pointer appearance-none pr-4 disabled:opacity-50 max-w-[200px] truncate"
                  >
                    <option value="">
                      {selectedDept === 'all' 
                        ? "Choisissez un secteur d'abord" 
                        : (isLoadingCities ? "Chargement..." : "Sélectionner une ville...")}
                    </option>
                    {!isLoadingCities && dropdownCities.map(c => <option key={c.code} value={c.code}>{c.nom}</option>)}
                  </select>
                </div>
              </div>

              <button onClick={handleSearch} className="px-10 py-5 bg-[#0F172A] text-white text-[10px] uppercase tracking-[0.2em] font-bold hover:bg-[#0D9488] transition-colors">
                Lancer l'analyse
              </button>
            </div>
          </section>

          <section className="px-8 md:px-16 lg:px-24 py-20 grid grid-cols-1 md:grid-cols-3 gap-16 bg-white">
            <div className="space-y-4">
              <h3 className="text-[10px] uppercase tracking-widest font-bold text-[#0D9488]">01 — La Mission</h3>
              <p className="font-serif text-2xl leading-snug">Démocratiser l'accès aux données sanitaires.</p>
              <p className="text-sm text-[#64748B] leading-relaxed">Nous agrégeons les contrôles officiels pour offrir une lecture claire de votre environnement immédiat.</p>
            </div>
            <div className="space-y-4">
              <h3 className="text-[10px] uppercase tracking-widest font-bold text-[#0D9488]">02 — Exploration</h3>
              <ul className="space-y-4 font-serif text-xl italic text-[#94A3B8]">
                {/* 🟢 NOUVEAU : On injecte instantanément les noms des villes au clic ! */}
                <li onClick={()=>{setSearchQuery('75056'); setSearchType('insee'); setCityName('Paris'); setView('results');}} className="hover:text-[#0D9488] cursor-pointer transition-colors">Qualité de l'eau à Paris →</li>
                <li onClick={()=>{setSearchQuery('69123'); setSearchType('insee'); setCityName('Lyon'); setView('results');}} className="hover:text-[#0D9488] cursor-pointer transition-colors">Qualité de l'eau à Lyon →</li>
                <li onClick={()=>{setSearchQuery('76540'); setSearchType('insee'); setCityName('Rouen'); setView('results');}} className="hover:text-[#0D9488] cursor-pointer transition-colors">Qualité de l'eau à Rouen →</li>
              </ul>
            </div>
            <div className="bg-[#0F172A] p-10 text-white flex flex-col justify-between shadow-xl">
              <p className="text-[9px] uppercase tracking-[0.3em] font-bold text-[#94A3B8]">Mise à jour SISE-Eaux</p>
              <p className="text-4xl font-serif italic my-6 leading-tight">{todayFormatted}</p>
              <p className="text-[9px] uppercase tracking-widest font-bold underline decoration-[#94A3B8] underline-offset-4 text-[#94A3B8]">Synchronisation validée</p>
            </div>
          </section>
        </main>
      )}

      {/* VUE 2 : RÉSULTATS */}
      {view === 'results' && (
        <main className="px-8 md:px-16 lg:px-24 py-16 animate-in slide-in-from-bottom-8">
          <div className="max-w-6xl mx-auto">
            
            <div className="mb-16 pb-4 border-b border-[#E2E8F0] flex flex-col md:flex-row justify-between items-center gap-6">
              <div className="flex items-center gap-4 text-[10px] uppercase tracking-[0.2em] font-bold text-[#64748B]">
                <div className="flex items-center gap-2"><MapPin size={14}/> {searchType === 'departement' ? searchQuery.substring(0,2) : searchQuery}</div>
                <span className="text-[#CBD5E1]">|</span> 
                <div className="flex items-center gap-2">
                  <Calendar size={14}/>
                  <select value={periodFilter} onChange={(e)=>setPeriodFilter(e.target.value)} className="bg-transparent text-[#0F172A] font-bold border-b border-[#0F172A] outline-none cursor-pointer pb-1 appearance-none">
                    <option value="all">Historique complet</option>
                    <option value="3m">3 derniers mois</option>
                    <option value="6m">6 derniers mois</option>
                  </select>
                </div>
              </div>
              <div className="flex gap-8 text-[10px] uppercase tracking-widest font-bold">
                <div className="text-[#0D9488] flex items-center gap-2"><div className="w-1.5 h-1.5 rounded-full bg-[#0D9488]"></div> CONFORMES : {stats.conforme}</div>
                <div className="text-[#E11D48] flex items-center gap-2"><div className="w-1.5 h-1.5 rounded-full bg-[#E11D48]"></div> ALERTES : {stats.alerte}</div>
                <div className="text-[#0F172A] flex items-center gap-2"><div className="w-1.5 h-1.5 rounded-full bg-[#0F172A]"></div> TOTAL : {stats.total}</div>
              </div>
            </div>

            <div className="flex flex-col lg:flex-row justify-between items-start lg:items-end mb-16 gap-12">
              <div className="max-w-xl">
                <button onClick={() => setView('home')} className="text-[9px] uppercase tracking-[0.2em] font-bold text-[#94A3B8] mb-8 hover:text-[#0F172A] transition-colors">— NOUVELLE RECHERCHE</button>
                <h2 className="font-serif text-6xl md:text-8xl mb-4 italic text-[#0F172A] leading-none">{cityName}.</h2>
                <p className="text-[#64748B] font-light text-2xl leading-relaxed">
                  {/* 🟢 NOUVEAU : Ajout du mot "communal" manquant */}
                  Réseau de distribution {searchType === 'departement' ? 'départemental' : 'communal'}.<br />
                  {stats.alerte === 0 ? (
                    <span className="text-[#0D9488] font-bold underline decoration-[1.5px] underline-offset-4 italic">Conformité validée</span>
                  ) : (
                    <><span className="text-[#E11D48] font-bold underline decoration-[1.5px] underline-offset-4 italic">Vigilance</span> : {stats.alerte} alerte(s) détectée(s).</>
                  )}
                </p>
              </div>

              <div className="w-full lg:w-[400px] bg-white p-6 border border-[#E2E8F0] rounded-xl shadow-sm h-48 flex flex-col justify-between">
                <div className="flex justify-between text-[8px] uppercase tracking-widest font-bold text-[#0F172A] mb-4">
                  <span>VOLUME D'ANALYSES</span>
                  <span className="text-[#0D9488]">TENDANCE</span>
                </div>
                <div className="flex-grow">
                  <ResponsiveContainer width="100%" height="100%">
                      <BarChart data={chartData}>
                        <XAxis dataKey="label" fontSize={8} axisLine={false} tickLine={false} stroke="#94A3B8" tickMargin={10} />
                        <Bar dataKey="value" fill="#0F172A" radius={[2, 2, 0, 0]} maxBarSize={35} />
                      </BarChart>
                  </ResponsiveContainer>
                </div>
              </div>
            </div>

            <div className="flex flex-col md:flex-row justify-between items-center py-4 border-y border-[#0F172A] mb-12 gap-6">
              <div className="flex gap-10 text-[10px] uppercase tracking-[0.15em] font-bold w-full md:w-auto overflow-x-auto">
                <button onClick={() => setFilter('all')} className={filter==='all'?'text-[#0F172A] border-b-2 border-[#0F172A] pb-3':'text-[#64748B] pb-3'}>Tous les statuts</button>
                <button onClick={() => setFilter('ok')} className={filter==='ok'?'text-[#0D9488] border-b-2 border-[#0D9488] pb-3':'text-[#64748B] pb-3'}>Conformes</button>
                <button onClick={() => setFilter('alert')} className={filter==='alert'?'text-[#E11D48] border-b-2 border-[#E11D48] pb-3':'text-[#64748B] pb-3'}>Alertes uniquement</button>
              </div>
              <div className="flex items-center gap-3 text-[10px] text-[#0F172A] font-bold uppercase tracking-[0.15em]">
                <Filter size={14} className="text-[#64748B]"/>
                <select value={paramFilter} onChange={(e) => setParamFilter(e.target.value)} className="bg-transparent outline-none cursor-pointer border-b border-[#CBD5E1] pb-1 max-w-[250px] truncate appearance-none">
                  <option value="all">Tous les paramètres affichés</option>
                  {uniqueParams.map(p => <option key={p} value={p}>{p}</option>)}
                </select>
              </div>
            </div>

            <div className="space-y-0">
              {filteredData.length === 0 && <div className="py-20 text-center text-[#64748B] italic font-serif text-xl">Aucune donnée trouvée.</div>}
              {filteredData.slice(0, 50).map((item) => (
                <div key={item.id} className="border-b border-[#E2E8F0]">
                  <div onClick={() => setExpandedRow(expandedRow === item.id ? null : item.id)} className="grid grid-cols-12 py-10 items-center cursor-pointer hover:bg-white transition-colors">
                    
                    <div className="col-span-2 text-[10px] text-[#94A3B8] font-bold uppercase tracking-widest">{item.date}</div>
                    
                    <div className="col-span-5 font-serif text-3xl flex items-center gap-4 text-[#0F172A]">
                      {item.param} <ChevronDown size={16} className={`text-[#CBD5E1] transition-transform ${expandedRow === item.id ? 'rotate-180 text-[#0F172A]' : ''}`} />
                    </div>
                    
                    <div className="col-span-2 text-right font-sans text-4xl font-light text-[#0F172A]">
                        {item.val} <span className="text-[10px] text-[#94A3B8] font-bold ml-1">{item.unit}</span>
                    </div>
                    
                    <div className="col-span-2 text-right text-[9px] uppercase font-bold tracking-widest text-[#94A3B8]">
                        LIMITE : {item.limit}
                    </div>
                    
                    <div className="col-span-1 text-right flex items-center justify-end gap-3">
                      <span className={`text-[10px] font-bold uppercase tracking-widest ${item.ok ? 'text-[#0D9488]' : 'text-[#E11D48]'}`}>
                        {item.ok ? 'CONFORME' : 'ALERTE'}
                      </span>
                      <span className={`w-2 h-2 rounded-full ${item.ok ? 'bg-[#0D9488]' : 'bg-[#E11D48]'}`}></span>
                    </div>

                  </div>
                  {expandedRow === item.id && (
                    <div className="px-8 py-10 bg-[#F8FAFC] border-l-[4px] border-[#0F172A] mb-4 text-sm text-[#64748B] grid grid-cols-2">
                        <div>
                            <p className="font-bold text-[#0F172A] mb-1 uppercase text-[10px] tracking-widest">Origine</p>
                            <p className="font-serif italic text-lg">{item.labo}</p>
                        </div>
                        <div>
                            <p className="font-bold text-[#0F172A] mb-1 uppercase text-[10px] tracking-widest">Détails du contrôle</p>
                            <p>{item.comment}</p>
                        </div>
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>
        </main>
      )}

      {/* VUE 3 : STATISTIQUES */}
      {view === 'stats' && (
        <main className="p-8 md:p-24 animate-in fade-in max-w-7xl mx-auto w-full flex-grow">
          <h2 className="font-serif text-7xl mb-16 italic border-b border-[#0F172A] pb-10 text-[#0F172A]">Bilan de la commune.</h2>
          
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-20">
              <div className="bg-white p-8 border border-[#E2E8F0] shadow-sm flex flex-col justify-between">
                  <span className="text-[10px] uppercase font-bold text-[#64748B] tracking-widest">Analyses totales</span>
                  <p className="font-serif text-6xl italic text-[#0F172A] my-6">{stats.total}</p>
                  <p className="text-xs text-[#94A3B8] underline decoration-[#CBD5E1] underline-offset-4">Base SISE-Eaux,<br/>{cityName}</p>
              </div>
              <div className="bg-white p-8 border border-[#E2E8F0] shadow-sm flex flex-col justify-between">
                  <span className="text-[10px] uppercase font-bold text-[#64748B] tracking-widest">Taux de conformité</span>
                  <p className="font-serif text-6xl italic text-[#0D9488] my-6">{stats.taux}%</p>
                  <p className="text-xs text-[#94A3B8]">Moyenne de la sélection</p>
              </div>
              <div className="bg-white p-8 border border-[#E2E8F0] shadow-sm flex flex-col justify-between">
                  <span className="text-[10px] uppercase font-bold text-[#64748B] tracking-widest">Alertes Actives</span>
                  <p className="font-serif text-6xl italic text-[#E11D48] my-6">{stats.alerte}</p>
                  <p className="text-xs text-[#94A3B8] underline decoration-[#CBD5E1] underline-offset-4">Dépassements signalés</p>
              </div>
              <div className="bg-white p-8 border border-[#E2E8F0] shadow-sm flex flex-col justify-between">
                  <span className="text-[10px] uppercase font-bold text-[#64748B] tracking-widest">Dernière Analyse</span>
                  <p className="font-serif text-4xl text-[#0F172A] my-6">{realData[0]?.date || "—"}</p>
                  <p className="text-xs text-[#94A3B8]">Dernier prélèvement enregistré</p>
              </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-20 items-start">
            <div className="bg-white p-12 border border-[#E2E8F0] rounded-xl h-[450px] shadow-sm flex flex-col">
              <h3 className="text-[10px] uppercase font-bold tracking-[0.2em] mb-12 text-[#94A3B8]">Volume d'analyses (Tous paramètres)</h3>
              <div className="flex-grow w-full">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={chartData}>
                    <XAxis dataKey="label" fontSize={10} axisLine={false} tickLine={false} stroke="#94A3B8" tickMargin={10} />
                    <Bar dataKey="value" fill="#0F172A" radius={[2, 2, 0, 0]} maxBarSize={60} />
                    <RechartsTooltip cursor={{fill: '#F8FAFC'}} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </div>
            
            <div className="pt-12 pl-12 border-l-[4px] border-[#0D9488]">
                <span className="text-[10px] uppercase font-bold text-[#0D9488] tracking-widest">Diagnostic Local</span>
                <p className="font-serif text-4xl text-[#0F172A] leading-tight my-6">
                    {stats.alerte > 0 ? "Des anomalies ont été détectées." : "La qualité de l'eau est optimale."}
                </p>
                <p className="text-lg text-[#64748B] font-light leading-relaxed">
                    {stats.alerte > 0 
                      ? `Attention, pour les filtres sélectionnés, le taux de conformité est de ${stats.taux}%. Des dépassements ont été enregistrés sur certains prélèvements.` 
                      : "Les infrastructures maintiennent un haut standard de qualité, avec des taux de conformité rassurants sur l'ensemble du réseau local."}
                </p>
            </div>
          </div>
        </main>
      )}

      {/* FOOTER */}
      <footer className="mt-auto px-8 md:px-16 py-12 border-t border-[#E2E8F0] flex flex-col items-center gap-4 bg-[#F8FAFC]">
        <div className="font-serif text-2xl font-bold opacity-30 italic">QualiEau.</div>
        <p className="text-[9px] uppercase tracking-widest font-bold text-[#94A3B8]">Portail Citoyen — Données Ministérielles SISE-Eaux</p>
      </footer>
    </div>
  );
}
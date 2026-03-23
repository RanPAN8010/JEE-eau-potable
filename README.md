# 💧 Projet Qualité de l'Eau (QualiEau)

Ce projet est une application Full-Stack (Java JEE + React) avec une base de données MySQL conteneurisée.
L'interface Frontend a été pré-compilée et intégrée dans le projet Java (`src/main/webapp`).

## 🛠️ Prérequis
- **Docker** (pour lancer la base de données)
- **Eclipse IDE** (version Enterprise/Web) ou tout IDE supportant Maven et Tomcat.

## 🚀 Instructions de lancement

### 1. Démarrer la base de données (MySQL)
Ouvrez un terminal à la racine du projet et lancez le conteneur de la base de données :
`docker-compose up -d mysql`

### 2. Importer dans Eclipse
1. Allez dans `File` > `Import` > `Maven` > `Existing Maven Projects`.
2. Sélectionnez le dossier du projet.
3. Eclipse détectera automatiquement qu'il s'agit d'un projet Web (grâce au `pom.xml`).
4. Si des erreurs apparaissent, faites : Clic droit sur le projet > `Maven` > `Update Project...` (cochez "Force Update").

### 3. Lancer l'application
1. Faites un clic droit sur le projet > `Run As` > `Run on Server`.
2. Choisissez un serveur **Tomcat (v9 ou v10)**.
3. L'application (Backend + Frontend) sera accessible à l'adresse : `http://localhost:8080/QualiEau/`
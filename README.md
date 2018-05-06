# Netheos coding exercice

[![Build Status](https://travis-ci.org/tpvillard/netheos.svg?branch=master)](https://travis-ci.org/tpvillard/netheos)

Produire le code serveur exposé via des API REST correspondant aux users stories décrites ci-dessous.

* User story 1

En tant qu'utilisateur ayant les droits "administrateur", je peux insérer une question / réponse dans la base de connaissances (FAQ) du produit. Une question / réponse est définie par :

Le libellé de la question ;
Le libellé de la réponse ;
La liste des tags associés.

* User story 2

En tant qu'utilisateur ayant les droits "administrateur", je peux lister toutes les questions / réponses de la base de connaissances.

* User story 3 :

En tant qu'utilisateur, je peux rechercher la réponse à une question sans avoir à saisir le texte exact correspondant à une question ou à une réponse de la base de connaissances.

## Solution

L'application utilise java8 et embarque un serveur jetty.
L'API JAX-RS est utilisée pour la création du web service Rest (implémentation Jersey)
slqlite est le moteur de base de donnée utilisée.

Pour construire l'application, faire mvn clean install (maven 3.3.9)

Pour lancer l'application faire mvn exec:java

L'application est servie sur le port 8082.

Editer src/main/java/resources/config.yaml pour modifier la configuration.

Pour effectuer des requêtes:

* Generer un token d'acces.

Utiliser la classe GenerateTokens (sous src/test). la classe fournit en sortie deux tokens. 
1 token pour l'administrateur bob, 1 token pour l'utilisatrice alice.
Les tokens sont des jwts ayant une durée de vie limitée à 60 minutes.
(L'application n'est pas complète et ne fournit pas de mécanisme pour rafraichir les tokens)

* Pour insérer une nouvelle faq (avec un token administrateur):

curl -H "Content-Type: application/json" -H "Authorization: Bearer \<token\>" -d '{"question":"Comment ça va?","answer":"Pas mal et toi?","tags":""}' http://localhost:8082/faq-api/faqs

* Pour lister toutes les faqs dans la base (avec un token administrateur):

curl -i -H "Accept: application/json" -H "Authorization: Bearer \<token\>" http://localhost:8082/faq-api/faqs

* Pour chercher des faqs dans la base (avec un token utilisateur):

curl -i -H "Accept: application/json" -H "Authorization: Bearer \<token\>" http://localhost:8082/faq-api/search?query=<string>


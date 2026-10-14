/**
 * Les systèmes extérieurs, simulés : le service Flotte, le service Compte usager, le prestataire de paiement.
 *
 * <p>Dans la vraie architecture de GreenMove ce sont d'autres services, interrogés en REST ou en HTTP. Pour le TP,
 * trois classes en mémoire suffisent. Elles n'appartiennent à aucun hexagone : chaque hexagone définit ses propres
 * ports et écrit ses propres adaptateurs par-dessus ces classes. Le domaine et l'application ne les voient jamais
 * (règle ArchUnit).</p>
 */
package fr.greenmove.exterieur;

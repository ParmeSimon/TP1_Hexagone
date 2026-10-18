package fr.ekod.mediatheque;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AnnuaireTest {

    private DepotAdherents depot;
    private Journal journal;
    private Annuaire annuaire;

    @BeforeEach
    void setUp() {
        depot = mock(DepotAdherents.class);
        journal = mock(Journal.class);
        annuaire = new Annuaire(depot, journal);
    }

    @Test
    void une_saisie_trop_courte_leve_une_exception() {
        IllegalArgumentException erreur =
                assertThrows(IllegalArgumentException.class, () -> annuaire.rechercher("a"));

        assertThat(erreur).hasMessage("saisie trop courte");
    }

    @Test
    void une_saisie_nulle_est_traitee_comme_vide() {
        IllegalArgumentException erreur =
                assertThrows(IllegalArgumentException.class, () -> annuaire.rechercher(null));

        assertThat(erreur).hasMessage("saisie trop courte");
    }

    @Test
    void la_casse_est_ignoree_et_le_fragment_est_transmis_en_minuscules() {
        when(depot.parNomContenant("dup")).thenReturn(List.of(new Adherent("Dupont", "Jean")));

        List<Adherent> resultats = annuaire.rechercher("  DuP ");

        assertThat(resultats).hasSize(1);
        verify(depot).parNomContenant("dup");
    }

    @Test
    void les_resultats_sont_tries_par_nom_puis_prenom() {
        List<Adherent> mockAdherents = List.of(
                new Adherent("Martin", "Paul"),
                new Adherent("Dupont", "Marie"),
                new Adherent("Dupont", "Alice")
        );
        when(depot.parNomContenant("dup")).thenReturn(mockAdherents);

        List<Adherent> resultats = annuaire.rechercher("dup");

        assertThat(resultats).containsExactly(
                new Adherent("Dupont", "Alice"),
                new Adherent("Dupont", "Marie"),
                new Adherent("Martin", "Paul")
        );
    }

    @Test
    void il_y_a_au_plus_dix_resultats() {
        List<Adherent> tropDeResultats = List.of(
                new Adherent("Nom1", "A"), new Adherent("Nom2", "B"),
                new Adherent("Nom3", "C"), new Adherent("Nom4", "D"),
                new Adherent("Nom5", "E"), new Adherent("Nom6", "F"),
                new Adherent("Nom7", "G"), new Adherent("Nom8", "H"),
                new Adherent("Nom9", "I"), new Adherent("Nom10", "J"),
                new Adherent("Nom11", "K"), new Adherent("Nom12", "L")
        );
        when(depot.parNomContenant("test")).thenReturn(tropDeResultats);

        List<Adherent> resultats = annuaire.rechercher("test");

        assertThat(resultats).hasSize(10);
    }

    @Test
    void si_aucun_resultat_ne_correspond_la_liste_est_vide_et_jamais_null() {
        when(depot.parNomContenant("inconnu")).thenReturn(null);

        List<Adherent> resultats = annuaire.rechercher("inconnu");

        assertThat(resultats).isNotNull().isEmpty();
    }

    @Test
    void la_recherche_acceptee_est_consignee_dans_le_journal() {
        when(depot.parNomContenant("dupont")).thenReturn(List.of(new Adherent("Dupont", "Jean")));

        annuaire.rechercher("  DuPont  ");

        verify(journal).consigner("recherche : DuPont, 1 résultats");
    }

    @Test
    void une_saisie_refusee_n_est_pas_consignee() {
        assertThrows(IllegalArgumentException.class, () -> annuaire.rechercher("x"));

        verifyNoInteractions(journal);
    }
}
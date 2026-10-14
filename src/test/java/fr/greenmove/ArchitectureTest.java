package fr.greenmove;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Les règles d'architecture, exécutables : elles échouent dès qu'un import traverse une frontière.
 *
 * <p>Elles passent sur le code de départ parce que les hexagones sont encore vides ({@code allowEmptyShould}).
 * Gardez-les vertes : elles font partie de la note (partie 1, section 3).</p>
 *
 * <p>Ce qu'un hexagone a le droit de voir de son voisin : ses ports entrants (paquet {@code application}) et
 * ses événements publiés (paquet {@code domaine.evenements}). Et seulement depuis son {@code infrastructure} :
 * un adaptateur appelle le voisin, jamais un cas d'usage, jamais le domaine.</p>
 */
@AnalyzeClasses(packages = "fr.greenmove", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    // ------------------------------------------------------------------ à l'intérieur de chaque hexagone

    @ArchTest
    static final ArchRule le_domaine_ne_depend_d_aucune_technique = noClasses()
            .that().resideInAPackage("..domaine..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..infrastructure..", "..application..", "fr.greenmove.exterieur..",
                    "org.springframework..", "jakarta.persistence..", "com.fasterxml..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule l_application_ne_depend_d_aucun_adaptateur = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..infrastructure..", "fr.greenmove.exterieur..", "org.springframework..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule le_domaine_ne_lit_pas_l_horloge_systeme = noClasses()
            .that().resideInAPackage("..domaine..")
            .should().callMethod(java.time.Instant.class, "now")
            .allowEmptyShould(true);

    // ------------------------------------------------------------------ entre les deux hexagones

    @ArchTest
    static final ArchRule le_coeur_de_location_ignore_facturation = noClasses()
            .that().resideInAnyPackage("..location.domaine..", "..location.application..")
            .should().dependOnClassesThat().resideInAPackage("fr.greenmove.facturation..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule le_coeur_de_facturation_ignore_location = noClasses()
            .that().resideInAnyPackage("..facturation.domaine..", "..facturation.application..")
            .should().dependOnClassesThat().resideInAPackage("fr.greenmove.location..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule location_ne_voit_de_facturation_que_ses_ports_entrants = noClasses()
            .that().resideInAPackage("..location.infrastructure..")
            .should().dependOnClassesThat(resideInAPackage("fr.greenmove.facturation..")
                    .and(not(resideInAPackage("..facturation.application.."))))
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule facturation_ne_voit_de_location_que_ses_evenements_et_ses_ports_entrants = noClasses()
            .that().resideInAPackage("..facturation.infrastructure..")
            .should().dependOnClassesThat(resideInAPackage("fr.greenmove.location..")
                    .and(not(resideInAnyPackage("..location.application..", "..location.domaine.evenements.."))))
            .allowEmptyShould(true);
}

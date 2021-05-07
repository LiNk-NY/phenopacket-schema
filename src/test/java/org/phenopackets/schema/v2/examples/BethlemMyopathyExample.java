package org.phenopackets.schema.v2.examples;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Timestamp;
import org.ga4gh.vrs.v1.Integer;
import org.phenopackets.schema.v2.Family;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.*;
import org.ga4gh.vrsatile.v1.*;
import org.ga4gh.vrs.v1.*;

import static org.phenopackets.schema.v2.PhenoPacketTestUtil.ontologyClass;

public class BethlemMyopathyExample {

    private static final String PROBAND_ID = "14 year-old boy";
    private static final String MOTHER_ID = "MOTHER";
    private static final String FATHER_ID = "FATHER";

    // VRS Allele
    private static final Allele c_877G_to_A = Allele.newBuilder()
            .setLiteralSequenceExpression(LiteralSequenceExpression.newBuilder().setSequence("A"))
            .setSequenceLocation(SequenceLocation.newBuilder()
                    .setSequenceInterval(
                            SequenceInterval.newBuilder()
                                    .setStartInteger(Integer.newBuilder().setValue(876))
                                    .setEndInteger(Integer.newBuilder().setValue(877))
                    ).setSequenceId("NM_001848.2"))
            .build();

    // Variation
    private static final VariationDescriptor heterozygousCOL6A1Variant = VariationDescriptor.newBuilder()
            .setId("id:1")
            .setVariation(Variation.newBuilder().setAllele(c_877G_to_A))
            .setDescription("NM_001848.2:c.877G>A")
            .setGeneContext(GeneDescriptor.newBuilder().setSymbol("COL6A1").setValueId("HGNC:2211").build())
            .setVrsRefAlleleSeq("G")
            .setDescription("Heterozygous 877G>A transition in COL6A1")
            .build();


    static Phenopacket proband() {

        OntologyClass mild = OntologyClass.newBuilder().setId("HP:0012825").setLabel("Mild").build();
        OntologyClass evidenceCode = OntologyClass.newBuilder().
                setId("ECO:0000033").
                setLabel("author statement supported by traceable reference").
                build();
        Evidence citation = Evidence.newBuilder().
                setReference(ExternalReference.newBuilder().
                        setId("PMID:30808312").
                        setDescription("COL6A1 mutation leading to Bethlem myopathy with recurrent hematuria: a case report.").
                        build()).
                setEvidenceCode(evidenceCode)
                .build();

        PhenotypicFeature decreasedFetalMovement = PhenotypicFeature.newBuilder()
                .setType(ontologyClass("HP:0001558", "Decreased fetal movement"))
                .setOnset(TimeElement.newBuilder().setOntologyClass(ontologyClass("HP:0011461", "Fetal onset")).build())
                .addEvidence(citation)
                .build();


        PhenotypicFeature absentCranialNerveAbnormality = PhenotypicFeature.newBuilder()
                .setType(ontologyClass("HP:0031910", "Abnormal cranial nerve physiology"))
                .setNegated(true)
                .addEvidence(citation)
                .build();

        PhenotypicFeature motorDelay = PhenotypicFeature.newBuilder()
                .setType(ontologyClass("HP:0001270","Motor delay"))
                .setOnset(TimeElement.newBuilder().setOntologyClass(ontologyClass("HP:0011463","Childhood onset")))
                .setSeverity(mild)
                .build();


        PhenotypicFeature hematuria = PhenotypicFeature.newBuilder()
                .setType(ontologyClass("HP:0011463", "Macroscopic hematuria"))
                .setOnset(TimeElement.newBuilder().setAge(Age.newBuilder().setIso8601Duration("P14Y")))
                .addModifiers(ontologyClass("HP:0031796","Recurrent"))
                .addEvidence(citation)
                .build();



        Individual proband = Individual.newBuilder()
                .setSex(Sex.MALE)
                .setId(PROBAND_ID)
                .setTimeAtEncounter(TimeElement.newBuilder().setAge(Age.newBuilder().setIso8601Duration("P14Y")))
                .build();
        return Phenopacket.newBuilder()
                .setId(PROBAND_ID)
                .setSubject(proband)
                .addPhenotypicFeatures(decreasedFetalMovement)
                .addPhenotypicFeatures(absentCranialNerveAbnormality)
                .addPhenotypicFeatures(hematuria)
                .addPhenotypicFeatures(motorDelay)
//                .addVariants(heterozygousCOL6A1Variant)
                .build();
    }

    static Phenopacket unaffectedMother() {
        Individual mother = Individual.newBuilder()
                .setSex(Sex.FEMALE)
                .setId(MOTHER_ID)
                .build();
        return Phenopacket.newBuilder()
                .setSubject(mother)
                .build();
    }

    static Phenopacket unaffectedFather() {
        Individual father = Individual.newBuilder()
                .setSex(Sex.MALE)
                .setId(FATHER_ID)
                .build();
        return Phenopacket.newBuilder()
                .setSubject(father)
                .build();
    }

    private static Pedigree pedigree() {
        Pedigree.Person pedProband = Pedigree.Person.newBuilder()
                .setIndividualId(PROBAND_ID)
                .setSex(Sex.MALE)
                .setMaternalId(MOTHER_ID)
                .setPaternalId(FATHER_ID)
                .setAffectedStatus(Pedigree.Person.AffectedStatus.AFFECTED)
                .build();

        Pedigree.Person pedMother = Pedigree.Person.newBuilder()
                .setIndividualId(MOTHER_ID)
                .setSex(Sex.FEMALE)
                .setAffectedStatus(Pedigree.Person.AffectedStatus.UNAFFECTED)
                .build();

        Pedigree.Person pedFather = Pedigree.Person.newBuilder()
                .setIndividualId(FATHER_ID)
                .setSex(Sex.MALE)
                .setAffectedStatus(Pedigree.Person.AffectedStatus.UNAFFECTED)
                .build();

        return Pedigree.newBuilder()
                .addPersons(pedProband)
                .addPersons(pedMother)
                .addPersons(pedFather)
                .build();
    }


    /**
     * Example taken from PMID:30808312
     */
    static Family rareDiseaseFamily() {

        long millis  = System.currentTimeMillis();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000)).build();

        MetaData metaData = MetaData.newBuilder()
                .addResources(Resource.newBuilder()
                        .setId("hp")
                        .setName("human phenotype ontology")
                        .setNamespacePrefix("HP")
                        .setIriPrefix("http://purl.obolibrary.org/obo/HP_")
                        .setUrl("http://purl.obolibrary.org/obo/hp.owl")
                        .setVersion("2018-03-08")
                        .build())
                .addResources(Resource.newBuilder()
                        .setId("geno")
                        .setName("Genotype Ontology")
                        .setNamespacePrefix("GENO")
                        .setIriPrefix("http://purl.obolibrary.org/obo/GENO_")
                        .setUrl("http://purl.obolibrary.org/obo/geno.owl")
                        .setVersion("19-03-2018")
                        .build())
                .addResources(Resource.newBuilder()
                        .setId("pubmed")
                        .setName("PubMed")
                        .setNamespacePrefix("PMID")
                        .setIriPrefix("https://www.ncbi.nlm.nih.gov/pubmed/")
                        .build())
                .setCreatedBy("Peter R.")
                .setCreated(timestamp)
                .addExternalReferences(ExternalReference.newBuilder()
                        .setId("PMID:30808312")
                        .setDescription("Bao M, et al. COL6A1 mutation leading to Bethlem myopathy with recurrent hematuria: " +
                                "a case report. BMC Neurol. 2019;19(1):32.")
                        .build())
                .build();

        return Family.newBuilder()
                .setId("family")
                .setProband(proband())
                .addAllRelatives(ImmutableList.of(unaffectedMother(), unaffectedFather()))
                .setPedigree(pedigree())
                .setMetaData(metaData)
                .build();
    }

}

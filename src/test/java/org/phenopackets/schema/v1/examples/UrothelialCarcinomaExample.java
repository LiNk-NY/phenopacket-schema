package org.phenopackets.schema.v1.examples;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.PhenoPacket;
import org.phenopackets.schema.v1.core.*;

import java.time.Instant;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.phenopackets.schema.v1.PhenoPacketTestUtil.ontologyClass;


/**
 * Add three variants to the urothelial carcinoma
 * 1. 	rs1242535815
 * chr5:g.1295228G>A (TERT promoter mutation, -124C>T
 * 2.  	rs730882008 chr17:g.7577093C>A (ClinVar 182938), TP53
 * 3. AKT chr14	105246551	105246551	C	T (hg37)
 *
 */

public class UrothelialCarcinomaExample {

    private final PhenoPacket phenopacket;

    private final String patientId = "patient1";
    private final String ageAtBiopsy = "P52Y2M";


    public UrothelialCarcinomaExample() {
        MetaData metaData = buildMetaData();

        this.phenopacket = PhenoPacket.newBuilder()
                .setSubject(subject())
              .addBiosamples(bladderBiopsy())
                .addBiosamples(prostateBiospy())
                .addBiosamples(leftUreterBiospy())
                .addBiosamples(rightUreterBiospy())
                .addBiosamples(pelvicLymphNodeBiospy())
                .addDiseases(infiltratingUrothelialCarcinoma())
                .setMetaData(metaData)
                .build();


    }


    private MetaData buildMetaData() {
        return MetaData.newBuilder()
                .addResources(Resource.newBuilder()
                        .setId("ncit")
                        .setName("NCI Thesaurus OBO Edition")
                        .setNamespacePrefix("NCIT")
                        .setUrl("http://purl.obolibrary.org/obo/ncit.owl")
                        .setVersion("18.05d")
                        .build())
                .build();
    }


    private Disease infiltratingUrothelialCarcinoma() {
            return Disease.newBuilder()
                .setId("NCIT:C39853")
                .setLabel("Infiltrating Urothelial Carcinoma")
                .build();
    }


    private Individual subject() {
        return Individual.newBuilder()
                .setId(this.patientId)
                .setDateOfBirth(Timestamp.newBuilder()
                        .setSeconds(Instant.parse("1964-03-15T00:00:00Z").getEpochSecond()))
                .build();
    }


    private Biosample biosampleBuilder(String patientId, String sampleId, String age, OntologyClass sampleType, List<Phenotype> phenotypes) {
        return Biosample.newBuilder().
                setIndividualId(patientId).
                setId(sampleId).
                setIndividualAgeAtCollection(Age.newBuilder().
                        setAge(age).
                        build()).
                setType(sampleType).
                addAllPhenotypes(phenotypes).
                build();
    }

    private Phenotype fromFinding(String id, String label) {
        OntologyClass oc = ontologyClass(id, label);
        return Phenotype.newBuilder().setType(oc).build();
    }


    private Biosample bladderBiopsy() {
        String sampleId = "sample1";
        // left wall of urinary bladder
        OntologyClass sampleType = ontologyClass("UBERON_0001256", "wall of urinary bladder");
        // also want to mention the procedure, Prostatocystectomy (NCIT:C94464)
        ImmutableList.Builder<Phenotype> builder = new ImmutableList.Builder<>();
        //Infiltrating Urothelial Carcinoma (Code C39853)
        Phenotype infiltratingUrothelialCarcinoma = fromFinding("NCIT:C39853", "Infiltrating Urothelial Carcinoma");
        builder.add(infiltratingUrothelialCarcinoma);
        // The tumor was staged as pT2b, meaning infiltration into the outer muscle layer of the bladder wall
        // pT2b Stage Finding (Code C48766)
        Phenotype pT2b = fromFinding("NCIT:C48766", "pT2b Stage Finding");
        builder.add(pT2b);
        //pN2 Stage Finding (Code C48750)
        // cancer has spread to 2 or more lymph nodes in the true pelvis (N2)
        Phenotype pN2 = fromFinding("NCIT:C48750", "pN2 Stage Finding");
        builder.add(pN2);
        return biosampleBuilder(patientId, sampleId, this.ageAtBiopsy, sampleType, builder.build());
    }

    private Biosample prostateBiospy() {
        String sampleId = "sample2";
        //prostate
        OntologyClass sampleType = ontologyClass("UBERON:0002367", "prostate gland");
        ImmutableList.Builder<Phenotype> builder = new ImmutableList.Builder<>();
        Phenotype prostateAcinarAdenocarcinoma = fromFinding("NCIT:C5596", "Prostate Acinar Adenocarcinoma");
        Phenotype gleason7 = fromFinding("NCIT:C28091","Gleason Score 7");
        builder.add(prostateAcinarAdenocarcinoma);
        builder.add(gleason7);
        return biosampleBuilder(patientId, sampleId, this.ageAtBiopsy, sampleType, builder.build());
    }

    private Biosample leftUreterBiospy() {
        String sampleId = "sample3";
        OntologyClass sampleType = ontologyClass("UBERON:0001223", "left ureter");
        ImmutableList.Builder<Phenotype> builder = new ImmutableList.Builder<>();
        Phenotype normalFinding = fromFinding("NCIT:C38757", "Negative Finding");
        builder.add(normalFinding);
        return biosampleBuilder(patientId, sampleId, this.ageAtBiopsy, sampleType, builder.build());
    }
    private Biosample rightUreterBiospy() {
        String sampleId = "sample4";
        OntologyClass sampleType = ontologyClass("UBERON:0001222", "right ureter");
        ImmutableList.Builder<Phenotype> builder = new ImmutableList.Builder<>();
        Phenotype normalFinding = fromFinding("NCIT:C38757", "Negative Finding");
        builder.add(normalFinding);
        return biosampleBuilder(patientId, sampleId, this.ageAtBiopsy, sampleType, builder.build());
    }

    private Biosample pelvicLymphNodeBiospy() {
        String sampleId = "sample5";
        OntologyClass sampleType = ontologyClass("UBERON:0015876", "pelvic lymph node");
        ImmutableList.Builder<Phenotype> builder = new ImmutableList.Builder<>();
        Phenotype metastasis = fromFinding("NCIT:C19151", "Metastasis");
        builder.add(metastasis);
        return biosampleBuilder(patientId, sampleId, this.ageAtBiopsy, sampleType, builder.build());
    }


    @Test
    void testPatientName() {
        String expected = this.patientId;
        assertEquals(expected,this.phenopacket.getSubject().getId());
    }

}

package tr.edu.ege.seagent.wodqa.evaluation;

import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;

public class Queries {

	/**
	 * Nearest airports query.
	 */
	public static String NEAREST_AIRPORTS_QUERY = "PREFIX  lgdo: <http://linkedgeodata.org/ontology/>"
			+ "PREFIX  dbpedia-owl: <http://dbpedia.org/ontology/>"
			+ "PREFIX  geo:  <http://www.w3.org/2003/01/geo/wgs84_pos#>"
			+ "PREFIX  owl:  <http://www.w3.org/2002/07/owl#>"
			+ "PREFIX  dbpedia: <http://dbpedia.org/resource/>"
			+ "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "SELECT DISTINCT ?dbpediaAirport "
			+ "WHERE"
			+ "{ "
			+ "?airport geo:long ?airLong ."
			+ "?airport geo:lat ?airLat ."
			+ "?airport rdf:type lgdo:Airport ."
			+ "?airport owl:sameAs ?dbpediaAirport ."
			+ "?dbpediaAirport ?props ?values."
			+ "dbpedia:Edinburgh geo:lat ?cityLat ."
			+ "dbpedia:Edinburgh geo:long ?cityLong ."
			+ "FILTER (  ((?cityLat - ?airLat ) < 1.5)  &&  (( ?cityLat - ?airLat ) > -1.5)   &&  (( ?cityLong - ?airLong ) > -1.5)  &&  (( ?cityLong - ?airLong ) < 1.5 ))"
			+ "}";

	public static final String CROSS_DOMAIN_QUERY_1 = "PREFIX dbpedia:<http://dbpedia.org/resource/> "
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
			+ "SELECT ?predicate ?object WHERE {"
			+ "{ dbpedia:Barack_Obama ?predicate ?object .}"
			+ "UNION"
			+ "{ ?subject owl:sameAs dbpedia:Barack_Obama ."
			+ "?subject ?predicate ?object .} }";

	public static final String CROSS_DOMAIN_QUERY_2 = "SELECT ?party ?page WHERE { "
			+ "<http://dbpedia.org/resource/Barack_Obama> <http://dbpedia.org/ontology/party> ?party . "
			+ "?x <http://data.nytimes.com/elements/topicPage> ?page . "
			+ "?x <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Barack_Obama> .}";

	public static final String CROSS_DOMAIN_QUERY_3 = "SELECT ?president ?party ?page WHERE { "
			+ "?president <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/President> . "
			+ "?president <http://dbpedia.org/ontology/nationality> <http://dbpedia.org/resource/United_States> . "
			+ "?president <http://dbpedia.org/ontology/party> ?party . "
			+ "?x <http://www.w3.org/2002/07/owl#sameAs> ?president ."
			+ "?x <http://data.nytimes.com/elements/topicPage> ?page ." + "}";

	public static final String CROSS_DOMAIN_QUERY_4 = "SELECT ?actor ?news WHERE "
			+ "{ ?film <http://purl.org/dc/terms/title> \"Tarzan\" . "
			+ "?film <http://data.linkedmdb.org/resource/movie/actor> ?actor . "
			+ "?actor <http://www.w3.org/2002/07/owl#sameAs> ?x. "
			+ "?y <http://data.nytimes.com/elements/topicPage> ?news."
			+ "?y <http://www.w3.org/2002/07/owl#sameAs> ?x . }";

	public static final String CROSS_DOMAIN_QUERY_5 = "SELECT ?film ?director ?genre WHERE { "
			+ "?film <http://dbpedia.org/ontology/director> ?director."
			+ "?director <http://dbpedia.org/ontology/nationality> <http://dbpedia.org/resource/Italy> . "
			+ "?x <http://www.w3.org/2002/07/owl#sameAs> ?film . "
			+ "?x <http://data.linkedmdb.org/resource/movie/genre> ?genre .}";

	public static final String CROSS_DOMAIN_QUERY_6 = "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
			+ "PREFIX foaf: <"
			+ FOAF.getURI()
			+ ">"
			+ "PREFIX geonames: <http://www.geonames.org/ontology#>"
			+ "SELECT ?name ?location ?news WHERE {"
			+ "?artist foaf:name ?name ."
			+ "?artist foaf:based_near ?location ."
			+ "?location geonames:parentFeature ?germany ."
			+ "?germany geonames:name 'Federal Republic of Germany' }";

	public static final String CROSS_DOMAIN_QUERY_7 = "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
			+ "PREFIX geonames: <http://www.geonames.org/ontology#>"
			+ "PREFIX nytimes: <http://data.nytimes.com/elements/>"
			+ "SELECT ?location ?news WHERE {"
			+ "?location geonames:parentFeature ?parent ."
			+ "?parent geonames:name 'California'  ."
			+ "?y owl:sameAs ?location." + "?y nytimes:topicPage ?news }";

	public static final String LIFE_SCIENCES_QUERY_1 = "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
			+ "PREFIX dbpedia-owl-drug: <http://dbpedia.org/ontology/Drug/>"
			+ "SELECT ?drug ?melt WHERE {"
			+ "{ ?drug drugbank:meltingPoint ?melt . } "
			+ "UNION { ?drug dbpedia-owl-drug:meltingPoint ?melt . } }";

	public static final String LIFE_SCIENCES_QUERY_2 = "PREFIX owl: <"
			+ OWL.getURI()
			+ ">"
			+ "SELECT ?predicate ?object WHERE {"
			+ "{ <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> ?predicate ?object } "
			+ "UNION { <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> owl:sameAs ?caff ."
			+ "?caff ?predicate ?object } }";

	public static final String LIFE_SCIENCES_QUERY_3 = "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
			+ "PREFIX owl: <"
			+ OWL.getURI()
			+ ">"
			+ "PREFIX rdf: <"
			+ RDF.getURI()
			+ ">"
			+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>"
			+ "SELECT ?Drug ?IntDrug ?IntEffect WHERE {"
			+ "?Drug rdf:type dbpedia-owl:Drug ."
			+ "?y owl:sameAs ?Drug ."
			+ "?Int drugbank:interactionDrug1 ?y ."
			+ "?Int drugbank:interactionDrug2 ?IntDrug ."
			+ "?Int drugbank:text ?IntEffect ." + " }";

	public static final String LIFE_SCIENCES_QUERY_4 = "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
			+ "PREFIX kegg: <http://bio2rdf.org/ns/kegg#>"
			+ "PREFIX drugbank-category: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/>"
			+ "PREFIX rdf: <"
			+ RDF.getURI()
			+ ">"
			+ "SELECT ?drugDesc ?cpd ?equation WHERE {"
			+ "?drug drugbank:drugCategory drugbank-category:cathartics ."
			+ "?drug drugbank:keggCompoundId ?cpd ."
			+ "?drug drugbank:description ?drugDesc ."
			+ "?enzyme kegg:xSubstrate ?cpd ."
			+ "?enzyme rdf:type kegg:Enzyme ."
			+ "?reaction kegg:xEnzyme ?enzyme ."
			+ "?reaction kegg:equation ?equation . }";

	public static final String LIFE_SCIENCES_QUERY_5 = "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
			+ "PREFIX bio2rdf: <http://bio2rdf.org/ns/bio2rdf#>"
			+ "PREFIX purl: <http://purl.org/dc/elements/1.1/>"
			+ "PREFIX chebi: <http://bio2rdf.org/ns/bio2rdf#>"
			+ "PREFIX rdf: <"
			+ RDF.getURI()
			+ ">"
			+ "SELECT ?drug ?keggUrl ?chebiImage WHERE {"
			+ "?drug rdf:type drugbank:drugs ."
			+ "?drug drugbank:keggCompoundId ?keggDrug ."
			+ "?keggDrug bio2rdf:url ?keggUrl ."
			+ "?drug drugbank:genericName ?drugBankName ."
			+ "?chebiDrug purl:title ?drugBankName ."
			+ "?chebiDrug chebi:image ?chebiImage . }";

	public static final String LIFE_SCIENCES_QUERY_6 = "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
			+ "PREFIX drugbank-category: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/>"
			+ "PREFIX bio2rdf: <http://bio2rdf.org/ns/bio2rdf#>"
			+ "PREFIX purl: <http://purl.org/dc/elements/1.1/>"
			+ "PREFIX kegg: <http://bio2rdf.org/ns/kegg#>"
			+ "PREFIX rdf: <"
			+ RDF.getURI()
			+ ">"
			+ "SELECT ?drug ?title WHERE {"
			+ "?drug drugbank:drugCategory drugbank-category:micronutrient ."
			+ "?drug drugbank:casRegistryNumber ?id ."
			+ "?keggDrug rdf:type kegg:Drug ."
			+ "?keggDrug bio2rdf:xRef ?id ."
			+ "?keggDrug purl:title ?title . }";

	public static final String LIFE_SCIENCES_QUERY_7 = "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
			+ "PREFIX bio2rdf: <http://bio2rdf.org/ns/bio2rdf#>"
			+ "PREFIX xsd: <"
			+ XSD.getURI()
			+ ">"
			+ "SELECT ?drug ?transform ?mass WHERE {{"
			+ "?drug drugbank:affectedOrganism  'Humans and other mammals'."
			+ "?drug drugbank:casRegistryNumber ?cas ."
			+ "?keggDrug bio2rdf:xRef ?cas ."
			+ "?keggDrug bio2rdf:mass ?mass. "
			+ "FILTER ( ?mass > '5' ).} "
			+ "OPTIONAL { ?drug drugbank:biotransformation ?transform . } "
			+ "}";

	public static final String COMPLEX_QUERY_1 = "SELECT DISTINCT ?drug ?enzyme ?reaction  Where {"
			+ "?drug1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/antibiotics> ."
			+ "?drug2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/antiviralAgents> ."
			+ "?drug3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/antihypertensiveAgents> ."
			+ "?I1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?drug1 . "
			+ "?I1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?drug ."
			+ "?I2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?drug2 . "
			+ "?I2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?drug ."
			+ "?I3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?drug3 ."
			+ "?I3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?drug ."
			+ "?drug <http://www.w3.org/2002/07/owl#sameAs> ?drug5 ."
			+ "?drug5 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Drug> ."
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/keggCompoundId> ?cpd ."
			+ "?enzyme <http://bio2rdf.org/ns/kegg#xSubstrate> ?cpd ."
			+ "?enzyme <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ns/kegg#Enzyme> ."
			+ "?reaction <http://bio2rdf.org/ns/kegg#xEnzyme> ?enzyme ."
			+ "?reaction <http://bio2rdf.org/ns/kegg#equation> ?equation ."
			+ "}";
	public static final String COMPLEX_QUERY_2 = "SELECT DISTINCT ?drug ?drug1 ?drug2 ?drug3 ?drug4  WHERE {"
			+ "?drug1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/antibiotics> ."
			+ "?drug2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/antiviralAgents> ."
			+ "?drug3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/antihypertensiveAgents> ."
			+ "?drug4 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/anti-bacterialAgents> ."
			+ "?I1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?drug1 ."
			+ "?I1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?drug ."
			+ "?I2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?drug2 ."
			+ "?I2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?drug ."
			+ "?I3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?drug3 ."
			+ "?I3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?drug ."
			+ "?I4 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?drug4 ."
			+ "?I4 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?drug ."
			+ "}";

	public static final String COMPLEX_QUERY_3 = "SELECT DISTINCT ?drug ?enzyme ?reaction  WHERE {"
			+ "?drug1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/possibleDiseaseTarget> <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/302> ."
			+ "?drug2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/possibleDiseaseTarget> <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/53> ."
			+ "?drug3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/possibleDiseaseTarget> <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/59> ."
			+ "?drug4 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/possibleDiseaseTarget> <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/105> ."
			+ "?drug1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/possibleDiseaseTarget> ?d ."
			+ "?drug2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/possibleDiseaseTarget> ?d ."
			+ "?drug3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/possibleDiseaseTarget> ?d ."
			+ "?drug4 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/possibleDiseaseTarget> ?d ."
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/possibleDiseaseTarget> ?d ."
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/casRegistryNumber> ?id ."
			+ "?keggDrug <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ns/kegg#Drug> ."
			+ "?keggDrug <http://bio2rdf.org/ns/bio2rdf#xRef> ?id ."
			+ "?keggDrug <http://purl.org/dc/elements/1.1/title> ?title ."
			+ "}";
	public static final String COMPLEX_QUERY_4 = "SELECT DISTINCT ?d ?drug5 ?cpd ?enzyme ?equation "
			+ "WHERE {"
			+ "?drug1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/possibleDiseaseTarget> <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/261> ."
			+ "?drug1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target> ?o."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/genbankIdGene> ?g."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/locus> ?l."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/molecularWeight> ?mw."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/hprdId> ?hp."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/swissprotName> ?sn."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/proteinSequence> ?ps."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/generalReference> ?gr."
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target>?o."
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/possibleDiseaseTarget> ?d1 . "
			+ "?drug <http://www.w3.org/2002/07/owl#sameAs> ?drug5 ."
			+ "?drug5 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Drug> ."
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/keggCompoundId> ?cpd ."
			+ "?enzyme <http://bio2rdf.org/ns/kegg#xSubstrate> ?cpd ."
			+ "?enzyme <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ns/kegg#Enzyme> ."
			+ "?reaction <http://bio2rdf.org/ns/kegg#xEnzyme> ?enzyme ."
			+ "?reaction <http://bio2rdf.org/ns/kegg#equation> ?equation ."
			+ "}";

	public static final String COMPLEX_QUERY_5 = "SELECT DISTINCT ?drug5 ?drug6"
			+ "WHERE {"
			+ "?drug1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/possibleDiseaseTarget>  <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/319> ."
			+ "?drug1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/possibleDiseaseTarget>  <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/270> ."
			+ "?I1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?drug1 ."
			+ "?I1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?drug ."
			+ "?drug1 <http://www.w3.org/2002/07/owl#sameAs> ?drug5 ."
			+ "?drug <http://www.w3.org/2002/07/owl#sameAs> ?drug6 ." + "}";

	public static final String COMPLEX_QUERY_6 = "SELECT DISTINCT ?drug "
			+ "WHERE "
			+ "{"
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/micronutrient> ."
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/dietarySupplement> ."
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/non-essentialAminoAcids> ."
			+ "OPTIONAL"
			+ "{?drug<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/indication>?i ."
			+ "?drug<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/biotransformation>?b ."
			+ "?drug<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/inchiKey>?k ."
			+ "?drug<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/synonym>?s ."
			+ "?drug<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/toxicity>?t .}."
			+ "OPTIONAL { ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/keggCompoundId>?cpd."
			+ "?enzyme <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ns/kegg#Enzyme>."
			+ "?enzyme <http://bio2rdf.org/ns/kegg#xSubstrate> ?cpd."
			+ "?reaction <http://bio2rdf.org/ns/kegg#xEnzyme>?enzyme."
			+ "?reaction <http://bio2rdf.org/ns/kegg#equation>?equation ."
			+ "} ."
			+ "OPTIONAL { "
			+ "?drug5 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Drug> ."
			+ "?drug <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Drug> "
			+ "}" + "}";
	public static final String COMPLEX_QUERY_7 = "SELECT DISTINCT ?d ?drug5 ?cpd ?enzyme ?equation "
			+ "WHERE {"
			+ "?drug1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/possibleDiseaseTarget> <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/261> ."
			+ "?drug1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target> ?o."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/genbankIdGene> ?g."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/locus> ?l."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/molecularWeight> ?mw."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/hprdId> ?hp."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/swissprotName> ?sn."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/proteinSequence> ?ps."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/generalReference> ?gr."
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target>?o."
			+ "OPTIONAL { ?drug <http://www.w3.org/2002/07/owl#sameAs> ?drug5 ."
			+ "?drug5 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Drug> ."
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/keggCompoundId> ?cpd ."
			+ "?enzyme <http://bio2rdf.org/ns/kegg#xSubstrate> ?cpd ."
			+ "?enzyme <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ns/kegg#Enzyme> ."
			+ "?reaction <http://bio2rdf.org/ns/kegg#xEnzyme> ?enzyme ."
			+ "?reaction <http://bio2rdf.org/ns/kegg#equation> ?equation ."
			+ "}" + "}";
	public static final String COMPLEX_QUERY_8 = "SELECT DISTINCT ?drug1 "
			+ "WHERE {"
			+ "?drug1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/possibleDiseaseTarget> <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/673> ."
			+ "?drug1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target> ?o."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/genbankIdGene> ?g."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/locus> ?l."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/molecularWeight> ?mw."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/hprdId> ?hp."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/swissprotName> ?sn."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/proteinSequence> ?ps."
			+ "?o <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/generalReference> ?gr."
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target>?o."
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/synonym>?o1 ."
			+ "OPTIONAL { ?drug <http://www.w3.org/2002/07/owl#sameAs> ?drug5 ."
			+ "?drug5 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Drug> ."
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/keggCompoundId> ?cpd ."
			+ "?enzyme <http://bio2rdf.org/ns/kegg#xSubstrate> ?cpd ."
			+ "?enzyme <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ns/kegg#Enzyme> ."
			+ "?reaction <http://bio2rdf.org/ns/kegg#xEnzyme> ?enzyme ."
			+ "?reaction <http://bio2rdf.org/ns/kegg#equation> ?equation ."
			+ "}" + "}";
	public static final String COMPLEX_QUERY_9 = "SELECT DISTINCT ?drug ?drug1 ?drug2 ?drug3 ?drug4  ?d1 WHERE {"
			+ "?drug1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/antibiotics> ."
			+ "?drug2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/antiviralAgents> ."
			+ "?drug3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/antihypertensiveAgents> ."
			+ "?drug4 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/anti-bacterialAgents> ."
			+ "?drug1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target> ?o1."
			+ "?o1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/genbankIdGene> ?g1."
			+ "?o1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/locus> ?l1."
			+ "?o1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/molecularWeight> ?mw1."
			+ "?o1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/hprdId> ?hp1."
			+ "?o1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/swissprotName> ?sn1."
			+ "?o1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/proteinSequence> ?ps1."
			+ "?o1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/generalReference> ?gr1."
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target>?o1."
			+ "?drug2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target> ?o2."
			+ "?o1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/genbankIdGene> ?g2."
			+ "?o2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/locus> ?l2."
			+ "?o2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/molecularWeight> ?mw2."
			+ "?o2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/hprdId> ?hp2."
			+ "?o2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/swissprotName> ?sn2."
			+ "?o2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/proteinSequence> ?ps2."
			+ "?o2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/generalReference> ?gr2."
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target>?o2."
			+ "?drug3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target> ?o3."
			+ "?o3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/genbankIdGene> ?g3."
			+ "?o3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/locus> ?l3."
			+ "?o3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/molecularWeight> ?mw3."
			+ "?o3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/hprdId> ?hp3."
			+ "?o3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/swissprotName> ?sn3."
			+ "?o3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/proteinSequence> ?ps3."
			+ "?o3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/generalReference> ?gr3."
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target>?o3."
			+ "?drug4 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target> ?o4."
			+ "?o4 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/genbankIdGene> ?g4."
			+ "?o4 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/locus> ?l4."
			+ "?o4 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/molecularWeight> ?mw4."
			+ "?o4 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/hprdId> ?hp4."
			+ "?o4 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/swissprotName> ?sn4."
			+ "?o4 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/proteinSequence> ?ps4."
			+ "?o4 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/generalReference> ?gr4."
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target>?o4."
			+ "OPTIONAL{   "
			+ "?I1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?drug1 ."
			+ "   ?I1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?drug ."
			+ "   ?I2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?drug2 ."
			+ "   ?I2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?drug ."
			+ "   ?I3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?drug3 ."
			+ "   ?I3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?drug ."
			+ "   ?I4 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?drug4 ."
			+ "   ?I4 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?drug .}"
			+ "}";
	public static final String COMPLEX_QUERY_10 = "SELECT ?title ?actor ?news ?director ?film ?n ?genre WHERE {"
			+ "?film  <http://purl.org/dc/terms/title> 'Tarzan' ."
			+ "?film <http://data.linkedmdb.org/resource/movie/actor> ?actor ."
			+ "?film <http://data.linkedmdb.org/resource/movie/production_company> <http://data.linkedmdb.org/resource/production_company/15> ."
			+ "?actor <http://www.w3.org/2002/07/owl#sameAs>  ?x ."
			+ "OPTIONAL{"
			+ "?x <http://dbpedia.org/ontology/director>  ?director ."
			+ "?director <http://dbpedia.org/ontology/nationality> ?n ."
			+ "?film <http://data.linkedmdb.org/resource/movie/genre> ?genre . "
			+ "OPTIONAL { ?y <http://www.w3.org/2002/07/owl#sameAs> ?x.} ."
			+ "?y <http://data.nytimes.com/elements/topicPage> ?news} ." + "}";

	public static final String SUBJECT_SHARING_RULE_EXAMPLE_QUERY = "SELECT * WHERE {?s <http://dbpedia.org/property/location> \"İzmir\"@en. "
			+ "?s <http://purl.org/dc/terms/subject> ?o.}";

	public static final String OBJECT_SHARING_RULE_EXAMPLE_QUERY = "SELECT * WHERE "
			+ "{<http://data.linkedmdb.org/resource/film/17342>  <http://data.linkedmdb.org/resource/movie/producer> ?producer. "
			+ "?film <http://www.w3.org/2002/07/owl#sameAs> ?producer.}";

	public static final String CHAINING_RULE_EXAMPLE_QUERY = "SELECT * WHERE {?s <http://www.w3.org/2002/07/owl#sameAs> ?film. ?film <http://data.linkedmdb.org/resource/movie/producer_name> \"Sergio Leone\".}";

	public static final String IRI_LINKS_TO_RULE_EXAMPLE_QUERY = "SELECT * WHERE {<http://dbpedia.org/resource/Ennio_Morricone> <http://www.w3.org/2002/07/owl#sameAs> ?person}";

	public static final String LINKING_TO_URI_RULE_EXAMPLE_QUERY = "SELECT * WHERE {?s <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/A_Fistful_of_Dollars>}";

	public static final String RDF_TYPE_MATCH_RULE_EXAMPLE_QUERY = "SELECT * WHERE {?producers <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/class/yago/GermanFilmDirectors>}";

	public static final String VOCABULARY_MATCH_RULE_EXAMPLE_QUERY = "SELECT * WHERE {?s <http://dbpedia.org/property/name> \"Nikola Tesla\"@en}";

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cnrs.opentheso.ws;

import com.zaxxer.hikari.HikariDataSource;
import fr.cnrs.opentheso.bdd.helper.AlignmentHelper;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.JsonArray;
import fr.cnrs.opentheso.bdd.helper.ConceptHelper;
import fr.cnrs.opentheso.bdd.helper.GroupHelper;
import fr.cnrs.opentheso.bdd.helper.PathHelper;
import fr.cnrs.opentheso.bdd.helper.PreferencesHelper;
import fr.cnrs.opentheso.bdd.helper.SearchHelper;
import fr.cnrs.opentheso.bdd.helper.nodes.NodeAlignment;
import fr.cnrs.opentheso.bdd.helper.nodes.NodeAutoCompletion;
import fr.cnrs.opentheso.bdd.helper.nodes.NodeEM;
import fr.cnrs.opentheso.bdd.helper.nodes.NodeIdValue;
import fr.cnrs.opentheso.bdd.helper.nodes.NodePreference;
import fr.cnrs.opentheso.bdd.helper.nodes.NodeRT;
import fr.cnrs.opentheso.bdd.helper.nodes.Path;
import fr.cnrs.opentheso.bdd.helper.nodes.concept.NodeConcept;
import fr.cnrs.opentheso.bdd.helper.nodes.concept.NodeConceptTree;
import fr.cnrs.opentheso.bdd.helper.nodes.notes.NodeNote;
import fr.cnrs.opentheso.bdd.helper.nodes.term.NodeTermTraduction;
import fr.cnrs.opentheso.core.exports.rdf4j.WriteRdf4j;
import fr.cnrs.opentheso.core.exports.rdf4j.ExportRdf4jHelper;
import fr.cnrs.opentheso.core.json.helper.JsonHelper;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

/**
 *
 * @author miled.rousset
 */
public class RestRDFHelper {
    
    public String getAllLinkedConceptsWithOntome__(HikariDataSource ds, String idTheso) {
        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if(nodePreference == null) return null;

        AlignmentHelper alignmentHelper = new AlignmentHelper();
        ArrayList<NodeIdValue> listLinkedConceptsWithOntome = alignmentHelper.getAllLinkedConceptsWithOntome(ds, idTheso);

        String datasJson;

        JsonArrayBuilder jsonArrayBuilderLang = Json.createArrayBuilder();

        for (NodeIdValue nodeIdValue : listLinkedConceptsWithOntome) {
            JsonObjectBuilder jobLang = Json.createObjectBuilder();
            jobLang.add("uri", getUri(ds, nodePreference, nodeIdValue.getId(), idTheso));
            jobLang.add("class", nodeIdValue.getValue());             
            jsonArrayBuilderLang.add(jobLang.build());
        }
        datasJson = jsonArrayBuilderLang.build().toString();

        if (datasJson != null) {
            return datasJson;
        } else {
            return null;
        }
    }    
   
    public String getLinkedConceptWithOntome__(HikariDataSource ds, String idTheso, String cidocClass) {
        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if(nodePreference == null) return null;
        
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        ArrayList<NodeIdValue> listLinkedConceptsWithOntome = alignmentHelper.getLinkedConceptsWithOntome(ds, idTheso, cidocClass);

        String datasJson;

        JsonArrayBuilder jsonArrayBuilderLang = Json.createArrayBuilder();

        for (NodeIdValue nodeIdValue : listLinkedConceptsWithOntome) {
            JsonObjectBuilder jobLang = Json.createObjectBuilder();
            jobLang.add("uri", getUri(ds, nodePreference, nodeIdValue.getId(), idTheso));
            jobLang.add("class", nodeIdValue.getValue());             
            jsonArrayBuilderLang.add(jobLang.build());
        }
        datasJson = jsonArrayBuilderLang.build().toString();

        if (datasJson != null) {
            return datasJson;
        } else {
            return null;
        }
    }    
    
    public String getInfosOfConcept(HikariDataSource ds,
            String idTheso,
            String idConcept,
            String idLang){
        if(idTheso == null || idConcept == null || idLang == null) {
            return null;
        }        
        String datas = getInfosOfConcept__(ds,
                 idTheso, idConcept, idLang);
        if(datas == null) return null;
        return datas;        
    }    
    
    /**
     * @param ds
     * @param idTheso
     * @param lang
     * @return 
     */
    private String getInfosOfConcept__(
            HikariDataSource ds,
            String idTheso,
            String idConcept,
            String idLang) {
        ConceptHelper conceptHelper = new ConceptHelper();
        NodeConcept nodeConcept = conceptHelper.getConcept(ds, idConcept, idTheso, idLang);
        if(nodeConcept == null) return null;
        
        JsonObjectBuilder job = Json.createObjectBuilder();

        /// Id
        job.add("id", nodeConcept.getConcept().getIdConcept());
        if(nodeConcept.getConcept().getIdArk() != null && !nodeConcept.getConcept().getIdArk().isEmpty()) {
            job.add("ark", nodeConcept.getConcept().getIdArk()); 
        }
        if(nodeConcept.getConcept().getIdHandle() != null && !nodeConcept.getConcept().getIdHandle().isEmpty()) {
            job.add("handle", nodeConcept.getConcept().getIdHandle()); 
        }        
          
        // label
        job.add("label", nodeConcept.getTerm().getLexical_value());
        
        // synonymes
        JsonArrayBuilder jsonArrayBuilderSyno = Json.createArrayBuilder();         
        for (NodeEM nodeEM : nodeConcept.getNodeEM()) {
            jsonArrayBuilderSyno.add(nodeEM.getLexical_value());
        }
        if(jsonArrayBuilderSyno != null)
            job.add("altLabel", jsonArrayBuilderSyno.build());

        // Associés 
        JsonArrayBuilder jsonArrayBuilderRelate = Json.createArrayBuilder();         
        String labelRT;
        for (NodeRT nodeRT : nodeConcept.getNodeRT()) {
            labelRT = conceptHelper.getLexicalValueOfConcept(ds, nodeRT.getIdConcept(), idTheso, idLang);
            if(labelRT != null && !labelRT.isEmpty())
                jsonArrayBuilderRelate.add(labelRT);
        }
        if(jsonArrayBuilderRelate != null)
            job.add("related", jsonArrayBuilderRelate.build());
        
        
        // traductions 
        JsonArrayBuilder jsonArrayBuilderTrad = Json.createArrayBuilder();         
        for (NodeTermTraduction nodeTermTraduction : nodeConcept.getNodeTermTraductions()) {
            JsonObjectBuilder jobTrad = Json.createObjectBuilder();
            jobTrad.add("lang", nodeTermTraduction.getLang());
            jobTrad.add("label", nodeTermTraduction.getLexicalValue());
         
            jsonArrayBuilderTrad.add(jobTrad.build());            
        }
        if(jsonArrayBuilderTrad != null)
            job.add("traduction", jsonArrayBuilderTrad.build());
        
        // Alignements 
        JsonArrayBuilder jsonArrayBuilderAlign = Json.createArrayBuilder();         
        for (NodeAlignment nodeAlignment : nodeConcept.getNodeAlignments()) {
            JsonObjectBuilder jobAlign = Json.createObjectBuilder();
            jobAlign.add("type", nodeAlignment.getAlignmentLabelType());
            jobAlign.add("uri", nodeAlignment.getUri_target());
         
            jsonArrayBuilderAlign.add(jobAlign.build());            
        }
        if(jsonArrayBuilderAlign != null)
            job.add("Alignment", jsonArrayBuilderAlign.build());         
        
        
        // définitions 
        JsonArrayBuilder jsonArrayBuilderDef = Json.createArrayBuilder();         
        for (NodeNote nodeNote : nodeConcept.getNodeNotesTerm()) {
            if("definition".equalsIgnoreCase(nodeNote.getNotetypecode())) {
                jsonArrayBuilderDef.add(nodeNote.getLexicalvalue());
            }
        }
        if(jsonArrayBuilderDef != null)
            job.add("definition", jsonArrayBuilderDef.build());




        if(job != null)
            return job.build().toString();
        else 
            return null;        
    }       
    
    
    /**
     * Permet de récupérer la liste des topTerms d'un thésaurus
     * @param ds
     * @param idTheso
     * @param idConcept
     * @param idLang
     * @return 
     */
    public String getNarrower(HikariDataSource ds,
            String idTheso,
            String idConcept,
            String idLang) {
        if(idTheso == null || idConcept == null || idLang == null) {
            return null;
        }        
        String datas = getNarrower__(ds,
                 idTheso, idConcept, idLang);
        if(datas == null) return null;
        return datas;
    }
    /**
     * recherche par valeur
     * @param ds
     * @param idTheso
     * @param lang
     * @return 
     */
    private String getNarrower__(
            HikariDataSource ds,
            String idTheso,
            String idConcept,
            String idLang) {

        ConceptHelper conceptHelper = new ConceptHelper();
        
        ArrayList<NodeConceptTree> nodeConceptTrees = conceptHelper.getListConcepts(
                ds,
                idConcept,
                idTheso,
                idLang,
                false);

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();        
        
        for (NodeConceptTree nodeConceptTree : nodeConceptTrees) {
            JsonObjectBuilder job = Json.createObjectBuilder();
            job.add("id", nodeConceptTree.getIdConcept());
            job.add("label", nodeConceptTree.getTitle());
            job.add("haveChildren", nodeConceptTree.isHaveChildren());            
            jsonArrayBuilder.add(job.build());
        }
        if(jsonArrayBuilder != null)
            return jsonArrayBuilder.build().toString();
        else 
            return null;        
    }         
        
    
    /**
     * Permet de récupérer la liste des topTerms d'un thésaurus
     * @param ds
     * @param idTheso
     * @param idLang
     * @return 
     */
    public String getTopTerms(HikariDataSource ds,
            String idTheso,
            String idLang) {
        if(idTheso == null || idLang == null) {
            return null;
        }        
        String datas = getTopTerms__(ds,
                 idTheso, idLang);
        if(datas == null) return null;
        return datas;
    }
    /**
     * recherche par valeur
     * @param ds
     * @param idTheso
     * @param lang
     * @return 
     */
    private String getTopTerms__(
            HikariDataSource ds,
            String idTheso,
            String idLang) {

        ConceptHelper conceptHelper = new ConceptHelper();
        
        ArrayList<NodeConceptTree> nodeConceptTrees = conceptHelper.getListOfTopConcepts(ds,
                        idTheso, idLang, false);
        if(nodeConceptTrees == null) return null;

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();        
        
        for (NodeConceptTree nodeConceptTree : nodeConceptTrees) {
            JsonObjectBuilder job = Json.createObjectBuilder();
            job.add("id", nodeConceptTree.getIdConcept());
            job.add("label", nodeConceptTree.getTitle());
            job.add("haveChildren", nodeConceptTree.isHaveChildren());            
            jsonArrayBuilder.add(job.build());
        }
        if(jsonArrayBuilder != null)
            return jsonArrayBuilder.build().toString();
        else 
            return null;        
        
    }      
    
    
    
    /**
     * Permet de retourner le prefLabel d'après un idArk avec la langue donnée
     * le résultat est en Json
     * 
     * @param ds 
     * @param naan 
     * @param idArk 
     * @param idLang 
     * @return  
     */
    public String getPrefLabelFromArk(HikariDataSource ds,
            String naan,
            String idArk,
            String idLang) {

        String datas = getPrefLabelFromArk__(ds,
                 naan, idArk, idLang);
        if(datas == null) return null;
        return datas;
    } 
    
    /**
     * recherche par valeur
     * @param ds
     * @param value
     * @param idTheso
     * @param lang
     * @return 
     */
    private String getPrefLabelFromArk__(
            HikariDataSource ds,
            String naan,
            String idArk,
            String idLang) {

        if(idArk == null || idLang == null) {
            return null;
        }
        ConceptHelper conceptHelper = new ConceptHelper();
        String idTheso = conceptHelper.getIdThesaurusFromArkId(ds,  naan + "/" + idArk);
        String idConcept = conceptHelper.getIdConceptFromArkId(ds, naan + "/" + idArk);
        
        if(idTheso == null || idConcept == null) return null;
        
        String value = conceptHelper.getLexicalValueOfConcept(ds, idConcept, idTheso, idLang);
        
        
        if(value == null || value.isEmpty())
            return null;

        JsonObject datasJson = Json.createObjectBuilder().add("prefLabel", value).build();

    /*    JsonArrayBuilder jab = Json.createArrayBuilder();        
        jab.add(builder);        
        
        JsonArray datasJson = jab.build();*/

        if(datasJson != null)
            return datasJson.toString();
        else 
            return null;
    }        
    
    /**
     * Permet de retourner un concept au format défini en passant par l'identifiant du concept
     * utilisé pour la négociation de contenu
     * 
     * @param ds 
     * @param idConcept 
     * @param idTheso 
     * @param format 
     * @return  
     */
    public String exportConceptFromId(HikariDataSource ds, 
            String idConcept, String idTheso, String format) {

        RDFFormat rDFFormat = getRDFFormat(format);
        WriteRdf4j writeRdf4j = getConceptFromId(ds, idConcept, idTheso);
        if(writeRdf4j == null) return null;

        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, rDFFormat);
        return out.toString();
    }
    
    private WriteRdf4j getConceptFromId(HikariDataSource ds,
            String idConcept, String idTheso) {
        if(idConcept == null || idTheso == null) {
            return null;
        }
        if(idConcept.isEmpty() || idTheso.isEmpty()) {
            return null;
        }
        idConcept = idConcept.replaceAll("\"", "");
        idTheso = idTheso.replaceAll("\"", "");
        idConcept = idConcept.replaceAll("'", "");
        idTheso = idTheso.replaceAll("'", "");        
        
        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if(nodePreference == null) return null;
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.setInfos(ds, "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());

        exportRdf4jHelper.addSignleConcept(idTheso, idConcept);
        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());
        return writeRdf4j;
    }

    /**
     * Permet de retourner un concept au format défini en passant par un identifiant Ark
     * utilisé pour la négociation de contenu
     * 
     * @param ds
     * @param idArk 
     * @param format 
     * @return  
     */
    public String exportConcept(HikariDataSource ds, 
            String idArk, String format) {

        RDFFormat rDFFormat = getRDFFormat(format);
        WriteRdf4j writeRdf4j = getConceptFromArk(ds, idArk);
        if(writeRdf4j == null) return null;

        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, rDFFormat);
        return out.toString();
    }
    
    private WriteRdf4j getConceptFromArk(HikariDataSource ds,
            String idArk) {
        
        ConceptHelper conceptHelper = new ConceptHelper();
        String idConcept = conceptHelper.getIdConceptFromArkId(ds, idArk);
        String idTheso = conceptHelper.getIdThesaurusFromArkId(ds, idArk);
        
        if(idConcept == null || idTheso == null) {
            return null;
        }
        
        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if(nodePreference == null) return null;
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.setInfos(ds, "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());

        exportRdf4jHelper.addSignleConcept(idTheso, idConcept);
        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());
        return writeRdf4j;
    }   
    
    
    /**
     * Permet de retourner un concept au format défini en passant par un identifiant Ark
     * et id thesaurus et filtré par langue et pour récupérer les labels des relations BT et NT
     * 
     * @param ds
     * @param idArk 
     * @param idTheso 
     * @param showLabels 
     * @param idLang 
     * @param format 
     * @return  
     */
    public String exportConceptFromArkWithLang(HikariDataSource ds, 
            String idArk, String idTheso, String idLang, boolean showLabels,
            String format) {

        RDFFormat rDFFormat = getRDFFormat(format);
        WriteRdf4j writeRdf4j = exportConceptFromArkWithLang__(
                ds, idArk, idTheso, idLang, showLabels);
        if(writeRdf4j == null) return null;

        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, rDFFormat);
        return out.toString();
    }
    
    private WriteRdf4j exportConceptFromArkWithLang__(HikariDataSource ds, 
            String idArk, String idTheso, String idLang, boolean showLabels) {
        
        ConceptHelper conceptHelper = new ConceptHelper();
        String idConcept = conceptHelper.getIdConceptFromArkId(ds, idArk);
        if(idTheso == null || idTheso.isEmpty())
            idTheso = conceptHelper.getIdThesaurusFromArkId(ds, idArk);
        
        if(idConcept == null || idTheso == null) {
            return null;
        }
        
        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if(nodePreference == null) return null;
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.setInfos(ds, "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());

        exportRdf4jHelper.addSignleConceptByLang(idTheso, idConcept, idLang, showLabels);
        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());
        return writeRdf4j;
    }     
    
    
    /**
     * Permet de retourner un concept au format défini en passant par un identifiant DOI
     * utilisé pour la négociation de contenu
     * 
     * @param ds
     * @param doi 
     * @param format 
     * @return  
     */
    public String exportConceptDoi(HikariDataSource ds, 
            String doi, String format) {

        RDFFormat rDFFormat = getRDFFormat(format);
        WriteRdf4j writeRdf4j = getConceptFromDoi(ds, doi);
        if(writeRdf4j == null) return null;

        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, rDFFormat);
        return out.toString();
    }            
    
    private WriteRdf4j getConceptFromDoi(HikariDataSource ds,
            String doi) {
        
        ConceptHelper conceptHelper = new ConceptHelper();
        String idConcept = doi;//conceptHelper.getIdConcept FromHandleId(ds, doi);
        String idTheso = conceptHelper.getIdThesaurusFromIdConcept(ds, idConcept);
        
        if(doi == null || idTheso == null) {
            return null;
        }
        
        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if(nodePreference == null) return null;
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.setInfos(ds, "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());

        exportRdf4jHelper.addSignleConcept(idTheso, idConcept);
        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());
        return writeRdf4j;
    }    
    
    /**
     * Permet de retourner un concept au format défini en passant par un identifiant Handle
     * utilisé pour la négociation de contenu
     * 
     * @param ds
     * @param handleId 
     * @param format 
     * @return  
     */
    public String exportConceptHdl(HikariDataSource ds, 
            String handleId, String format) {

        RDFFormat rDFFormat = getRDFFormat(format);
        WriteRdf4j writeRdf4j = getConceptFromHandle(ds, handleId);
        if(writeRdf4j == null) return null;

        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, rDFFormat);
        return out.toString();
    }            
    
    private WriteRdf4j getConceptFromHandle(HikariDataSource ds,
            String handleId) {
        
        ConceptHelper conceptHelper = new ConceptHelper();
        String idConcept = conceptHelper.getIdConceptFromHandleId(ds, handleId);
        String idTheso = conceptHelper.getIdThesaurusFromHandleId(ds, handleId);
        
        if(idConcept == null || idTheso == null) {
            return null;
        }
        
        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if(nodePreference == null) return null;
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.setInfos(ds, "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());

        exportRdf4jHelper.addSignleConcept(idTheso, idConcept);
        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());
        return writeRdf4j;
    }
    
    /**
     * permet de retourner le format du RDF en utilisant le paramètre d'entrée 
     * @param format
     * @return 
     */
    private RDFFormat getRDFFormat(String format) {
        RDFFormat rDFFormat = RDFFormat.RDFJSON;
        switch (format) {
            case "application/rdf+xml":
                rDFFormat = RDFFormat.RDFXML;
                break;
            case "application/ld+json":
                rDFFormat = RDFFormat.JSONLD;
                break;
            case "text/turtle":
                rDFFormat = RDFFormat.TURTLE;
                break;
            case "application/json":
                rDFFormat = RDFFormat.RDFJSON;
                break;                
        }
        return rDFFormat;
    }    
    
    /**
     * Permet de retourner un concept au format défini en passant par un identifiant Ark
     * utilisé pour la négociation de contenu
     * 
     * @param ds
     * @param idTheso
     * @param lang
     * @param group
     * @param format 
     * @param value 
     * @return  
     */
    public String findConcepts(HikariDataSource ds,
            String idTheso, String lang, String group,
            String value, String format) {

        RDFFormat rDFFormat = getRDFFormat(format);
        WriteRdf4j writeRdf4j = findConcepts__(ds,
                 value, idTheso, lang, group);
        if(writeRdf4j == null) return null;

        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, rDFFormat);
        return out.toString();
    }
    
    /**
     * recherche par valeur
     * @param ds
     * @param value
     * @param idTheso
     * @param lang
     * @param group
     * @return 
     */
    private WriteRdf4j findConcepts__(
            HikariDataSource ds,
            String value, String idTheso, String lang, String group) {

        if(value == null || idTheso == null) {
            return null;
        }        
        NodePreference nodePreference = new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.setInfos(ds, "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());

        SearchHelper searchHelper = new SearchHelper();

        ArrayList<String> idConcepts = new ArrayList<>();
                
        ArrayList<String> idConcepts1 = searchHelper.searchExactTermNew(ds, value, lang, idTheso, group);
        
        if(idConcepts1 != null) {
            idConcepts.addAll(idConcepts1);
        }
        
        ArrayList<String> idConcepts2 = searchHelper.searchTermNew(ds, value, lang, idTheso, group);
        if(idConcepts2 != null) {
            idConcepts.addAll(idConcepts2);
        }
        
        // pour enlever les doublons.
        List<String> deDupStringList = idConcepts.stream().distinct().collect(Collectors.toList());
        
        for (String idConcept : deDupStringList) {
            exportRdf4jHelper.addSignleConcept(idTheso, idConcept);
        }
        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());
        return writeRdf4j;   
    }
    
    /**
     * Permet de retourner les concepts qui correspondent à la notation 
     * utilisé pour la négociation de contenu
     * 
     * @param ds
     * @param idTheso
     * @param format 
     * @param value 
     * @return  
     */
    public String findNotation(HikariDataSource ds,
            String idTheso,
            String value, String format) {

        RDFFormat rDFFormat = getRDFFormat(format);
        WriteRdf4j writeRdf4j = findNotation__(ds,
                 value, idTheso);
        if(writeRdf4j == null) return null;

        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, rDFFormat);
        return out.toString();
    }
    
    /**
     * recherche par Notation
     * @param ds
     * @param value
     * @param idTheso
     * @param lang
     * @return 
     */
    private WriteRdf4j findNotation__(
            HikariDataSource ds,
            String value, String idTheso) {

        if(value == null || idTheso == null) {
            return null;
        }        
        NodePreference nodePreference = new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.setInfos(ds, "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());

        SearchHelper searchHelper = new SearchHelper();

        ArrayList<String> idConcepts = searchHelper.searchNotationId(ds, value, idTheso);
        
        if(idConcepts == null || idConcepts.isEmpty()) {
            return null;
        }
        
        for (String idConcept : idConcepts) {
            exportRdf4jHelper.addSignleConcept(idTheso, idConcept);
        }
        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());
        return writeRdf4j;   
    }    
    
    /**
     * Permet de retourner les concepts au format Json
     * avec valeur et URI (pour les programmes qui utilisent l'autocomplétion)
     * 
     * @param ds
     * @param idTheso
     * @param lang 
     * @param group 
     * @param value 
     * @param withNotes 
     * @return  
     */
    public String findAutocompleteConcepts(HikariDataSource ds,
            String idTheso, String lang, String group,
            String value, boolean withNotes) {

        String datas = findAutocompleteConcepts__(ds,
                 value, idTheso, lang, group, withNotes);
        if(datas == null) return null;
        return datas;
    }    
    
    
    /**
     * recherche par valeur
     * @param ds
     * @param value
     * @param idTheso
     * @param lang
     * @return 
     */
    private String findAutocompleteConcepts__(
            HikariDataSource ds,
            String value, String idTheso,
            String lang, String group, boolean withNotes) {

        if(value == null || idTheso == null) {
            return null;
        }        
        NodePreference nodePreference = new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }

        SearchHelper searchHelper = new SearchHelper();
        JsonHelper jsonHelper = new JsonHelper();
        String uri;
        ArrayList<NodeAutoCompletion> nodeAutoCompletion;
        
        
        // recherche de toutes les valeurs
        nodeAutoCompletion = searchHelper.searchAutoCompletionWS(ds, value, lang, group, idTheso, withNotes);
        
        if(nodeAutoCompletion == null || nodeAutoCompletion.isEmpty())
            return null;

        for (NodeAutoCompletion nodeAutoCompletion1 : nodeAutoCompletion) {
            uri = getUri(nodePreference, nodeAutoCompletion1, idTheso);
            if(withNotes)
                jsonHelper.addJsonDataFull(uri, nodeAutoCompletion1.getPrefLabel(), 
                        nodeAutoCompletion1.getDefinition(), nodeAutoCompletion1.isIsAltLabel());
            else
            jsonHelper.addJsonData(uri, nodeAutoCompletion1.getPrefLabel());
        }
        JsonArray datasJson = jsonHelper.getBuilder();
        if(datasJson != null)
            return datasJson.toString();
        else 
            return null;
    }
    
    /**
     * Permet de retourner les concepts au format Json
     * avec valeur et URI (pour les programmes qui utilisent l'autocomplétion)
     * mais aussi la branche complète vers la racine
     * 
     * @param ds
     * @param idTheso
     * @param lang 
     * @param group 
     * @param value 
     * @return  
     */
    public String findDatasForWidget(HikariDataSource ds,
            String idTheso, String lang, String group,
            String value) {

        String datas = findDatasForWidget__(ds,
                 value, idTheso, lang, group);
        if(datas == null) return null;
        return datas;
    }    
    
    
    /**
     * recherche par valeur
     * @param ds
     * @param value
     * @param idTheso
     * @param lang
     * @return 
     */
    private String findDatasForWidget__(
            HikariDataSource ds,
            String value, String idTheso,
            String lang, String group) {

        if(value == null || idTheso == null) {
            return null;
        }        
        NodePreference nodePreference = new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }

        SearchHelper searchHelper = new SearchHelper();

        
        // recherche de toutes les valeurs
        ArrayList<String> nodeIds = searchHelper.searchAutoCompletionWSForWidget(ds, value, lang, group, idTheso);
        
        if(nodeIds == null || nodeIds.isEmpty())
            return null;

        // construire le tableau JSON avec le chemin vers la racine pour chaque Id
        PathHelper pathHelper = new PathHelper();
        ArrayList<Path> paths;
        
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();        
        
        for (String idConcept : nodeIds) {
            paths = pathHelper.getPathOfConcept(ds, idConcept, idTheso);
            if(paths != null && !paths.isEmpty()){
                pathHelper.getPathWithLabelAsJson(ds, 
                        paths, 
                        jsonArrayBuilder,
                        idTheso, lang, idConcept);
            }
        }
    //    datasJson = jsonArrayBuilder.build().toString();
        if(jsonArrayBuilder != null)
            return jsonArrayBuilder.build().toString();
        else 
            return null;
    }    
    
    /**
     * Fonction qui permet de récupérer une branche complète en partant d'un
     * concept et en allant jusqu'à la racine (vers le haut)
     *
     * @param ds
     * @param idConcept
     * @param idTheso
     * @param format
     * @return skos
     */
    public String brancheOfConceptsTop(HikariDataSource ds,
            String idConcept, String idTheso, String format) {

        RDFFormat rDFFormat = getRDFFormat(format);
        WriteRdf4j writeRdf4j = brancheOfConceptsTop__(ds,
                 idConcept, idTheso);
        if(writeRdf4j == null) return null;

        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, rDFFormat);
        return out.toString();
    }    
    
    /**
     * Fonction qui permet de récupérer une branche complète en partant d'un
     * concept et en allant jusqu'à la racine (vers le haut)
     *
     * @param ds
     * @param idConcept
     * @param idTheso
     * @return skos
     */
    private WriteRdf4j brancheOfConceptsTop__(
            HikariDataSource ds,
            String idConcept, String idTheso) {

        if(idConcept == null || idTheso == null) {
            return null;
        }
        NodePreference nodePreference = new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.setInfos(ds, "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());

        ArrayList<String> path = new ArrayList<>();
        ArrayList<ArrayList<String>> branchs = new ArrayList<>();

        ConceptHelper conceptHelper = new ConceptHelper();
        path.add(idConcept);
        branchs = conceptHelper.getPathOfConceptWithoutGroup(ds, idConcept, idTheso, path, branchs);
        for (ArrayList<String> branch : branchs) {
            for (String idc : branch) {
                exportRdf4jHelper.addSignleConcept(idTheso, idc);                
            }
        }
        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());
        return writeRdf4j;
    }    
    
    /**
     * Fonction qui permet de récupérer une branche complète en partant d'un
     * concept et en allant jusqu'à la fin (vers le bas)
     *
     * @param ds
     * @param idConcept
     * @param idTheso
     * @param format
     * @return skos
     */
    public String brancheOfConceptsDown(HikariDataSource ds,
            String idConcept, String idTheso, String format) {

        RDFFormat rDFFormat = getRDFFormat(format);
        WriteRdf4j writeRdf4j = brancheOfConceptsDown__(ds,
                 idConcept, idTheso);
        if(writeRdf4j == null) return null;

        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, rDFFormat);
        return out.toString();
    }    
    
    /**
     * Fonction qui permet de récupérer une branche complète en partant d'un
     * concept et en allant jusqu'à la fin (vers le bas)
     *
     * @param ds
     * @param idConcept
     * @param idTheso
     * @return skos
     */
    private WriteRdf4j brancheOfConceptsDown__(
            HikariDataSource ds,
            String idConcept, String idTheso) {

        if(idConcept == null || idTheso == null) {
            return null;
        }
        NodePreference nodePreference = new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.setInfos(ds, "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());

        ArrayList<String> path;// = new ArrayList<>();
    //    ArrayList<ArrayList<String>> branchs = new ArrayList<>();

        ConceptHelper conceptHelper = new ConceptHelper();
        path = conceptHelper.getIdsOfBranch(ds, idConcept, idTheso);
        
    //    path.add(idConcept);
        
        for (String idC : path) {
            exportRdf4jHelper.addSignleConcept(idTheso, idC);                
        }
        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());
        return writeRdf4j;
    }      
    
    /**
     * Fonction qui permet de récupérer toute la branche d'un groupe
     * en partant d'un identifiant d'un group/domaine
     *
     * @param ds
     * @param idGroup
     * @param idTheso
     * @param format
     * @return skos
     */
    public String brancheOfGroup(HikariDataSource ds,
            String idGroup, String idTheso, String format) {

        RDFFormat rDFFormat = getRDFFormat(format);
        WriteRdf4j writeRdf4j = brancheOfGroup__(ds,
                 idGroup, idTheso);
        if(writeRdf4j == null) return null;

        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, rDFFormat);
        return out.toString();
    }    
    
    /**
     * Fonction qui permet de récupérer une branche complète en partant d'un
     * concept et en allant jusqu'à la racine (vers le haut)
     *
     * @param ds
     * @param idConcept
     * @param idTheso
     * @return skos
     */
    private WriteRdf4j brancheOfGroup__(
            HikariDataSource ds,
            String idGroup, String idTheso) {

        if(idGroup == null || idTheso == null) {
            return null;
        }
        NodePreference nodePreference = new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.setInfos(ds, "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());



        ConceptHelper conceptHelper = new ConceptHelper();
        ArrayList<String> branchs = conceptHelper.getAllIdConceptOfThesaurusByGroup(ds, idTheso, idGroup);
        for (String idConcept : branchs) {
            exportRdf4jHelper.addSignleConcept(idTheso, idConcept);
        }

        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());
        return writeRdf4j;
    }      
    
    /**
     * Fonction qui permet de récupérer un thésaurus entier
     *
     * @param ds
     * @param idTheso
     * @param format
     * @return skos
     */
    public String getTheso(HikariDataSource ds,
            String idTheso, String format) {

        RDFFormat rDFFormat = getRDFFormat(format);
        WriteRdf4j writeRdf4j = getTheso__(ds, idTheso);
        if(writeRdf4j == null) return null;

        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, rDFFormat);
        return out.toString();
    }    
    
    /**
     * Fonction qui permet de récupérer une branche complète en partant d'un
     * concept et en allant jusqu'à la racine (vers le haut)
     *
     * @param ds
     * @param idTheso
     * @return skos
     */
    private WriteRdf4j getTheso__(
            HikariDataSource ds, String idTheso) {

        if(idTheso == null) {
            return null;
        }
        NodePreference nodePreference = new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.setInfos(ds, "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());

        ConceptHelper conceptHelper = new ConceptHelper();        
        ArrayList<String> allConcepts = conceptHelper.getAllIdConceptOfThesaurus(ds, idTheso);   
        allConcepts.forEach(idConcept -> {
            exportRdf4jHelper.addSignleConcept(idTheso, idConcept);
        });

        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());
        return writeRdf4j;
    }      
    
    /**
     * Permet de retourner un group au format défini en passant par un identifiant Ark
     * utilisé pour la négociation de contenu
     * 
     * @param ds
     * @param idTheso
     * @param idGroup
     * @param format 
     * @return  
     */
    public String exportGroup(HikariDataSource ds, 
            String idTheso, String idGroup, String format) {

        RDFFormat rDFFormat = getRDFFormat(format);
        WriteRdf4j writeRdf4j = getGroupFromId(ds, idTheso, idGroup);
        if(writeRdf4j == null) return null;

        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, rDFFormat);
        return out.toString();
    }    
    
    
    /**
     * Permet de retourner un group au format défini en passant par un identifiant Ark
     * utilisé pour la négociation de contenu
     * 
     * @param ds
     * @param idArk 
     * @param format 
     * @return  
     */
    public String exportGroup(HikariDataSource ds, 
            String idArk, String format) {

        RDFFormat rDFFormat = getRDFFormat(format);
        WriteRdf4j writeRdf4j = getGroupFromArk(ds, idArk);
        if(writeRdf4j == null) return null;

        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, rDFFormat);
        return out.toString();
    }
    
    private WriteRdf4j getGroupFromArk(HikariDataSource ds,
            String idArk) {
        
        GroupHelper groupHelper = new GroupHelper();
        String idGroup = groupHelper.getIdGroupFromArkId(ds, idArk);
        String idTheso = groupHelper.getIdThesaurusFromArkId(ds, idArk);
        
        if(idGroup == null || idTheso == null) {
            return null;
        }
        
        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if(nodePreference == null) return null;
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.setInfos(ds, "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());

        exportRdf4jHelper.addSingleGroup(idTheso, idGroup);
        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());
        return writeRdf4j;
    }    
    
    private WriteRdf4j getGroupFromId(HikariDataSource ds,
            String idTheso, String idGroup) {
        if(idGroup == null || idTheso == null) {
            return null;
        }
        
        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if(nodePreference == null) return null;
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.setInfos(ds, "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());

        exportRdf4jHelper.addSingleGroup(idTheso, idGroup);
        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());
        return writeRdf4j;
    }      
    
    /**
     * Cette fonction permet de retourner l'URI du concept avec identifiant Ark
     * ou Handle si renseignés, sinon l'URL du Site
     *
     * @param nodeConceptExport
     * @return
     */
    private String getUri(NodePreference nodePreference,
            NodeAutoCompletion nodeAutoCompletion1, String idTheso) {
        String uri = "";
        if (nodeAutoCompletion1 == null) {
            //      System.out.println("nodeConcept = Null");
            return uri;
        }
        if (nodeAutoCompletion1.getIdConcept() == null) {
            //    System.out.println("nodeConcept.getConcept = Null");
            return uri;
        }
        
        // Choix de l'URI pour l'export : 
        // Si Handle est actif, on le prend en premier 
        // sinon,  on vérifie si Ark est actif, 
        // en dernier, on prend l'URL basique d'Opentheso
        // 1 seule URI est possible pour l'export par concept
        

        // URI de type Ark
        if (nodeAutoCompletion1.getIdArk() != null) {
            if (!nodeAutoCompletion1.getIdArk().trim().isEmpty()) {
                uri = nodePreference.getServeurArk() + nodeAutoCompletion1.getIdArk();
                return uri;
            }
        }
        // URI de type Handle
        if (nodeAutoCompletion1.getIdHandle() != null) {
            if (!nodeAutoCompletion1.getIdHandle().trim().isEmpty()) {
                uri = "https://hdl.handle.net/" + nodeAutoCompletion1.getIdHandle();
                return uri;
            }
        }        
        // si on ne trouve pas ni Handle, ni Ark
        // http://localhost:8083/opentheso/?idc=66&idt=1
        uri = nodePreference.getCheminSite() + "?idc=" + nodeAutoCompletion1.getIdConcept()
                + "&idt=" + idTheso;
        return uri;
    }
    

    /**
     * Cette fonction permet de retourner l'URI du concept suivant le type d'identifiant précisé dans les préférences 
     * @param nodePreference
     * @param idConcept
     * @return 
     */
    private String getUri(HikariDataSource ds, NodePreference nodePreference, String idConcept, String idTheso) {
        String uri;

        // Choix de l'URI pour l'export : 
        // Si Handle est actif, on le prend en premier 
        // sinon,  on vérifie si Ark est actif, 
        // en dernier, on prend l'URL basique d'Opentheso
        // 1 seule URI est possible pour l'export par concept
        // URI de type Ark
        ConceptHelper conceptHelper = new ConceptHelper();
        String identifier;
        if(nodePreference.isOriginalUriIsArk()) {
            identifier = conceptHelper.getIdArkOfConcept(ds, idConcept, idTheso);
            if (identifier != null && !identifier.isEmpty()) {
                uri = nodePreference.getUriArk()+ identifier;
                return uri;
            }
        }
        
        if(nodePreference.isOriginalUriIsHandle()) {
            // URI de type Handle
            identifier = conceptHelper.getIdHandleOfConcept(ds, idConcept, idTheso);
            if (identifier != null && !identifier.isEmpty()) {
                uri = "https://hdl.handle.net/" + identifier;
                return uri;
            }
        }
/*        if(nodePreference.isOriginalUriIsDoi()) {
            // URI de type Doi
            if (nodeConceptExport.getConcept().getIdDoi() != null) {
                if (!nodeConceptExport.getConcept().getIdDoi().trim().isEmpty()) {
                    uri = "https://doi.org/" + nodeConceptExport.getConcept().getIdDoi();
                    return uri;
                }
            }
        } */
        // si on ne trouve pas ni Handle, ni Ark
        if(nodePreference.getOriginalUri() != null && !nodePreference.getOriginalUri().isEmpty()) {
            uri = nodePreference.getOriginalUri()+ "/?idc=" + idConcept
                        + "&idt=" + idTheso;
            return uri;
        }
        return idConcept;
    }    
}

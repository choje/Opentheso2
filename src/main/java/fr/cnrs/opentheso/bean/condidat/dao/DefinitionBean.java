package fr.cnrs.opentheso.bean.condidat.dao;

import fr.cnrs.opentheso.bean.condidat.CandidatBean;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.PrimeFaces;

@Named(value = "definitionBean")
@SessionScoped
public class DefinitionBean implements Serializable {

    @Inject
    private CandidatBean candidatBean;

    private String definition;
    private String oldDefinition;

    public void init() {
        this.definition = "";
    }

    public void init(String definition) {
        this.definition = definition;
        this.oldDefinition = definition;
    }

    public void editDefinition() {
        int pos = candidatBean.getCandidatSelected().getDefenitions().indexOf(oldDefinition);

        if (pos > -1) {
            candidatBean.getCandidatSelected().getDefenitions().set(pos, definition);
        }
        definition = "";
        
        candidatBean.showMessage(FacesMessage.SEVERITY_INFO, "Définition modifiée avec sucée");
            
        PrimeFaces.current().ajax().update("messageIndex");
        PrimeFaces.current().ajax().update("candidatForm");
    }
    
    public void addDefinition() {
        
        if (StringUtils.isEmpty(definition)) {
            return;
        }
        
        for (String str : candidatBean.getCandidatSelected().getDefenitions()) {
            if (str.equals(definition)) {
                definition = "";
                return;
            }
        }
        candidatBean.getCandidatSelected().getDefenitions().add(definition);
        definition = "";
        
        candidatBean.showMessage(FacesMessage.SEVERITY_INFO, "Définition ajoutée avec sucée");
            
        PrimeFaces.current().ajax().update("messageIndex");
        PrimeFaces.current().ajax().update("candidatForm");
    }
    
    public void deleteDefinition() {
        
        if (StringUtils.isEmpty(definition)) {
            return;
        }
        
        for (String str : candidatBean.getCandidatSelected().getDefenitions()) {
            if (str.equals(definition)) {
                candidatBean.getCandidatSelected().getDefenitions().remove(str);
                definition = "";
                return;
            }
        }
        definition = "";
        
        candidatBean.showMessage(FacesMessage.SEVERITY_INFO, "Définition supprimée avec sucée");
            
        PrimeFaces.current().ajax().update("messageIndex");
        PrimeFaces.current().ajax().update("candidatForm");
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getOldDefinition() {
        return oldDefinition;
    }

    public void setOldDefinition(String oldDefinition) {
        this.oldDefinition = oldDefinition;
    }

}

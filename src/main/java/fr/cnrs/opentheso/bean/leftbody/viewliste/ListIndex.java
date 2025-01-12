/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cnrs.opentheso.bean.leftbody.viewliste;

import fr.cnrs.opentheso.bdd.helper.SearchHelper;
import fr.cnrs.opentheso.bdd.helper.nodes.NodeIdValue;
import fr.cnrs.opentheso.bean.menu.connect.Connect;
import fr.cnrs.opentheso.bean.menu.theso.SelectedTheso;
import fr.cnrs.opentheso.bean.rightbody.RightBodySetting;
import fr.cnrs.opentheso.bean.rightbody.viewconcept.ConceptView;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author miledrousset
 */
@Named(value = "listIndex")
@SessionScoped
public class ListIndex implements Serializable {
    @Inject private Connect connect;
    @Inject private SelectedTheso selectedTheso;
    @Inject private RightBodySetting rightBodySetting;
    @Inject private ConceptView conceptBean;
    
    private String searchValue;
    private NodeIdValue selectedNode;
    private List<NodeIdValue> nodeIdValues;
    
    private boolean withAltLabel;
    private boolean permuted;

    @PreDestroy
    public void destroy(){
        reset();
    } 
    
    public void reset(){
        searchValue = null;
        selectedNode = null;
        if(nodeIdValues!= null){
            nodeIdValues.clear();
            nodeIdValues = null;
        }         
    }
    
    public void onChange(){
        String idTheso = selectedTheso.getCurrentIdTheso();
        String idLang = selectedTheso.getCurrentLang();
        if(idTheso == null || idTheso.isEmpty()) return;
        if(idLang == null || idLang.isEmpty()) return;
        if(searchValue == null || searchValue.isEmpty()) return;
        
        SearchHelper searchHelper = new SearchHelper();
        nodeIdValues = searchHelper.searchTermForIndex(
                connect.getPoolConnexion(),
                searchValue, 
                idLang, idTheso,
                permuted, withAltLabel);
    }

    public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage("Car Selected", ((NodeIdValue)event.getObject()).getValue());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
            rightBodySetting.setShowConceptToOn();
            conceptBean.getConcept(
                    selectedTheso.getCurrentIdTheso(),
                    ((NodeIdValue)event.getObject()).getId(),
                    selectedTheso.getCurrentLang());

        rightBodySetting.setIndex("0");         
        
    }  
    
    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public NodeIdValue getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(NodeIdValue selectedNode) {
        this.selectedNode = selectedNode;
    }

    public List<NodeIdValue> getNodeIdValues() {
        return nodeIdValues;
    }

    public void setNodeIdValues(List<NodeIdValue> nodeIdValues) {
        this.nodeIdValues = nodeIdValues;
    }

    public boolean isWithAltLabel() {
        return withAltLabel;
    }

    public void setWithAltLabel(boolean withAltLabel) {
        this.withAltLabel = withAltLabel;
    }

    public boolean isPermuted() {
        return permuted;
    }

    public void setPermuted(boolean permuted) {
        this.permuted = permuted;
    }


}

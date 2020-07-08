/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cnrs.opentheso.bean.leftbody.viewtree;

import fr.cnrs.opentheso.bean.leftbody.TreeNodeData;
import fr.cnrs.opentheso.bean.leftbody.DataService;
import java.io.Serializable;
import java.util.ArrayList;

import fr.cnrs.opentheso.bdd.helper.ConceptHelper;
import fr.cnrs.opentheso.bdd.helper.PathHelper;
import fr.cnrs.opentheso.bdd.helper.nodes.NodeUser;
import fr.cnrs.opentheso.bdd.helper.nodes.Path;
import fr.cnrs.opentheso.bdd.helper.nodes.concept.NodeConceptTree;
import fr.cnrs.opentheso.bean.leftbody.LeftBodySetting;
import fr.cnrs.opentheso.bean.menu.connect.Connect;
import fr.cnrs.opentheso.bean.menu.theso.RoleOnThesoBean;
import fr.cnrs.opentheso.bean.menu.theso.SelectedTheso;
import fr.cnrs.opentheso.bean.rightbody.viewconcept.ConceptView;
import fr.cnrs.opentheso.bean.rightbody.RightBodySetting;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author miledrousset
 */
@Named(value = "tree")
@SessionScoped

public class Tree implements Serializable {

    @Inject private Connect connect;
    @Inject private RightBodySetting rightBodySetting;
    @Inject private LeftBodySetting leftBodySetting;
    @Inject private ConceptView conceptBean;
    @Inject private SelectedTheso selectedTheso;
    @Inject private RoleOnThesoBean roleOnThesoBean;

    private DataService dataService;

    private TreeNode selectedNode; // le neoud sélectionné par clique
    private TreeNode root;

    private String idTheso;
    private String idLang;
    private boolean isSortedByNotation;

    ArrayList<TreeNode> selectedNodes; // enregistre les noeuds séléctionnés apres une recherche

    @PostConstruct
    public void init() {
//      initialise("th44", "fr");
    }

    public void reset() {
        root = null;
        selectedNode = null;
        rightBodySetting.init();
        isSortedByNotation = false;
    }

    public void initialise(String idTheso, String idLang) {
        this.idTheso = idTheso;
        this.idLang = idLang;
        selectedTheso.setSelectedLang(idLang);
        selectedTheso.setCurrentLang(idLang);
        
        dataService = new DataService();
        root = dataService.createRoot();
        addFirstNodes();
        selectedNodes = new ArrayList<>();
        leftBodySetting.setIndex("0");        
    }

    public boolean isDragAndDrop(NodeUser nodeUser) {
        if(nodeUser == null) return false;
        if(roleOnThesoBean == null) return false;
        if(roleOnThesoBean.isIsSuperAdmin() || roleOnThesoBean.isIsAdminOnThisTheso() || roleOnThesoBean.isIsManagerOnThisTheso())
            return true;
        else
            return false;
    }
    
    private boolean addFirstNodes() {
        ConceptHelper conceptHelper = new ConceptHelper();
        TreeNodeData data;
        //    String label;


        // liste des Tops termes ou concepts de premier niveau
        /* ArrayList<String> idTopConcepts = conceptHelper.getAllTopTermOfThesaurus(
                connect.getPoolConnexion(),
                idTheso); */

        // la liste est triée par alphabétique ou notation
        ArrayList<NodeConceptTree> nodeConceptTrees
                = conceptHelper.getListOfTopConcepts(connect.getPoolConnexion(),
                        idTheso, idLang, isSortedByNotation);

        for (NodeConceptTree nodeConceptTree : nodeConceptTrees) {
            /* label = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), idTopConcept, idTheso, idLang);
            if (label == null || label.isEmpty()) {
                label = "(" + idTopConcept + ")";
            }*/
            data = new TreeNodeData(
                    nodeConceptTree.getIdConcept(),
                    nodeConceptTree.getTitle(),
                    nodeConceptTree.getNotation(),
                    false,//isgroup
                    false,//isSubGroup
                    false,//isConcept
                    true,//isTopConcept
                    "topTerm"
            );
            if (nodeConceptTree.isHaveChildren()) {
                dataService.addNodeWithChild("concept", data, root);
            } else {
                dataService.addNodeWithoutChild("file", data, root);
            }
        }

        /*    
        for (String idTopConcept : idTopConcepts) {
            label = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), idTopConcept, idTheso, idLang);
            if (label == null || label.isEmpty()) {
                label = "(" + idTopConcept + ")";
            }
            data = new TreeNodeData(
                    idTopConcept,
                    label,
                    "",
                    false,//isgroup
                    false,//isSubGroup
                    false,//isConcept
                    true,//isTopConcept
                    "topTerm"
            );
            if (conceptHelper.haveChildren(connect.getPoolConnexion(), idTheso, idTopConcept)) {
                dataService.addNodeWithChild("concept", data, root);
            } else {
                dataService.addNodeWithoutChild("file", data, root);
            }
        }*/
        return true;
    }

    public TreeNode getRoot() {
        return root;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    private boolean addConceptsChild(TreeNode parent) {
        ConceptHelper conceptHelper = new ConceptHelper();
        TreeNodeData data;
        String label;
/*        ArrayList<String> conceptIds = conceptHelper.getListChildrenOfConcept(
                connect.getPoolConnexion(),
                ((TreeNodeData) parent.getData()).getNodeId(),
                idTheso);*/

        ArrayList<NodeConceptTree> nodeConceptTrees = conceptHelper.getListConcepts(
                connect.getPoolConnexion(),
                ((TreeNodeData) parent.getData()).getNodeId(),
                idTheso,
                selectedTheso.getCurrentLang(),
                isSortedByNotation);        
        
        for (NodeConceptTree nodeConceptTree : nodeConceptTrees) {
            if (nodeConceptTree.getIdConcept() == null) continue;
        //    label = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), conceptId, idTheso, idLang);
            label = nodeConceptTree.getTitle();
            if (nodeConceptTree.getTitle().isEmpty()) {
                label = "(" + nodeConceptTree.getIdConcept() + ")";
            }
                
            data = new TreeNodeData(
                    nodeConceptTree.getIdConcept(),
                    label,
                    nodeConceptTree.getNotation(),
                    false,//isgroup
                    false,//isSubGroup
                    true,//isConcept
                    false,//isTopConcept
                    "term"
            );
            if (conceptHelper.haveChildren(connect.getPoolConnexion(), idTheso, nodeConceptTree.getIdConcept())) {
                dataService.addNodeWithChild("concept", data, parent);
            } else {
                dataService.addNodeWithoutChild("file", data, parent);
            }
        }
        return true;
    }
    
    
    /////// pour l'ajout d'un fils supplementaire après un ajout de concept 
    
    public void addNewChild(TreeNode parent, String idConcept, String idTheso, String idLang) {
            ConceptHelper conceptHelper = new ConceptHelper();
            TreeNodeData data;
            String label = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), idConcept, idTheso, idLang);
            if (label == null || label.isEmpty()) {
                label = "(" + idConcept + ")";
            }
            data = new TreeNodeData(
                    idConcept,
                    label,
                    "",
                    false,//isgroup
                    false,//isSubGroup
                    true,//isConcept
                    false,//isTopConcept
                    "term"
            );
            if (conceptHelper.haveChildren(connect.getPoolConnexion(), idTheso, idConcept)) {
                dataService.addNodeWithChild("concept", data, parent);
            } else {
                dataService.addNodeWithoutChild("file", data, parent);
            }

    }

    public void onNodeExpand(NodeExpandEvent event) {
        DefaultTreeNode parent = (DefaultTreeNode) event.getTreeNode();
        if (parent.getChildCount() == 1 && parent.getChildren().get(0).getData().toString().equals("DUMMY")) {
            parent.getChildren().remove(0);
            addConceptsChild(parent);
        }
        /*     FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Expanded", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);*/
    }

    public void onNodeCollapse(NodeCollapseEvent event) {
        /*      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Collapsed", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);
         */
    }

    public void onNodeSelect(NodeSelectEvent event) {
        if (((TreeNodeData) selectedNode.getData()).isIsConcept()) {
            rightBodySetting.setShowConceptToOn();
            conceptBean.getConceptForTree(idTheso,
                    ((TreeNodeData) selectedNode.getData()).getNodeId(), idLang);
        }
        if (((TreeNodeData) selectedNode.getData()).isIsTopConcept()) {
            rightBodySetting.setShowConceptToOn();

            conceptBean.getConceptForTree(idTheso,
                    ((TreeNodeData) selectedNode.getData()).getNodeId(), idLang);
        }
        
        rightBodySetting.setIndex("0");
    }

    public void onNodeUnselect(NodeUnselectEvent event) {
        /*    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Unselected", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);*/
    }

    /**
     * permet de déplier l'arbre suivant le Path ou les paths en paramètre
     *
     * @param idConcept
     * @param idTheso
     * @param idLang #MR
     */
    public void expandTreeToPath(
            String idConcept,
            String idTheso,
            String idLang) {

        ArrayList<Path> paths = new PathHelper().getPathOfConcept(
                connect.getPoolConnexion(), idConcept, idTheso);

        if (root == null) {
            initialise(idTheso, idLang);
        }

        // cas de changement de langue pendant la navigation dans les concepts
        // il faut reconstruire l'arbre dès le début
        if (!idLang.equalsIgnoreCase(this.idLang)) {
            initialise(idTheso, idLang);
        }

        if (!paths.isEmpty()) {
            // pour déselectionner les noeuds avant de séléctionner le neoud trouvé
            selectedNodes.forEach((selectedNode1) -> {
                selectedNode1.setSelected(false);
            });
            if (selectedNode != null) {
                selectedNode.setSelected(false);
            }
            selectedNodes.clear();
        }

        TreeNode treeNodeParent = root;
        treeNodeParent.setExpanded(true);
        for (Path thisPath : paths) {
            for (String idC : thisPath.getPath()) {
                treeNodeParent = selectChildNode(treeNodeParent, idC);
                if (treeNodeParent == null) {
                    // erreur de cohérence
                    return;
                }
                // compare le dernier élément au concept en cours, si oui, on expand pas, sinon, erreur ...
                if (!((TreeNodeData) treeNodeParent.getData()).getNodeId().equalsIgnoreCase(thisPath.getPath().get(thisPath.getPath().size() - 1))) {
                    treeNodeParent.setExpanded(true);
                }
            }
            treeNodeParent.setSelected(true);
            selectedNodes.add(treeNodeParent);
            selectedNode = treeNodeParent;
            treeNodeParent = root;
        }
        leftBodySetting.setIndex("0");
    }
    
    /**
     * permet de déplier l'arbre suivant le Path ou les paths en paramètre
     * On reconstruit l'arbre dès le début suite à des modifications 
     *
     * @param idConcept
     * @param idTheso
     * @param idLang #MR
     */
    public void initAndExpandTreeToPath(
            String idConcept,
            String idTheso,
            String idLang) {

        ArrayList<Path> paths = new PathHelper().getPathOfConcept(
                connect.getPoolConnexion(), idConcept, idTheso);

        initialise(idTheso, idLang);

        if (!paths.isEmpty()) {
            // pour déselectionner les noeuds avant de séléctionner le neoud trouvé
            selectedNodes.forEach((selectedNode1) -> {
                selectedNode1.setSelected(false);
            });
            if (selectedNode != null) {
                selectedNode.setSelected(false);
            }
            selectedNodes.clear();
        }

        TreeNode treeNodeParent = root;
        treeNodeParent.setExpanded(true);
        for (Path thisPath : paths) {
            for (String idC : thisPath.getPath()) {
                treeNodeParent = selectChildNode(treeNodeParent, idC);
                if (treeNodeParent == null) {
                    // erreur de cohérence
                    return;
                }
                // compare le dernier élément au concept en cours, si oui, on expand pas, sinon, erreur ...
                if (!((TreeNodeData) treeNodeParent.getData()).getNodeId().equalsIgnoreCase(thisPath.getPath().get(thisPath.getPath().size() - 1))) {
                    treeNodeParent.setExpanded(true);
                }
            }
            treeNodeParent.setSelected(true);
            selectedNodes.add(treeNodeParent);
            selectedNode = treeNodeParent;
            treeNodeParent = root;
        }
        leftBodySetting.setIndex("0");
    }
    
    

    /**
     * permet de trouver le noeud dans la liste des enfants suivant
     * l'identifiant du concept elle retourne le noeud trouvé
     *
     * @param treeNode
     * @param idConcept
     * @return
     */
    private TreeNode selectChildNode(TreeNode treeNodeParent, String idConceptChildToFind) {
        // test si les fils ne sont pas construits
        if (treeNodeParent.getChildCount() == 1 && treeNodeParent.getChildren().get(0).getData().toString().equals("DUMMY")) {
            treeNodeParent.getChildren().remove(0);
            addConceptsChild(treeNodeParent);
        }
        List<TreeNode> treeNodes = treeNodeParent.getChildren();

        for (TreeNode treeNode : treeNodes) {
            if (((TreeNodeData) treeNode.getData()).getNodeId().equalsIgnoreCase(idConceptChildToFind)) {
                return treeNode;
            }
        }
        // pas de noeud trouvé dans les fils
        return null;
    }

}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.cnrs.opentheso.bdd.helper.nodes;

import javax.faces.context.FacesContext;
import java.util.ResourceBundle;

/**
 *
 * @author miled.rousset
 */
public class NodeImage {

    private String idConcept;
    private String idThesaurus;
    private String imageName;
    private String copyRight;
    private String uri;
    
    private String oldUri; // pour la modification d'une Uri
    
    public NodeImage() {
    }

    public String getIdConcept() {
        return idConcept;
    }

    public void setIdConcept(String idConcept) {
        this.idConcept = idConcept;
    }

    public String getIdThesaurus() {
        return idThesaurus;
    }

    public void setIdThesaurus(String idThesaurus) {
        this.idThesaurus = idThesaurus;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getCopyRight() {
        return copyRight;
    }

    public void setCopyRight(String copyRight) {
        this.copyRight = copyRight;
    }

    public String getUri() {
        if (uri.startsWith("http")) {
            return uri;
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            try {
                ResourceBundle bundlePool = context.getApplication().getResourceBundle(context, "conHikari");
                if(bundlePool == null){
                    return null;
                }

                //{scheme}://{server}{/prefix}/{identifier}/{region}/{size}/{rotation}/{quality}.{format}
                return new StringBuffer(bundlePool.getString("iiif.serverName"))
                        .append("/")
                        .append(bundlePool.getString("iiif.prefix"))
                        .append("/")
                        .append(uri)
                        .append("/")
                        .append(bundlePool.getString("iiif.region"))
                        .append("/")
                        .append(bundlePool.getString("iiif.size"))
                        .append("/")
                        .append(bundlePool.getString("iiif.rotation"))
                        .append("/")
                        .append(bundlePool.getString("iiif.quality"))
                        .append(".")
                        .append(bundlePool.getString("iiif.format"))
                        .toString();
            } catch (Exception e) {
                return null;
            }
        }
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getOldUri() {
        return oldUri;
    }

    public void setOldUri(String oldUri) {
        this.oldUri = oldUri;
    }
    
}

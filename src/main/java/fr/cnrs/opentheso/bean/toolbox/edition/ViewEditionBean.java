package fr.cnrs.opentheso.bean.toolbox.edition;

import org.primefaces.PrimeFaces;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author miledrousset
 */
@Named(value = "viewEditionBean")
@SessionScoped
public class ViewEditionBean implements Serializable {

    private String alternateColor;

    private String login, password, urlServer, nomGraphe;

    // les vues
    private boolean isViewListTheso;
    private boolean isViewExportSkos;
    private boolean isViewExportPDF;
    private boolean isViewExportCSV;
    private boolean isViewImportSkos;
    private boolean isViewImportCsv;
    private boolean isViewImportVirtuoso;

    private boolean isViewNewTheso;
    private boolean isViewModifyTheso;

    private boolean isExportStarted;

    private boolean isImportStarted;

    public ViewEditionBean() {
        alternateColor = "#C8EAD6";
        isViewListTheso = true;
        isViewExportSkos = false;
        isViewImportSkos = false;
        isViewImportCsv = false;
        isImportStarted = false;
        isViewNewTheso = false;
        isViewModifyTheso = false;
        isViewExportPDF = false;
        isViewExportCSV = false;
        isViewImportVirtuoso = false;
    }

    public void init() {

        login = "dba";
        password = "dba";
        urlServer = "localhost:1111";
        nomGraphe = "localhost/test2";

        alternateColor = "#C8EAD6";
        isViewListTheso = true;
        isViewExportSkos = false;
        isExportStarted = false;
        isViewImportSkos = false;
        isViewImportCsv = false;
        isImportStarted = false;
        isViewNewTheso = false;
        isViewExportPDF = false;
        isViewModifyTheso = false;
        isViewExportCSV = false;
        isViewImportVirtuoso = false;
    }

    public String getNewAlternateColor() {
        if (alternateColor.equalsIgnoreCase("#FFFFFF")) {
            alternateColor = "#C8EAD6";
        } else {
            alternateColor = "#FFFFFF";
        }
        return alternateColor;
    }

    public String getAlternateColor(int position) {
        if (position == 0) {
            alternateColor = "#C8EAD6";
        } else {
            if (alternateColor.equalsIgnoreCase("#FAFAFA")) {
                alternateColor = "#C8EAD6";
            } else {
                alternateColor = "#FAFAFA";
            }
        }
        return alternateColor;
    }

    public boolean isIsViewListTheso() {
        return isViewListTheso;
    }

    public void setIsViewListTheso(boolean isViewListTheso) {
        this.isViewListTheso = isViewListTheso;
        isViewExportSkos = false;
    }

    public boolean isIsViewExportSkos() {
        return isViewExportSkos;
    }

    public void setIsViewExportSkos(boolean isViewExportSkos) {
        this.isViewExportSkos = isViewExportSkos;
        isViewListTheso = false;
        isViewImportSkos = false;
        isViewImportCsv = false;
        isViewNewTheso = false;
        isViewExportPDF = false;
        isViewModifyTheso = false;
        isViewExportCSV = false;
        isViewImportVirtuoso = false;
    }

    public boolean isIsExportStarted() {
        return isExportStarted;
    }

    public void setIsExportStarted(boolean isExportStarted) {
        this.isExportStarted = isExportStarted;
    }

    public boolean isIsViewImportSkos() {
        return isViewImportSkos;
    }

    public void setIsViewImportSkos(boolean isViewImportSkos) {
        this.isViewImportSkos = isViewImportSkos;
        isViewListTheso = false;
        isViewExportSkos = false;
        isViewNewTheso = false;
        isViewModifyTheso = false;
        isViewImportCsv = false;
        isViewExportPDF = false;
        isViewExportCSV = false;
        isViewImportVirtuoso = false;
    }

    public boolean isIsImportStarted() {
        return isImportStarted;
    }

    public void setIsImportStarted(boolean isImportStarted) {
        this.isImportStarted = isImportStarted;
        isExportStarted = false;
    }

    public boolean isIsViewNewTheso() {
        return isViewNewTheso;
    }

    public void setIsViewNewTheso(boolean isViewNewTheso) {
        this.isViewNewTheso = isViewNewTheso;
        isViewExportSkos = false;
        isViewListTheso = false;
        isViewImportSkos = false;
        isViewImportCsv = false;
        isViewModifyTheso = false;
        isViewExportPDF = false;
        isViewExportCSV = false;
        isViewImportVirtuoso = false;
    }

    public boolean isIsViewModifyTheso() {
        return isViewModifyTheso;
    }

    public void setIsViewModifyTheso(boolean isViewModifyTheso) {
        this.isViewModifyTheso = isViewModifyTheso;
        isViewNewTheso = false;
        isViewExportSkos = false;
        isViewListTheso = false;
        isViewImportSkos = false;
        isViewImportCsv = false;
        isViewExportPDF = false;
        isViewExportCSV = false;
        isViewImportVirtuoso = false;
    }

    public boolean isIsViewImportCsv() {
        return isViewImportCsv;
    }

    public void setIsViewImportCsv(boolean isViewImportCsv) {
        this.isViewImportCsv = isViewImportCsv;
        isViewNewTheso = false;
        isViewExportSkos = false;
        isViewListTheso = false;
        isViewImportSkos = false;
        isViewModifyTheso = false;
        isViewExportPDF = false;
        isViewExportCSV = false;
        isViewImportVirtuoso = false;
    }

    public boolean isExportView() {
        return isViewExportPDF || isViewExportCSV || isViewExportSkos || isViewImportVirtuoso;
    }

    public boolean isIsViewExportPDF() {
        return isViewExportPDF;
    }

    public void setIsViewExportPDF(boolean isViewExportPDF) {
        this.isViewExportPDF = isViewExportPDF;
        isViewNewTheso = false;
        isViewExportSkos = false;
        isViewListTheso = false;
        isViewImportSkos = false;
        isViewModifyTheso = false;
        isViewImportCsv = false;
        isViewExportCSV = false;
        isViewImportVirtuoso = false;
    }

    public void setIsViewExportCSV(boolean isViewExportCSV) {
        this.isViewExportCSV = isViewExportCSV;
        isViewExportPDF = false;
        isViewNewTheso = false;
        isViewExportSkos = false;
        isViewListTheso = false;
        isViewImportSkos = false;
        isViewModifyTheso = false;
        isViewImportCsv = false;
        isViewImportVirtuoso = false;
    }

    public void setIsViewExportVirtuoso(boolean isViewImportVirtuoso) {
        this.isViewImportVirtuoso = isViewImportVirtuoso;
        isViewExportPDF = false;
        isViewNewTheso = false;
        isViewExportSkos = false;
        isViewListTheso = false;
        isViewImportSkos = false;
        isViewModifyTheso = false;
        isViewImportCsv = false;
    }

    public boolean isIsViewExportCSV() {
        return isViewImportCsv;
    }

    public boolean isViewImportVirtuoso() {
        return isViewImportVirtuoso;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrlServer() {
        return urlServer;
    }

    public void setUrlServer(String urlServer) {
        this.urlServer = urlServer;
    }

    public String getNomGraphe() {
        return nomGraphe;
    }

    public void setNomGraphe(String nomGraphe) {
        this.nomGraphe = nomGraphe;
    }
}

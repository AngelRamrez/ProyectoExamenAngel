/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.AngelRamirez;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author programacion
 */
@Entity
@Table(name = "cocinero")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cocinero.findAll", query = "SELECT c FROM Cocinero c")
    , @NamedQuery(name = "Cocinero.findByIdcocinero", query = "SELECT c FROM Cocinero c WHERE c.idcocinero = :idcocinero")
    , @NamedQuery(name = "Cocinero.findByNombrecocinero", query = "SELECT c FROM Cocinero c WHERE c.nombrecocinero = :nombrecocinero")
    , @NamedQuery(name = "Cocinero.findByTelefonococinero", query = "SELECT c FROM Cocinero c WHERE c.telefonococinero = :telefonococinero")})
public class Cocinero implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idcocinero")
    private Integer idcocinero;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "nombrecocinero")
    private String nombrecocinero;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "telefonococinero")
    private String telefonococinero;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idcocinero")
    private List<Detallefactura> detallefacturaList;

    public Cocinero() {
    }

    public Cocinero(Integer idcocinero) {
        this.idcocinero = idcocinero;
    }

    public Cocinero(Integer idcocinero, String nombrecocinero, String telefonococinero) {
        this.idcocinero = idcocinero;
        this.nombrecocinero = nombrecocinero;
        this.telefonococinero = telefonococinero;
    }

    public Integer getIdcocinero() {
        return idcocinero;
    }

    public void setIdcocinero(Integer idcocinero) {
        this.idcocinero = idcocinero;
    }

    public String getNombrecocinero() {
        return nombrecocinero;
    }

    public void setNombrecocinero(String nombrecocinero) {
        this.nombrecocinero = nombrecocinero;
    }

    public String getTelefonococinero() {
        return telefonococinero;
    }

    public void setTelefonococinero(String telefonococinero) {
        this.telefonococinero = telefonococinero;
    }

    @XmlTransient
    public List<Detallefactura> getDetallefacturaList() {
        return detallefacturaList;
    }

    public void setDetallefacturaList(List<Detallefactura> detallefacturaList) {
        this.detallefacturaList = detallefacturaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idcocinero != null ? idcocinero.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cocinero)) {
            return false;
        }
        Cocinero other = (Cocinero) object;
        if ((this.idcocinero == null && other.idcocinero != null) || (this.idcocinero != null && !this.idcocinero.equals(other.idcocinero))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.AngelRamirez.Cocinero[ idcocinero=" + idcocinero + " ]";
    }
    
}

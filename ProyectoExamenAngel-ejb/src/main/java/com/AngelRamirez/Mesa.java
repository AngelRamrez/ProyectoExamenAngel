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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author programacion
 */
@Entity
@Table(name = "mesa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Mesa.findAll", query = "SELECT m FROM Mesa m")
    , @NamedQuery(name = "Mesa.findByIdmesa", query = "SELECT m FROM Mesa m WHERE m.idmesa = :idmesa")
    , @NamedQuery(name = "Mesa.findByNumeromesa", query = "SELECT m FROM Mesa m WHERE m.numeromesa = :numeromesa")})
public class Mesa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idmesa")
    private Integer idmesa;
    @Size(max = 5)
    @Column(name = "numeromesa")
    private String numeromesa;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idmesa")
    private List<Factura> facturaList;
    @JoinColumn(name = "idareas", referencedColumnName = "idareas")
    @ManyToOne(optional = false)
    private Areas idareas;

    public Mesa() {
    }

    public Mesa(Integer idmesa) {
        this.idmesa = idmesa;
    }

    public Integer getIdmesa() {
        return idmesa;
    }

    public void setIdmesa(Integer idmesa) {
        this.idmesa = idmesa;
    }

    public String getNumeromesa() {
        return numeromesa;
    }

    public void setNumeromesa(String numeromesa) {
        this.numeromesa = numeromesa;
    }

    @XmlTransient
    public List<Factura> getFacturaList() {
        return facturaList;
    }

    public void setFacturaList(List<Factura> facturaList) {
        this.facturaList = facturaList;
    }

    public Areas getIdareas() {
        return idareas;
    }

    public void setIdareas(Areas idareas) {
        this.idareas = idareas;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idmesa != null ? idmesa.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Mesa)) {
            return false;
        }
        Mesa other = (Mesa) object;
        if ((this.idmesa == null && other.idmesa != null) || (this.idmesa != null && !this.idmesa.equals(other.idmesa))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.AngelRamirez.Mesa[ idmesa=" + idmesa + " ]";
    }
    
}

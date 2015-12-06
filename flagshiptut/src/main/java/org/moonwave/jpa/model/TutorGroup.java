package org.moonwave.jpa.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;


/**
 * The persistent class for the tutor_group database table.
 * 
 */
@Entity
@Table(name="tutor_group")

@NamedQueries({
    @NamedQuery(name="TutorGroup.findAll",  query="SELECT t FROM TutorGroup t"),
    @NamedQuery(name="TutorGroup.findById", query="SELECT t FROM TutorGroup t WHERE t.id = :id")
})

public class TutorGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private short id;

    private String alias;

    private String name;

    private short ordinal;

    //bi-directional many-to-many association to User
    @ManyToMany(mappedBy="tutorGroups", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    private List<User> users;

    public TutorGroup() {
    }

    public short getId() {
        return this.id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getOrdinal() {
        return this.ordinal;
    }

    public void setOrdinal(short ordinal) {
        this.ordinal = ordinal;
    }

    public List<User> getUsers() {
        return this.users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public int hashCode() {
        return ((Short)id).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if ((o == null) || !(o instanceof TutorGroup)) {
            return false;
        }
        TutorGroup other = (TutorGroup) o;
        if (this.id != other.id) {
            return false;
        }
        if (! this.alias.equals(other.alias)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id= ").append(id);
        sb.append(",alias= ").append(alias);
        sb.append(",name= ").append(name);
        sb.append(",ordinal= ").append(ordinal);
        return sb.toString();
    }
}
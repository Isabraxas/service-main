package com.viridian.service.statement.persistence.auto;

import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import com.viridian.service.statement.persistence.RolePermission;

/**
 * Class _Permission was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Permission extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "ID";

    public static final Property<String> NAME = Property.create("name", String.class);
    public static final Property<List<RolePermission>> X_ROLE = Property.create("xROLE", List.class);

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }

    public void addToXROLE(RolePermission obj) {
        addToManyTarget("xROLE", obj, true);
    }
    public void removeFromXROLE(RolePermission obj) {
        removeToManyTarget("xROLE", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<RolePermission> getXROLE() {
        return (List<RolePermission>)readProperty("xROLE");
    }


}

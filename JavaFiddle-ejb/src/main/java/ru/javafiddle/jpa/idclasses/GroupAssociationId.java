package ru.javafiddle.jpa.idclasses;

import java.io.Serializable;

/**
 * Created by artyom on 17.02.16.
 */
public class GroupAssociationId implements Serializable {

    private int userId;
    private int groupId;

    public int hashCode() {
        return  userId + groupId;
    }

    public boolean equals(Object object) {
        if (object instanceof GroupAssociationId) {
            GroupAssociationId otherId = (GroupAssociationId) object;
            return (otherId.userId == this.userId) && (otherId.groupId == this.groupId);
        }
        return false;
    }
}

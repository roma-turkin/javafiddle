package ru.javafiddle.jpa.entity;

/**
 * Created by Fedor on 18.11.2015.
 */
import ru.javafiddle.jpa.idclasses.GroupAssociationId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * A relationship between users and groups.
 */
@Entity
@Table(name = "\"UserGroup\"")
@IdClass(GroupAssociationId.class)
public class UserGroup {

    @Id
    @Column(name = "\"userId\"")
    private int userId;
    @Id
    @Column(name = "\"groupId\"")
    private int groupId;

    @ManyToOne
    @PrimaryKeyJoinColumn(name = "\"userId\"", referencedColumnName = "\"userId\"")
    private User member;

    @ManyToOne
    @PrimaryKeyJoinColumn(name = "\"groupId\"", referencedColumnName = "\"groupId\"")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "\"accessId\"")
    private Access access;

    public UserGroup(Group group, User member, Access access) {
        this.group = group;
        this.member = member;
        this.access = access;
    }

    public UserGroup() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public User getMember() {
        return member;
    }

    public void setMember(User member) {
        this.member = member;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    @Override
    public String toString() {
        return "UserGroup{" +
                "userId=" + userId +
                ", groupId=" + groupId +
                ", access=" + access.getAccessName() +
                '}';
    }
}
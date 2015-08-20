////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2015 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////

package com.denimgroup.threadfix.data.entities;

import com.denimgroup.threadfix.views.AllViews;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.denimgroup.threadfix.CollectionUtils.map;

/**
 * @author zabdisubhan
 */
@Entity
@Table(name = "AcceptanceCriteriaStatus"
        ,
        uniqueConstraints = {
        @UniqueConstraint(columnNames = { "`applicationId`", "`acceptanceCriteriaId`" })}
)
public class AcceptanceCriteriaStatus extends AuditableEntity {

    private Boolean passing = false;
    private Application application;
    private AcceptanceCriteria acceptanceCriteria;
    private List<EmailList> emailLists;
    private List<String> emailAddresses;
    private Boolean sendEmail = false;
    private transient boolean statusChanged = false;

    @ElementCollection
    @Column(name = "emailAddress", length = 128)
    @CollectionTable(name = "AcceptanceCriteriaStatusEmailAddress", joinColumns = @JoinColumn(name = "AcceptanceCriteriaStatusId"))
    @JsonView(AllViews.AcceptanceCriteriaPageView.class)
    public List<String> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(List<String> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "emailListId")
    @JsonView(AllViews.AcceptanceCriteriaPageView.class)
    public List<EmailList> getEmailLists() {
        return emailLists;
    }

    public void setEmailLists(List<EmailList> emailLists) {
        this.emailLists = emailLists;
    }

    @Column
    @JsonView({AllViews.FormInfo.class, AllViews.AcceptanceCriteriaPageView.class})
    public Boolean isPassing() {
        return passing != null && passing;
    }

    public void setPassing(boolean passing) {
        this.passing = passing;
    }

    @Transient
    public boolean hasStatusChanged() {
        return statusChanged;
    }

    public void setStatusChanged(boolean statusChanged) {
        this.statusChanged = statusChanged;
    }

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "`applicationId`", nullable = false)
    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    @Column
    @JsonView(AllViews.AcceptanceCriteriaPageView.class)
    public Boolean isSendEmail() {
        return sendEmail != null && sendEmail;
    }

    public void setSendEmail(Boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    @Transient
    @JsonProperty("application")
    @JsonView(AllViews.AcceptanceCriteriaPageView.class)
    public Map<String, Object> getApplicationJson() {
        if(application != null) {
            return map(
                    "id", application.getId(),
                    "name", application.getName(),
                    "team", map(
                            "id", application.getOrganization().getId(),
                            "name", application.getOrganization().getName()));
        } else {
            return null;
        }
    }

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "`acceptanceCriteriaId`", nullable = false)
    public AcceptanceCriteria getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    public void setAcceptanceCriteria(AcceptanceCriteria acceptanceCriteria) {
        this.acceptanceCriteria = acceptanceCriteria;
    }

    @Transient
    @JsonProperty("acceptanceCriteria")
    @JsonView({AllViews.TableRow.class, AllViews.FormInfo.class, AllViews.AcceptanceCriteriaPageView.class})
    public Map<String, ? extends Serializable> getAcceptanceCriteriaJson() {
        if(acceptanceCriteria != null) {
            return map(
                    "id", acceptanceCriteria.getId(),
                    "name", acceptanceCriteria.getName(),
                    "filterName", acceptanceCriteria.getFilterJsonBlob().getName());
        } else {
            return null;
        }
    }

    @Transient
    @JsonView({AllViews.TableRow.class, AllViews.FormInfo.class, AllViews.AcceptanceCriteriaPageView.class})
    public String getName() {
        return application.getName();
    }
}

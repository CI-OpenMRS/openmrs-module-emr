<%
    ui.decorateWith("appui", "standardEmrPage")
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("emr.app.inpatients.label")}"}
    ];

    jq(function() {
       jq("#inpatients-filterByLocation").change(function(event){

           var selectedItems= "";
           var selectedItemId="";
           jq("select option:selected").each(function(){
               selectedItems = jq(this).text() + "; id=" + this.value;
               selectedItemId =this.value;
           });
           console.log("selectItems=" + selectedItems);
       });
    });

</script>

<h3>${ ui.message("emr.inpatients.title") }</h3>

    <div style="float:right;">

        ${ ui.includeFragment("emr", "field/location", [
            "id": "inpatients-filterByLocation",
            "formFieldName": "filterByLocationId",
            "label": "emr.inpatients.filterByCurrentWard",
            "withTag": "Admission Location"
        ] ) }
        <br>
        <em>Patient Count:</em>
        <span>${visitSummaries.size()}</span>
    </div>

<table id="active-visits" width="100%" border="1" cellspacing="0" cellpadding="2">
    <thead>
    <tr>
        <th>${ ui.message("emr.patient.identifier") }</th>
        <th>${ ui.message("emr.person.name") }</th>
        <th>${ ui.message("emr.inpatients.firstAdmitted") }</th>
        <th>${ ui.message("emr.inpatients.currentWard") }</th>
    </tr>
    </thead>
    <tbody>
    <% if (visitSummaries.size() == 0) { %>
    <tr>
        <td colspan="4">${ ui.message("emr.none") }</td>
    </tr>
    <% } %>
    <% visitSummaries.each { v ->
        def admitted = v.admissionEncounter
        def latest = v.latestAdtEncounter
    %>
    <tr id="visit-${ v.visit.id }">
        <td>${ v.visit.patient.patientIdentifier.identifier }</td>
        <td>
            <a href="${ ui.pageLink("emr", "patient", [ patientId: v.visit.patient.id ]) }">
                ${ ui.format(v.visit.patient) }
            </a>
        </td>
        <td>
            <% if (admitted) { %>
            ${ ui.format(admitted.location) }
            <br/>
            <small>
                ${ ui.format(admitted.encounterDatetime) }
            </small>
            <% } %>
        </td>
        <td>
            <% if (latest) { %>
            ${ ui.format(latest.location) }
            <br/>
            <small>
                ${ ui.format(latest.encounterDatetime) }
            </small>

            <% } %>
        </td>
    </tr>
    <% } %>
    </tbody>
</table>
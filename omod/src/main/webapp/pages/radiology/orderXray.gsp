<%
    ui.decorateWith("emr", "standardEmrPage")

    ui.includeJavascript("emr", "knockout-2.1.0.js")
    ui.includeJavascript("emr", "custom/xrayOrder.js")

    ui.includeCss("emr", "orderXray.css")
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html">
    jQuery(function() {
        jq('button.confirm').click(function(){

            if (!jq(this).attr("disabled")) {
                jq(this).closest("form").submit();
            }

            jq(this).attr('disabled', 'disabled');
            jq(this).addClass("disabled");

        });

        ko.applyBindings( new StudiesViewModel(${xrayOrderables}, ${portableLocations}), jq('#contentForm').get(0) );

        // Preventing form submission when pressing enter on study-search input field
        jq('#study-search').bind('keypress', function(eventKey) {
           if(event.keyCode == 13) {
               event.preventDefault();
               return false;
           }
        });
    });
</script>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.format(patient.familyName) }, ${ ui.format(patient.givenName) }", link:'${ui.pageLink("emr", "patient", [patientId: patient.id])}' },
        { label: "${ui.message("emr.task.orderXray.label")}" }
    ];
</script>


${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }

<div id="contentForm">
<h1>${ ui.message("emr.orderXray.title") }</h1>
<form action="${ ui.actionLink("emr", "radiology/radiologyRequisition", "orderXray") }" data-bind="submit: isValid">
    <input type="hidden" name="successUrl" value="${ ui.pageLink("emr", "patient", [ patientId: patient.id ]) }"/>
    <input type="hidden" name="patient" value="${ patient.id }"/>
    <input type="hidden" name="requestedBy" value="${ currentProvider.id }"/>


     <div class="left-column">
        <label for="study-search"><strong>${ ui.message("emr.orderXray.studySearchInstructions") }</strong></label><br/>
        <input id="study-search" type="text"
               data-bind="autocomplete:searchTerm, search:convertedStudies, select:selectStudy, clearValue:function() { return true; }"
               placeholder="${ ui.message("emr.orderXray.studySearchPlaceholder") }"/>
    </div>
    
    <div class="right-column">
        <div class="row">
            ${ ui.includeFragment("emr", "field/radioButtons", [
                    label: ui.message("emr.order.timing"),
                    formFieldName: "urgency",
                    options: [
                        [ value: "ROUTINE", label: ui.message("emr.order.timing.routine"), checked: true ],
                        [ value: "STAT", label: ui.message("emr.order.timing.urgent") ]
                    ]
            ]) }
        </div>
        <div class="row">
            <p><strong>${ ui.message("emr.orderXray.portableQuestion") }</strong></p>
            <p>
                <input type="checkbox" class="field-value" value="portable" data-bind="checked: portable"/>
                <span>${ ui.message("emr.yes") }</span>
                <select name="examLocation" data-bind="enable:portable, options:locations, optionsText:'name', optionsValue:'id', optionsCaption:'${ ui.escapeJs(ui.message("emr.orderXray.examLocationQuestion")) }', value:portableLocation">
                </select>
            </p>
        </div>
    </div>

    <div class="left-column">
        ${ ui.includeFragment("emr", "field/textarea", [ label:"<strong>" + ui.message("emr.order.indication") + "</strong>", formFieldName: "clinicalHistory", labelPosition: "top", rows: 5, cols: 35 ]) }
    </div>
   
    <div class="right-column">
        <div data-bind="visible: selectedStudies().length == 0">
            <span style="color: blue;">${ ui.message("emr.orderXray.noStudiesSelected") }</span>
        </div>
        <div data-bind="visible: selectedStudies().length > 0">
            <label>${ ui.message("emr.orderXray.selectedStudies") }</label>
            <ul id="selected-studies" data-bind="foreach: selectedStudies">
                <li>
                    <input type="hidden" data-bind="value: id" name="studies" />
                    <span data-bind="text: name"></span>
                    <span data-bind="click: \$root.unselectStudy">X</span>
                </li>
            </ul>
        </div>
    </div>

    <div id="bottom">
        <button id="cancel" class="cancel" onclick="location.href = emr.pageLink('emr', 'patient', { patientId: <%= patient.id %> })">
            ${ ui.message("emr.cancel") }
        </button>
        <button type="submit" class="confirm" id="next" data-bind="css: { disabled: !isValid() }, enable: isValid()">
            ${ ui.message("emr.save") }
        </button>
    </div>
</form>
</div>


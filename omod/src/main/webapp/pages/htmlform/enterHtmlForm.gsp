<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<input id="cancel-form" type="button" value="${ ui.message("htmlformentry.discard") }"/>

<script type="text/javascript">
    jq(function() {
        jq('#cancel-form').click(function() {
            location.href = '${ returnUrl }';
        }).insertAfter(jq('input.submitButton'));
    });
</script>

${ ui.includeFragment("emr", "htmlform/enterHtmlForm", [
        patient: patient,
        formUuid: formUuid,
        htmlFormId: htmlFormId,
        visit: visit,
        returnUrl: returnUrl
]) }
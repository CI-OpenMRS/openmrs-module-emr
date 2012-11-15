<%
    config.require("label")
    config.require("formFieldName")
    config.require("options")
%>

<label for="${ config.id }-field">${ config.label }</label>


<select id="${ config.id }-field" name="${ config.formFieldName}" />

    <option>${ config.emptyOptionLabel ?: ''}</option>

    <% config.options.each {
        def selected = it.selected || it.value == config.initialValue
    %>
        <option value="${ it.value }"  <% if (selected) { %>selected<% } %>/>${ it.label }</option>
    <% } %>

</select>

${ ui.includeFragment("emr", "fieldErrors", [ fieldName: config.formFieldName ]) }

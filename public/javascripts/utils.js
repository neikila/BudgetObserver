/**
 * Created by neikila on 30.12.15.
 */
var postJSON = function(url, json, func) {
    $.ajax({
        beforeSend: function (xhrObj) {
            xhrObj.setRequestHeader("Content-Type", "application/json;text/json;");
            xhrObj.setRequestHeader("Accept", "application/json;text/json;");
        },
        type: "POST",
        url: url,
        data: JSON.stringify(json),
        dataType: "json",
        success: function (jsonResponse) {
            func(jsonResponse)
        }
    });
};
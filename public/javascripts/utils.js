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

$.fn.xpathEvaluate = function (xpathExpression) {
    // NOTE: vars not declared local for debug purposes
    $this = this.first(); // Don't make me deal with multiples before coffee

    // Evaluate xpath and retrieve matching nodes
    xpathResult = this[0].evaluate(xpathExpression, this[0], null, XPathResult.ORDERED_NODE_ITERATOR_TYPE, null);

    result = [];
    while (elem = xpathResult.iterateNext()) {
        result.push(elem);
    }

    $result = jQuery([]).pushStack( result );
    return $result;
}
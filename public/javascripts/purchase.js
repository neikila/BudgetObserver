/**
 * Created by neikila on 11.01.16.
 */

var purchasesPage = function() {
    var groupName = undefined;
    var pieChart = null;

    function page(nextGroupName) {
        console.log("Start loading page");
        groupName = nextGroupName;
        purchasesPage.getPurchasesFromServer();
    }

    page.getGroupName = function() {
        return groupName;
    };

    page.setGroupName = function(groupNameParam) {
        groupName = groupNameParam;
        $(".panel-heading").text(groupName);
    };

    page.getGroupPieDataFromServer = function(num) {
            $.ajax({
                type: "GET",
                url: "/getGroupPieData",
                data: {
                    "groupName": groupName
                },
                success: function(pieData){
                    var graph = document.getElementById('graph').getContext('2d');
                    var pieOptions = {
                        animationSteps: num,
                        animationEasing: 'easeInOutQuart'
                    };
                    if (pieChart != null) {
                        pieChart.destroy();
                    }
                    pieChart = new Chart(graph).Pie(pieData, pieOptions);
                    var list = $(document).xpathEvaluate("//ul[@aria-labelledby='product']");
                    list.empty();
                    pieData.forEach(function(group) {
                        list.append("<li><a class='result' data-id='{{_id}}'>" + group.label + "</a></li>");
                    });
                    $(document).xpathEvaluate("//ul[@aria-labelledby='product']/li").click(function() {$("#product").val($(this).children().text())});
                }
            });
        };

    page.setListeners = function () {
        $('div.dropdown').keydown(function(e) {
            if (e.keyCode == 9 || e.keyCode == 27)
                $("div.dropdown-backdrop").click();
        });

        $("#savePur").click(function(){
            postJSON("/saveJSON", {
                "product": $("#product").val(),
                "amount": Number($("#amount").val()),
                "groupName": groupName
            }, function(json){
                var num = $(document).xpathEvaluate("//table[contains(@class, 'table')]/tbody/\*").size() + 1;
                if (num != 1) {
                    $("<tr><th scope='row'>" + num + "</th><td>" + json.product + "</td><td>" + json.amount + "</td></tr>")
                        .insertBefore($(document).xpathEvaluate("//table[contains(@class, 'table')]/tbody/\*[1]"));
                } else {
                    $(".dropdown div").attr("data-toggle", "dropdown");
                    $(document).xpathEvaluate("//table[contains(@class, 'table')]/tbody")
                        .append("<tr><th scope='row'>" + num + "</th><td>" + json.product + "</td><td>" + json.amount + "</td></tr>");
                }
                page.getGroupPieDataFromServer(0);
            });
            return false;
        });

        $(window).on("resize", function(event) {
            page.resize();
        });
    };

    page.resize = function() {
        var pie = $("canvas");
        var pieholder = $("div.pieholder");
        var pieLength = pieholder.width();
        if (pieLength > 400) {
            pieLength = 400
        }
        pieholder.empty().append("<canvas id='graph'></canvas>");
        pieChart = null;
        page.setPieSize();
        page.getGroupPieDataFromServer(0);
    };

    page.getPurchasesFromServer = function() {
            var requestData = {};
            if (groupName != undefined) {
                requestData = {
                    "groupName": groupName
                }
            }
            $.ajax({
                type: "GET",
                url: "/purchasesJSON",
                data: requestData,
                success: function(json){
                    page.setGroupName(json.groupName);
                    var tableEl = $("tbody");
                    var indexPurchase = 0;
                    console.log("json: " + json);
                    tableEl.empty();
                    json.purchases.reverse().forEach(function(purchase) {
                        tableEl.append("<tr>" +
                            "<th scope='row'>" + (json.purchases.length - indexPurchase++) + "</th>" +
                            "<td>" + purchase.productName + " </td>" +
                            "<td>" + purchase.amount + "</td>" +
                            "</tr>");
                    });
                    if (json.purchases.length > 0)
                        $(".dropdown div").attr("data-toggle", "dropdown");
                    page.getGroupPieDataFromServer(100);
                }
            });
        };

    page.setPieSize = function() {
        var pie = $("canvas");
        var pieLength = $("div.pieholder").width();
        if (pieLength > 400) {
            pieLength = 400
        }
        pie.attr("width", pieLength);
        pie.attr("height", pieLength);
    };

    page.setUserData = function() {
        $("h1 a").text(user.username);
    };

    return page;
}();

purchasesPage.setListeners();
purchasesPage.setUserData();
purchasesPage.setPieSize();

$(document).ready(function () {
    if (sessionStorage.groupToShow) {
        purchasesPage(sessionStorage.groupToShow);
    } else {
        purchasesPage(user.defaultGroup);
    }
});

navbarController.setPos(positionsEnum.HOME);
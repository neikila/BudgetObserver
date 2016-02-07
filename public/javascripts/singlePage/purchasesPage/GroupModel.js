/**
    * Created by neikila.
    */
define(["backbone", "purchasesCollection"], function (Backbone, PurchasesCollection) {
    return Backbone.Model.extend({
        defaults: {
            groupName: "",
            isLoaded: false,
            purchases: new PurchasesCollection([]),
            purchasesGrouped: new PurchasesCollection([])
        },

        setListeners: function (appState) {
            var self = this;
            this.listenTo(appState, 'change:groupToShow', function () {
                self.set("groupName", appState.get("groupToShow"));
                self.getFromServer();
                self.getGroupPieDataFromServer();
            });
            return this;
        },

        getFromServer: function () {
            var self = this;
            var requestData = {};
            if (self.get("groupName") != "") {
                requestData = {
                    "groupName": self.get("groupName")
                };
                $.ajax({
                    type: "GET",
                    url: "/purchasesJSON",
                    data: requestData,
                    success: function (json) {
                        var purchases = self.get("purchases");
                        purchases.reset(json.purchases);
                        self.set("purchases", purchases);
                        self.set("isLoaded", true);
                    }
                });
            }
        },

        getGroupPieDataFromServer: function() {
            var self = this;
            $.ajax({
                type: "GET",
                url: "/getGroupPieData",
                data: {
                    "groupName": self.get("groupName")
                },
                success: function(pieData){
                    self.get("purchasesGrouped").reset(pieData);
                }
            });
        }
    })
});

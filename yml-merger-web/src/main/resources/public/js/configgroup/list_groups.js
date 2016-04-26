ConfigGroupManager.module("GroupsApp.List", function(List, ConfigGroupManager,  Backbone, Marionette, $, _) {

    List.Group = Marionette.ItemView.extend({
        tagName: "tr",
        template: "#group-list-item",

        events: {
            "click button.js-delete" : "deleteClicked",
            "click button.js-show" : "showGroup"
        },

        showGroup: function(e){
            e.preventDefault();
            e.stopPropagation();
            this.trigger("group:details", this.model);
        },

        deleteClicked: function(e){
            e.preventDefault();
            e.stopPropagation();
            this.trigger("group:delete", this.model);
        }
    });

    List.Groups = Marionette.CompositeView.extend({
        tagName: "table",
        className: "table table-hover",
        template: "#group-list",
        itemView: List.Group,
        itemViewContainer: "tbody"
    });

    List.Layout = Marionette.Layout.extend({
        template: "#group-list-layout",

        regions: {
            panelRegion: "#panel-region",
            groupsRegion: "#groups-region",
            showRegion: "show-region"
        }
    });

    List.Panel = Marionette.ItemView.extend({
        template: "#group-list-panel",

        triggers: {
            "click button.js-new" : "group:new"
        }
    });

});
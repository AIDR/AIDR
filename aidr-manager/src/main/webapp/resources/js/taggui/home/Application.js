Ext.application({
    requires: [
        'Ext.container.Viewport'
    ],

    name: 'TAGGUI.home',

    controllers: [
        'TaggerHomeController'
    ],

    appFolder: BASE_URL + '/resources/js/taggui/home',

    launch: function () {
        Ext.QuickTips.init();
        Ext.create('Ext.container.Viewport', {
            cls: 'mainWraper',
            layout: 'fit',
            items: [
                {
                    xtype: 'container',
                    autoScroll: true,
                    layout: {
                        type: 'vbox',
                        align: 'center'
                    },
                    items: [
                        {
                            xtype: 'container',
                            width: '100%',
                            html: '<div class="headerWrapper">' +
                                '<div id="header" class="header"><img class="headeraidrlogo" src="' + BASE_URL + '/resources/img/AIDR/aidr_logo_240x90.png"></div>' +
                                '</div>'
                        },
                        {
                            xtype: 'tagger-home-view'
                        },
                        {
                            xtype: 'panel',
                            width: 0,
                            border: false,
                            flex: 1,
                            margin: '0 0 25 0'
                        },
                        {
                            xtype: 'aidr-footer'
                        }
                    ]
                }
            ]
        });
    }
});
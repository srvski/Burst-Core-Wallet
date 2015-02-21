/**
 * @depends {nrs.js}
 */
var NRS = (function(NRS, $, undefined) {

	NRS.updateNotificationUI = function() {
		var subTypeCount = 0;
		var totalCount = 0;

		var $menuItem = $('#notification_menu');
		var $popoverItem = $("<div id='notification_popover'></div>");
		
		$.each(NRS.transactionTypes, function(typeIndex, typeDict) {
			$.each(typeDict["subTypes"], function(subTypeIndex, subTypeDict) {
				if (subTypeDict["notificationCount"] > 0) {
					subTypeCount += 1;
					totalCount += subTypeDict["notificationCount"];
					var html = "";
					html += "<a href='#' style='display:block;background-color:#f0f0f0;border:1px solid #e2e2e2;padding:4px 12px 9px 12px;margin:2px;'>";
					html += "<div style='float:right;'><div style='display:inline-block;margin-top:2px;'>";
					html += "<span class='badge' style='background-color:#e65;'>" + subTypeDict["notificationCount"] + "</span>";
					html += "</div></div>";
					html += NRS.getTransactionIconHTML(typeIndex, subTypeIndex) + "&nbsp; ";
					html += '<span style="font-size:12px;color:#000;display:inline-block;margin-top:5px;">';
					html += $.t(subTypeDict['i18nKeyTitle'], subTypeDict['title']);
					html += '</span>';
					html += "</a>";

					var $subTypeItem = $(html);
					$subTypeItem.click(function(e) {
						e.preventDefault();
						NRS.goToPage(subTypeDict["receiverPage"]);
						$menuItem.popover('hide');
					});
					$subTypeItem.appendTo($popoverItem);
				}
			});
		});
		if (totalCount > 0) {
			$menuItem.find('span.nm_badge').css('backgroundColor', '#e65');

			var $markReadDiv = $("<div style='text-align:center;padding:12px 12px 8px 12px;'></div>");
			var $markReadLink= $("<a href='#' style='color:#3c8dbc;'>" + $.t('notifications_mark_as_read', 'Mark all as read') + "</a>");
			$markReadLink.click(function(e) {
				e.preventDefault();
				NRS.resetNotificationState();
				$menuItem.popover('hide');
			});
			$markReadLink.appendTo($markReadDiv);
			$popoverItem.append($markReadDiv);
		} else {
			$menuItem.find('span.nm_badge').css('backgroundColor', '#a6a6a6');
			var html = "";
			html += "<div style='text-align:center;padding:12px;'>" + $.t('no_notifications', 'No current notifications') + "</div>";
			$popoverItem.append(html);
		}

		$menuItem.find('span.nm_badge').html(String(totalCount));
		$menuItem.show();

		var template = '<div class="popover" style="min-width:350px;"><div class="arrow"></div><div class="popover-inner">';
		template += '<h3 class="popover-title"></h3><div class="popover-content"><p></p></div></div></div>';

		if($menuItem.data('bs.popover')) {
    		$menuItem.data('bs.popover').options.content = $popoverItem;
		} else {
			$menuItem.popover({
				"html": true,
				"content": $popoverItem,
				"trigger": "click",
				template: template
			});
		}
	}

	NRS.saveNotificationTimestamps = function() {
		var cookieDict = {};
		$.each(NRS.transactionTypes, function(typeIndex, typeDict) {
			$.each(typeDict["subTypes"], function(subTypeIndex, subTypeDict) {
				var cookieKey = "ts_" + String(typeIndex) + "_" + String(subTypeIndex);
				cookieDict[cookieKey] = subTypeDict["notificationTS"];
			});
		});
		NRS.setCookie("notification_timestamps", JSON.stringify(cookieDict), 100);
	}

	NRS.initNotificationCounts = function(time) {
		var fromTS = time - 60 * 60 * 24 * 14;
		NRS.sendRequest("getAccountTransactions+", {
			"account": NRS.account,
			"timestamp": fromTS,
			"firstIndex": 0,
			"lastIndex": 99
		}, function(response) {
			if (response.transactions && response.transactions.length) {
				for (var i=0; i<response.transactions.length; i++) {
					var t = response.transactions[i];
					var subTypeDict = NRS.transactionTypes[t.type]["subTypes"][t.subtype];
					if (t.recipient && t.recipient == NRS.account && subTypeDict["receiverPage"] && t.timestamp > subTypeDict["notificationTS"]) {
						NRS.transactionTypes[t.type]["notificationCount"] += 1;
						subTypeDict["notificationCount"] += 1;
					}
				}
			}
			NRS.updateNotificationUI();
		});
	}

	NRS.resetNotificationState = function(page) {
		NRS.sendRequest("getTime", function(response) {
			if (response.time) {
				$.each(NRS.transactionTypes, function(typeIndex, typeDict) {
					$.each(typeDict["subTypes"], function(subTypeIndex, subTypeDict) {
						if (!page || (page && subTypeDict["receiverPage"] == page)) {
							var countBefore = subTypeDict["notificationCount"];
							subTypeDict["notificationTS"] = response.time;
							subTypeDict["notificationCount"] = 0;
							typeDict["notificationCount"] -= countBefore;
						}
					});
				});
				NRS.saveNotificationTimestamps();
				NRS.updateNotificationUI();
			}
		});
	}

	NRS.updateNotifications = function() {
		var cookie = NRS.getCookie("notification_timestamps");
		if (cookie) {
			var cookieDict = JSON.parse(cookie);
		} else {
			var cookieDict = {};
		}

		NRS.sendRequest("getTime", function(response) {
			if (response.time) {
				$.each(NRS.transactionTypes, function(typeIndex, typeDict) {
					typeDict["notificationCount"] = 0;
					$.each(typeDict["subTypes"], function(subTypeIndex, subTypeDict) {
						var cookieKey = "ts_" + String(typeIndex) + "_" + String(subTypeIndex);
						if (cookieDict[cookieKey]) {
							subTypeDict["notificationTS"] = cookieDict[cookieKey];
						} else {
							subTypeDict["notificationTS"] = response.time;
						}
						subTypeDict["notificationCount"] = 0;
					});
				});
				NRS.initNotificationCounts(response.time);
				NRS.saveNotificationTimestamps();
			}
		});
	}


	return NRS;
}(NRS || {}, jQuery));
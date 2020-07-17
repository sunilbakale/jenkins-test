
$(document).ready(function(){
	$('#new_event_end_date').datepicker({
		onSelect: function () {
			var newEvntStartDate = $('#new_event_start_date').val();
			var newEvntEndDate = $('#new_event_end_date').val();
			// $('#new_event_end_date').val(newEvntStartDate);
			if( newEvntStartDate <= newEvntEndDate ){
				$('#new_event_end_date,#new_event_start_date').css({'background-color': 'white', 'color': 'black'});
				$('#new_event_end_time,#new_event_start_time').css({'background-color': 'white', 'color': 'black'});
				$('#errMsg').text("");
				$('#create-event-button').prop('disabled', false);
			} else {
				$('#new_event_end_date').css({'background-color': 'red', 'color': 'white'});
				$('#new_event_end_time').css({'background-color': 'red', 'color': 'white'});
				$('#errMsg').text("Check Start and End Date&Time");
				$('#create-event-button').prop('disabled', true);
			}
		}
	});
	$('#update_event_end_date').datepicker({
		onSelect: function () {
			var newEvntStartDate = $('#update_event_start_date').val();
			var newEvntEndDate = $('#update_event_end_date').val();
			// $('#new_event_end_date').val(newEvntStartDate);
			if( newEvntStartDate <= newEvntEndDate ){
				$('#update_event_end_date,#update_event_start_date').css({'background-color': 'white', 'color': 'black'});
				$('#updateEvtEndTime,#updateEvtStatTime').css({'background-color': 'white', 'color': 'black'});
				$('.errMsg').text("");
				$('#update-event-button').prop('disabled', false);
			} else {
				$('#update_event_end_date').css({'background-color': 'red', 'color': 'white'});
				$('#updateEvtEndTime').css({'background-color': 'red', 'color': 'white'});
				$('.errMsg').text("Check Start and End Date&Time");
				$('#update-event-button').prop('disabled', true);

				$('#updateEvt').show();
				$('#updateRecEvt').hide();
				$('#allEvtUpdate').prop('checked',true);
			}
		}
	});
	$('#new_event_start_date').datepicker({
		onSelect: function () {
			var newEvntStartDate = $('#new_event_start_date').val();
			$('#new_event_end_date').val(newEvntStartDate);


			//To change Monthly onMonthDay val
			var onMonthDayVal = new Date($('#new_event_start_date').val()).getDate();
			$('#onMonthDay').val(onMonthDayVal);
		}
	});

	$('#update_event_start_date').datepicker({
		onSelect: function () {
			var newEvntStartDate = $('#update_event_start_date').val();
			$('#update_event_end_date').val(newEvntStartDate);
			var onMonthDayVal = new Date($('#update_event_start_date').val()).getDate();
			$('#onMonthDay_edit').val(onMonthDayVal);
			$('#updateEvt').show();
			$('#updateRecEvt').hide();
			$('#startDateTimeFromDb').val(new Date(newEvntStartDate).getTime()).trigger('change');
			$('#endDateTimeFromDb').val(new Date(newEvntStartDate).getTime()).trigger('change');
			$('#allEvtUpdate').prop('checked',true);
		}
	});

$('#modal-view-event').on('shown.bs.modal',function () {
		var inputToEventDetail = {};
		var inputEventId = $('.eventId').val();
		if(inputEventId.includes('#')){
			var eventId = inputEventId.split('#')[0];
			inputToEventDetail.inputEventId = eventId;
		}else {
			inputToEventDetail.inputEventId = inputEventId;
		}
		var fetchCalendarEventAjaxById = getCompleteUrl("fetchCalendarEventAjaxById");
		$.ajax({
			type: "GET",
			data: inputToEventDetail,
			url: fetchCalendarEventAjaxById,
			success: function(response) {
				$('#startDateTimeFromDb').val(response.startDateTime);
				$('#endDateTimeFromDb').val(response.endDateTime);
			},
			error:function(){
			},
			dataType: "json",
		});
	})
	$('#new_event_start_time').on('changeTime', function() {

		var startTimeChange = $('#new_event_start_time').val();
		var format24 = convertTimeFrom12To24(startTimeChange);
		var changeTime = new Date(),
			hours = parseInt(format24.split(':')[0]),
			minutes = parseInt(format24.split(':')[1]);
		changeTime.setHours(hours);
		changeTime.setMinutes(minutes);
		var changedHours = changeTime.getHours()+1;
		changeTime.setHours(changedHours)
		changedHours = changedHours % 12;
		changedHours = changedHours ? changedHours : 12;
		var changeAmOrPm = (changeTime.getHours() < 12) ? "AM" : "PM";

		var changeMin =changeTime.getMinutes();
		changeMin = changeMin > 9 ? changeMin : '0' + changeMin;
		var timeDisp = changedHours+":"+changeMin+" "+changeAmOrPm;
		$('#new_event_end_time').val(timeDisp);

	});

	$('#updateEvtStatTime').on('changeTime', function() {

		var startTimeChange = $('#updateEvtStatTime').val();
		var format24 = convertTimeFrom12To24(startTimeChange);
		var changeTime = new Date(),
			hours = parseInt(format24.split(':')[0]),
			minutes = parseInt(format24.split(':')[1]);
		changeTime.setHours(hours);
		changeTime.setMinutes(minutes);
		var changedHours = changeTime.getHours()+1;
		changeTime.setHours(changedHours);
		changedHours = changedHours % 12;
		changedHours = changedHours ? changedHours : 12;
		var changeAmOrPm = (changeTime.getHours() < 12) ? "AM" : "PM";
		var changeMin =changeTime.getMinutes();
		changeMin = changeMin > 9 ? changeMin : '0' + changeMin;
		var timeDisp = changedHours+":"+changeMin+" "+changeAmOrPm;
		$('#updateEvtEndTime').val(timeDisp);
		$('#updateEvt').show();
		$('#updateRecEvt').hide();
		$('#allEvtUpdate').prop('checked',true);
	});

	/*multiple dropdown*/
	$('#studentSelectedList').fastselect({placeholder:'Choose Student'});

	$('#modal-view-event-add').on('hidden.bs.modal', function () {
		$('#add-event-form')[0].reset();
		$('#add-event-form').trigger('reset');
		$('#studentSelectedList').data('fastselect').destroy();
		$('#studentSelectedList').fastselect({placeholder:'Choose Student'});
		$('#repeat_select').prop('selectedIndex', 0).trigger('change');
		});

	$('#modal-view-event').on('hidden.bs.modal', function () {
		$('#edit-event-form')[0].reset();
		$('#edit-event-form').trigger('reset');
		$('#studentSelectedDropDown').data('fastselect').destroy();
		$('#edit-event-repeat-select').prop('selectedIndex', 0).trigger('change');
		$('#delRecEvt').trigger('change');
		$('#updateEvnt').trigger('change');
	});

	/*dropdown-on select to change content*/
	$('#repeat_select').on('change', function() {
		var target = $(this).data('target');
		var show = $("option:selected", this).data('show');
		$(target).children().addClass('hide');
		$(show).removeClass('hide');

		/*value for monthly repeat select*/
		var onMonthDayVal = new Date($('#new_event_start_date').val()).getDate();
		$('#onMonthDay').val(onMonthDayVal);

		$('.untilSeletr').prop('selectedIndex',0).trigger('change');
	}).trigger('change');

	$('.untilSeletr').on('change', function() {
		var target = $(this).data('target');
		var show = $("option:selected", this).data('show');
		$(target).children().addClass('hide');
		$(show).removeClass('hide');
	}).trigger('change');

	$('#edit-event-repeat-select').on('change', function() {
		var target = $(this).data('target');
		var show = $("option:selected", this).data('show');
		$(target).children().addClass('hide');
		$(show).removeClass('hide');

		$('.untilSeletr').prop('selectedIndex',0).trigger('change');
	}).trigger('change');

	jQuery("#create-event-button").click(function(){
		$('#studentSelectedList').fastselect({placeholder:'Choose Student'});
		var newEventInfo = {};
		$.each($('#add-event-form').serializeArray(), function(i, field) {
			newEventInfo[field.name] = field.value;
		});
		var ename = $('#createEvtName').val();
		if (ename === '' || $('#edesc').val()==''){
			$('#errMsg').text("* Check input fields")
		}else {
			var startDateTimePicker = $("#new_event_start_date").val();
			var startDate = new Date(startDateTimePicker).toDateString();
			var startTime = $('#new_event_start_time').val();
			var combStartDateTime = new Date(startDate + ' ' + startTime);
			var endDateTimePicker = $("#new_event_end_date").val();
			var endDate = new Date(endDateTimePicker).toDateString();
			var endTime = $('#new_event_end_time').val();
			var combEndDateTime =new Date(endDate + ' ' + endTime);
			newEventInfo.endDate = combEndDateTime.getTime();

			var studentSelectedValues = $('#studentSelectedList').val();

			var classType = $('.active_status:checked').val();

			var eventLoc = "ONLINE";
			var isPrivateEvent;
			if ($('#evtPrivacy').is(":checked")) {
				isPrivateEvent = "Y";
			} else {
				isPrivateEvent = "N";
			}
			var startDateVal = new Date($("#new_event_start_date").val()).getTime();
			newEventInfo.startDateVal = startDateVal;

			// var recEndVal = $('.untilSeletr').children("option:selected").val();
			var recurringType = $('#repeat_select').children("option:selected").val();
			switch (recurringType) {
				case "NEVER":
					var isRecurringEvent = "N";
					newEventInfo.isRecurringEvent = isRecurringEvent;
					break;
				case "DAILY":
					isRecurringEvent = "Y";
					newEventInfo.isRecurringEvent = isRecurringEvent;
					recurringType = "DAILY";
					newEventInfo.recurringType = recurringType;
					var until = $('#untilSeletr_daily').children("option:selected").val();
					console.log(until)

					if(until === 'FOR_EVER'){
						newEventInfo.until = until;
						var endDateRecurring = new Date("01/12/2030");
						newEventInfo.recEvtEndDate = endDateRecurring.getTime();
					}else if (until === 'OCCURRENCE'){
						var recOcur = $('#daily_ocr').val();
						newEventInfo.recOcur = recOcur;
						newEventInfo.until = until;

						var recEvtEndDate = new Date("01/12/2030");
						newEventInfo.recEvtEndDate = recEvtEndDate.getTime();
					}else {
						newEventInfo.until = until;
						var recSpecDate = new Date($('#daily_TDate').val());
						recSpecDate.setHours(23);
						newEventInfo.recSpecDate = recSpecDate.getTime();
						var recEvtEndDate = recSpecDate;
						newEventInfo.recEvtEndDate = recEvtEndDate.getTime();
					}
					break;
				case "WEEKLY":
					isRecurringEvent = "Y";
					newEventInfo.isRecurringEvent = isRecurringEvent;
					recurringType = 'WEEKLY';
					newEventInfo.recurringType = recurringType;
					var onSunday;
					if($('#weekday-sun').is(":checked")){
						onSunday="Y"
						newEventInfo.onSunday = onSunday;
					}else {
						onSunday="N"
						newEventInfo.onSunday = onSunday;
					}
					var onMonday;
					if($('#weekday-mon').is(":checked")){
						onMonday="Y"
						newEventInfo.onMonday = onMonday;
					}else {
						onMonday="N"
						newEventInfo.onMonday = onMonday;
					}
					var onTuesday;
					if($('#weekday-tue').is(":checked")){
						onTuesday="Y"
						newEventInfo.onTuesday = onTuesday;
					}else {
						onTuesday="N"
						newEventInfo.onTuesday = onTuesday;
					}
					var onWednesday;
					if($('#weekday-wed').is(":checked")){
						onWednesday="Y"
						newEventInfo.onWednesday = onWednesday;
					}else {
						onWednesday="N"
						newEventInfo.onWednesday = onWednesday;
					}
					var onThursday;
					if($('#weekday-thu').is(":checked")){
						onThursday="Y"
						newEventInfo.onThursday = onThursday;
					}else {
						onThursday="N"
						newEventInfo.onThursday = onThursday;
					}
					var onFriday;
					if($('#weekday-fri').is(":checked")){
						onFriday="Y"
						newEventInfo.onFriday = onFriday;
					}else {
						onFriday="N"
						newEventInfo.onFriday = onFriday;
					}
					var onSaturday;
					if($('#weekday-sat').is(":checked")){
						onSaturday="Y"
						newEventInfo.onSaturday = onSaturday;
					}else {
						onSaturday="N"
						newEventInfo.onSaturday = onSaturday;
					}
					var until = $('#untilSeletr_weekly').children("option:selected").val();
					if(until === 'FOR_EVER'){
						newEventInfo.until = until;
						var endDateRecurring = new Date("01/12/2030");
						newEventInfo.recEvtEndDate = endDateRecurring.getTime();

					}else if (until === 'OCCURRENCE'){
						var recOcur = $('#weekly_ocr').val();
						newEventInfo.recOcur = recOcur;
						newEventInfo.until = until;

						var recEvtEndDate = new Date("01/12/2030");
						newEventInfo.recEvtEndDate = recEvtEndDate.getTime();
					}else {
						newEventInfo.until = until;
						var recSpecDate = new Date($('#weekly_TDate').val());
						recSpecDate.setHours(23);
						newEventInfo.recSpecDate = recSpecDate.getTime();

						var recEvtEndDate = recSpecDate;
						newEventInfo.recEvtEndDate = recEvtEndDate.getTime();
					}
					break;
				case "MONTHLY":
					console.log("MONTHLY")
					isRecurringEvent = "Y";
					newEventInfo.isRecurringEvent = isRecurringEvent;
					recurringType = 'MONTHLY';
					newEventInfo.recurringType = recurringType;
					var onMonthDay= $('#onMonthDay').val();
					newEventInfo.onMonthDay = onMonthDay;
					var until = $('#untilSeletr_monthly').children("option:selected").val();
					if(until === 'FOR_EVER'){
						newEventInfo.until = until;
						var endDateRecurring = new Date("01/12/2030");
						newEventInfo.recEvtEndDate = endDateRecurring.getTime();
					}else if (until === 'OCCURRENCE'){
						var recOcur = $('#monthly_ocr').val();
						newEventInfo.recOcur = recOcur;
						newEventInfo.until = until;

						var recEvtEndDate = new Date("01/12/2030");
						newEventInfo.recEvtEndDate = recEvtEndDate.getTime();
					}else {
						newEventInfo.until = until;
						var recSpecDate = new Date($('#monthly_TDate').val());
						recSpecDate.setHours(23);
						newEventInfo.recSpecDate = recSpecDate.getTime();

						var recEvtEndDate = recSpecDate;
						newEventInfo.recEvtEndDate = recEvtEndDate.getTime();
					}
					break;
			}
				var edesc = $('#edesc').val();
				newEventInfo.edesc = edesc;

			// var endDateVal = new Date($('#new_event_end_date').val()).getTime();
			// newEventInfo.endDateVal = endDateVal;

				$('#errMsg').text("")
				newEventInfo.ename = ename;
				newEventInfo.startDate = combStartDateTime.getTime();

				newEventInfo.studentIds = studentSelectedValues;
				newEventInfo.classType = classType;
				newEventInfo.eventLoc = eventLoc;
				newEventInfo.isPrivateEvent = isPrivateEvent;
				newEventInfo.isRecurringEvent = isRecurringEvent;
				var createEventUrl = getCompleteUrl("createCalendarEvent");
			}
		$.ajax({
			type: "POST",
			url: createEventUrl,
			data: newEventInfo,
			success: function(response) {
				if( response.STATUS == "SUCCESS"){
					$('#modal-view-event-add').modal('hide');
					$('#calendar').fullCalendar('refetchEvents');
					bs4pop.notice('New Calendar Event Added',{
						type: 'success',
						position: 'topright',
						appendType: 'append',
						autoClose: 5000,
						width:6000
					})
				}else {
					bs4pop.notice('Unable to add event');
				}
			},
			error:function(response){
			},
			dataType: "json",
		});
	});

	jQuery("#update-event-button").click(function(){

		var updateEventInfo = {};
		$.each($('#add-event-form').serializeArray(), function(i, field) {
			updateEventInfo[field.name] = field.value;
		});
		var eventName = $('#eName').val();
		if(eventName === ''){
			$('.errMsg').text("* Fill the Title field")
		}else {
			$('.errMsg').text("")
			updateEventInfo.eventName = eventName;

			var inputEventId = $('#event_no').val();
			if (inputEventId.includes("#")) {

				var updateEvtType = $("input[name='updateEvt']:checked").val();
				console.log(updateEvtType)
				switch (updateEvtType) {
					case "CURRENT_EVT" :
						updateEventInfo.eventId = inputEventId;
						console.log("current event"+inputEventId)

						break;
					case "ALL_EVT" :
						var arrayEvtId = [];
						var eventId ;
						arrayEvtId = inputEventId.split('#');
						eventId = arrayEvtId[0];
						updateEventInfo.eventId = eventId;
						console.log("All event"+eventId)
						break;
				}
			} else{
				updateEventInfo.eventId = inputEventId;
			}

			var isPrivateEvent;
			if ($('#editEvtPrivacy').is(":checked")) {
				isPrivateEvent = "Y";
			} else {
				isPrivateEvent = "N";
			}
			updateEventInfo.isPrivateEvent = isPrivateEvent;

			if(updateEvtType === 'CURRENT_EVT'){
				var startDateTimePicker = $("#update_event_start_date").val();
				var startDate = new Date(startDateTimePicker).toDateString();
				var startTime = $('#updateEvtStatTime').val();
				var UpdateStartDateTime = new Date(startDate + ' ' + startTime);

				updateEventInfo.updateStartDateTime = UpdateStartDateTime.getTime();

				var endDateTimePicker = $("#update_event_end_date").val();
				var endDate = new Date(endDateTimePicker).toDateString();
				var endTime = $('#updateEvtEndTime').val();
				var UpdateEndDateTime = new Date(endDate + ' ' + endTime);
				updateEventInfo.updateEndDateTime = UpdateEndDateTime.getTime();
			}else {
				var startDateTimePicker = new Date(parseInt($('#startDateTimeFromDb').val()));
				var startDate = new Date(startDateTimePicker).toDateString();
				var startTime = $('#updateEvtStatTime').val();
				var UpdateStartDateTime = new Date(startDate + ' ' + startTime);

				updateEventInfo.updateStartDateTime = UpdateStartDateTime.getTime();

				var endDateTimePicker = new Date(parseInt($('#endDateTimeFromDb').val()))
				var endDate = new Date(endDateTimePicker).toDateString();
				var endTime = $('#updateEvtEndTime').val();
				var UpdateEndDateTime = new Date(endDate + ' ' + endTime);
				updateEventInfo.updateEndDateTime = UpdateEndDateTime.getTime();
			}


			var studentSelectedValues = $('#studentSelectedDropDown').val();
			updateEventInfo.studentIds = studentSelectedValues;

			var classType = $('.active_status:checked').val();
			updateEventInfo.classType = classType;

			var updateEventDesc = $('#updateEventDesc').val();
			updateEventInfo.updateEventDesc = updateEventDesc;

			var startDateVal = UpdateStartDateTime.getTime();
			updateEventInfo.startDateVal = startDateVal;

			var recurringType = $('#edit-event-repeat-select').children("option:selected").val();
			switch (recurringType) {
				case "NEVER":
					var isRecurringEvent = "N";

					updateEventInfo.isRecurringEvent = isRecurringEvent;
					break;
				case "DAILY":
					isRecurringEvent = "Y";
					updateEventInfo.isRecurringEvent = isRecurringEvent;
					recurringType = "DAILY";
					updateEventInfo.recurringType = recurringType;
					var until = $('#editUntilSel_daily').children("option:selected").val();
					console.log(until)

					if(until === 'FOR_EVER'){
						updateEventInfo.until = until;
						var endDateRecurring = new Date("01/12/2030");
						updateEventInfo.recEvtEndDate = endDateRecurring.getTime();
					}else if (until === 'OCCURRENCE'){
						var recOcur = $('#daily_ocr_edit').val();
						updateEventInfo.recOcur = recOcur;
						updateEventInfo.until = until;

						var recEvtEndDate = new Date("01/12/2030");
						updateEventInfo.recEvtEndDate = recEvtEndDate.getTime();
					}else {
						updateEventInfo.until = until;
						var recSpecDate = new Date($('#daily_TDateEdit').val());
						recSpecDate.setHours(23);
						updateEventInfo.recSpecDate = recSpecDate.getTime();
						var recEvtEndDate = recSpecDate;
						updateEventInfo.recEvtEndDate = recEvtEndDate.getTime();
					}
					break;
				case "WEEKLY":
					isRecurringEvent = "Y";
					updateEventInfo.isRecurringEvent = isRecurringEvent;
					recurringType = 'WEEKLY';
					updateEventInfo.recurringType = recurringType;

					var onSunday;
					if($('#edit-weekday-sun').is(":checked")){
						onSunday="Y"
						updateEventInfo.onSunday = onSunday;
					}else {
						onSunday="N"
						updateEventInfo.onSunday = onSunday;
					}
					var onMonday;
					if($('#edit-weekday-mon').is(":checked")){
						onMonday="Y"
						updateEventInfo.onMonday = onMonday;
					}else {
						onMonday="N"
						updateEventInfo.onMonday = onMonday;
					}
					var onTuesday;
					if($('#edit-weekday-tue').is(":checked")){
						onTuesday="Y"
						updateEventInfo.onTuesday = onTuesday;
					}else {
						onTuesday="N"
						updateEventInfo.onTuesday = onTuesday;
					}
					var onWednesday;
					if($('#edit-weekday-wed').is(":checked")){
						onWednesday="Y"
						updateEventInfo.onWednesday = onWednesday;
					}else {
						onWednesday="N"
						updateEventInfo.onWednesday = onWednesday;
					}
					var onThursday;
					if($('#edit-weekday-thu').is(":checked")){
						onThursday="Y"
						updateEventInfo.onThursday = onThursday;
					}else {
						onThursday="N"
						updateEventInfo.onThursday = onThursday;
					}
					var onFriday;
					if($('#edit-weekday-fri').is(":checked")){
						onFriday="Y"
						updateEventInfo.onFriday = onFriday;
					}else {
						onFriday="N"
						updateEventInfo.onFriday = onFriday;
					}
					var onSaturday;
					if($('#edit-weekday-sat').is(":checked")){
						onSaturday="Y"
						updateEventInfo.onSaturday = onSaturday;
					}else {
						onSaturday="N"
						updateEventInfo.onSaturday = onSaturday;
					}
					var until = $('#editUntilSel_weekly').children("option:selected").val();
					if(until === 'FOR_EVER'){
						updateEventInfo.until = until;
						var endDateRecurring = new Date("01/12/2030");
						updateEventInfo.recEvtEndDate = endDateRecurring.getTime();
					}else if (until === 'OCCURRENCE'){
						var recOcur = $('#weekly_ocr_edit').val();
						updateEventInfo.recOcur = recOcur;
						updateEventInfo.until = until;

						var recEvtEndDate = new Date("01/12/2030");
						updateEventInfo.recEvtEndDate = recEvtEndDate.getTime();
					}else {
						updateEventInfo.until = until;
						var recSpecDate = new Date($('#weekly_TDateEdit').val());
						recSpecDate.setHours(23);
						updateEventInfo.recSpecDate = recSpecDate.getTime();

						var recEvtEndDate = recSpecDate;
						updateEventInfo.recEvtEndDate = recEvtEndDate.getTime();
					}
					break;
				case "MONTHLY":
					console.log("MONTHLY")
					isRecurringEvent = "Y";
					updateEventInfo.isRecurringEvent = isRecurringEvent;
					recurringType = 'MONTHLY';
					updateEventInfo.recurringType = recurringType;
					var onMonthDay= $('#onMonthDay_edit').val();
					updateEventInfo.onMonthDay = onMonthDay;
					var until = $('#editUntilSel_monthly').children("option:selected").val();
					if(until === 'FOR_EVER'){
						updateEventInfo.until = until;
						var endDateRecurring = new Date("01/12/2030");
						updateEventInfo.recEvtEndDate = endDateRecurring.getTime();
					}else if (until === 'OCCURRENCE'){
						var recOcur = $('#monthly_ocr_edit').val();
						updateEventInfo.recOcur = recOcur;
						updateEventInfo.until = until;

						var recEvtEndDate = new Date("01/12/2030");
						updateEventInfo.recEvtEndDate = recEvtEndDate.getTime();
					}else {
						updateEventInfo.until = until;
						var recSpecDate = new Date($('#monthly_TDateEdit').val());
						recSpecDate.setHours(23);
						updateEventInfo.recSpecDate = recSpecDate.getTime();

						var recEvtEndDate = recSpecDate;
						updateEventInfo.recEvtEndDate = recEvtEndDate.getTime();
					}
					break;
			}

			var updateEventUrl = getCompleteUrl("updateCalendarEvent");
		}
		console.log("Invoking update event backend...");
		console.log(updateEventInfo);

		$.ajax({
			type: "POST",
			url: updateEventUrl,
			data: updateEventInfo,
			success: function(response) {
				if(response.STATUS === 'SUCCESS')
				{
					$('#modal-view-event').modal('hide');
				$('#calendar').fullCalendar('refetchEvents');
				console.log(response)
			}else {
					$('#modal-view-event').modal('hide');
					$('#calendar').fullCalendar('refetchEvents');
					console.log(response)
				}},
			error:function(response){
			},
			dataType: "json",
		});
	});

	$('#del_event_btn').click(function () {
		var inputDeleteEvent = {};
		var inputEventId = $('#del_event').val();
		if(inputEventId.includes('#')){
			var delType = $("input[name='delEvt']:checked").val();
			switch (delType) {
				case "CURRENT_EVT":
					inputDeleteEvent.del_eventId = inputEventId;
					break;
				case "ALL_EVT":
					var evtArray = [];
					evtArray = inputEventId.split('#');
					var evtId = evtArray[0];
					inputDeleteEvent.del_eventId = evtId;
					break;
				default:
					break;
			}
		}else
		{
			inputDeleteEvent.del_eventId = inputEventId;
		}
		var delEventUrl = getCompleteUrl("deleteEvent");

		$.ajax({
			type: "POST",
			data: inputDeleteEvent,
			url: delEventUrl,
			success: function(response) {
				if( response.STATUS == "SUCCESS"){
					$('#modal-view-event').modal('hide');
					$('#calendar').fullCalendar('refetchEvents');
					bs4pop.notice('Event Deleted Successful',{
						type: 'success',
						position: 'topright',
						appendType: 'append',
						autoClose: 5000,
						width:6000
					})
				}else {
					bs4pop.notice('Unable to Delete event');
				}
			},
			error:function(response){
			},
			dataType: "json",
		});
	});

});

function roundQuarter() {
	var roundMin = new Date();
	if (roundMin.getMinutes() > 0 & roundMin.getMinutes() < 15){
		roundMin.setMinutes(15);}
	else if (roundMin.getMinutes() > 15 & roundMin.getMinutes() <30){roundMin.setMinutes(30);
	} else if(roundMin.getMinutes() > 30 & roundMin.getMinutes() < 45){roundMin.setMinutes(45);}
	else {roundMin.setMinutes(59 + 1);}
	return roundMin;
}
function monthDetails(inputMonth) {
	var monthDetails = inputMonth + 1;
	return monthDetails < 10 ? '0' + monthDetails : '' + monthDetails;
}
function hourDetails(inputHour){
	var hour = inputHour.getHours();
	hour = hour % 12;
	hour = hour ? hour : 12;
	return hour;
}
function minuteDetails(inputMin){
	var min = inputMin.getMinutes();
	return min > 9 ? min : '0' + min;
}
function amOrPmDetails(inputMin){
	var amOrPm = inputMin.getHours();
	return (amOrPm < 12) ? "AM" : "PM";
}
function timeDetails (input) {
	var hour = input.getHours();
	hour = hour % 12 ;
	hour = hour ? hour : 12;
	var min = input.getMinutes();
	min = min > 9 ? min : '0' + min;
	return hour+":"+ min;
}
function convertTimeFrom12To24(timeStr) {
	var colon = timeStr.indexOf(':');
	var hours = timeStr.substr(0, colon),
		minutes = timeStr.substr(colon+1, 2),
		meridian = timeStr.substr(colon+4, 2).toUpperCase();


	var hoursInt = parseInt(hours, 10),
		offset = meridian == 'PM' ? 12 : 0;

	if (hoursInt === 12) {
		hoursInt = offset;
	} else {
		hoursInt += offset;
	}
	return hoursInt + ":" + minutes;
}

(function () {
	'use strict';
	// ------------------------------------------------------- //
	// Calendar
	// ------------------------------------------------------ //
	jQuery(function() {
		// page is ready
		var curDate = new Date();
		var hours = curDate.getHours()-1 +":"+curDate.getMinutes();
		jQuery('#calendar').fullCalendar({
			timezone: 'local',
			nowIndicator: true,
			now: curDate,
			themeSystem: 'bootstrap4',
			// emphasizes business hours
			businessHours: false,
			defaultView: 'agendaDay',
			timeFormat: 'hh:mm a',
			height:660,
			width:500,
			fixedWeekCount:false,
			eventBorderColor: 'inherit',
			scrollTime: hours,
			allDay: false,
			eventBackgroundColor: '#f8f9fa',
			// event dragging & resizing
			editable: true,
			// header
			header: {
				left: 'title',
				center: 'month,agendaWeek,agendaDay',
				right: 'today prev,next'
			},
			events: '/portal/c/fetchCalendarEventsAjax',
			// events: [
			// 	{
			// 		title: 'Barber',
			// 		description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras eu pellentesque nibh. In nisl nulla, convallis ac nulla eget, pellentesque pellentesque magna.',
			// 		start: '2020-02-07',
			// 		end: '2020-02-07',
			// 		className: 'fc-bg-default',
			// 		icon : "circle"
			// 	},
			// 	{
			// 		title: 'Flight Paris',
			// 		description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras eu pellentesque nibh. In nisl nulla, convallis ac nulla eget, pellentesque pellentesque magna.',
			// 		start: '2020-08-08T14:00:00',
			// 		end: '2020-08-08T20:00:00',
			// 		className: 'fc-bg-deepskyblue',
			// 		icon : "cog",
			// 		allDay: false
			// 	},
			// 	{
			// 		title: 'Team Meeting',
			// 		description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras eu pellentesque nibh. In nisl nulla, convallis ac nulla eget, pellentesque pellentesque magna.',
			// 		start: '2020-02-10T13:00:00',
			// 		end: '2020-02-10T16:00:00',
			// 		className: 'fc-bg-pinkred',
			// 		icon : "group",
			// 		allDay: false
			// 	},
			// 	{
			// 		title: 'Meeting',
			// 		description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras eu pellentesque nibh. In nisl nulla, convallis ac nulla eget, pellentesque pellentesque magna.',
			// 		start: '2020-02-12',
			// 		className: 'fc-bg-lightgreen',
			// 		icon : "suitcase"
			// 	},
			// 	{
			// 		title: 'Conference',
			// 		description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras eu pellentesque nibh. In nisl nulla, convallis ac nulla eget, pellentesque pellentesque magna.',
			// 		start: '2020-02-13',
			// 		end: '2020-02-15',
			// 		className: 'fc-bg-blue',
			// 		icon : "calendar"
			// 	},
			// 	{
			// 		title: 'Baby Shower',
			// 		description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras eu pellentesque nibh. In nisl nulla, convallis ac nulla eget, pellentesque pellentesque magna.',
			// 		start: '2020-08-13',
			// 		end: '2020-08-14',
			// 		className: 'fc-bg-default',
			// 		icon : "child"
			// 	},
			// 	{
			// 		title: 'Birthday',
			// 		description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras eu pellentesque nibh. In nisl nulla, convallis ac nulla eget, pellentesque pellentesque magna.',
			// 		start: '2020-09-13',
			// 		end: '2020-09-14',
			// 		className: 'fc-bg-default',
			// 		icon : "birthday-cake"
			// 	},
			// 	{
			// 		title: 'Restaurant',
			// 		description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras eu pellentesque nibh. In nisl nulla, convallis ac nulla eget, pellentesque pellentesque magna.',
			// 		start: '2020-10-15T09:30:00',
			// 		end: '2020-10-15T11:45:00',
			// 		className: 'fc-bg-default',
			// 		icon : "glass",
			// 		allDay: false
			// 	},
			// 	{
			// 		title: 'Dinner',
			// 		description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras eu pellentesque nibh. In nisl nulla, convallis ac nulla eget, pellentesque pellentesque magna.',
			// 		start: '2020-11-15T20:00:00',
			// 		end: '2020-11-15T22:30:00',
			// 		className: 'fc-bg-default',
			// 		icon : "cutlery",
			// 		allDay: false
			// 	},
			// 	{
			// 		title: 'Shooting',
			// 		description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras eu pellentesque nibh. In nisl nulla, convallis ac nulla eget, pellentesque pellentesque magna.',
			// 		start: '2020-02-25',
			// 		end: '2020-02-25',
			// 		className: 'fc-bg-blue',
			// 		icon : "camera"
			// 	},
			// 	{
			// 		title: 'Go Space :)',
			// 		description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras eu pellentesque nibh. In nisl nulla, convallis ac nulla eget, pellentesque pellentesque magna.',
			// 		start: '2020-12-27',
			// 		end: '2020-12-27',
			// 		className: 'fc-bg-default',
			// 		icon : "rocket"
			// 	},
			// 	{
			// 		title: 'Dentist',
			// 		description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras eu pellentesque nibh. In nisl nulla, convallis ac nulla eget, pellentesque pellentesque magna.',
			// 		start: '2020-12-29T11:30:00',
			// 		end: '2020-12-29T012:30:00',
			// 		className: 'fc-bg-blue',
			// 		icon : "medkit",
			// 		allDay: false
			// 	}
			// ],

			eventRender: function(event, element) {
				if(event.icon){

				}
				element.find(".fc-time").prepend("<i class='fa fa-circle' style='font-size:10px;color:#007bff'></i>");
				element.popover({
					title: event.title,
					content: event.description,
					trigger: 'hover',
					placement: 'top',
					container: 'body'
				});
			},
			dayClick: function(date,allDay,getView) {
				var viewMode = getView.name;

				if (viewMode === 'month') {
					var dateClicked = new Date(date.format());
					var dayClicked = dateClicked.getDate();
					var monthClicked = dateClicked.getMonth()+1;
					monthClicked = monthClicked < 10 ? '0' + monthClicked : '' + monthClicked;
					var yearClicked = dateClicked.getFullYear();
					var clickedDate = monthClicked+"/"+dayClicked+"/"+yearClicked ;
					$('#new_event_start_date').val(clickedDate);
					$('#new_event_end_date').val(clickedDate);

					var curTime = roundQuarter()
					var amOrPm = (curTime.getHours() < 12) ? "AM" : "PM";
					var hours = curTime.getHours();
					hours = hours % 12;
					hours = hours ? hours : 12;
					var startMin =curTime.getMinutes();
					startMin = startMin > 9 ? startMin : '0' + startMin;
					var startTimeDisp = hours+":"+startMin+" "+amOrPm;
					$('#new_event_start_time').val(startTimeDisp);

					var endhours = curTime.getHours()+ 1;
					curTime.setHours(endhours);
					var endTimeAmPm = curTime.getHours() < 12 ? 'AM' : 'PM';
					endhours = endhours % 12;
					endhours = endhours ? endhours : 12;
					var endMin =curTime.getMinutes();
					endMin = endMin > 9 ? endMin : '0' + endMin;

					var endTimeDisp = endhours+":"+endMin+" "+endTimeAmPm;
					$('#new_event_end_time').val(endTimeDisp);
					jQuery('#modal-view-event-add').modal();
				}else{
					var slotDate = date.format();
					var dateClicked = new Date(date);
					var dayClicked = dateClicked.getDate();
					var monthClicked = dateClicked.getMonth()+1;
					monthClicked = monthClicked < 10 ? '0' + monthClicked : '' + monthClicked;
					var yearClicked = dateClicked.getFullYear();
					var clickedDate = monthClicked+"/"+dayClicked+"/"+yearClicked ;
					$('#new_event_start_date').val(clickedDate);
					$('#new_event_end_date').val(clickedDate);

					var curTime = new Date(slotDate);
					var amOrPm = (curTime.getHours() < 12) ? "AM" : "PM";
					var hours = curTime.getHours();
					hours = hours % 12;
					hours = hours ? hours : 12;
					var startMin =curTime.getMinutes();
					startMin = startMin > 9 ? startMin : '0' + startMin;
					var startTimeDisp = hours+":"+startMin+" "+amOrPm;
					$('#new_event_start_time').val(startTimeDisp);

					console.log(curTime)
					var endhours = curTime.getHours()+ 1;
					curTime.setHours(endhours)
					var endTimeAmPmDay = curTime.getHours() < 12 ? 'AM' : 'PM';
					endhours = endhours % 12;
					endhours = endhours ? endhours : 12;
					var endMin =curTime.getMinutes();
					endMin = endMin > 9 ? endMin : '0' + endMin;

					var endTimeDisp = endhours+":"+endMin+" "+endTimeAmPmDay;
					$('#new_event_end_time').val(endTimeDisp);
					jQuery('#modal-view-event-add').modal();
				}
				},
			eventClick: function(event, jsEvent, view) {
				jQuery('.event-icon').html("<i class='fa fa-"+event.icon+"'></i>");
				var eventId = event.eventId;
				console.log("event id: "+eventId)
					/*.eventId-> This is passed for delete event */
				jQuery('.eventId').val(eventId);

				jQuery('.eventUrl').attr('href',event.url);
				jQuery('#modal-view-event').modal();

				var inputToEventDetail = {};
				var inputEventId = event.eventId;
				inputToEventDetail.inputEventId = inputEventId;
				var fetchCalendarEventAjaxById = getCompleteUrl("fetchCalendarEventAjaxById");
				
				function eventResponseData(response){
					var title = $('#eName').val(response.title);

					var startDateTimeResp = new Date(response.startDateTime);
					var startDateResp = monthDetails(startDateTimeResp.getMonth())+"/"+startDateTimeResp.getDate()+"/"+startDateTimeResp.getFullYear();
					$('#update_event_start_date').val(startDateResp);
					var startTimeResp = hourDetails(startDateTimeResp)+":"+minuteDetails(startDateTimeResp)+" "+amOrPmDetails(startDateTimeResp)
					$('#updateEvtStatTime').val(startTimeResp);

					var endDateTimeResp = new Date(response.endDateTime);
					var endDateResp = monthDetails(endDateTimeResp.getMonth())+"/"+endDateTimeResp.getDate()+"/"+endDateTimeResp.getFullYear();
					$('#update_event_end_date').val(endDateResp);
					var endTimeResp = hourDetails(endDateTimeResp)+":"+minuteDetails(endDateTimeResp)+" "+amOrPmDetails(endDateTimeResp);
					$('#updateEvtEndTime').val(endTimeResp);

					var isPrivateEvent = response.isPrivateEvent;
					if(isPrivateEvent === "Y"){
						$("#editEvtPrivacy").prop('checked',true);
					}else{
						$("#editEvtPrivacy").prop('checked',false);
					}

					var isRecurringEvent = response.isRecurringEvent;

					if(isRecurringEvent === 'N') {
						$('#edit-event-repeat-select').val(isRecurringEvent);
						$('#edit-event-repeat-select').val("NEVER").trigger('change');
						$('#delRecEvt').hide();
						$('#delEvt').show();
						$('#updateEvt').show();
						$('#updateRecEvt').hide();
					}else {
						var recurringType = response.recurringEventVO.recurringType;
						var until = response.recurringEventVO.until;
						var numberOfOccurrence = response.recurringEventVO.numberOfOccurrence;
						var specDateVal = new Date(response.recurringEventVO.endDate).toLocaleDateString();
						$('#delRecEvt').show();
						$('#delEvt').hide();
						$('#updateEvt').hide();
						$('#updateRecEvt').show();
						$('#edit-event-repeat-select').val(recurringType).trigger('change');
						switch (recurringType) {
							case "DAILY":

								if(until === 'FOR_EVER'){
									$('#editUntilSel_daily').val(until).trigger('change');
								}else if(until === 'OCCURRENCE'){
									$('#editUntilSel_daily').val(until).trigger('change');
									$('#daily_ocr_edit').val(numberOfOccurrence);
								}else {
									$('#editUntilSel_daily').val(until).trigger('change');
									$('#daily_TDateEdit').val(specDateVal);
								}
							case "WEEKLY":
								var onSunday = response.recurringEventVO.onSunday;
								if(onSunday === "Y"){
									$('#edit-weekday-sun').prop('checked',true);
								}else {
									$('#edit-weekday-sun').prop('checked',false);
								}
								var onMonday = response.recurringEventVO.onMonday;
								if(onMonday === "Y"){
									$('#edit-weekday-mon').prop('checked',true);
								}else {
									$('#edit-weekday-mon').prop('checked',false);
								}
								var onTuesday = response.recurringEventVO.onTuesday;
								if(onTuesday === "Y"){
									$('#edit-weekday-tue').prop('checked',true);
								}else {
									$('#edit-weekday-tue').prop('checked',false);
								}
								var onWednesday = response.recurringEventVO.onWednesday;
								if(onWednesday === "Y"){
									$('#edit-weekday-wed').prop('checked',true);
								}else {
									$('#edit-weekday-wed').prop('checked',false);
								}
								var onThursday = response.recurringEventVO.onThursday;
								if(onThursday === "Y"){
									$('#edit-weekday-thu').prop('checked',true);
								}else {
									$('#edit-weekday-thu').prop('checked',false);
								}
								var onFriday = response.recurringEventVO.onFriday;
								if(onFriday === "Y"){
									$('#edit-weekday-fri').prop('checked',true);
								}else {
									$('#edit-weekday-fri').prop('checked',false);
								}
								var onSaturday = response.recurringEventVO.onSaturday;
								if(onSaturday === "Y"){
									$('#edit-weekday-sat').prop('checked',true);
								}else {
									$('#edit-weekday-sat').prop('checked',false);
								}
								if(until === 'FOR_EVER'){
									$('#editUntilSel_weekly').val(until).trigger('change');
								}else if(until === 'OCCURRENCE'){
									$('#editUntilSel_weekly').val(until).trigger('change');
									$('#weekly_ocr_edit').val(numberOfOccurrence);
								}else {
									$('#editUntilSel_weekly').val(until).trigger('change');
									$('#weekly_TDateEdit').val(specDateVal);
								}
							case "MONTHLY":
								var onMonthDay = response.recurringEventVO.onMonthDay;
								$('#onMonthDay_edit').val(onMonthDay);
								if(until === 'FOR_EVER'){
									$('#editUntilSel_monthly').val(until).trigger('change');
								}else if(until === 'OCCURRENCE'){
									$('#editUntilSel_monthly').val(until).trigger('change');
									$('#monthly_ocr_edit').val(numberOfOccurrence);
								}else {
									$('#editUntilSel_monthly').val(until).trigger('change');
									$('#monthly_TDateEdit').val(specDateVal);
								}


						}
					}
					var typeResp = response.type;
					if(typeResp === "HOME"){
						$('#homeClass').prop('checked',true)
					}else if (typeResp === "ONLINE"){
						$('#onlineClass').prop('checked',true)
					}else {
						$('#centerClass').prop('checked',true)
					}

					var studentResp = response.guestList;
					$('.multipleSelect').val(studentResp);
					$(".multipleSelect").fastselect().trigger('change');

					$('#updateEventDesc').val(response.description);
				}
				$.ajax({
					type: "GET",
					data: inputToEventDetail,
					url: fetchCalendarEventAjaxById,
					success: function(response) {
						eventResponseData(response);
					},
					error:function(){
					},
					dataType: "json",
				});

			},
			eventDrop: function(info) {

				var eventId = info.eventId;
				var title = info.title;
				var description = info.description;
				var updateStartDateTime = new Date(info.start.format()).getTime();
				var updateEndDateTime = new Date(info.end.format()).getTime();
				var updateEventByDragUrl = getCompleteUrl("updateCalendarEvent");
				var updateEventInput = {eventId:eventId,updateStartDateTime:updateStartDateTime,updateEndDateTime:updateEndDateTime,updateEventDesc:description,eventName:title}
			$.ajax({
				type: "POST",
				url: updateEventByDragUrl,
				data: updateEventInput,
				success: function(response) {
					if(response.STATUS === 'SUCCESS')
					{
						$('#calendar').fullCalendar('refetchEvents');
						bs4pop.notice('Event Saved.',{
							type: 'success',
							position: 'topright',
							appendType: 'append',
							autoClose: 5000,
							width:6000,
							className: 'text-danger'
						})
					}else {
						$('#calendar').fullCalendar('refetchEvents');
						console.log("Error"+response)
					}},
				error:function(response){
				},
				dataType: "json",
			});
			},
			eventResize: function (event) {
				var eventId = event.eventId;
				var title = event.title;
				var description = event.description;
				var updateStartDateTime = new Date(event.start.format()).getTime();
				var updateEndDateTime = new Date(event.end.format()).getTime();
				var updateEventByDragUrl = getCompleteUrl("updateCalendarEvent");
				var updateEventInput = {eventId:eventId,updateStartDateTime:updateStartDateTime,updateEndDateTime:updateEndDateTime,updateEventDesc:description,eventName:title}
				$.ajax({
					type: "POST",
					url: updateEventByDragUrl,
					data: updateEventInput,
					success: function(response) {
						if(response.STATUS === 'SUCCESS')
						{
							$('#calendar').fullCalendar('refetchEvents');
							bs4pop.notice('Event Saved.',{
								type: 'success',
								position: 'topright',
								appendType: 'append',
								autoClose: 5000,
								width:6000,
								className: 'text-danger'
							})
						}else {
							$('#calendar').fullCalendar('refetchEvents');
							console.log("Error"+response)
						}},
					error:function(response){
					},
					dataType: "json",
				});
			}
			
		})
	});

})(jQuery);

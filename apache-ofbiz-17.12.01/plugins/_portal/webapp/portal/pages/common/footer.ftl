    <!-- Bootstrap core JavaScript -->
    <script src="../static/vendor/jquery/jquery.min.js"></script>
    <script src="../static/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
    <script src="../static/vendor/bootstrap/bs4 pop/bs4.pop.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/fullcalendar/3.9.0/fullcalendar.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/air-datepicker/2.2.3/js/datepicker.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/air-datepicker/2.2.3/js/i18n/datepicker.en.js"></script>

    <script src="../static/vendor/jquery/jquery.datetimepicker.full.js"></script>
    <script type="text/javascript" src="../static/vendor/timepicker/jquery.timepicker.js"></script>
    <script src="../static/vendor/avatar.js"></script>
    <script src="../static/js/jquery.fulltable/jquery.fulltable.js"></script>

    <!-- Portal Scripts -->
    <script src="../static/js/students.js?rnd=20200517"></script>
    <script src="../static/js/cthcalender.js"></script>
    <script src="../static/js/new_invoice.js"></script>
    <script src="../static/js/billing.js"></script>
    <script src="../static/js/invoices.js"></script>
    <script src="../static/js/edit_invoice.js"></script>
    <script src="../static/js/myAccount.js"></script>
    <script src="../static/js/files.js"></script>

    <#--dd-->
    <script src="../static/vendor/dropdown_plugin/fastselect.standalone.js"></script>

    <script src="../static/js/expense.js"></script>
    <script src="../static/js/dashboard.js"></script>
    <script src="../static/js/notes.js"></script>
    <#--ckeditor cdn-->
    <script src="//cdn.ckeditor.com/4.14.0/standard/ckeditor.js"></script>

    <#--invoice-->
    <script src="../static/vendor/invoice/jquery.invoice.js"></script>

    <!-- Menu Toggle Script -->
    <script>
    $("#menu-toggle").click(function(e) {
      e.preventDefault();
      $("#wrapper").toggleClass("toggled");
    });
    </script>

    <script>
    function getCompleteUrl(uri) {
      return "<@ofbizUrl>"+uri+"</@ofbizUrl>";
    }
    </script>

    <#--calendar-->
    <script>
        $('#new_event_end_date').datepicker(
              {
                step: 15,
                  autoClose: true,
                  language: 'en'
              }
        );
        $("#new_event_start_time").timepicker(
            {
                'timeFormat': 'h:i A',
                'step': 15,
                'setTime': new Date(),
            }
        );

        $("#new_event_end_time").timepicker(
            {
                'timeFormat': 'h:i A',
                'step': 15,
                // 'setTime': new Date(),
            }
        );
        $('#new_event_start_date').datepicker(
              {
                  autoClose: true,
                  language: 'en'
              }
        );
        $('#update_event_start_date').datepicker(
              {
                timepicker:false,
                step: 15,
                  autoClose: true,
                  language: 'en'
              }
        );

        $('#update_event_end_date').datepicker(
              {
                timepicker:false,
                step: 15,
                autoClose: true,
                  language: 'en'
              }
        );
        $('.specificDatePicker').datepicker(
            {
                timepicker:false,
                autoClose: true,
                language: 'en'
            }
        );

        $('#updateEvtStatTime').timepicker({
            'timeFormat': 'h:i A',
            'step': 15,
            // 'setTime': new Date(),
            // 'minTime': new Date()
        });
        $('#updateEvtEndTime').timepicker({
            'timeFormat': 'h:i A',
            'step': 15,
            // 'setTime': roundQuarter(),
            'minTime': roundQuarter()
        });

        $('.datepickerBs').datepicker({
            autoClose: true,
            language: 'en'
        })
    </script>

</body>

</html>

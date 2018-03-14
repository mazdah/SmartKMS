var isCreateIndex = true;

$("._indexsel").hide();

$(function () {
    'use strict';

    // Initialize the jQuery File Upload widget:
    $('#fileupload').fileupload({
        // Uncomment the following to send cross-domain cookies:
        //xhrFields: {withCredentials: true},
        url: '/SmartKMS/fileupload'
    });

    // Enable iframe cross-domain access via redirect option:
    $('#fileupload').fileupload(
        'option',
        'redirect',
        '/SmartKMS/result'
    );
});

$(document).ready(function () {
	//Initialize Select2 Elements
    $(".select2").select2()
    
    $("._start").click(function (event) {
    	
    		event.preventDefault();
    		
    		var formData = $('#fileupload')[0];
    		var data = new FormData(formData);
    		
    		$.ajax({
    	        url: "/SmartKMS/fileupload",
    	        type: "POST",
    	        data: data,
    	        enctype: 'multipart/form-data',
    	        processData: false,
    	        contentType: false,
    	        cache: false,
    	        success: function (data, status, jqXHR) {
    	          // Handle upload success
    	          alert("File succesfully uploaded");
    	        },
    	        error: function (jqXHR, status) {
    	          // Handle upload error
    	          alert(
    	              "File not uploaded \n\n" + JSON.stringify(jqXHR));
    	        }
    	      });
    });
    
    $(document).on("click", "._createindex", function () {
	    	var indexTemplate = '<input class="form-control _indexName col-md-5" type="text" name="indexName" placeholder="Index 이름" style="width: 40%;">'
	    		+ '<button type="button" class="btn btn-primary _selectindex col-md-2" >'
	    		+ '<i class="glyphicon glyphicon-upload"></i>'
	    		+ '<span>기존 Index 선택</span>'
	    		+ '</button>';
    	
    		$("._indexform").empty();
		$("._indexform").append(indexTemplate);
    });
    
    $(document).on("click", "._selectindex", function () {
    		var indexTemplate = '<select class="form-control select2 _indexName col-md-5" name="indexName" style="width: 40%;">'
	        + '<option selected="selected">Alabama</option>'
	        + '<option>Alaska</option>'
	        + '<option>California</option>'
	        + '<option>Delaware</option>'
	        + '<option>Tennessee</option>'
	        + '<option>Texas</option>'
	        + '<option>Washington</option>'
	        + '</select>'
	        + '<button type="button" class="btn btn-primary _createindex col-md-2">'
	        + '  <i class="glyphicon glyphicon-upload"></i>'
	        + '  <span>새 Index 만들기</span>'
	        + '</button>';
    		$("._indexform").empty();
    		$("._indexform").append(indexTemplate);
    });
});
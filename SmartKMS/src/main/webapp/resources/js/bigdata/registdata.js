var isCreateIndex = true;

$("._indexsel").hide();

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
    
    $("._createindex").click(function () {
    		$("._indexsel").hide();
    		$("._indexinput").show();
    });
    
    $("._selectindex").click(function () {
    		$("._indexsel").show();
		$("._indexinput").hide();
    });
});
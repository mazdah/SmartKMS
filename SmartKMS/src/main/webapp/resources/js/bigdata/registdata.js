var isCreateIndex = true;

$("._indexsel").hide();

$(document).ready(function () {
	//Initialize Select2 Elements
    $(".select2").select2()
    
    $("._start").click(function () {
    		alert("file upload");
    		$("#fileupload").submit();
    		
//    		$("#fileupload").ajaxSubmit({
//    			dataType: 'json',
//    			url: '/SmartKMS/fileupload',
//    			success: function (data, status, jqXHR) {
//    				alert(JSON.stringify(jqXHR));
//    			},
//    			error: function (jqXHR, status) {
//    				alert(JSON.stringify(jqXHR));
//    			}
//    			
//    		});
    		
    		$.ajax({
    	        url: "/SmartKMS/fileupload",
    	        type: "POST",
    	        data: new FormData($("#fileupload")[0]),
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
    	              "File not uploaded (perhaps it's too much big)\n\n" + JSON.stringify(jqXHR));
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
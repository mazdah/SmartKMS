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
    
    $('#fileupload').bind('fileuploaddestroy', function (e, data) {
    		var fileName = data.context.find('a[download]').attr('download');

	    	if (confirm("'" + fileName + "' 파일을 삭제하시겠습니까?")) {
	    		return true
	    	} else {
	    		e.preventDefault();
	    		return false;
	    	}
    	});
});

$(document).ready(function () {
	//Initialize Select2 Elements
    $(".select2").select2();
    
//    $(document).on("click", "._deleteFile", function (event) {
//    		alert("delete clicked!");
//    	
//    });
    
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
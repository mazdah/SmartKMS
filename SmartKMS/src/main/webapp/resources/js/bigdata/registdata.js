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
    
    $('#fileupload').bind('fileuploaddone', function (e, data) {
    		// data.result
        // data.textStatus;
        // data.jqXHR;
  
    		// 만일 Import Elasticsearch가 체크되어있다면 logn polling 수행
    		var isImport = data.result.files[0].isImport;
    		
    		if (isImport == true) {
    			$("._modal-message").empty();
        		$("._modal-message").append("<p>현재 업로드한 엑셀 파일의 데이터를 Elasticsearch로 import 하고 있습니다.</p><p>작업이 끝날 때까지 페이지 이동이나 새로고침을 자제해주세요.</p><p>파일 이름 : <font color='#00FFFF'><strong>" + data.result.files[0].name + "</strong></font></p>");
        		console.log("progress start!!!");
        		
        		$("._progress").attr("aria-valuenow", 0);
            $("._progress").css("width", "0%");
    			
    			importData(data.result.files[0].name);
    			poll();
    			$("#modal-success").modal({backdrop: 'static', keyboard: false});
    			
    			$("._close").attr("disabled","disabled");
    		}
    	});
    
    function importData(fileName) {
    		$.ajax({
            url: '/SmartKMS/startimport?fileName=' + fileName,
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            success: function(data, status, jqXHR) {

            },
            error: function (jqXHR, status) {
            		alert("[" + status + "] data import started Fail!\n\n" + JSON.stringify(jqXHR));
            }
        })
    }
    
    var isContinue = true; 
    function poll() {
    		
    		var timeout;
        var $req = $.ajax({
            url: '/SmartKMS/checkprocess',
            type: 'GET',
            dataType: 'json',
            success: function(data, status, jqXHR) {
                console.log(JSON.stringify(data));
                
                var total = data.totalLine;
                var now = data.importedLine;

                if (total > 0) {
                		$("._total").text(total);
                }                
                $("._now").text(now);
                
                var progressValue = (now / total * 100) + "";
                
                if (now > total) {
                		progressValue = 100;
                		
                		$("._modal-message").empty();
                		$("._modal-message").append("<p>데이터 import 작업이 모두 완료되었습니다.</p><p>창을 닫고 다음 작업을 진행해도 좋습니다.</p>");
                		console.log("progress end!!!");
                		
                		$("._close").removeAttr("disabled");
                		isContinue = false;
                }
                
                $("._progress").attr("aria-valuenow", progressValue);
                $("._progress").css("width", progressValue + "%");
            },
            error: function (jqXHR, status) {
            	
            },
            timeout: 3000,
            complete: function () {
            		if (isContinue == true) {
            			timeout = setTimeout(function() { poll(); }, 500);
            		} else {
            			clearTimeout(timeout);
	        	        	$req.abort();
	        	    		$req = null;
            		}
            }
        });
    }
    
    $("#modal-success").on('hidden.bs.modal', function (e) {
    	  	location.reload();
    	})
});

$(document).ready(function () {
	//Initialize Select2 Elements
    $(".select2").select2();
    
    $.ajax({
        url: '/SmartKMS/indices',
        type: 'GET',
        dataType: 'json',
        success: function(data, status, jqXHR) {
            alert(JSON.stringify(data));
        },
        error: function (jqXHR, status) {
        	
        }
    });
    
  //iCheck for checkbox and radio inputs
    $('input[type="checkbox"].minimal, input[type="radio"].minimal').iCheck({
      checkboxClass: 'icheckbox_minimal-blue',
      radioClass   : 'iradio_minimal-blue'
    })
    
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
var isCreateIndex = true;
var indexSelectHtml = "";
var typeSelectHtml = "";

var hour = 0;
var minute = 0;
var second = 0;
var timer;
var timeout;
var $req;
var redayIndex = "N";
var readyType = "N";

var currPage = 0;

$("._indexsel").hide();
$("._indexoptions").hide();

function getTimestampToDate(timestamp){

	var date = new Date(timestamp);	
	var chgTimestamp = date.getFullYear().toString()	 + "-"
	+addZero(date.getMonth()+1)	+ "-"
	+addZero(date.getDate().toString())	+ " "
	+addZero(date.getHours().toString())	+ " : "
	+addZero(date.getMinutes().toString()) + " : "	
	+addZero(date.getSeconds().toString());
	
	return chgTimestamp;
}

 

function addZero(data){

	return (data<10) ? "0"+data : data;

}

$(function () {
    'use strict';
    
  //Initialize Select2 Elements
    $(".select2").select2();

    function setTimer(){
    	   timer = setInterval(function(){
    		   second++;
    		   
    		   if (second == 60) {
    			   second = 0;
    			   minute++;
    			   
    			   if (minute == 60) {
    				   minute = 0;
    				   hour++;
    			   }
    		   }
    		   
    		   var hourStr = hour < 10?"0"+hour:""+hour;
    		   var minuteStr = minute < 10?"0"+minute:""+minute;
    		   var secondSt = second < 10?"0"+second:""+second;
    		   
    		   $("._hour").text(hourStr);
    		   $("._minute").text(minuteStr);
    		   $("._second").text(secondSt);
    	   },1000);
    	}
    
    var table = $('#example1').DataTable({
    		"processing": true,
        "serverSide": true,
        "pageLength": 10,
        "ajax": {
            "url": "/SmartKMS/filelist",
            "data": function ( data ) {
            		var table = $('#example1').DataTable()
                data.page = (table != undefined)?table.page.info().page:0;
                	data.size = (table != undefined)?table.page.info().length:10;
                	data.sort = data.columns[data.order[0].column].data + ',' + data.order[0].dir;
            	}
        },
        "columns": [
                    { "data": "orgFileName", "name" : "fileName", "title" : "파일명"},
                    { "data": "fileSize", "name" : "fileSize" , "title" : "size", "className" : "align-right"},
                    { "data": "totalRows", "name" : "totalRows" , "title" : "row", "className" : "align-right"},
                    { "data": "elasticIndex", "name" : "elIndex" , "title" : "Index"},
                    { "data": "elasticType", "name" : "elType" , "title" : "Type"},
                    { "data": "import", "name" : "import" , "title" : "import 여부", "className" : "align-center"},
                    { "data": "uploadDate", "name" : "uploadDate" , "title" : "upload 날짜"},
                    { "data": "importDate", "name" : "importDate" , "title" : "import 날짜"}
                ],
        "columnDefs": [
		        		{
		        			"className":	"align-center",
		                "render": function ( data, type, row ) {
		                    return getTimestampToDate(data);
		                },
		                "targets": 6
		            },
		            {
		            		"className":	"align-center",
		                "render": function ( data, type, row ) {
		                    return getTimestampToDate(data);
		                },
		                "targets": 7
		            },
		            {
		            		"className":	"align-center",
		            		"targets": 8,
		                "data": null,
		                "render": function (data, type, row) {
		                					var renderStr = "";
		                					
		                					renderStr += "<button class='btn btn-warning _downdload'><i class='glyphicon glyphicon-download'></i> Download</button>";
		                					renderStr += "<button class='btn btn-danger _delete'><i class='glyphicon glyphicon-trash'></i> Delete</button>";
		                					
		                					if (row.import) {
		                						renderStr += "<button class='btn btn-default _import' disabled><i class='glyphicon glyphicon-import'></i> Import</button>";
		                					} else {
		                						renderStr += "<button class='btn btn-success _import'><i class='glyphicon glyphicon-import'></i> Import</button>";
		                					}
		                					
					                		return renderStr;
					                }
		                		
		            }
		            
        ]  
      });
    
    $('#example1 tbody').on( 'click', '._downdload', function () {
        var data = table.row( $(this).parents('tr') ).data();
//        alert("download : " + JSON.stringify(data));
        controller.downloadFile(data.filePath, data.orgFileName);
    } );
    
    $('#example1 tbody').on( 'click', '._delete', function () {
        var data = table.row( $(this).parents('tr') ).data();
//        alert("delete : " + JSON.stringify(data));
        controller.deleteFile(data.fileId, data.orgFileName);
    } );
    
    $('#example1 tbody').on( 'click', '._import', function () {
        var data = table.row( $(this).parents('tr') ).data();
//        alert("import : " + JSON.stringify(data));
        
        $("._modal-message").empty();
		$("._modal-message").append("<p>현재 업로드한 엑셀 파일의 데이터를 변환/적재 하고 있습니다.</p><p>작업이 끝날 때까지 페이지 이동이나 새로고침을 자제해주세요.</p><p>파일 이름 : <font color='#00FFFF'><strong>" + data.orgFileName + "</strong></font></p>");
		console.log("progress start!!!");
		
		$("._progress").attr("aria-valuenow", 0);
		$("._progress").css("width", "0%");
    
        controller.importData(data.fileId, data.orgFileName, data.elasticIndex, data.elasticType);
        controller.poll();
		$("#modal-success").modal({backdrop: 'static', keyboard: false});
		setTimer()
		
		$("._close").attr("disabled","disabled");
    } );
    
    //$('#example1').dataTable().fnSetFilteringEnterPress();
    
    // Initialize the jQuery File Upload widget:
    $('#fileupload').fileupload({
        // Uncomment the following to send cross-domain cookies:
        //xhrFields: {withCredentials: true},
        url: '/SmartKMS/fileupload'
    });

    // Enable iframe cross-domain access via redirect option:
//    $('#fileupload').fileupload(
//        'option',
//        'redirect',
//        '/SmartKMS/result'
//    );
    
    $('#fileupload').bind('fileuploaddestroy', function (e, data) {
    		var fileName = data.context.find('a[download]').attr('download');

	    	if (confirm("'" + fileName + "' 파일을 삭제하시겠습니까?")) {
	    		return true
	    	} else {
	    		e.preventDefault();
	    		return false;
	    	}
    	})
    	.bind('fileuploaddone', function (e, data) {
    		// data.result
        // data.textStatus;
        // data.jqXHR;
  
    		// 만일 Import Elasticsearch가 체크되어있다면 logn polling 수행
    		var isImport = data.result.files[0].isImport;
    		
    		if (isImport == true) {
    			$("._modal-message").empty();
        		$("._modal-message").append("<p>현재 업로드한 엑셀 파일의 데이터를 변환/적재 하고 있습니다.</p><p>작업이 끝날 때까지 페이지 이동이나 새로고침을 자제해주세요.</p><p>파일 이름 : <font color='#00FFFF'><strong>" + data.result.files[0].name + "</strong></font></p>");
        		console.log("progress start!!!");
        		
        		$("._progress").attr("aria-valuenow", 0);
            $("._progress").css("width", "0%");
    			
    			controller.importData(data.result.files[0].id, data.result.files[0].name, data.result.files[0].indexName, data.result.files[0].type);
    			controller.poll();
    			$("#modal-success").modal({backdrop: 'static', keyboard: false});
    			setTimer()
    			
    			$("._close").attr("disabled","disabled");
    		}
    		else {
    			alert("파일을 정상적으로 Upload 하였습니다.\n파일명 : " + data.result.files[0].name);
    			$('#example1').DataTable().ajax.reload();
    		}
    	})
    	.bind('fileuploadsubmit', function (e, data) {
    		var indexName = $('._indexName').val();
    		var typeName = $('._type').val();
    		
    		var selIndexName = $('._indexName option:selected').val();
    		var selTypeName = $('._type option:selected').val();
    		
    		//alert("readyType = " + readyType + " : " + typeName + " : " + selTypeName);
    		
    		if (readyIndex == "S") {
    			if (selIndexName == "" || selIndexName == undefined) {
        			alert("Index Name을 입력해주세요.");
        			return false;
        		}
    		} else {
    			if (indexName == "" || indexName == undefined) {
        			alert("Index Name을 입력해주세요.");
        			return false;
        		}
    		}
    		
    		if (readyType == "S") {
    			if (selTypeName == "" || selTypeName == undefined) {
        			alert("Type을 입력해주세요.");
        			return false;
        		}
    		} else {
    			if (typeName == "" || typeName == undefined) {
        			alert("Type을 입력해주세요.");
        			return false;
        		}
    		}
    		
    		
    		return true;
    	})
    	.bind('fileuploaddestroyed', function (e, data) {
    		alert("파일을 정상적으로 삭제 하였습니다.\n파일명 : " + data.result.files[0].name);
    		$('#example1').DataTable().ajax.reload();
    	});
    
    $("#modal-success").on('hidden.bs.modal', function (e) {
    	  	location.reload();
    	})
});

$(document).ready(function () {
    
  //iCheck for checkbox and radio inputs
    $('input[type="checkbox"].minimal, input[type="radio"].minimal').iCheck({
      checkboxClass: 'icheckbox_minimal-blue',
      radioClass   : 'iradio_minimal-blue'
    })
    
    $(document).on("click", "._createindex", function () {
	    	var indexTemplate = '<input class="form-control _indexName col-md-6" type="text" name="indexName" placeholder="Index 이름" style="width: 40%;">'
	    		+ '<button type="button" class="btn btn-primary _selectindex col-md-3" >'
	    		+ '<i class="glyphicon glyphicon-th-list"></i>&nbsp;&nbsp;&nbsp;'
	    		+ '<span>기존 Index 선택</span>'
	    		+ '</button>';
    	
    		$("._indexform").empty();
		$("._indexform").append(indexTemplate);
		
		view.changeForm(true);
		
		readyIndex = "N";
    });
    
    $(document).on("click", "._selectindex", function () {
    		if (indexSelectHtml == "") {
    			alert("기등록된 index가 없습니다. index를 새로 생성하세요.");
    			return
    		}
    		
    		$("._indexform").empty();
    		$("._indexform").append(indexSelectHtml);
    		view.changeForm(false);
    		readyIndex = "S";
    });
    
    $(document).on("click", "._createtype", function () {
	    	var typeTemplate = '<input class="form-control col-md-6 _type" type="text" name="type" placeholder="Input Type" style="width: 40%;">'
		    + '<button type="button" class="btn btn-primary _selecttype col-md-3" >'
            + '<i class="glyphicon glyphicon-th-list"></i>&nbsp;&nbsp;&nbsp;'
            + '<span>기존 type 선택</span>'
            + '</button>';
		
		$("._typeform").empty();
		$("._typeform").append(typeTemplate);
		
		readyType = "N";
	});
	
	$(document).on("click", "._selecttype", function () {
			if (typeSelectHtml == "") {
				alert("기등록된 type이 없습니다. type을 새로 생성하세요.");
				return
			}
			
			$("._typeform").empty();
			$("._typeform").append(typeSelectHtml);
			
			readyType = "S";
	});
	
	$(document).on("change", "._indexName", function () {
		controller.getType($("._indexName option:selected").val());
	});
	
	$("._indexsubmit").click(function() {
		//alert("인덱스 생성!!!");
		controller.createIndex();
	});
});

var view = function () {
	
	var _init = function() {
		
	};
	
	var _changeForm = function (isIndex) {
		if (isIndex == true) {
			$("._typeform").hide();
			$("._isimport").hide();
			$("._uploadbuttons").hide();
			
			$("._indexoptions").show();
		} else {
			$("._typeform").show();
			$("._isimport").show();
			$("._uploadbuttons").show();
			
			$("._indexoptions").hide();
		}
	}
	
	return {
		init			: _init,
		changeForm		: _changeForm
	}
}();

view.init();

var controller = function () {
	
	var isContinue = true; 
	
	var _init = function() {
		_getIndexList();
	};
	
	
	var _getIndexList = function() {
		 $.ajax({
	        url: '/SmartKMS/indices',
	        type: 'GET',
	        dataType: 'json',
	        success: function(data, status, jqXHR) {
	            if (data.length > 0) {
		            	var indexTemplate = '<select class="form-control select2 _indexName col-md-6" name="indexName" style="width: 40%;">';
		            	
		            	for(i in data) {
		            		if (i == 0) {
		            			indexTemplate += '<option value="' + data[i] + '" selected="selected">' + data[i] + '</option>';
		            		} else {
		            			indexTemplate += '<option value="' + data[i] + '">' + data[i] + '</option>';
		            		}
		            	}

		            	indexTemplate += '</select>';
		            	indexTemplate += '<button type="button" class="btn btn-primary _createindex col-md-3">';
		            	indexTemplate += '  <i class="glyphicon glyphicon-plus"></i>&nbsp;&nbsp;&nbsp;';
		            	indexTemplate += '  <span>새 Index 만들기</span>';
		            	indexTemplate += '</button>';

		            	$("._indexform").empty();
		            	$("._indexform").append(indexTemplate);
		            	
		            	view.changeForm(false);
		            		
		            	indexSelectHtml = indexTemplate;
		            	_getType(data[0]);
		            	readyIndex = "S";
	            } else {
	            	view.changeForm(true);
	            	readyIndex = "N";
	            }
	        },
	        error: function (jqXHR, status) {
	        	
	        }
	    });
	};
	
	var _getType = function (index) {
		$.ajax({
	        url: '/SmartKMS/types?index=' + index,
	        type: 'GET',
	        dataType: 'json',
	        success: function(data, status, jqXHR) {
	            if (data.length > 0) {
	    	            	var typeTemplate = '<label><i class="glyphicon glyphicon-exclamation-sign"></i>&nbsp;&nbsp;&nbsp;Type</label><br>';
	    	            	typeTemplate += '<select class="form-control select2 _type col-md-6" name="type" style="width: 40%;">';
	    	            	
	    	            	for(i in data) {
	    	            		if (i == 0) {
	    	            			typeTemplate += '<option value="' + data[i] + '" selected="selected">' + data[i] + '</option>';
	    	            		} else {
	    	            			typeTemplate += '<option value="' + data[i] + '">' + data[i] + '</option>';
	    	            		}
	    	            	}
	
	    	            	typeTemplate += '</select>';
	    	            	typeTemplate += '<button type="button" class="btn btn-primary _createtype col-md-3">';
	    	            	typeTemplate += '  <i class="glyphicon glyphicon-plus"></i>&nbsp;&nbsp;&nbsp;';
	    	            	typeTemplate += '  <span>새 type 만들기</span>';
	    	            	typeTemplate += '</button>';
	
	    	            	$("._typeform").empty();
	    	            	$("._typeform").append(typeTemplate);
	    	            		
	    	            	typeSelectHtml = typeTemplate;
	    	            	
	    	            	redyType = "S";
	            } else {
	            		redyType = "N";
	            }
	        },
	        error: function (jqXHR, status) {
	        	
	        }
	    });
	};
	
	var _createIndex = function() {
		var indexName = $("._indexName").val();
		var type = $("._newtype").val();
		var shard = $("._shard").val();
		var replica = $("._replica").val();
		
		if (indexName == "" || indexName == undefined) {
			alert("Index 이름은 필수 입력입니다.");
			return;
		}
		
		if (type == "" || type == undefined) {
			alert("Type은 필수 입력입니다.");
			return;
		}
		
		
		var indexObj = {};
		indexObj.indexName = indexName;
		indexObj.alias = $("._alias").val();
		indexObj.type = type;
		
		if (shard == "" || shard == undefined) {
			indexObj.shard = 1;
		} else {
			indexObj.shard = shard;
		}
		
		if (replica == "" || replica == undefined) {
			indexObj.replica = 0;
		} else {
			indexObj.replica = replica;
		}
		
		
		indexObj.mapping = $("._mapping").val();
		
		$.ajax({
	        url: '/SmartKMS/createindex',
	        type: 'POST',
	        dataType: 'json',
	        contentType: 'application/json; charset=utf-8',
	        data: JSON.stringify(indexObj),
	        success: function(data, status, jqXHR) {
	        	alert("success : " + data.message);
	        	location.reload();
	        },
	        error: function (jqXHR, status) {
	        	alert("fail : " + JSON.stringify(jqXHR));
	        }
		});
	};
	
	var _importData = function(id, fileName, indexName, type) {
		$.ajax({
	        url: '/SmartKMS/startimport?id=' + id + '&fileName=' + fileName + '&indexName=' + indexName + '&type=' + type,
	        type: 'GET',
	        dataType: 'json',
	        contentType: 'application/json; charset=utf-8',
	        success: function(data, status, jqXHR) {
	        		//alert("[" + status + "] data import Success!\n\n" + JSON.stringify(data));
	        		isContinue = false;   
	        		clearTimeout(timeout);
	    	        	$req.abort();
	    	    		$req = null;
	        	
	        		if (data.code == "9999") {
	        			$("._modal-message").empty();
	    	        		$("._modal-message").append("<p>작업이 모두 완료되었습니다.</p><p>창을 닫고 다음 작업을 진행해도 좋습니다.</p>");
	    	        		console.log("parsing end!!!");
	        		} else {
	        			$("._modal-message").empty();
		        			$("._modal-message").append("<font color='#ff0000'><p>데이터 import 중 오류가 발생했습니다.</p><p><strong>" + data.message + "</strong></p></font>");
	        		}
	            	
	        		clearInterval(timer);
	        		$("._close").removeAttr("disabled");
	        },
	        error: function (jqXHR, status) {
	        		alert("[" + status + "] data import started Fail!\n\n" + JSON.stringify(jqXHR));
	        		isContinue = false;
	        		location.reload();
	        }
		});
	};

    var _poll = function() {   		
        $req = $.ajax({
            url: '/SmartKMS/checkprocess',
            type: 'GET',
            dataType: 'json',
            success: function(data, status, jqXHR) {
                console.log(JSON.stringify(data));
                
                var total = data.totalLine;
                var now = data.importedLine;
                var isImportComplete = data.isImportComplete;
                var importedCnt = data.importedCnt;

                if (total > 0) {
                		$("._total").text(total);
                }                
                $("._now").text(now);
                
                var progressValue = (now / total * 100) + "";
                
                if (now != 0 && now >= total) {
                		progressValue = 100;
                		

//            			$("._modal-message").empty();
//                		$("._modal-message").append("<p>데이터 분석 / 변환 작업이 모두 완료되었습니다.</p><p>데이터를 elasticsearch로 import 중이니 잠시만 더 기다려주세요..</p>");
                		console.log("parsing end!!!");      
                }
                
                $("._nowimport").text(importedCnt)
                
                $("._progress").attr("aria-valuenow", progressValue);
                $("._progress").css("width", progressValue + "%");
            },
            error: function (jqXHR, status) {
            	
            },
            timeout: 3000,
            complete: function () {
            		//if (isContinue == true) {
            			timeout = setTimeout(function() { controller.poll(); }, 500);
//            		} else {
//            			clearTimeout(timeout);
//	        	        	$req.abort();
//	        	    		$req = null;
//            		}
            }
        });
    };
    
    var _deleteFile = function (fileId, fileName) {
    		$.ajax({
	        url: '/SmartKMS/filedelete?fileId=' + fileId + '&fileName=' + fileName,
	        type: 'GET',
	        dataType: 'json',
	        contentType: 'application/json; charset=utf-8',
	        success: function(data, status, jqXHR) {
	        		//alert("[" + status + "] data import Success!\n\n" + JSON.stringify(data));
	        		alert(data.message);
	        		$('#example1').DataTable().ajax.reload();
	        },
	        error: function (jqXHR, status) {
	        		alert("[" + status + "] 파일을 삭제하지 못하였습니다.\n\n" + JSON.stringify(jqXHR));
	        }
		});
    };
    
    var _downloadFile = function (filePath, fileName) {
    		window.location.href = '/SmartKMS/download?filePath=' + filePath + '&fileName=' + fileName;
//		$.ajax({
//        url: '/SmartKMS/download?filePath=' + filePath + '&fileName=' + fileName,
//        type: 'GET',
//        dataType: 'json',
//        contentType: 'application/json; charset=utf-8',
//        success: function(data, status, jqXHR) {
//        		//alert("[" + status + "] data import Success!\n\n" + JSON.stringify(data));
//        		alert("파일을 정상적으로 다운로드 하였습니다.");
//        },
//        error: function (jqXHR, status) {
//        		alert("[" + status + "] 파일을 다운로드하지 못하였습니다.\n\n" + JSON.stringify(jqXHR));
//        }
//		});
	};
	
	return {
		init			: _init,
		getType		: _getType,
		createIndex	: _createIndex,
		importData	: _importData,
		poll		: _poll,
		deleteFile	: _deleteFile,
		downloadFile: _downloadFile
	}
}();

controller.init();
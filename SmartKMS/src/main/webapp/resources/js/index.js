$('#ciframe').attr("height", $(document).height() - 200);

var resizeObj = (function()
{
   window.addEventListener('resize',handler,false);
   var timeOut;
   var hanArr = [];
   function handler()
   {
      if( !timeOut )
      {
         timeOut = setTimeout(function()
         {
            timeOut = null;
            for(var i = 0; i<hanArr.length; i++){
              hanArr[i]();
            }
         },10);
      }
   }
   return {
      onResize:function(func)
      {
         hanArr.push(func);
      },
      deleteResize:function()
      {
      },
      clear : function()
      {
      }
  }
})();

resizeObj.onResize(function()
{
//	var docH = $(document).height() - 200;
//  // 실행 
//	$('#ciframe').attr("height", docH);
	
//	while ($('#ciframe').attr('height') != $(document).height() - 200) {
		$('#ciframe').attr("height", $(document).height() - 200);
//	}
	
});

function displayHeader(main, sub, parentMenu, childMenu) {
	var headerStr = '<h1 class="_header_main">'
    headerStr += main;
	headerStr += '<small class="header_sub">' + sub + '</small>';
	headerStr += '</h1>';
	headerStr += '<ol class="breadcrumb">';
	headerStr += '<li><a href="#"><i class="fa fa-dashboard"></i> <span class="_bcmain">' + parentMenu + '</span></a></li>';
	headerStr += '<li class="active _bcsub">' + childMenu + '</li>';
	headerStr += '</ol>';
	
	$(".content-header").empty();
	$(".content-header").append(headerStr);
}

$(document).ready(function() {
	$("._dataregist").click(function () {
		$("#ciframe").attr("src", "/SmartKMS/dataregist");
		
		displayHeader("데이터 등록", "엑셀 또는 CSV 파일 등록", "빅데이터 분석", "데이터 등록")
	});
	
	$("._indexlist").click(function () {
		$("#ciframe").attr("src", "/SmartKMS/indexlist");
		
		displayHeader("Index 목록", "기등록된 Index 목록 및 상세 내용 조회", "빅데이터 분석", "Index 목록")
	});

	$("._datachart").click(function () {
		$("#ciframe").attr("src", "/SmartKMS/datachart");
		
		displayHeader("데이터 Chart", "배치 분석된 데이터 chart 조회", "빅데이터 분석", "데이터 Chart")
	});

	$("._realtimechart").click(function () {
		$("#ciframe").attr("src", "/SmartKMS/realtimechart");
		
		displayHeader("Realtime Chart", "API를 통해 실시간 조회된 데이터의 chart", "빅데이터 분석", "Realtime Chart")
	});
});

var view = function () {
	var _init = function () {
		
	};
	
	return {
		init			: _init
	};
}();

view.init();

var controller = function () {
	var _init = function () {
		
	};
	
	return {
		init			: _init
	};
}();

controller.init();

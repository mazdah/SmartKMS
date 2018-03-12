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

$(document).ready(function() {
	$("._dataregist").click(function () {
		$("#ciframe").attr("src", "/SmartKMS/dataregist");
	});
	
	$("._indexlist").click(function () {
		$("#ciframe").attr("src", "/SmartKMS/indexlist");
	});

	$("._datachart").click(function () {
		$("#ciframe").attr("src", "/SmartKMS/datachart");
	});

	$("._realtimechart").click(function () {
		$("#ciframe").attr("src", "/SmartKMS/realtimechart");
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
